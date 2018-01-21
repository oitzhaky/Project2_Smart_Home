package com.example.oitzh.myapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class ActionsActivity extends AppCompatActivity {

    public List<ScenarioButton> actions;
    ScenarioButton lights, ac, tv, boiler, security;
    HashMap<Object, List<String>> sensorsInfo;
    Scenario scenarioToEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actions);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Set Toolbar title
        ab.setTitle("Choose action sensors");
        ab.setDisplayHomeAsUpEnabled(true);


        //Action array
        ac = new ScenarioButton(R.id.acBtn, ScenarioAction.Action.Ac.enumToDescriptionArray(), new boolean[]{false, false}, (String) findViewById(R.id.acBtn).getTag(), R.id.spinner_AC);
        tv = new ScenarioButton(R.id.tvBtn, ScenarioAction.Action.Tv.enumToDescriptionArray(), new boolean[]{false, false}, (String) findViewById(R.id.tvBtn).getTag(), R.id.spinner_TV);
        lights = new ScenarioButton(R.id.lightsBtn, ScenarioAction.Action.Light.enumToDescriptionArray(), new boolean[]{false, false, false, false}, (String) findViewById(R.id.lightsBtn).getTag(), R.id.spinner_Lights);
        boiler = new ScenarioButton(R.id.boilerBtn, ScenarioAction.Action.Boiler.enumToDescriptionArray(), new boolean[]{false, false}, (String) findViewById(R.id.boilerBtn).getTag(), R.id.spinner_Boiler);
        security = new ScenarioButton(R.id.securityBtn, ScenarioAction.Action.Alert.enumToDescriptionArray(), new boolean[]{false, false}, (String) findViewById(R.id.securityBtn).getTag(), R.id.spinner_Security);
        actions = new ArrayList<>(Arrays.asList(lights, tv, ac, boiler, security));

        sensorsInfo = (HashMap<Object, List<String>>) getIntent().getSerializableExtra("sensorsInfo");

        //Try to get scenario if "edit" pressed
        scenarioToEdit = (Scenario) getIntent().getSerializableExtra("edit");
        //Initialize current activity in case "Edit" was pressed
        if (scenarioToEdit != null) {
            updateActivityButtonsByScenario(scenarioToEdit);
        }

//region SPINNER
        AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // On selecting a spinner item
                String item = parent.getItemAtPosition(position).toString();
                // Showing selected spinner item
                //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };

        // Spinner element
        Spinner acSpinner = (Spinner) findViewById(R.id.spinner_AC);
        Spinner boilerSpinner = (Spinner) findViewById(R.id.spinner_Boiler);
        Spinner lightsSpinner = (Spinner) findViewById(R.id.spinner_Lights);
        Spinner securitySpinner = (Spinner) findViewById(R.id.spinner_Security);
        Spinner tvSpinner = (Spinner) findViewById(R.id.spinner_TV);

        // Spinner click listener
        acSpinner.setOnItemSelectedListener(spinnerListener);
        boilerSpinner.setOnItemSelectedListener(spinnerListener);
        lightsSpinner.setOnItemSelectedListener(spinnerListener);
        securitySpinner.setOnItemSelectedListener(spinnerListener);
        tvSpinner.setOnItemSelectedListener(spinnerListener);

        // Spinner Drop down elements
        List<String> acSensors = sensorsInfo.get(ScenarioAction.Name.Ac) != null ? sensorsInfo.get(ScenarioAction.Name.Ac) : new ArrayList<String>(Arrays.asList("N/A"));
        List<String> boilerSensors = sensorsInfo.get(ScenarioAction.Name.Boiler) != null ? sensorsInfo.get(ScenarioAction.Name.Boiler) : new ArrayList<String>(Arrays.asList("N/A"));
        List<String> lightsSensors = sensorsInfo.get(ScenarioAction.Name.Light) != null ? sensorsInfo.get(ScenarioAction.Name.Light) : new ArrayList<String>(Arrays.asList("N/A"));
        List<String> securitySensors = sensorsInfo.get(ScenarioAction.Name.Alert) != null ? sensorsInfo.get(ScenarioAction.Name.Alert) : new ArrayList<String>(Arrays.asList("N/A"));
        List<String> tvSensors = sensorsInfo.get(ScenarioAction.Name.Tv) != null ? sensorsInfo.get(ScenarioAction.Name.Tv) : new ArrayList<String>(Arrays.asList("N/A"));

        // Creating adapter for spinner
        ArrayAdapter<String> acDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, acSensors);
        ArrayAdapter<String> boilerDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, boilerSensors);
        ArrayAdapter<String> lightsDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lightsSensors);
        ArrayAdapter<String> securityDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, securitySensors);
        ArrayAdapter<String> tvDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tvSensors);

        // Drop down layout style - list view with radio button
        acDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        boilerDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lightsDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        securityDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tvDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        acSpinner.setAdapter(acDataAdapter);
        boilerSpinner.setAdapter(boilerDataAdapter);
        lightsSpinner.setAdapter(lightsDataAdapter);
        securitySpinner.setAdapter(securityDataAdapter);
        tvSpinner.setAdapter(tvDataAdapter);
        //endregion
    }

    protected void onClickFunc(View view) {
        int id = view.getId();
        final ToggleButton toggleButton = (ToggleButton) findViewById(id);
        //make action buttons clickable - we have at least one input button!
        makeActionBtnsClickable();

        AlertDialog.Builder builder = new AlertDialog.Builder(ActionsActivity.this);
        builder.setTitle("Choose Triggers");
        builder.setIcon(R.drawable.ic_event_black_48px);
        builder.setCancelable(false);  // disallow cancel of AlertDialog on click of back button and outside touch
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(getBaseContext(), "Cancel clicked", Toast.LENGTH_SHORT).show();
//            }
//        });
        LayoutInflater inflater = this.getLayoutInflater();
        switch (id) {
            //ScenarioButton Buttons
            case R.id.lightsBtn:
                //region LIGHTS BUTTON
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (AllOptionsUnchecked(lights.selectedDialogOptions)) { //if all options are unchecked, unselect the button
                            toggleButton.setChecked(false);
                        } else {
                            toggleButton.setChecked(true); //some triggers are marked
                        }
                    }
                });
                builder.setMultiChoiceItems(lights.dialogOptions, lights.selectedDialogOptions, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        final AlertDialog alertDialog = (AlertDialog) dialog;
                        final ListView alertDialogList = alertDialog.getListView();
                        if (isChecked) {
                            alertDialogList.getChildAt((which + 1) % 2).setEnabled(false); //UI-wise: disable the other option
                            lights.selectedDialogOptions[(which + 1) % 2] = false; //Update the other option as unselected so when the dialog will be opened, options will be marked correctly
                        } else {
                            alertDialogList.getChildAt((which + 1) % 2).setEnabled(true);
                            lights.selectedDialogOptions[(which + 1) % 2] = true;
                        }
                    }
                });
                builder.create().show();
                break;
            //endregion
            case R.id.acBtn:
            case R.id.tvBtn:
            case R.id.securityBtn:
            case R.id.boilerBtn:
                //region BUTTON
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (AllOptionsUnchecked(getScenarioButtonByTag((String) view.getTag()).selectedDialogOptions)) { //if all options are unchecked, unselect the button
                            toggleButton.setChecked(false);
                        } else {
                            toggleButton.setChecked(true); //some triggers are marked
                        }
                    }
                });
                builder.setMultiChoiceItems(getScenarioButtonByTag((String) view.getTag()).dialogOptions, getScenarioButtonByTag((String) view.getTag()).selectedDialogOptions, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        updateMultiChoiceItems(dialog, which, isChecked, getScenarioButtonByTag((String) view.getTag()).dialogOptions[0], getScenarioButtonByTag((String) view.getTag()).dialogOptions[1], getScenarioButtonByTag((String) view.getTag()));
                    }
                });
                builder.create().show();
                break;
            //endregion
        }
    }

    private void updateActivityButtonsByScenario(Scenario scenarioToEdit) {
        for (ScenarioButton toggleButton : scenarioToEdit.getActionToggledButtonsArray()) {
            Iterator<ScenarioButton> actionIterator = actions.iterator();
            String currentToggleBtnName = toggleButton.getTagName();
            while (actionIterator.hasNext()) {
                ScenarioButton actionCurr = actionIterator.next();
                if (actionCurr.getTagName().equals(currentToggleBtnName)) {
                    actionCurr.selectedDialogOptions = toggleButton.getSelectedDialogOptions(); //TODO:should use NEW?
                    actionCurr.dialogOptions = toggleButton.getDialogOptions();
                    int id = actionCurr.getToggleButtonID();
                    ToggleButton toggleBtn = (ToggleButton) findViewById(id);
                    toggleBtn.setChecked(true);
                    //Extract spinner state
                    Spinner currentButtonSpinner = (Spinner) findViewById(actionCurr.spinnerId);
                    currentButtonSpinner.setSelection(sensorsInfo.get(ScenarioAction.Name.valueOf(actionCurr.getTagName())).indexOf(toggleButton.selectedSpinnerItem));
                }
            }
        }
    }


    private void updateMultiChoiceItems(DialogInterface dialog, int which, boolean isChecked, String option1, String option2, ScenarioButton scenarioButton) {
        final AlertDialog alertDialog = (AlertDialog) dialog;
        final ListView alertDialogList = alertDialog.getListView();

        if (isChecked) {
            if (scenarioButton.dialogOptions[which].equals(option1)) {
                int stringIndex = Arrays.asList(scenarioButton.dialogOptions).indexOf(option2);
                alertDialogList.getChildAt(stringIndex).setEnabled(false); //UI-wise: disable the other option
                scenarioButton.selectedDialogOptions[stringIndex] = false; //Update the other option as unselected so when the dialog will be opened, options will be marked correctly
            } else {
                int stringIndex = Arrays.asList(scenarioButton.dialogOptions).indexOf(option1);
                alertDialogList.getChildAt(stringIndex).setEnabled(false);
                scenarioButton.selectedDialogOptions[stringIndex] = false;
            }
        } else {
            if (scenarioButton.dialogOptions[which].equals(option1)) {
                int stringIndex = Arrays.asList(scenarioButton.dialogOptions).indexOf(option2);
                alertDialogList.getChildAt(stringIndex).setEnabled(true);
                scenarioButton.selectedDialogOptions[which] = false; //Update the other option as unselected so when the dialog will be opened, options will be marked correctly
            } else {
                int stringIndex = Arrays.asList(scenarioButton.dialogOptions).indexOf(option1);
                alertDialogList.getChildAt(stringIndex).setEnabled(true);
                scenarioButton.selectedDialogOptions[which] = false;
            }
        }
    }


    protected boolean AllOptionsUnchecked(boolean[] selectedLocationOptions) {
        for (boolean bool : selectedLocationOptions) {
            if (bool == true) {
                return false;
            }
        }
        return true;
    }


    public List<ScenarioButton> getSelectedActionButtons() {
        List<ScenarioButton> ButtonsArray = new ArrayList<>();
        for (ScenarioButton scenarioButton : actions) {
            int id = scenarioButton.getToggleButtonID();
            final ToggleButton toggleButton = (ToggleButton) findViewById(id);
            if (toggleButton.isChecked()) {
                scenarioButton.selectedSpinnerItem = (String) ((Spinner) findViewById(scenarioButton.spinnerId)).getSelectedItem();
                ButtonsArray.add(scenarioButton);
            }
        }
        return ButtonsArray;
    }

    public void makeActionBtnsClickable() {
        for (ScenarioButton scenarioButton : actions) {
            int id = scenarioButton.getToggleButtonID();
            final ToggleButton toggleButton = (ToggleButton) findViewById(id);
            toggleButton.setClickable(true);
        }
    }

    public void makeActionBtnsUnClickable() {
        for (ScenarioButton scenarioButton : actions) {
            int id = scenarioButton.getToggleButtonID();
            final ToggleButton toggleButton = (ToggleButton) findViewById(id);
            toggleButton.setClickable(false);
        }
    }


    protected void submitData(View view) {
        //Create Intent to hold the returned message for the calling activity
        Intent returnIntent = new Intent();

        List<ScenarioButton> actionsButtons = getSelectedActionButtons();
        scenarioToEdit.setActionButtons(actionsButtons);

        Scenario scenario = new Scenario(scenarioToEdit.getScenarioName(),scenarioToEdit.getInputToggledButtonsArray(),scenarioToEdit.getActionToggledButtonsArray());
        returnIntent.putExtra("result", scenario);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();

    }

    public ScenarioButton getScenarioButtonByTag(String tag) {
        List<ScenarioButton> buttons = new ArrayList<>(actions);
        for (ScenarioButton scenarioButton : buttons) {
            if (scenarioButton.getTagName().equals(tag)) {
                return scenarioButton;
            }
        }
        return null;
    }
}


