package com.example.hubbler_sudesh.dynamicform;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener {

    private static final String TAG = "MainActivity";
    String jsonFileName = "formData.json";
    LinkedHashMap<String, JSONObject> map_formField = new LinkedHashMap<>();
    LinearLayout parentLayout;
    LayoutInflater layoutInflater;
    Toolbar toolbar;
    Map<String,View> map_viewRef = new HashMap<>();
    List<View> fieldViews = new ArrayList<View>();
    JSONArray submittedJsonForm = new JSONArray();
    JSONObject field_Json = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialzeToolbar_Layout();
        createFormFields();
        JSONObject field_Json = new JSONObject();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_Submit:
                getFormFieldsValue();
                Toast.makeText(this, "Form submitted", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void createFormFields() {

        map_formField = readFormData();

        if (map_formField.size() != 0) {
            buildForm(map_formField);
        }
    }

    private void initialzeToolbar_Layout() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        parentLayout = findViewById(R.id.parentLayout);
        layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
    }

    private String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = this.getAssets().open(jsonFileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public void mCreateAndSaveFile(String params, String mJsonResponse) {
        try {
            FileWriter file = null;
            try {
                file = new FileWriter(this.getFilesDir().getPath() + "/" + params);
            } catch (IOException e) {
                e.printStackTrace();
            }
            file.write(mJsonResponse);
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void mReadJsonData(String params, int formIndex) {
        try {
            File f = new File(this.getFilesDir().getPath() + "/" + params);
            FileInputStream is = new FileInputStream(f);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String mResponse = new String(buffer);
            Log.i(TAG, "mReadJsonData: saved file " + mResponse);
            if (!mResponse.isEmpty() || mResponse != null) {
                reloadSavedJsonArray(mResponse);
                if (formIndex != 9999)
                    addSavedValuesToFields(mResponse, formIndex);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void reloadSavedJsonArray(String mResponse) {

        try {
            submittedJsonForm = new JSONArray(mResponse);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addSavedValuesToFields(String mResponse, int formIndex) {

        JSONArray valuesOfFields = null;
        try {
            valuesOfFields = new JSONArray(mResponse);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "addSavedValuesToFields: " + valuesOfFields);

        JSONObject jsonObject = null;     // LATER SAVE THE FILE NAME CORRESPONDING TO INDEX FOR GETTING THE FILE LATER
        try {
            jsonObject = (JSONObject) valuesOfFields.get(formIndex);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < fieldViews.size(); i++) {
            View v = fieldViews.get(i);

            String valueOfField = jsonObject.optString(v.getTag().toString());
            Log.i(TAG, "addSavedValuesToFields: FieldValue " + valueOfField);

            if (v instanceof TextInputLayout) {
                ((TextInputLayout) v).getEditText().setText(valueOfField);
            }

            if (v instanceof Spinner) {
                    int selectedIndex=0;
                    String selectedOpt = jsonObject.optString(v.getTag().toString());
                    Spinner currentSpinner = (Spinner) v;
                    for(int index = 0; index < currentSpinner.getCount(); index++)
                    {
                        if(currentSpinner.getItemAtPosition(index).toString().equalsIgnoreCase(selectedOpt)) {
                            selectedIndex = index;
                            break;
                        }
                    }
                currentSpinner.setSelection(selectedIndex);
            }

            if(v instanceof TextView) {
                TextView textView = (TextView) v;
                String textFieldValue = jsonObject.optString(v.getTag().toString());
                textView.setText(textFieldValue);
            }
        }

    }

    private void getFormFieldsValue() {

        for (int i = 0; i < fieldViews.size(); i++) {

            View field_view = fieldViews.get(i);

            if (field_view instanceof TextInputLayout) {
                TextInputLayout textView = (TextInputLayout) field_view;

                String fieldName = textView.getTag().toString();
                String fieldValue = textView.getEditText().getText().toString();

                addFieldToSubmittedJsonArray(fieldName, fieldValue);
            }
            if (field_view instanceof Spinner) {
                Spinner spinner = (Spinner) field_view;

                String fieldName = spinner.getTag().toString();
                String fieldValue = spinner.getSelectedItem().toString();

                addFieldToSubmittedJsonArray(fieldName, fieldValue);
            }
            if(field_view instanceof TextView) {

                TextView textView = (TextView) field_view;
                String fieldName = textView.getTag().toString();
                String fieldValue = textView.getText().toString();

                addFieldToSubmittedJsonArray(fieldName, fieldValue);
            }
//            if(field_view instanceof ListView) {
//
//                ListView listView = (ListView) field_view;
//                String fieldName = listView.getTag().toString();
//                String[] fieldValue = new String[listView.getAdapter().getCount()];
//
//                for(int item=0;item<listView.getCount();item++)
//                {
//                   fieldValue[item] = listView.getAdapter().getItem(item).toString();
//                }
//
//                addFieldToSubmittedJsonArray(fieldName,fieldValue);
//            }

            if (i == fieldViews.size() - 1) {

                // show current file index here
               boolean newForm = getIntent().getExtras().getBoolean("newForm");
               if(newForm)
               {
                   submittedJsonForm.put(field_Json);
                   mCreateAndSaveFile("hubnewJson", submittedJsonForm.toString());
                   goToFormSelectActivity(field_Json,-1);
               }
               else
               {
                   int indexPos = getIntent().getExtras().getInt("formIndex");
                   try {
                       submittedJsonForm.put(indexPos,field_Json);
                       mCreateAndSaveFile("hubnewJson", submittedJsonForm.toString());
                       goToFormSelectActivity(field_Json,indexPos);
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }
               }
            }
        }
    }

    private void goToFormSelectActivity(JSONObject field_Json, int updatedPos ) {

//        Intent returnIntent = new Intent(this, FormSelectionActivity.class);
        Intent returnIntent = getIntent();
        returnIntent.putExtra("viewUpdated",true);
        returnIntent.putExtra("newField",field_Json.toString());
        if(updatedPos != -1)
            returnIntent.putExtra("updatedIndexPos",updatedPos);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    public LinkedHashMap<String, JSONObject> readFormData() {

        LinkedHashMap<String, JSONObject> fields = new LinkedHashMap<>();

        try {
            String jsonString = loadJSONFromAsset();
            if (jsonString != null) {
//                JSONObject obj = new JSONObject(jsonString);
                JSONArray m_jArry = new JSONArray(jsonString);

                for (int i = 0; i < m_jArry.length(); i++) {
                    JSONObject jsonObject = m_jArry.getJSONObject(i);
                    Log.d("Details-->", jsonObject.getString("fieldName"));
                    fields.put(jsonObject.getString("fieldName"), jsonObject);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return fields;
    }

    private void addFieldToSubmittedJsonArray(String fieldName, String fieldValue) {

        try {
            field_Json.put(fieldName, fieldValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void buildForm(Map mp) {

        Iterator it = mp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();

//            View field =
            createView(pair.getValue());
            System.out.println(pair.getKey() + " = " + pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
        Boolean isNewForm = false;
        int formIndex;
        isNewForm = getIntent().getExtras().getBoolean("newForm");
        formIndex = getIntent().getExtras().getInt("formIndex");

        if (!isNewForm) {
            mReadJsonData("hubnewJson", formIndex);
        } else {
            mReadJsonData("hubnewJson", 9999);
        }
    }

    private void createView(Object value) {

        JSONObject jsonObject = (JSONObject) value;
        GetViewForType(jsonObject);
    }

    private void GetViewForType(JSONObject fieldObject) {

        String fieldType = null;
        String fieldName = null;

        try {
            fieldType = fieldObject.optString("type");
            fieldName = fieldObject.get("fieldName").toString();

            if (fieldType != null) {
                switch (fieldType) {
                    case "number":

                        createTextInputField(fieldName, InputType.TYPE_CLASS_NUMBER);
                        break;

                    case "paragraph":

                        createParaField(fieldName);
                        break;

                    case "text":

                        createTextInputField(fieldName, InputType.TYPE_CLASS_TEXT);
                        break;

                    case "multiSelect":
                        createMultiselectDropdown(fieldName,fieldObject);
                        break;

                    case "date":

                        createDatePicker(fieldName);
                        break;

                    case "email":

                        createTextInputField(fieldName, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                        break;

                    case "dropdown":
                        createDropDownField(fieldName, fieldObject);
                        break;

                    default:
                        Log.e(TAG, "GetViewForType: View is not defined");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void createDatePicker(final String fieldName) {

        Button button = new Button(this);
        button.setText("Select "+fieldName);

//        LinearLayout ll = findViewById(R.id.dateLayout);

        TextView textView = new TextView(this);
        textView.setText("--/--/--");
        textView.setTextSize(23);
        textView.setTag("dateDisplay");

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month =  c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, MainActivity.this, year, month, day);

        datePickerDialog.getDatePicker().setTag(fieldName);
        LinearLayout.LayoutParams dateView = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        dateView.setMargins(10,0,0,10);
        textView.setLayoutParams(dateView);
        button.setLayoutParams(dateView);
        map_viewRef.put(fieldName,textView);
        parentLayout.addView(textView);
        fieldViews.add(textView);

        parentLayout.addView(button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                datePickerDialog.show();
            }
        });
    }

    private void createParaField(String fieldName) {

        View v;
        TextInputLayout textInputLayout;
        v = layoutInflater.inflate(R.layout.inputfield, null, false);
        textInputLayout = v.findViewById(R.id.textInputLayout);
        textInputLayout.getEditText().setSingleLine(false);
        textInputLayout.getEditText().setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        textInputLayout.getEditText().setMinLines(3);
        textInputLayout.setHint("Enter your " + fieldName);
        textInputLayout.setTag(fieldName);
        map_viewRef.put(fieldName,v);
        parentLayout.addView(v);
        fieldViews.add(textInputLayout);
    }

    private void createDropDownField(String fieldName, JSONObject fieldObject) {

        JSONArray jsonArray = null;
        jsonArray = fieldObject.optJSONArray("value");

        int len = jsonArray.length();

        ArrayList<String> options = new ArrayList<>();

        for (int j = 0; j < len; j++) {
            Object json = jsonArray.opt(j);
            options.add(json.toString());
        }

        Spinner spinner = new Spinner(this);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, options);
        spinner.setAdapter(arrayAdapter);
        spinner.setTag(fieldName);
        parentLayout.addView(spinner);
        fieldViews.add(spinner);
    }

    private void createMultiselectDropdown(final String fieldName, JSONObject fieldObject) {

        JSONArray jsonArray = null;
        AlertDialog mDialog;
        jsonArray = fieldObject.optJSONArray("value");
        final boolean[] checkedItems;
        final ArrayList<Integer> mUserItems = new ArrayList<>();

        final String[] optionsArray = new String[jsonArray.length()];
        for( int i=0; i< jsonArray.length(); i++)
        {
            try {
                optionsArray[i] = jsonArray.get(i).toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        checkedItems = new boolean[optionsArray.length];

        final TextView selectedItems = new TextView(this);
        Button btn_selectItems = new Button(this);
        LinearLayout.LayoutParams temp_layoutParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btn_selectItems.setText("Select your "+fieldName);
        selectedItems.setLayoutParams(temp_layoutParam);
        btn_selectItems.setLayoutParams(temp_layoutParam);
        parentLayout.addView(selectedItems);
        parentLayout.addView(btn_selectItems);

        btn_selectItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                mBuilder.setTitle("Select Your Branch");
                mBuilder.setMultiChoiceItems(optionsArray, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        if (isChecked) {
                            if (!mUserItems.contains(position)) {
                                mUserItems.add(position);
                            }
                        }
                        else if(mUserItems.contains(position)){
                            mUserItems.removeAll(Arrays.asList(position));
                        }
                    }

                });
                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        String item="";
                        for(int i=0; i < mUserItems.size(); i++){

                            item = item +optionsArray[mUserItems.get(i)];
                            if(i != mUserItems.size() -1)
                            {
                                item = item + ", ";
                            }
                        }
                        selectedItems.setText(item);
                    }
                });

                mBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                mBuilder.setNeutralButton("Clear all", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        for(int i=0; i< checkedItems.length; i++)
                        {
                            checkedItems[i] = false;
                            mUserItems.clear();
                            selectedItems.setText("");
                        }
                    }
                });

               AlertDialog mDialog = mBuilder.create();
                mDialog.show();
                selectedItems.setTag(fieldName);
                fieldViews.add(selectedItems);
            }
        });
    }

    private void createTextInputField(String fieldName, int inputType) {

        View v;
        v = layoutInflater.inflate(R.layout.inputfield, null, false);
        TextInputLayout textInputLayout;
        textInputLayout = v.findViewById(R.id.textInputLayout);
        textInputLayout.setHint("Enter your " + fieldName);
        textInputLayout.getEditText().setInputType(inputType);
        textInputLayout.setTag(fieldName);
        map_viewRef.put(fieldName,v);
        parentLayout.addView(v);
        fieldViews.add(textInputLayout);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {

        String selectedItemText = (String) parent.getItemAtPosition(position);
        // If user change the default selection
        // First item is disable and it is used for hint
        if (position > 0) {
            // Notify the selected item text
            Toast.makeText(getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {


        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH,month);
        c.set(Calendar.DAY_OF_MONTH,day);
        String currentDate = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.ENGLISH).format(c.getTime());
        TextView v = (TextView) map_viewRef.get(datePicker.getTag());
        v.setText(currentDate);
//        selectedDate.setText(currentDate);
    }
}
