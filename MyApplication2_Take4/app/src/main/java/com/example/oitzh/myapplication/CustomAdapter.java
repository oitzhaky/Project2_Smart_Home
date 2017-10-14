package com.example.oitzh.myapplication;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

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

            int [] ids = new int [] {R.id.image1, R.id.image2, R.id.image3, R.id.image4,R.id.image5, R.id.image6,R.id.image7, R.id.image8,};
            List<ImageView> imageViewArray = new ArrayList<>();

            for(int id: ids) {
                imageViewArray.add((ImageView) convertView.findViewById(id));
            }

            //Set the view's text with the scenario's name
            tvName.setText(m.getScenarioName());

            //Set pictures
            //Iterate over Input buttons
            int counter=0;
            for (IA toggleButton : m.getToggledButtonsArray()) {
                switch (toggleButton.getToggleButtonID()) {
                    case (R.id.gpsBtn):
                        imageViewArray.get(counter).setImageResource(R.drawable.ic_map_marker);
                        imageViewArray.get(counter).setPadding(50,10,0,0);
                        counter++;
                }
            }

            imageViewArray.get(counter).setImageResource(R.drawable.ic_arrow_right_thick);
            imageViewArray.get(counter).setPadding(50,10,0,0);
            counter++;

            //TODO: Iterate over outputs
            imageViewArray.get(counter).setImageResource(R.drawable.ic_television_classic);
            imageViewArray.get(counter).setPadding(50,10,0,0);
            counter++;

            //Remove unused imageView views
            while(counter<8){
                imageViewArray.get(counter).setVisibility(View.GONE);
                counter++;
            }





        // click listener for remove button
        imgRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Scenario removedScenario = itemModelList.remove(position);
                mainActivity.publishRemovedScenario(removedScenario);
                notifyDataSetChanged();
            }
        });
        // click listener for edit button
        imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Scenario editScenario = itemModelList.remove(position);
                //itemModelList.add(editScenario);
                mainActivity.editActivity(imgEdit, editScenario);
                //Toast.makeText(context, String.valueOf(position) , Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();
            }
        });
    }
        return convertView;
}
}
