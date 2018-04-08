package com.example.hubbler_sudesh.dynamicform;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by hubbler-sudesh on 23/03/2018 AD.
 */

class CustomAdapter extends ArrayAdapter {

    private Context context;
    private List<CharSequence> itemList;
    public CustomAdapter(Context context, int textViewResourceId,List<CharSequence> itemList) {

        super(context, textViewResourceId);
        this.context=context;
        this.itemList=itemList;
    }

    @Override
    public boolean isEnabled(int position) {

        if (position == 0) {
            // Disable the first item from Spinner
            // First item will be use for hint
            return false;
        } else {
            return true;
        }
    }

    public TextView getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        TextView tv = (TextView) view;
        if (position == 0) {
            // Set the hint text color gray
            tv.setTextColor(Color.GRAY);
        } else {
            tv.setTextColor(Color.BLACK);
        }
        return (TextView) view;
    }

}