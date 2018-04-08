package com.example.hubbler_sudesh.dynamicform;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FormSelectionActivity extends AppCompatActivity {

    private static final String TAG = "FormSelectionActivity";
    List<View> fieldViews = new ArrayList<View>();
    List<Object> items = new ArrayList<Object>();
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;
    private RecyclerView formsRecyclerView;
    FormsAdapter valuesInField;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_selection);
//        mReadJsonData("hubnewJson");
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        formsRecyclerView = findViewById(R.id.rv_formSelection);
        formsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        new Background_JsonParser().execute();

    }

    public class Background_JsonParser extends AsyncTask<String, Void, JSONArray> {

        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(FormSelectionActivity.this);
            pDialog.setMessage("Getting Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... strings) {

            JSONArray jsonArray_fieldData = new JSONArray();
            try {
                File f = new File(getApplicationContext().getFilesDir().getPath() + "/" + "hubnewJson");
                if (f.exists() && !f.isDirectory()) {
                    FileInputStream is = new FileInputStream(f);
                    int size = is.available();
                    byte[] buffer = new byte[size];
                    is.read(buffer);
                    is.close();
                    String mResponse = new String(buffer);
                    Log.i(TAG, "mReadJsonData: saved file " + mResponse);

                    if (!mResponse.isEmpty() || mResponse != null) {
                        try {
                            jsonArray_fieldData = new JSONArray(mResponse);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return jsonArray_fieldData;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {

            pDialog.dismiss();
            Log.i(TAG, "onPostExecute: JsonArray " + jsonArray);
            if (jsonArray.length() == 0)
                createNewForm();
            else
                addFormsToRecyclerView(jsonArray);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        JSONObject newFormObject;
        if (requestCode == 0 && data!=null) {
            Boolean didViewUpdate = data.getBooleanExtra("viewUpdated", false);
            String newFormData = data.getStringExtra("newField");
            int updatedIndexPos = data.getIntExtra("updatedIndexPos",-1);

            try {
                newFormObject = new JSONObject(newFormData);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.i(TAG, "onActivityResult: New form object "+newFormData);
            if (didViewUpdate) {
                mReadJsonData("hubnewJson");
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.form_selection, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_newForm:
                createNewForm();
                Toast.makeText(this, "Opening new Form", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void createNewForm() {

        Intent myIntent = new Intent(FormSelectionActivity.this, MainActivity.class);
        myIntent.putExtra("newForm", true); //Optional parameters
        startActivityForResult(myIntent, SECOND_ACTIVITY_REQUEST_CODE);
    }

    public JSONArray mReadJsonData(String params) {
        JSONArray jsonArray_fieldData = new JSONArray();
        try {
            File f = new File(this.getFilesDir().getPath() + "/" + params);
            if (f.exists() && !f.isDirectory()) {
                FileInputStream is = new FileInputStream(f);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                String mResponse = new String(buffer);
                Log.i(TAG, "mReadJsonData: saved file " + mResponse);

                if (!mResponse.isEmpty() || mResponse != null) {
                    try {
                        jsonArray_fieldData = new JSONArray(mResponse);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return jsonArray_fieldData;
    }

    private void addFormsToRecyclerView(JSONArray mResponse) {

        JSONArray valuesOfFields = null;

        valuesOfFields = mResponse;
        if (formsRecyclerView == null)
            formsRecyclerView = findViewById(R.id.rv_formSelection);
        valuesInField = new FormsAdapter(valuesOfFields);
        formsRecyclerView.setAdapter(valuesInField);


        Log.i(TAG, "addSavedValuesToFields: " + valuesOfFields);
    }
}
