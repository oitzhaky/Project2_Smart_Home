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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class InputActivity extends AppCompatActivity {

    final int MY_CHILD_ACTIVITY = 1;
    public List<ScenarioButton> inputs;
    ScenarioButton location, climate, motion, time;
    HashMap<Object, List<String>> sensorsInfo;
    Scenario scenarioToEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Set Toolbar title
        ab.setTitle("Choose input sensors");
        ab.setDisplayHomeAsUpEnabled(true);

        //Input array
        location = new ScenarioButton((R.id.gpsBtn), ScenarioInput.Trigger.Location.enumToDescriptionArray(), new boolean[]{false, false}, (String) findViewById(R.id.gpsBtn).getTag(), R.id.spinner_Location);
        motion = new ScenarioButton((R.id.motionBtn), ScenarioInput.Trigger.Location.enumToDescriptionArray(), new boolean[]{false, false}, (String) findViewById(R.id.motionBtn).getTag(), R.id.spinner_Motion);
        time = new ScenarioButton((R.id.timeBtn), ScenarioInput.Trigger.Time.enumToDescriptionArray(), new boolean[ScenarioInput.Trigger.Time.values().length], (String) findViewById(R.id.timeBtn).getTag(), R.id.spinner_Time);
        climate = new ScenarioButton((R.id.climateBtn), ScenarioInput.Trigger.Climate.enumToDescriptionArray(), new boolean[]{false, false}, (String) findViewById(R.id.climateBtn).getTag(), R.id.spinner_Climate);
        inputs = new ArrayList<>(Arrays.asList(location, climate, motion, time));


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
                // Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };

        // Spinner element
        Spinner LocationSpinner = (Spinner) findViewById(R.id.spinner_Location);
        Spinner climateSpinner = (Spinner) findViewById(R.id.spinner_Climate);
        Spinner motionSpinner = (Spinner) findViewById(R.id.spinner_Motion);
        Spinner timeSpinner = (Spinner) findViewById(R.id.spinner_Time);

        // Spinner click listener
        LocationSpinner.setOnItemSelectedListener(spinnerListener);
        climateSpinner.setOnItemSelectedListener(spinnerListener);
        motionSpinner.setOnItemSelectedListener(spinnerListener);
        timeSpinner.setOnItemSelectedListener(spinnerListener);

        // Spinner Drop down elements
        List<String> locationsSensors = sensorsInfo.get(ScenarioInput.Name.Location) != null ? sensorsInfo.get(ScenarioInput.Name.Location) : new ArrayList<String>(Arrays.asList("N/A"));
        List<String> climatesSensors = sensorsInfo.get(ScenarioInput.Name.Climate) != null ? sensorsInfo.get(ScenarioInput.Name.Climate) : new ArrayList<String>(Arrays.asList("N/A"));
        List<String> motionSensors = sensorsInfo.get(ScenarioInput.Name.Motion) != null ? sensorsInfo.get(ScenarioInput.Name.Motion) : new ArrayList<String>(Arrays.asList("N/A"));
        List<String> timeSensors = sensorsInfo.get(ScenarioInput.Name.Time) != null ? sensorsInfo.get(ScenarioInput.Name.Time) : new ArrayList<String>(Arrays.asList("N/A"));


        // Creating adapter for spinner
        ArrayAdapter<String> locationDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, locationsSensors);
        ArrayAdapter<String> climateDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, climatesSensors);
        ArrayAdapter<String> motionDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, motionSensors);
        ArrayAdapter<String> timeDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, timeSensors);

        // Drop down layout style - list view with radio button
        locationDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        climateDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        motionDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        LocationSpinner.setAdapter(locationDataAdapter);
        climateSpinner.setAdapter(climateDataAdapter);
        motionSpinner.setAdapter(motionDataAdapter);
        timeSpinner.setAdapter(timeDataAdapter);
        //endregion
    }

    protected void onClickFunc(View view) {
        int id = view.getId();
        final ToggleButton toggleButton = (ToggleButton) findViewById(id);

        AlertDialog.Builder builder = new AlertDialog.Builder(InputActivity.this);
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
            case R.id.climateBtn:
                //region CLIMATE BUTTON
                final View viewDialog = inflater.inflate(R.layout.climate_alert, null); //inflate XML to the dialog
                builder.setView(viewDialog);

                final CheckBox tempAbove_checkBox = (CheckBox) viewDialog.findViewById(R.id.tempAbove_checkBox);
                final CheckBox tempbelow_checkBox = (CheckBox) viewDialog.findViewById(R.id.tempBelow_checkBox);
                final EditText tempAbove_editText = (EditText) viewDialog.findViewById(R.id.tempAbove_text);
                final EditText tempBelow_editText = (EditText) viewDialog.findViewById(R.id.tempBelow_text);
                final int aboveStringIndex = findIndexInArrayContainingString(Arrays.asList(climate.dialogOptions), "Above");
                final int belowStringIndex = findIndexInArrayContainingString(Arrays.asList(climate.dialogOptions), "Below");

                //Build last state of dialog controls before showing it
                buildLastStateDialog(tempAbove_checkBox, tempbelow_checkBox, tempAbove_editText, tempBelow_editText, aboveStringIndex, belowStringIndex);

                tempAbove_checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateDialogControls(tempAbove_checkBox, tempbelow_checkBox, tempBelow_editText);
                    }
                });
                tempbelow_checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateDialogControls(tempbelow_checkBox, tempAbove_checkBox, tempAbove_editText);
                    }
                });

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (tempAbove_checkBox.isChecked()) {
                            UpdateCustomInputBtnOptionAndSelected(tempAbove_editText, aboveStringIndex, belowStringIndex);
                        } else if (tempbelow_checkBox.isChecked()) {
                            UpdateCustomInputBtnOptionAndSelected(tempBelow_editText, belowStringIndex, aboveStringIndex);
                        } else {
                            UnselectBtnOptionsAndUpdateStrings(aboveStringIndex, belowStringIndex); //delete any text written and update all options in selectedOption array as unselected
                        }
                        if (AllOptionsUnchecked(climate.selectedDialogOptions)) { //if all options are unchecked, unselect the button
                            toggleButton.setChecked(false);
                            if (allBtnsUnpressed()) {

                            }
                        } else {
                            toggleButton.setChecked(true); //some triggers are marked
                        }
                    }
                });
                builder.create().show();
                break;
            //endregion
            case R.id.timeBtn:
                //region TIME BUTTON
                final View viewDialogTime = inflater.inflate(R.layout.time_alert, null); //inflate XML to the dialog
                builder.setView(viewDialogTime);

                final CheckBox atTime_checkBox = (CheckBox) viewDialogTime.findViewById(R.id.atTime_checkBox);
                final CheckBox rangeTime_checkBox = (CheckBox) viewDialogTime.findViewById(R.id.rangeTime_checkBox);
                final List<CheckBox> timeCheckBoxes = new ArrayList<>(Arrays.asList(atTime_checkBox, rangeTime_checkBox));

                final EditText atTime_editText = (EditText) viewDialogTime.findViewById(R.id.atTime_text);
                final EditText rangeLowerTime_editText = (EditText) viewDialogTime.findViewById(R.id.rangeLowerTime_text);
                final EditText rangeUpperTime_editText = (EditText) viewDialogTime.findViewById(R.id.rangeUpperTime_text);
                final List<EditText> timeTexts = new ArrayList<>(Arrays.asList(atTime_editText, rangeLowerTime_editText, rangeUpperTime_editText));

                //Build last state of dialog controls before showing it
                for (int index = 0; index < time.dialogOptions.length; index++) {
                    if (index == time.dialogOptions.length - 1) {
                        String string = time.dialogOptions[index].substring(time.dialogOptions[index].indexOf(" ") + 1, time.dialogOptions[index].indexOf("a") - 1);
                        timeTexts.get(index).setText(string.equals("HH:MM") ? "" : string);
                        string = time.dialogOptions[index].substring(time.dialogOptions[index].lastIndexOf(" ") + 1);
                        timeTexts.get(index + 1).setText(string.equals("HH:MM") ? "" : string);
                        timeCheckBoxes.get(index).setChecked(time.selectedDialogOptions[index]);
                    } else {
                        String string = time.dialogOptions[index].substring(time.dialogOptions[index].lastIndexOf(" ") + 1);
                        timeTexts.get(index).setText(string.equals("HH:MM") ? "" : string);
                        timeCheckBoxes.get(index).setChecked(time.selectedDialogOptions[index]);
                    }
                }


                View.OnClickListener timeOnClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (atTime_checkBox.isChecked()) {
                            for (CheckBox checkBox : timeCheckBoxes) { // make all other checkbox disable
                                if (!checkBox.equals(atTime_checkBox)) {
                                    checkBox.setChecked(false);
                                    checkBox.setClickable(false);
                                    checkBox.setEnabled(false);
                                }
                                for (EditText editText : timeTexts) {
                                    if (!editText.equals(atTime_editText)) {
                                        editText.setEnabled(false);
                                        editText.setClickable(false);
                                    }
                                }
                            }
                        } else if (rangeTime_checkBox.isChecked()) {
                            for (CheckBox checkBox : timeCheckBoxes) { // make all other checkbox disable
                                if (!checkBox.equals(rangeTime_checkBox)) {
                                    checkBox.setChecked(false);
                                    checkBox.setClickable(false);
                                    checkBox.setEnabled(false);
                                }
                                for (EditText editText : timeTexts) {
                                    if (!editText.equals(rangeLowerTime_editText) && !editText.equals(rangeUpperTime_editText)) {
                                        editText.setEnabled(false);
                                        editText.setClickable(false);
                                    }
                                }
                            }
                        } else {//button unchecked
                            for (CheckBox checkBox : timeCheckBoxes) { // make all other checkbox disable
                                checkBox.setClickable(true);
                                checkBox.setEnabled(true);
                            }
                            for (EditText editText : timeTexts) {
                                editText.setEnabled(true);
                                editText.setClickable(true);
                            }
                        }
                    }
                };

                //Define on click Listener
                atTime_checkBox.setOnClickListener(timeOnClickListener);
                rangeTime_checkBox.setOnClickListener(timeOnClickListener);

                final List<LinearLayout> layouts = new ArrayList<>(Arrays.asList((LinearLayout) viewDialogTime.findViewById(R.id.atTime_layout), (LinearLayout) viewDialogTime.findViewById(R.id.betweenTime_layout)));

                for (int i = 0; i < time.selectedDialogOptions.length; i++) {
                    time.selectedDialogOptions[i] = false;
                }
                for (int i = 0; i < time.dialogOptions.length; i++) {
                    time.dialogOptions[i] = time.dialogOptions[i].substring(0, time.dialogOptions[i].lastIndexOf(" ") + 1) + "HH:MM";
                    if (time.dialogOptions[i].contains("Between")) {
                        time.dialogOptions[i] = "Between HH:MM and HH:MM";
                    }
                }

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        for (LinearLayout linearLayout : layouts) {
                            int count = linearLayout.getChildCount();
                            CheckBox checkBox = (CheckBox) linearLayout.getChildAt(0);
                            EditText editText = (EditText) linearLayout.getChildAt(1);
                            if (count == 4) { //option of range-time
                                EditText editText2 = (EditText) linearLayout.getChildAt(3);
                                if (checkBox.isChecked()) {
                                    if (!editText.getText().toString().equals("") && !editText2.getText().toString().equals("")) {
                                        //Update selected option
                                        final int StringIndex = findIndexInArrayContainingString(Arrays.asList(time.dialogOptions), checkBox.getText().toString());
                                        time.selectedDialogOptions[StringIndex] = true;
                                        time.dialogOptions[StringIndex] = "Between " + editText.getText().toString() +
                                                " and " + editText2.getText().toString();
                                    }
                                }
                            } else if (checkBox.isChecked()) {
                                if (!editText.getText().toString().equals("")) {
                                    //Update selected option
                                    final int StringIndex = findIndexInArrayContainingString(Arrays.asList(time.dialogOptions), checkBox.getText().toString());
                                    time.selectedDialogOptions[StringIndex] = true;
                                    time.dialogOptions[StringIndex] = time.dialogOptions[StringIndex].substring(0, time.dialogOptions[StringIndex].lastIndexOf(" ") + 1) + editText.getText().toString(); //Add the ScenarioTime to the string
                                }
                            }
                        }
                        if (AllOptionsUnchecked(time.selectedDialogOptions)) { //if all options are unchecked, unselect the button
                            toggleButton.setChecked(false);
                        } else {
                            toggleButton.setChecked(true); //some triggers are marked
                        }
                    }
                });
                builder.create().show();
                break;
            //endregion
            case R.id.motionBtn:
            case R.id.gpsBtn:
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
        for (ScenarioButton toggleButton : scenarioToEdit.getInputToggledButtonsArray()) {
            //Iterate over the buttons from edit Scenario and update the new activity's buttons accordingly
            String currentToggleBtnName = toggleButton.getTagName();
            Iterator<ScenarioButton> inputIterator = inputs.iterator();
            while (inputIterator.hasNext()) {
                ScenarioButton scenarioButtonCurr = inputIterator.next();
                if (scenarioButtonCurr.getTagName().equals(currentToggleBtnName)) {
                    scenarioButtonCurr.selectedDialogOptions = toggleButton.getSelectedDialogOptions(); //TODO:should use NEW?
                    scenarioButtonCurr.dialogOptions = toggleButton.getDialogOptions();
                    int id = scenarioButtonCurr.getToggleButtonID();
                    ToggleButton toggleBtn = (ToggleButton) findViewById(id);
                    toggleBtn.setChecked(true);
                    //Extract spinner state
                    Spinner currentButtonSpinner = (Spinner) findViewById(scenarioButtonCurr.spinnerId);
                    currentButtonSpinner.setSelection(sensorsInfo.get(ScenarioInput.Name.valueOf(toggleButton.getTagName())).indexOf(toggleButton.selectedSpinnerItem));
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

    private void UnselectBtnOptionsAndUpdateStrings(int aboveStringIndex, int belowStringIndex) {
        climate.selectedDialogOptions[aboveStringIndex] = false;
        climate.selectedDialogOptions[belowStringIndex] = false;
        climate.dialogOptions[belowStringIndex] = climate.dialogOptions[belowStringIndex].substring(0, climate.dialogOptions[belowStringIndex].lastIndexOf(" ") + 1) + "degrees";//Add "degrees" to the end of the string
        climate.dialogOptions[aboveStringIndex] = climate.dialogOptions[aboveStringIndex].substring(0, climate.dialogOptions[aboveStringIndex].lastIndexOf(" ") + 1) + "degrees";//Add "degrees" to the end of the string
    }

    private void updateDialogControls(CheckBox checkedBox, CheckBox otherCheckBox, EditText otherEditText) {
        if (checkedBox.isChecked()) {
            disableOtherCheckBoxAndText(otherCheckBox, otherEditText);
        } else {
            enableOtherCheckBoxAndText(otherCheckBox, otherEditText);
        }
    }

    private void buildLastStateDialog(CheckBox checkBox1, CheckBox checkBox2, EditText editText1, EditText editText2, int index1, int index2) {
        String lastWordOfOption = climate.dialogOptions[index1].substring(climate.dialogOptions[index1].lastIndexOf(" ") + 1);
        editText1.setText(lastWordOfOption.equals("Degrees") ? "" : lastWordOfOption);
        checkBox1.setChecked(climate.selectedDialogOptions[index1]);
        checkBox2.setChecked(climate.selectedDialogOptions[index2]);
        lastWordOfOption = climate.dialogOptions[index2].substring(climate.dialogOptions[index2].lastIndexOf(" ") + 1);
        editText2.setText(lastWordOfOption.equals("Degrees") ? "" : lastWordOfOption);
    }

    private void UpdateCustomInputBtnOptionAndSelected(EditText tempAbove_editText, int aboveStringIndex, int belowStringIndex) {
        if (!tempAbove_editText.getText().toString().equals("")) {
            climate.selectedDialogOptions[aboveStringIndex] = true;
            climate.dialogOptions[aboveStringIndex] = climate.dialogOptions[aboveStringIndex].substring(0, climate.dialogOptions[aboveStringIndex].lastIndexOf(" ") + 1) + tempAbove_editText.getText().toString(); //Add the Temp to the string
            climate.dialogOptions[belowStringIndex] = climate.dialogOptions[belowStringIndex].substring(0, climate.dialogOptions[belowStringIndex].lastIndexOf(" ") + 1) + "degrees";//Add "degrees" to the end of the string
            climate.selectedDialogOptions[belowStringIndex] = false;
        } else {
            climate.selectedDialogOptions[aboveStringIndex] = false;
            climate.selectedDialogOptions[belowStringIndex] = false;
        }
    }

    private void enableOtherCheckBoxAndText(CheckBox otherCheckBox, EditText otherEditText) {
        otherCheckBox.setClickable(true);
        otherCheckBox.setEnabled(true);
        otherEditText.setEnabled(true);
        otherEditText.setClickable(true);
    }

    private void disableOtherCheckBoxAndText(CheckBox otherCheckBox, EditText otherEditText) {
        otherCheckBox.setChecked(false);
        otherCheckBox.setClickable(false);
        otherCheckBox.setEnabled(false); //disable the checkbox
        otherEditText.setEnabled(false);
        otherEditText.setClickable(false);
    }

    protected boolean AllOptionsUnchecked(boolean[] selectedLocationOptions) {
        for (boolean bool : selectedLocationOptions) {
            if (bool == true) {
                return false;
            }
        }
        return true;
    }

    public List<ScenarioButton> getSelectedInputButtons() {
        List<ScenarioButton> ButtonsArray = new ArrayList<>();
        for (ScenarioButton scenarioButton : inputs) {
            int id = scenarioButton.getToggleButtonID();
            final ToggleButton toggleButton = (ToggleButton) findViewById(id);
            if (toggleButton.isChecked()) {
                scenarioButton.selectedSpinnerItem = (String) ((Spinner) findViewById(scenarioButton.spinnerId)).getSelectedItem();
                ButtonsArray.add(scenarioButton);
            }
        }
        return ButtonsArray;
    }

    public boolean allBtnsUnpressed() {
        for (ScenarioButton scenarioButton : inputs) {
            int id = scenarioButton.getToggleButtonID();
            final ToggleButton toggleButton = (ToggleButton) findViewById(id);
            if (toggleButton.isChecked()) {
                return false;
            }
        }
        return true;
    }

    public int findIndexInArrayContainingString(List<String> arr, String partLine) {
        for (int i = 0; i < arr.size(); i++) {
            if (arr.get(i).contains(partLine)) {
                return i;
            }
        }
        return 0;
    }

    protected void submitData(View view) {

        if (allBtnsUnpressed()) {
            Toast.makeText(getApplicationContext(), "Please choose at least one input",
                    Toast.LENGTH_SHORT).show();
        } else {
            //Create Intent to hold the returned message for the calling activity
            Intent returnIntent = new Intent();

            List<ScenarioButton> inputsButtons = getSelectedInputButtons();
            scenarioToEdit.setInputButtons(inputsButtons);

            //Call an activity for editing existing a scenario
            Intent i = new Intent(this, ActionsActivity.class);
            //Scenario editedScenario = itemScenarioList.get(itemScenarioList.size()-1);
            i.putExtra("edit", scenarioToEdit);
            i.putExtra("sensorsInfo", sensorsInfo);
            startActivityForResult(i, MY_CHILD_ACTIVITY);
        }
    }

    public ScenarioButton getScenarioButtonByTag(String tag) {
        List<ScenarioButton> buttons = new ArrayList<>(inputs);
        for (ScenarioButton scenarioButton : buttons) {
            if (scenarioButton.getTagName().equals(tag)) {
                return scenarioButton;
            }
        }
        return null;
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


