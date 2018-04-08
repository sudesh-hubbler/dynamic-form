package com.example.hubbler_sudesh.dynamicform;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by hubbler-sudesh on 03/04/2018 AD.
 */

public class FormsAdapter extends RecyclerView.Adapter<FormsAdapter.FormViewHolder> {

//    private JSONArray formsCollection;
    public ArrayList<JSONObject> formsList;
    public FormsAdapter(JSONArray formsCollection) {

//        this.formsCollection = formsCollection;
        formsList = new ArrayList<JSONObject>();

        for(int i=0;i<formsCollection.length();i++)
        {
            try {
                formsList.add(formsCollection.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public FormViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.form_selection_row,parent,false);

        return new FormViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FormViewHolder holder, final int position) {

        try {
            JSONObject obj = formsList.get(position);
            String name = obj.getString("Name");
            String email = obj.getString("Email");
            String indexPos = Integer.toString(position);
            holder.idNo.setText(indexPos);
            holder.name.setText(name);
            holder.email.setText(email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent myIntent = new Intent(view.getContext(), MainActivity.class);
                myIntent.putExtra("formIndex", position); //Optional parameters
                Context mContext = view.getContext();
                ((Activity)mContext).startActivityForResult(myIntent,0);
            }
        });
    }


    @Override
    public int getItemCount() {
        return formsList.size();
    }

    public class FormViewHolder extends RecyclerView.ViewHolder {

        public TextView idNo, name, email;
        public FormViewHolder(View itemView) {
            super(itemView);

            idNo = itemView.findViewById(R.id.idNo);
            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
        }
    }
}
