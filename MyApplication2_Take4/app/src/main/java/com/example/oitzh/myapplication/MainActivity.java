package com.example.oitzh.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    EditText editTextView;
    ArrayList<Model> ItemModelList;
    CustomAdapter customAdapter;
    final int  MY_CHILD_ACTIVITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listview);
        editTextView = (EditText) findViewById(R.id.editTextView);
        ItemModelList = new ArrayList<Model>();
        customAdapter = new CustomAdapter(getApplicationContext(), ItemModelList);
        listView.setEmptyView(findViewById(android.R.id.empty));
        listView.setAdapter(customAdapter);

    }

    @SuppressLint("NewApi")
    public void addValue(View v) {
        String name = editTextView.getText().toString();
        if (name.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Plz enter Values",
                    Toast.LENGTH_SHORT).show();
        } else {
            Model md = new Model(name);
            ItemModelList.add(md);
            customAdapter.notifyDataSetChanged();
            editTextView.setText("");
        }
    }

    public void createActivity(View v) {
        Intent i = new Intent(this, Input_actionsActivity.class);
        startActivityForResult(i, MY_CHILD_ACTIVITY);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (MY_CHILD_ACTIVITY) : {
                if (resultCode == Activity.RESULT_OK) {
                    // TODO Extract the data returned from the child Activity.
                    EventSer event = (EventSer) data.getSerializableExtra("result");
                    ItemModelList.add(new Model(event.printString()));
                    //customAdapter.notifyDataSetChanged();


                    //String returnValue = data.getStringExtra("result");
                    //ItemModelList.add(new Model(returnValue));
                    customAdapter.notifyDataSetChanged();

                   // Toast.makeText(getApplicationContext(), returnValue, Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }
}
