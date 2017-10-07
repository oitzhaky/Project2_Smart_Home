package com.example.oitzh.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    EditText editTextView;
    ArrayList<Scenario> itemScenarioList;
    CustomAdapter customAdapter;
    final int  MY_CHILD_ACTIVITY = 1;
    final int MY_EDIT_CHILD_ACTIVITY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listview);
        editTextView = (EditText) findViewById(R.id.editTextView);
        itemScenarioList = new ArrayList<Scenario>();
        customAdapter = new CustomAdapter(getApplicationContext(), itemScenarioList, this);
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
            //TODO: use this lines for giving the scenario a name
            Scenario md = new Scenario(name);
            itemScenarioList.add(md);
            customAdapter.notifyDataSetChanged();
            editTextView.setText("");
        }
    }

    //Call an activity for creating a scenario
    public void createActivity(View v) {
        Intent i = new Intent(this, Input_actionsActivity.class);
        startActivityForResult(i, MY_CHILD_ACTIVITY);
    }

    //Call an activity for editing existing a scenario
    //TODO: 2 problems: how to get the right position and how to pass info and receive back?
    public void editActivity(View v, Scenario editedScenario ) {
        Intent i = new Intent(this, Input_actionsActivity.class);
        //Scenario editedScenario = itemScenarioList.get(itemScenarioList.size()-1);
        i.putExtra("edit",editedScenario);
        startActivityForResult(i, MY_EDIT_CHILD_ACTIVITY);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (MY_CHILD_ACTIVITY) : {
                if (resultCode == Activity.RESULT_OK) {
                    Scenario scenarioCreated = (Scenario) data.getSerializableExtra("result");
                    //TODO: add the scenario itself to the list and not the printstring()!
                    itemScenarioList.add(new Scenario(scenarioCreated.printString()));
                    customAdapter.notifyDataSetChanged();


                    //String returnValue = data.getStringExtra("result");
                    //ItemModelList.add(new Model(returnValue));
                    //customAdapter.notifyDataSetChanged();

                   // Toast.makeText(getApplicationContext(), returnValue, Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }
}
