package com.example.hubbler_sudesh.dynamicform;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.ProgressBar;

import org.json.JSONObject;

/**
 * Created by hubbler-sudesh on 26/03/2018 AD.
 */

public class JsonParser extends AsyncTask<String,String,JSONObject> {

    ProgressBar progressBar;

//    public Background_JsonParser(Activity activity,String paramOne, Stri) {
//    }

    @Override
    protected void onPreExecute() {

//        progressBar = new ProgressBar(context,null,android.R.attr.progressBarStyleSmall);
//        super.onPreExecute();


    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected JSONObject doInBackground(String... strings) {
        return null;
    }
}
