package com.example.hubbler_sudesh.dynamicform;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;

/**
 * Created by hubbler-sudesh on 03/04/2018 AD.
 */

public class DatePickerFragment extends DialogFragment {

    String fieldName;


    public DatePickerFragment(String fieldName) {

        this.fieldName = fieldName;
    }

    public DatePickerFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month =  c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getActivity(),year,month,day);
        datePickerDialog.getDatePicker().setTag(fieldName);
        return datePickerDialog;
    }
}
