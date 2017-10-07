package com.example.oitzh.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by oitzh on 09/09/2017.
 */

public class CustomAdapter extends BaseAdapter {
    Context context;
    MainActivity mainActivity;
    ArrayList<Scenario> itemModelList;

    public CustomAdapter(Context context, ArrayList<Scenario> scenarioList, MainActivity mainActivity) {
        this.context = context;
        this.itemModelList = scenarioList;
        this.mainActivity = mainActivity;
    }

    @Override
    public int getCount() {
        return itemModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = null;
        if (convertView == null) {
            final LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.item, null);
            TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
            ImageView imgRemove = (ImageView) convertView.findViewById(R.id.imgRemove);
            final ImageView imgEdit = (ImageView) convertView.findViewById(R.id.imgEdit);
            final Scenario m = itemModelList.get(position);

            //Set the view's text
            tvName.setText(m.getScenarioName());

            // click listener for remove button
            imgRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemModelList.remove(position);
                    notifyDataSetChanged();
                }
            });
            // click listener for edit button
            imgEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Scenario editScenario =  itemModelList.remove(position);
                    //itemModelList.add(editScenario);
                    mainActivity.editActivity(imgEdit,editScenario);
                    //Toast.makeText(context, String.valueOf(position) , Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                    //TODO:implement
                }
            });
        }
        return convertView;
    }
}
