package com.example.oitzh.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;


public class NameActivity extends AppCompatActivity {

    final int MY_CHILD_ACTIVITY = 1;
    HashMap<Object, List<String>> sensorsInfo;
    boolean editMode;
    Scenario scenarioToEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Set Toolbar title
        ab.setTitle("Give a name to your scenario");
        ab.setDisplayHomeAsUpEnabled(true);


        sensorsInfo = (HashMap<Object, List<String>>) getIntent().getSerializableExtra("sensorsInfo");

        //Try to get scenario if "edit" pressed
        scenarioToEdit = (Scenario) getIntent().getSerializableExtra("edit");
        //Initialize current activity in case "Edit" was pressed
        if (scenarioToEdit != null) {
            editMode = true;
            updateActivityButtonsByScenario(scenarioToEdit);
        }
    }


    private void updateActivityButtonsByScenario(Scenario scenarioToEdit) {
        TextView editTextView = (TextView) findViewById(R.id.editTextView);
        editTextView.setText(scenarioToEdit.getScenarioName());
    }

    protected void next(View view) {
        TextView editTextView = (TextView) findViewById(R.id.editTextView);
        String name = editTextView.getText().toString();

        if (name.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter a name for the scenario",
                    Toast.LENGTH_SHORT).show();
        } else {

            if (!editMode) {
                scenarioToEdit = new Scenario(name);
            }

            //Call an activity for editing existing a scenario
            Intent i = new Intent(this, InputActivity.class);
            //Scenario editedScenario = itemScenarioList.get(itemScenarioList.size()-1);
            i.putExtra("edit", scenarioToEdit);
            i.putExtra("sensorsInfo", sensorsInfo);
            startActivityForResult(i, MY_CHILD_ACTIVITY);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (MY_CHILD_ACTIVITY): {
                if (resultCode == Activity.RESULT_OK) {
                    Scenario scenarioCreated = (Scenario) data.getSerializableExtra("result");

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", scenarioCreated);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();

                }
            }
        }
    }


}


