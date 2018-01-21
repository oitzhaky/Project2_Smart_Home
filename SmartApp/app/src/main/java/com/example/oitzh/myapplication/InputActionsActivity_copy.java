//package com.example.oitzh.myapplication;
//
//import android.app.Activity;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.v7.app.ActionBar;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.CheckBox;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.Spinner;
//import android.widget.TextView;
//import android.widget.Toast;
//import android.widget.ToggleButton;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Iterator;
//import java.util.List;
//
//
//public class InputActionsActivity_copy extends AppCompatActivity {
//
//    public List<ScenarioButton> actions;
//    public List<ScenarioButton> inputs;
//    ScenarioButton location, climate, motion, time, lights, ac, tv, boiler, alert;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_input_action);
//        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
//        setSupportActionBar(myToolbar);
//
//        // Get a support ActionBar corresponding to this toolbar
//        ActionBar ab = getSupportActionBar();
//
//        // Set Toolbar title
//        ab.setTitle("Configure in 1,2,3...");
//        ab.setDisplayHomeAsUpEnabled(true);
//
//        //Input array
//        location = new ScenarioButton((R.id.gpsBtn), ScenarioInput.Trigger.Location.enumToDescriptionArray(), new boolean[]{false, false}, (String) findViewById(R.id.gpsBtn).getTag());
//        motion = new ScenarioButton((R.id.motionBtn), ScenarioInput.Trigger.Location.enumToDescriptionArray(), new boolean[]{false, false}, (String) findViewById(R.id.motionBtn).getTag());
//        time = new ScenarioButton((R.id.timeBtn), ScenarioInput.Trigger.Time.enumToDescriptionArray(), new boolean[ScenarioInput.Trigger.Time.values().length], (String) findViewById(R.id.timeBtn).getTag());
//        climate = new ScenarioButton((R.id.climateBtn), ScenarioInput.Trigger.Climate.enumToDescriptionArray(), new boolean[]{false, false}, (String) findViewById(R.id.climateBtn).getTag());
//        inputs = new ArrayList<>(Arrays.asList(location, climate, motion, time));
//
//        //Action array
//        ac = new ScenarioButton(R.id.acBtn, ScenarioAction.Action.Ac.enumToDescriptionArray(), new boolean[]{false, false}, (String) findViewById(R.id.acBtn).getTag());
//        tv = new ScenarioButton(R.id.tvBtn, ScenarioAction.Action.Tv.enumToDescriptionArray(), new boolean[]{false, false}, (String) findViewById(R.id.tvBtn).getTag());
//        lights = new ScenarioButton(R.id.lightsBtn, ScenarioAction.Action.Light.enumToDescriptionArray(), new boolean[]{false, false, false, false}, (String) findViewById(R.id.lightsBtn).getTag());
//        boiler = new ScenarioButton(R.id.boilerBtn, ScenarioAction.Action.Boiler.enumToDescriptionArray(), new boolean[]{false, false}, (String) findViewById(R.id.boilerBtn).getTag());
//        alert = new ScenarioButton(R.id.securityBtn, ScenarioAction.Action.Alert.enumToDescriptionArray(), new boolean[]{false, false}, (String) findViewById(R.id.securityBtn).getTag());
//        actions = new ArrayList<>(Arrays.asList(lights, tv, ac, boiler, alert));
//
//        //Try to get scenario if "edit" pressed
//        Scenario scenarioToEdit = (Scenario) getIntent().getSerializableExtra("edit");
//        //Initialize current activity in case "Edit" was pressed
//        if (scenarioToEdit != null) {
//            updateActivityButtonsByScenario(scenarioToEdit);
//        }
//        //make sure no action btn is clickable before any input was chosen;
//        //Pay attention to do it after extracting scenario if exists!
//        if (allBtnsUnpressed()) {
//            makeActionBtnsUnClickable();
//        }
//
//        // Spinner element
//        Spinner spinner = (Spinner) findViewById(R.id.spinner_Location);
//        // Spinner click listener
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                // On selecting a spinner item
//                String item = parent.getItemAtPosition(position).toString();
//
//                // Showing selected spinner item
//                Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
//
//        // Spinner Drop down elements
//        List<String> categories = new ArrayList<String>();
//        categories.add("Automobile");
//        categories.add("Business Services");
//        categories.add("Computers");
//        categories.add("Education");
//        categories.add("Personal");
//        categories.add("Travel");
//
//        // Creating adapter for spinner
//        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
//
//        // Drop down layout style - list view with radio button
//        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        // attaching data adapter to spinner
//        spinner.setAdapter(dataAdapter);
//    }
//
//
//    protected void onClickFunc(View view) {
//        int id = view.getId();
//        final ToggleButton toggleButton = (ToggleButton) findViewById(id);
//        //make action buttons clickable - we have at least one input button!
//        makeActionBtnsClickable();
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(InputActionsActivity_copy.this);
//        builder.setTitle("Choose Triggers");
//        builder.setIcon(R.drawable.ic_event_black_48px);
//        builder.setCancelable(false);  // disallow cancel of AlertDialog on click of back button and outside touch
////        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
////
////            @Override
////            public void onClick(DialogInterface dialog, int which) {
////                Toast.makeText(getBaseContext(), "Cancel clicked", Toast.LENGTH_SHORT).show();
////            }
////        });
//        LayoutInflater inflater = this.getLayoutInflater();
//        switch (id) {
//            //ScenarioButton Buttons
//            case R.id.gpsBtn:
//                //region GPS BUTTON
//                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (AllOptionsUnchecked(location.selectedDialogOptions)) { //if all options are unchecked, unselect the button
//                            toggleButton.setChecked(false);
//                            if (allBtnsUnpressed()) {
//                                makeActionBtnsUnClickable(); //No input is selected - make action buttons un-clickable
//                            }
//                        } else {
//                            toggleButton.setChecked(true); //some triggers are marked
//                        }
//                    }
//                });
//                builder.setMultiChoiceItems(location.dialogOptions, location.selectedDialogOptions, new DialogInterface.OnMultiChoiceClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//                        updateMultiChoiceItems(dialog, which, isChecked, location.dialogOptions[0], location.dialogOptions[1], location);
//                    }
//                });
//                builder.create().show();
//                break;
//            //endregion
//            case R.id.climateBtn:
//                //region CLIMATE BUTTON
//                final View viewDialog = inflater.inflate(R.layout.climate_alert, null); //inflate XML to the dialog
//                builder.setView(viewDialog);
//
//                final CheckBox tempAbove_checkBox = (CheckBox) viewDialog.findViewById(R.id.tempAbove_checkBox);
//                final CheckBox tempbelow_checkBox = (CheckBox) viewDialog.findViewById(R.id.tempBelow_checkBox);
//                final EditText tempAbove_editText = (EditText) viewDialog.findViewById(R.id.tempAbove_text);
//                final EditText tempBelow_editText = (EditText) viewDialog.findViewById(R.id.tempBelow_text);
//                final int aboveStringIndex = findIndexInArrayContainingString(Arrays.asList(climate.dialogOptions), "Above");
//                final int belowStringIndex = findIndexInArrayContainingString(Arrays.asList(climate.dialogOptions), "Below");
//
//                //Build last state of dialog controls before showing it
//                buildLastStateDialog(tempAbove_checkBox, tempbelow_checkBox, tempAbove_editText, tempBelow_editText, aboveStringIndex, belowStringIndex);
//
//                tempAbove_checkBox.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        updateDialogControls(tempAbove_checkBox, tempbelow_checkBox, tempBelow_editText);
//                    }
//                });
//                tempbelow_checkBox.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        updateDialogControls(tempbelow_checkBox, tempAbove_checkBox, tempAbove_editText);
//                    }
//                });
//
//                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (tempAbove_checkBox.isChecked()) {
//                            UpdateCustomInputBtnOptionAndSelected(tempAbove_editText, aboveStringIndex, belowStringIndex);
//                        } else if (tempbelow_checkBox.isChecked()) {
//                            UpdateCustomInputBtnOptionAndSelected(tempBelow_editText, belowStringIndex, aboveStringIndex);
//                        } else {
//                            UnselectBtnOptionsAndUpdateStrings(aboveStringIndex, belowStringIndex); //delete any text written and update all options in selectedOption array as unselected
//                        }
//                        if (AllOptionsUnchecked(climate.selectedDialogOptions)) { //if all options are unchecked, unselect the button
//                            toggleButton.setChecked(false);
//                            if (allBtnsUnpressed()) {
//                                makeActionBtnsUnClickable(); //No input is selected - make action buttons un-clickable
//                            }
//                        } else {
//                            toggleButton.setChecked(true); //some triggers are marked
//                        }
//                    }
//                });
//                builder.create().show();
//                break;
//            //endregion
//            case R.id.motionBtn:
//                //region MOTION BUTTON
//                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (AllOptionsUnchecked(motion.selectedDialogOptions)) { //if all options are unchecked, unselect the button
//                            toggleButton.setChecked(false);
//                            if (allBtnsUnpressed()) {
//                                makeActionBtnsUnClickable(); //No input is selected - make action buttons un-clickable
//                            }
//                        } else {
//                            toggleButton.setChecked(true); //some triggers are marked
//                        }
//                    }
//                });
//                builder.setMultiChoiceItems(motion.dialogOptions, motion.selectedDialogOptions, new DialogInterface.OnMultiChoiceClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//                        final AlertDialog alertDialog = (AlertDialog) dialog;
//                        final ListView alertDialogList = alertDialog.getListView();
//
//                        if (isChecked) {
//                            if (motion.dialogOptions[which].equals("When Leaving")) {
//                                int stringIndex = Arrays.asList(motion.dialogOptions).indexOf("When Arriving");
//                                alertDialogList.getChildAt(stringIndex).setEnabled(false); //UI-wise: disable the other option
//                                motion.selectedDialogOptions[stringIndex] = false; //Update the other option as unselected so when the dialog will be opened, options will be marked correctly
//                            } else {
//                                int stringIndex = Arrays.asList(motion.dialogOptions).indexOf("When Leaving");
//                                alertDialogList.getChildAt(stringIndex).setEnabled(false);
//                                motion.selectedDialogOptions[stringIndex] = false;
//                            }
//                        } else {
//                            if (motion.dialogOptions[which].equals("When Leaving")) {
//                                int stringIndex = Arrays.asList(motion.dialogOptions).indexOf("When Arriving");
//                                alertDialogList.getChildAt(stringIndex).setEnabled(true);
//                                motion.selectedDialogOptions[which] = false; //Update the other option as unselected so when the dialog will be opened, options will be marked correctly
//                            } else {
//                                int stringIndex = Arrays.asList(motion.dialogOptions).indexOf("When Leaving");
//                                alertDialogList.getChildAt(stringIndex).setEnabled(true);
//                                motion.selectedDialogOptions[which] = false;
//                            }
//                        }
//                    }
//                });
//                builder.create().show();
//                break;
//            //endregion
//            case R.id.timeBtn:
//                //region TIME BUTTON
//                final View viewDialogTime = inflater.inflate(R.layout.time_alert, null); //inflate XML to the dialog
//                builder.setView(viewDialogTime);
//
//                final CheckBox atTime_checkBox = (CheckBox) viewDialogTime.findViewById(R.id.atTime_checkBox);
//                final CheckBox rangeTime_checkBox = (CheckBox) viewDialogTime.findViewById(R.id.rangeTime_checkBox);
//                final List<CheckBox> timeCheckBoxes = new ArrayList<>(Arrays.asList(atTime_checkBox, rangeTime_checkBox));
//
//                final EditText atTime_editText = (EditText) viewDialogTime.findViewById(R.id.atTime_text);
//                final EditText rangeLowerTime_editText = (EditText) viewDialogTime.findViewById(R.id.rangeLowerTime_text);
//                final EditText rangeUpperTime_editText = (EditText) viewDialogTime.findViewById(R.id.rangeUpperTime_text);
//                final List<EditText> timeTexts = new ArrayList<>(Arrays.asList(atTime_editText, rangeLowerTime_editText, rangeUpperTime_editText));
//
//                //Build last state of dialog controls before showing it
//                for (int index = 0; index < time.dialogOptions.length; index++) {
//                    if (index == time.dialogOptions.length - 1) {
//                        String string = time.dialogOptions[index].substring(time.dialogOptions[index].indexOf(" ") + 1, time.dialogOptions[index].indexOf("a") - 1);
//                        timeTexts.get(index).setText(string.equals("HH:MM") ? "" : string);
//                        string = time.dialogOptions[index].substring(time.dialogOptions[index].lastIndexOf(" ") + 1);
//                        timeTexts.get(index + 1).setText(string.equals("HH:MM") ? "" : string);
//                        timeCheckBoxes.get(index).setChecked(time.selectedDialogOptions[index]);
//                    } else {
//                        String string = time.dialogOptions[index].substring(time.dialogOptions[index].lastIndexOf(" ") + 1);
//                        timeTexts.get(index).setText(string.equals("HH:MM") ? "" : string);
//                        timeCheckBoxes.get(index).setChecked(time.selectedDialogOptions[index]);
//                    }
//                }
//
//
//                View.OnClickListener timeOnClickListener = new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (atTime_checkBox.isChecked()) {
//                            for (CheckBox checkBox : timeCheckBoxes) { // make all other checkbox disable
//                                if (!checkBox.equals(atTime_checkBox)) {
//                                    checkBox.setChecked(false);
//                                    checkBox.setClickable(false);
//                                    checkBox.setEnabled(false);
//                                }
//                                for (EditText editText : timeTexts) {
//                                    if (!editText.equals(atTime_editText)) {
//                                        editText.setEnabled(false);
//                                        editText.setClickable(false);
//                                    }
//                                }
//                            }
//                        } else if (rangeTime_checkBox.isChecked()) {
//                            for (CheckBox checkBox : timeCheckBoxes) { // make all other checkbox disable
//                                if (!checkBox.equals(rangeTime_checkBox)) {
//                                    checkBox.setChecked(false);
//                                    checkBox.setClickable(false);
//                                    checkBox.setEnabled(false);
//                                }
//                                for (EditText editText : timeTexts) {
//                                    if (!editText.equals(rangeLowerTime_editText) && !editText.equals(rangeUpperTime_editText)) {
//                                        editText.setEnabled(false);
//                                        editText.setClickable(false);
//                                    }
//                                }
//                            }
//                        } else {//button unchecked
//                            for (CheckBox checkBox : timeCheckBoxes) { // make all other checkbox disable
//                                checkBox.setClickable(true);
//                                checkBox.setEnabled(true);
//                            }
//                            for (EditText editText : timeTexts) {
//                                editText.setEnabled(true);
//                                editText.setClickable(true);
//                            }
//                        }
//                    }
//                };
//
//                //Define on click Listener
//                atTime_checkBox.setOnClickListener(timeOnClickListener);
//                rangeTime_checkBox.setOnClickListener(timeOnClickListener);
//
//                final List<LinearLayout> layouts = new ArrayList<>(Arrays.asList((LinearLayout) viewDialogTime.findViewById(R.id.atTime_layout), (LinearLayout) viewDialogTime.findViewById(R.id.betweenTime_layout)));
//
//                for (int i = 0; i < time.selectedDialogOptions.length; i++) {
//                    time.selectedDialogOptions[i] = false;
//                }
//                for (int i = 0; i < time.dialogOptions.length; i++) {
//                    time.dialogOptions[i] = time.dialogOptions[i].substring(0, time.dialogOptions[i].lastIndexOf(" ") + 1) + "HH:MM";
//                    if (time.dialogOptions[i].contains("Between")) {
//                        time.dialogOptions[i] = "Between HH:MM and HH:MM";
//                    }
//                }
//
//                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        for (LinearLayout linearLayout : layouts) {
//                            int count = linearLayout.getChildCount();
//                            CheckBox checkBox = (CheckBox) linearLayout.getChildAt(0);
//                            EditText editText = (EditText) linearLayout.getChildAt(1);
//                            if (count == 4) { //option of range-time
//                                EditText editText2 = (EditText) linearLayout.getChildAt(3);
//                                if (checkBox.isChecked()) {
//                                    if (!editText.getText().toString().equals("") && !editText2.getText().toString().equals("")) {
//                                        //Update selected option
//                                        final int StringIndex = findIndexInArrayContainingString(Arrays.asList(time.dialogOptions), checkBox.getText().toString());
//                                        time.selectedDialogOptions[StringIndex] = true;
//                                        time.dialogOptions[StringIndex] = "Between " + editText.getText().toString() +
//                                                " and " + editText2.getText().toString();
//                                    }
//                                }
//                            } else if (checkBox.isChecked()) {
//                                if (!editText.getText().toString().equals("")) {
//                                    //Update selected option
//                                    final int StringIndex = findIndexInArrayContainingString(Arrays.asList(time.dialogOptions), checkBox.getText().toString());
//                                    time.selectedDialogOptions[StringIndex] = true;
//                                    time.dialogOptions[StringIndex] = time.dialogOptions[StringIndex].substring(0, time.dialogOptions[StringIndex].lastIndexOf(" ") + 1) + editText.getText().toString(); //Add the ScenarioTime to the string
//                                }
//                            }
//                        }
//                        if (AllOptionsUnchecked(time.selectedDialogOptions)) { //if all options are unchecked, unselect the button
//                            toggleButton.setChecked(false);
//                            if (allBtnsUnpressed()) {
//                                makeActionBtnsUnClickable(); //No input is selected - make action buttons un-clickable
//                            }
//                        } else {
//                            toggleButton.setChecked(true); //some triggers are marked
//                        }
//                    }
//                });
//                builder.create().show();
//                break;
//            //endregion
//
//            //Action Buttons
//            case R.id.acBtn:
//                //region Ac BUTTON
//                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (AllOptionsUnchecked(ac.selectedDialogOptions)) { //if all options are unchecked, unselect the button
//                            toggleButton.setChecked(false);
//                            if (allBtnsUnpressed()) {
//                                makeActionBtnsUnClickable(); //No input is selected - make action buttons un-clickable
//                            }
//                        } else {
//                            toggleButton.setChecked(true); //some triggers are marked
//                        }
//                    }
//                });
//                builder.setMultiChoiceItems(ac.dialogOptions, ac.selectedDialogOptions, new DialogInterface.OnMultiChoiceClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//                        updateMultiChoiceItems(dialog, which, isChecked, ac.dialogOptions[0], ac.dialogOptions[1], ac);
//                    }
//                });
//                builder.create().show();
//                break;
//            ///endregion
//            case R.id.tvBtn:
//                //region Tv BUTTON
//                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (AllOptionsUnchecked(tv.selectedDialogOptions)) { //if all options are unchecked, unselect the button
//                            toggleButton.setChecked(false);
//                            if (allBtnsUnpressed()) {
//                                makeActionBtnsUnClickable(); //No input is selected - make action buttons un-clickable
//                            }
//                        } else {
//                            toggleButton.setChecked(true); //some triggers are marked
//                        }
//                    }
//                });
//                builder.setMultiChoiceItems(tv.dialogOptions, tv.selectedDialogOptions, new DialogInterface.OnMultiChoiceClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//                        updateMultiChoiceItems(dialog, which, isChecked, tv.dialogOptions[0], tv.dialogOptions[1], tv);
//                    }
//                });
//                builder.create().show();
//                break;
//            //endregion
//            case R.id.securityBtn:
//                //region Alert System BUTTON
//                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (AllOptionsUnchecked(alert.selectedDialogOptions)) { //if all options are unchecked, unselect the button
//                            toggleButton.setChecked(false);
//                            if (allBtnsUnpressed()) {
//                                makeActionBtnsUnClickable(); //No input is selected - make action buttons un-clickable
//                            }
//                        } else {
//                            toggleButton.setChecked(true); //some triggers are marked
//                        }
//                    }
//                });
//                builder.setMultiChoiceItems(alert.dialogOptions, alert.selectedDialogOptions, new DialogInterface.OnMultiChoiceClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//                        updateMultiChoiceItems(dialog, which, isChecked, alert.dialogOptions[0], alert.dialogOptions[1], alert);
//                    }
//                });
//                builder.create().show();
//                break;
//            ///endregion
//            case R.id.boilerBtn:
//                //region Boiler BUTTON
//                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (AllOptionsUnchecked(boiler.selectedDialogOptions)) { //if all options are unchecked, unselect the button
//                            toggleButton.setChecked(false);
//                            if (allBtnsUnpressed()) {
//                                makeActionBtnsUnClickable(); //No input is selected - make action buttons un-clickable
//                            }
//                        } else {
//                            toggleButton.setChecked(true); //some triggers are marked
//                        }
//                    }
//                });
//                builder.setMultiChoiceItems(boiler.dialogOptions, boiler.selectedDialogOptions, new DialogInterface.OnMultiChoiceClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//                        updateMultiChoiceItems(dialog, which, isChecked, boiler.dialogOptions[0], boiler.dialogOptions[1], boiler);
//                    }
//                });
//                builder.create().show();
//                break;
//            //endregion
//            case R.id.lightsBtn:
//                //region LIGHTS BUTTON
//                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (AllOptionsUnchecked(lights.selectedDialogOptions)) { //if all options are unchecked, unselect the button
//                            toggleButton.setChecked(false);
//                            if (allBtnsUnpressed()) {
//                                makeActionBtnsUnClickable(); //No input is selected - make action buttons un-clickable
//                            }
//                        } else {
//                            toggleButton.setChecked(true); //some triggers are marked
//                        }
//                    }
//                });
//                builder.setMultiChoiceItems(lights.dialogOptions, lights.selectedDialogOptions, new DialogInterface.OnMultiChoiceClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//                        final AlertDialog alertDialog = (AlertDialog) dialog;
//                        final ListView alertDialogList = alertDialog.getListView();
//                        if (isChecked) {
//                            alertDialogList.getChildAt((which + 1) % 2).setEnabled(false); //UI-wise: disable the other option
//                            lights.selectedDialogOptions[(which + 1) % 2] = false; //Update the other option as unselected so when the dialog will be opened, options will be marked correctly
//                        } else {
//                            alertDialogList.getChildAt((which + 1) % 2).setEnabled(true);
//                            lights.selectedDialogOptions[(which + 1) % 2] = true;
//                        }
//                    }
//                });
//                builder.create().show();
//                break;
//            //endregion
//        }
//    }
//
//    private void updateActivityButtonsByScenario(Scenario scenarioToEdit) {
//        TextView editTextView = (TextView) findViewById(R.id.editTextView);
//
//        editTextView.setText(scenarioToEdit.getScenarioName());
//        for (ScenarioButton toggleButton : scenarioToEdit.getInputToggledButtonsArray()) {
//            //Iterate over the buttons from edit Scenario and update the new activity's buttons accordingly
//            String currentToggleBtnName = toggleButton.getTagName();
//            Iterator<ScenarioButton> inputIterator = inputs.iterator();
//            while (inputIterator.hasNext()) {
//                ScenarioButton scenarioButtonCurr = inputIterator.next();
//                if (scenarioButtonCurr.getTagName().equals(currentToggleBtnName)) {
//                    scenarioButtonCurr.selectedDialogOptions = toggleButton.getSelectedDialogOptions(); //TODO:should use NEW?
//                    scenarioButtonCurr.dialogOptions = toggleButton.getDialogOptions();
//                    int id = scenarioButtonCurr.getToggleButtonID();
//                    ToggleButton toggleBtn = (ToggleButton) findViewById(id);
//                    toggleBtn.setChecked(true);
//                }
//            }
//        }
//        for (ScenarioButton toggleButton : scenarioToEdit.getActionToggledButtonsArray()) {
//            Iterator<ScenarioButton> actionIterator = actions.iterator();
//            String currentToggleBtnName = toggleButton.getTagName();
//            while (actionIterator.hasNext()) {
//                ScenarioButton actionCurr = actionIterator.next();
//                if (actionCurr.getTagName().equals(currentToggleBtnName)) {
//                    actionCurr.selectedDialogOptions = toggleButton.getSelectedDialogOptions(); //TODO:should use NEW?
//                    actionCurr.dialogOptions = toggleButton.getDialogOptions();
//                    int id = actionCurr.getToggleButtonID();
//                    ToggleButton toggleBtn = (ToggleButton) findViewById(id);
//                    toggleBtn.setChecked(true);
//                }
//            }
//        }
//    }
//
//    private void updateMultiChoiceItems(DialogInterface dialog, int which, boolean isChecked, String option1, String option2, ScenarioButton scenarioButton) {
//        final AlertDialog alertDialog = (AlertDialog) dialog;
//        final ListView alertDialogList = alertDialog.getListView();
//
//        if (isChecked) {
//            if (scenarioButton.dialogOptions[which].equals(option1)) {
//                int stringIndex = Arrays.asList(scenarioButton.dialogOptions).indexOf(option2);
//                alertDialogList.getChildAt(stringIndex).setEnabled(false); //UI-wise: disable the other option
//                scenarioButton.selectedDialogOptions[stringIndex] = false; //Update the other option as unselected so when the dialog will be opened, options will be marked correctly
//            } else {
//                int stringIndex = Arrays.asList(scenarioButton.dialogOptions).indexOf(option1);
//                alertDialogList.getChildAt(stringIndex).setEnabled(false);
//                scenarioButton.selectedDialogOptions[stringIndex] = false;
//            }
//        } else {
//            if (scenarioButton.dialogOptions[which].equals(option1)) {
//                int stringIndex = Arrays.asList(scenarioButton.dialogOptions).indexOf(option2);
//                alertDialogList.getChildAt(stringIndex).setEnabled(true);
//                scenarioButton.selectedDialogOptions[which] = false; //Update the other option as unselected so when the dialog will be opened, options will be marked correctly
//            } else {
//                int stringIndex = Arrays.asList(scenarioButton.dialogOptions).indexOf(option1);
//                alertDialogList.getChildAt(stringIndex).setEnabled(true);
//                scenarioButton.selectedDialogOptions[which] = false;
//            }
//        }
//    }
//
//    private void UnselectBtnOptionsAndUpdateStrings(int aboveStringIndex, int belowStringIndex) {
//        climate.selectedDialogOptions[aboveStringIndex] = false;
//        climate.selectedDialogOptions[belowStringIndex] = false;
//        climate.dialogOptions[belowStringIndex] = climate.dialogOptions[belowStringIndex].substring(0, climate.dialogOptions[belowStringIndex].lastIndexOf(" ") + 1) + "degrees";//Add "degrees" to the end of the string
//        climate.dialogOptions[aboveStringIndex] = climate.dialogOptions[aboveStringIndex].substring(0, climate.dialogOptions[aboveStringIndex].lastIndexOf(" ") + 1) + "degrees";//Add "degrees" to the end of the string
//    }
//
//    private void updateDialogControls(CheckBox tempAbove_checkBox, CheckBox tempbelow_checkBox, EditText tempBelow_editText) {
//        if (tempAbove_checkBox.isChecked()) {
//            disableOtherCheckBoxAndText(tempbelow_checkBox, tempBelow_editText);
//        } else {
//            enableOtherCheckBoxAndText(tempbelow_checkBox, tempBelow_editText);
//        }
//    }
//
//    private void buildLastStateDialog(CheckBox tempAbove_checkBox, CheckBox tempbelow_checkBox, EditText tempAbove_editText, EditText tempBelow_editText, int aboveStringIndex, int belowStringIndex) {
//        final String aboveTempLastWord = climate.dialogOptions[aboveStringIndex].substring(climate.dialogOptions[aboveStringIndex].lastIndexOf(" ") + 1);
//        tempAbove_editText.setText(aboveTempLastWord.equals("Degrees") ? "" : aboveTempLastWord);
//        tempAbove_checkBox.setChecked(climate.selectedDialogOptions[aboveStringIndex]);
//        tempbelow_checkBox.setChecked(climate.selectedDialogOptions[belowStringIndex]);
//        final String belowTempLastWord = climate.dialogOptions[belowStringIndex].substring(climate.dialogOptions[belowStringIndex].lastIndexOf(" ") + 1);
//        tempBelow_editText.setText(belowTempLastWord.equals("Degrees") ? "" : belowTempLastWord);
//    }
//
//    private void UpdateCustomInputBtnOptionAndSelected(EditText tempAbove_editText, int aboveStringIndex, int belowStringIndex) {
//        if (!tempAbove_editText.getText().toString().equals("")) {
//            climate.selectedDialogOptions[aboveStringIndex] = true;
//            climate.dialogOptions[aboveStringIndex] = climate.dialogOptions[aboveStringIndex].substring(0, climate.dialogOptions[aboveStringIndex].lastIndexOf(" ") + 1) + tempAbove_editText.getText().toString(); //Add the Temp to the string
//            climate.dialogOptions[belowStringIndex] = climate.dialogOptions[belowStringIndex].substring(0, climate.dialogOptions[belowStringIndex].lastIndexOf(" ") + 1) + "degrees";//Add "degrees" to the end of the string
//            climate.selectedDialogOptions[belowStringIndex] = false;
//        } else {
//            climate.selectedDialogOptions[aboveStringIndex] = false;
//            climate.selectedDialogOptions[belowStringIndex] = false;
//        }
//    }
//
//    private void enableOtherCheckBoxAndText(CheckBox tempbelow_checkBox, EditText tempBelow_editText) {
//        tempbelow_checkBox.setClickable(true);
//        tempbelow_checkBox.setEnabled(true);
//        tempBelow_editText.setEnabled(true);
//        tempBelow_editText.setClickable(true);
//    }
//
//    private void disableOtherCheckBoxAndText(CheckBox tempbelow_checkBox, EditText tempBelow_editText) {
//        tempbelow_checkBox.setChecked(false);
//        tempbelow_checkBox.setClickable(false);
//        tempbelow_checkBox.setEnabled(false); //disable the checkbox
//        tempBelow_editText.setEnabled(false);
//        tempBelow_editText.setClickable(false);
//    }
//
//    protected boolean AllOptionsUnchecked(boolean[] selectedLocationOptions) {
//        for (boolean bool : selectedLocationOptions) {
//            if (bool == true) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    public List<ScenarioButton> getSelectedInputButtons() {
//        List<ScenarioButton> ButtonsArray = new ArrayList<>();
//        for (ScenarioButton scenarioButton : inputs) {
//            int id = scenarioButton.getToggleButtonID();
//            final ToggleButton toggleButton = (ToggleButton) findViewById(id);
//            if (toggleButton.isChecked()) {
//                ButtonsArray.add(scenarioButton);
//            }
//        }
//        return ButtonsArray;
//    }
//
//    public List<ScenarioButton> getSelectedActionButtons() {
//        List<ScenarioButton> ButtonsArray = new ArrayList<>();
//        for (ScenarioButton scenarioButton : actions) {
//            int id = scenarioButton.getToggleButtonID();
//            final ToggleButton toggleButton = (ToggleButton) findViewById(id);
//            if (toggleButton.isChecked()) {
//                ButtonsArray.add(scenarioButton);
//            }
//        }
//        return ButtonsArray;
//    }
//
//    public void makeActionBtnsClickable() {
//        for (ScenarioButton scenarioButton : actions) {
//            int id = scenarioButton.getToggleButtonID();
//            final ToggleButton toggleButton = (ToggleButton) findViewById(id);
//            toggleButton.setClickable(true);
//        }
//    }
//
//    public void makeActionBtnsUnClickable() {
//        for (ScenarioButton scenarioButton : actions) {
//            int id = scenarioButton.getToggleButtonID();
//            final ToggleButton toggleButton = (ToggleButton) findViewById(id);
//            toggleButton.setClickable(false);
//        }
//    }
//
//    public boolean allBtnsUnpressed() {
//        for (ScenarioButton scenarioButton : inputs) {
//            int id = scenarioButton.getToggleButtonID();
//            final ToggleButton toggleButton = (ToggleButton) findViewById(id);
//            if (toggleButton.isChecked()) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    public int findIndexInArrayContainingString(List<String> arr, String partLine) {
//        for (int i = 0; i < arr.size(); i++) {
//            if (arr.get(i).contains(partLine)) {
//                return i;
//            }
//        }
//        return 0;
//    }
//
//    protected void submitData(View view) {
//        TextView editTextView = (TextView) findViewById(R.id.editTextView);
//        String name = editTextView.getText().toString();
//
//        if (name.isEmpty()) {
//            Toast.makeText(getApplicationContext(), "Please enter a name for the scenario",
//                    Toast.LENGTH_SHORT).show();
//        } else {
//            //Create Intent to hold the returned message for the calling activity
//            Intent returnIntent = new Intent();
//
//            List<ScenarioButton> inputsButtons = getSelectedInputButtons();
//            List<ScenarioButton> actionsButtons = getSelectedActionButtons();
//
//            Scenario scenario = new Scenario(name, inputsButtons, actionsButtons);
//            returnIntent.putExtra("result", scenario);
//            setResult(Activity.RESULT_OK, returnIntent);
//            finish();
//        }
//    }
//
//    public ScenarioButton getScenarioButtonByTag(String tag){
//        List<ScenarioButton> buttons = new ArrayList<>(inputs);
//        buttons.addAll(actions);
//        for(ScenarioButton scenarioButton: buttons){
//            if(scenarioButton.getTagName().equals(tag)){
//                return scenarioButton;
//            }
//        }
//        return null;
//    }
//}
//
//
