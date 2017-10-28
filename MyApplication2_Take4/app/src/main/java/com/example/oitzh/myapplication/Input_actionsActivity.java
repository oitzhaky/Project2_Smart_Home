package com.example.oitzh.myapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


public class Input_actionsActivity extends AppCompatActivity {

    public List<Action> actions;
    public List<Input> inputs;
    Input location;
    Input climate;
    Input motion;
    Input time;
    Action lights;
    Action ac;
    Action tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_action);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Set Toolbar title
        ab.setTitle("Configure");
        ab.setDisplayHomeAsUpEnabled(true);

        location = new Input((R.id.gpsBtn), new String[]{"When leaving home", "When arriving home"}, new boolean[]{false, false}, (String) findViewById(R.id.gpsBtn).getTag());
        motion = new Input((R.id.motionBtn), new String[]{"When leaving home", "When arriving home"}, new boolean[]{false, false}, (String) findViewById(R.id.motionBtn).getTag());
        time = new Input((R.id.timeBtn), new String[]{"From HH:MM", "Until HH:MM", "At HH:MM", "Between HH:MM and HH:MM"}, new boolean[]{false, false, false, false}, (String) findViewById(R.id.timeBtn).getTag());
        climate = new Input((R.id.climateBtn), new String[]{"When Temp is above degrees", "When Temp is below degrees", "When Temp is above degrees and below degrees"}, new boolean[]{false, false, false}, (String) findViewById(R.id.climateBtn).getTag());
        inputs = new ArrayList<>(Arrays.asList(location, climate, motion));

        lights = new Action(R.id.lightsBtn);
        ac = new Action(R.id.acBtn);
        tv = new Action(R.id.tvBtn);
        actions = new ArrayList<>(Arrays.asList(lights, tv, ac));

        Scenario scenarioToEdit = (Scenario) getIntent().getSerializableExtra("edit");
        TextView editTextView = (TextView) findViewById(R.id.editTextView);

        if (scenarioToEdit != null) { //Initialize current activity
            editTextView.setText(scenarioToEdit.getScenarioName());
            for (IA toggleButton : scenarioToEdit.getToggledButtonsArray()) {
                //Iterate over the buttons from edit Scenario and update the new activity's buttons accordingly
                String currentToggleBtnName = toggleButton.getTagName();
                Iterator<Input> it = inputs.iterator(); //TODO: the same for outputs array!!
                while (it.hasNext()) {
                    Input curr = it.next();
                    if (curr.getTagName().equals(currentToggleBtnName)) {
                        curr.selectedDialogOptions = toggleButton.getSelectedDialogOptions(); //TODO:should use NEW?
                        int id = curr.getToggleButtonID();
                        ToggleButton toggleBtn = (ToggleButton) findViewById(id);
                        toggleBtn.setChecked(true);
                    }
                }
            }
        }
        //make sure no action btn is clickable before any input was chosen;
        //Pay attention to do it after extracting scenario if exists!
        if (allBtnsUnpressed()) {
            makeActionBtnsUnClickable();
        }
    }

    protected void onClickFunc(View view) {
        int id = view.getId();
        final ToggleButton toggleButton = (ToggleButton) findViewById(id);
        //make action buttons clickable - we have at least one input button!
        makeActionBtnsClickable();

        AlertDialog.Builder builder = new AlertDialog.Builder(Input_actionsActivity.this);
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
            case R.id.gpsBtn:
                //region GPS BUTTON
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (AllOptionsUnchecked(location.selectedDialogOptions)) { //if all options are unchecked, unselect the button
                            toggleButton.setChecked(false);
                            if (allBtnsUnpressed()) {
                                makeActionBtnsUnClickable(); //No input is selected - make action buttons un-clickable
                            }
                        } else {
                            toggleButton.setChecked(true); //some triggers are marked
                        }
                    }
                });
                builder.setMultiChoiceItems(location.dialogOptions, location.selectedDialogOptions, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        final AlertDialog alertDialog = (AlertDialog) dialog;
                        final ListView alertDialogList = alertDialog.getListView();

                        if (isChecked) {
                            if (location.dialogOptions[which].equals("When leaving home")) {
                                int stringIndex = Arrays.asList(location.dialogOptions).indexOf("When arriving home");
                                alertDialogList.getChildAt(stringIndex).setEnabled(false); //UI-wise: disable the other option
                                location.selectedDialogOptions[stringIndex] = false; //Update the other option as unselected so when the dialog will be opened, options will be marked correctly
                            } else {
                                int stringIndex = Arrays.asList(location.dialogOptions).indexOf("When leaving home");
                                alertDialogList.getChildAt(stringIndex).setEnabled(false);
                                location.selectedDialogOptions[stringIndex] = false;
                            }
                            //Toast.makeText(Input_actionsActivity.this, "You chose " + Input_actionsActivity.this.location.dialogOptions[which], Toast.LENGTH_SHORT).show(); //print comment
                        } else {
                            if (location.dialogOptions[which].equals("When leaving home")) {
                                int stringIndex = Arrays.asList(location.dialogOptions).indexOf("When arriving home");
                                alertDialogList.getChildAt(stringIndex).setEnabled(true);
                                location.selectedDialogOptions[which] = false; //Update the other option as unselected so when the dialog will be opened, options will be marked correctly
                            } else {
                                int stringIndex = Arrays.asList(location.dialogOptions).indexOf("When leaving home");
                                alertDialogList.getChildAt(stringIndex).setEnabled(true);
                                location.selectedDialogOptions[which] = false;
                            }
//                            if (AllOptionsUnchecked(location.selectedDialogOptions)) { //if all options are unchecked, unselect the Togglebutton
//                                toggleButton.setChecked(false);

//                            }
                            //Toast.makeText(Input_actionsActivity.this, "You UN-chose " + Input_actionsActivity.this.location.dialogOptions[which], Toast.LENGTH_SHORT).show(); //print comment
                        }
                    }
                });
                builder.create().show();
                break;
            //endregion
            case R.id.climateBtn:
                //region CLIMATE BUTTON
                final View viewDialog = inflater.inflate(R.layout.climate_alert, null); //inflate XML to the dialog
                builder.setView(viewDialog);

                final CheckBox tempAbove_checkBox = (CheckBox) viewDialog.findViewById(R.id.tempAbove_checkBox);
                final CheckBox tempbelow_checkBox = (CheckBox) viewDialog.findViewById(R.id.tempBelow_checkBox);
                final EditText tempAbove_editText = (EditText) viewDialog.findViewById(R.id.tempAbove_text);
                final EditText tempBelow_editText = (EditText) viewDialog.findViewById(R.id.tempBelow_text);
                final int aboveStringIndex = findIndexInArrayContainingString(Arrays.asList(climate.dialogOptions), "above");
                final int belowStringIndex = findIndexInArrayContainingString(Arrays.asList(climate.dialogOptions), "below");

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
                                makeActionBtnsUnClickable(); //No input is selected - make action buttons un-clickable
                            }
                        } else {
                            toggleButton.setChecked(true); //some triggers are marked
                        }
                    }
                });
                builder.create().show();
                break;
            //endregion
            case R.id.motionBtn:
                //region MOTION BUTTON
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (AllOptionsUnchecked(motion.selectedDialogOptions)) { //if all options are unchecked, unselect the button
                            toggleButton.setChecked(false);
                            if (allBtnsUnpressed()) {
                                makeActionBtnsUnClickable(); //No input is selected - make action buttons un-clickable
                            }
                        } else {
                            toggleButton.setChecked(true); //some triggers are marked
                        }
                    }
                });
                builder.setMultiChoiceItems(motion.dialogOptions, motion.selectedDialogOptions, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        final AlertDialog alertDialog = (AlertDialog) dialog;
                        final ListView alertDialogList = alertDialog.getListView();

                        if (isChecked) {
                            if (motion.dialogOptions[which].equals("When leaving home")) {
                                int stringIndex = Arrays.asList(motion.dialogOptions).indexOf("When arriving home");
                                alertDialogList.getChildAt(stringIndex).setEnabled(false); //UI-wise: disable the other option
                                motion.selectedDialogOptions[stringIndex] = false; //Update the other option as unselected so when the dialog will be opened, options will be marked correctly
                            } else {
                                int stringIndex = Arrays.asList(motion.dialogOptions).indexOf("When leaving home");
                                alertDialogList.getChildAt(stringIndex).setEnabled(false);
                                motion.selectedDialogOptions[stringIndex] = false;
                            }
                            //Toast.makeText(Input_actionsActivity.this, "You chose " + Input_actionsActivity.this.location.dialogOptions[which], Toast.LENGTH_SHORT).show(); //print comment
                        } else {
                            if (motion.dialogOptions[which].equals("When leaving home")) {
                                int stringIndex = Arrays.asList(motion.dialogOptions).indexOf("When arriving home");
                                alertDialogList.getChildAt(stringIndex).setEnabled(true);
                                motion.selectedDialogOptions[which] = false; //Update the other option as unselected so when the dialog will be opened, options will be marked correctly
                            } else {
                                int stringIndex = Arrays.asList(motion.dialogOptions).indexOf("When leaving home");
                                alertDialogList.getChildAt(stringIndex).setEnabled(true);
                                motion.selectedDialogOptions[which] = false;
                            }
//                            if (AllOptionsUnchecked(location.selectedDialogOptions)) { //if all options are unchecked, unselect the Togglebutton
//                                toggleButton.setChecked(false);

//                            }
                            //Toast.makeText(Input_actionsActivity.this, "You UN-chose " + Input_actionsActivity.this.location.dialogOptions[which], Toast.LENGTH_SHORT).show(); //print comment
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


                final CheckBox fromTime_checkBox = (CheckBox) viewDialogTime.findViewById(R.id.fromTime_checkBox);
                final CheckBox untilTime_checkBox = (CheckBox) viewDialogTime.findViewById(R.id.untilTime_checkBox);
                final CheckBox atTime_checkBox = (CheckBox) viewDialogTime.findViewById(R.id.atTime_checkBox);
                final CheckBox rangeTime_checkBox = (CheckBox) viewDialogTime.findViewById(R.id.rangeTime_checkBox);
                final List<CheckBox> timeCheckBoxes = new ArrayList<CheckBox>(Arrays.asList(fromTime_checkBox, untilTime_checkBox, atTime_checkBox, rangeTime_checkBox));

                final EditText fromTime_editText = (EditText) viewDialogTime.findViewById(R.id.fromTime_text);
                final EditText untilTime_editText = (EditText) viewDialogTime.findViewById(R.id.untilTime_text);
                final EditText atTime_editText = (EditText) viewDialogTime.findViewById(R.id.atTime_text);
                final EditText rangeLowerTime_editText = (EditText) viewDialogTime.findViewById(R.id.rangeLowerTime_text);
                final EditText rangeUpperTime_editText = (EditText) viewDialogTime.findViewById(R.id.rangeUpperTime_text);
                final List<EditText> timeTexts = new ArrayList<EditText>(Arrays.asList(fromTime_editText, untilTime_editText, atTime_editText, rangeLowerTime_editText, rangeUpperTime_editText));

                final int fromStringIndex = findIndexInArrayContainingString(Arrays.asList(climate.dialogOptions), "From");
                final int untilStringIndex = findIndexInArrayContainingString(Arrays.asList(climate.dialogOptions), "Until");
                final int atStringIndex = findIndexInArrayContainingString(Arrays.asList(climate.dialogOptions), "At");
                final int betweenStringIndex = findIndexInArrayContainingString(Arrays.asList(climate.dialogOptions), "Between");

                //Build last state of dialog controls before showing it

                for ( int index = 0; index < time.dialogOptions.length; index++) {
                    if (index == time.dialogOptions.length-1) {
                        String string = time.dialogOptions[index].substring(time.dialogOptions[index].indexOf(" ") + 1, time.dialogOptions[index].indexOf("a"));
                        timeTexts.get(index).setText(string.equals("HH:MM") ? "" : string);
                         string = time.dialogOptions[index].substring(time.dialogOptions[index].lastIndexOf(" ") + 1);
                        timeTexts.get(index+1).setText(string.equals("HH:MM") ? "" : string);
                        timeCheckBoxes.get(index).setChecked(time.selectedDialogOptions[index]);
                    }else{
                    String string = time.dialogOptions[index].substring(time.dialogOptions[index].lastIndexOf(" ") + 1);
                    timeTexts.get(index).setText(string.equals("HH:MM") ? "" : string);
                    timeCheckBoxes.get(index).setChecked(time.selectedDialogOptions[index]);
                    }
                }


                //Define on click Listener
                fromTime_checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (fromTime_checkBox.isChecked()) {
                            for (CheckBox checkBox : timeCheckBoxes) { // make all other checkbox disable
                                if (!checkBox.equals(fromTime_checkBox)) {
                                    checkBox.setChecked(false);
                                    checkBox.setClickable(false);
                                    checkBox.setEnabled(false);
                                }
                                for (EditText editText : timeTexts) {
                                    if (!editText.equals(fromTime_editText)) {
                                        editText.setEnabled(false);
                                        editText.setClickable(false);
                                    }
                                }
                            }
                        } else {
                            for (CheckBox checkBox : timeCheckBoxes) { // make all other checkbox disable
                                if (!checkBox.equals(fromTime_checkBox)) {
                                    checkBox.setClickable(true);
                                    checkBox.setEnabled(true);
                                }
                                for (EditText editText : timeTexts) {
                                    if (!editText.equals(fromTime_editText)) {
                                        editText.setEnabled(true);
                                        editText.setClickable(true);
                                    }
                                }
                            }

                        }
                    }
                });

                untilTime_checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (untilTime_checkBox.isChecked()) {
                            for (CheckBox checkBox : timeCheckBoxes) { // make all other checkbox disable
                                if (!checkBox.equals(untilTime_checkBox)) {
                                    checkBox.setChecked(false);
                                    checkBox.setClickable(false);
                                    checkBox.setEnabled(false);
                                }
                                for (EditText editText : timeTexts) {
                                    if (!editText.equals(untilTime_editText)) {
                                        editText.setEnabled(false);
                                        editText.setClickable(false);
                                    }
                                }
                            }
                        } else {
                            for (CheckBox checkBox : timeCheckBoxes) { // make all other checkbox disable
                                if (!checkBox.equals(untilTime_checkBox)) {
                                    checkBox.setClickable(true);
                                    checkBox.setEnabled(true);
                                }
                                for (EditText editText : timeTexts) {
                                    if (!editText.equals(untilTime_editText)) {
                                        editText.setEnabled(true);
                                        editText.setClickable(true);
                                    }
                                }
                            }

                        }
                    }
                });

                atTime_checkBox.setOnClickListener(new View.OnClickListener() {
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
                        } else {
                            for (CheckBox checkBox : timeCheckBoxes) { // make all other checkbox disable
                                if (!checkBox.equals(atTime_checkBox)) {
                                    checkBox.setClickable(true);
                                    checkBox.setEnabled(true);
                                }
                                for (EditText editText : timeTexts) {
                                    if (!editText.equals(atTime_editText)) {
                                        editText.setEnabled(true);
                                        editText.setClickable(true);
                                    }
                                }
                            }

                        }
                    }
                });

                rangeTime_checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (rangeTime_checkBox.isChecked()) {
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
                        } else {
                            for (CheckBox checkBox : timeCheckBoxes) { // make all other checkbox disable
                                if (!checkBox.equals(rangeTime_checkBox)) {
                                    checkBox.setClickable(true);
                                    checkBox.setEnabled(true);
                                }
                                for (EditText editText : timeTexts) {
                                    if (!editText.equals(rangeLowerTime_editText) && !editText.equals(rangeUpperTime_editText)) {
                                        editText.setEnabled(true);
                                        editText.setClickable(true);
                                    }
                                }
                            }

                        }
                    }
                });


                final List<LinearLayout> layouts = new ArrayList<>(Arrays.asList((LinearLayout) viewDialogTime.findViewById(R.id.fromTime_layout), (LinearLayout) viewDialogTime.findViewById(R.id.untilTime_layout),
                        (LinearLayout) viewDialogTime.findViewById(R.id.atTime_layout), (LinearLayout) viewDialogTime.findViewById(R.id.betweenTime_layout)));

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Boolean isAllChkBoxUnchecked = true;
                        for (LinearLayout linearLayout : layouts) {
                            int count = linearLayout.getChildCount();
                            CheckBox checkBox = (CheckBox) linearLayout.getChildAt(0);
                            EditText editText = (EditText) linearLayout.getChildAt(1);
                            if (count == 4) { //option of range-time
                                EditText editText2 = (EditText) linearLayout.getChildAt(3);
                                if (checkBox.isChecked()) {
                                    if (!editText.getText().toString().equals("") && !editText2.getText().toString().equals("")) {
                                        isAllChkBoxUnchecked = false;

                                        for ( int i = 0; i < time.selectedDialogOptions.length; i++) {
                                            time.selectedDialogOptions[i] = false;
                                        }
                                        for (int  i = 0; i < time.dialogOptions.length; i++) {
                                            time.dialogOptions[i] = time.dialogOptions[i].substring(0, time.dialogOptions[i].lastIndexOf(" ") + 1) + "HH:MM";
                                            if (time.dialogOptions[i].contains("Between")) {
                                                time.dialogOptions[i] = "Between HH:MM and HH:MM";
                                            }
                                        }

                                        //Update selected option
                                        final int StringIndex = findIndexInArrayContainingString(Arrays.asList(time.dialogOptions), checkBox.getText().toString());
                                        time.selectedDialogOptions[StringIndex] = true;
                                        time.dialogOptions[StringIndex] = "Between " + editText.getText().toString() +
                                                "and " + editText2.getText().toString();
                                    }
                                }
                            }
                            else if (checkBox.isChecked()) {
                                if (!editText.getText().toString().equals("")) {
                                    isAllChkBoxUnchecked = false;
                                    for (int i = 0; i < time.selectedDialogOptions.length; i++) {
                                        time.selectedDialogOptions[i] = false;
                                    }
                                    for (int i = 0; i < time.dialogOptions.length; i++) {
                                        time.dialogOptions[i] = time.dialogOptions[i].substring(0, time.dialogOptions[i].lastIndexOf(" ") + 1) + "HH:MM";
                                        if (time.dialogOptions[i].contains("Between")) {
                                            time.dialogOptions[i] = "Between HH:MM and HH:MM";
                                        }
                                    }

                                    //Update selected option
                                    final int StringIndex = findIndexInArrayContainingString(Arrays.asList(time.dialogOptions), checkBox.getText().toString());
                                    time.selectedDialogOptions[StringIndex] = true;
                                    time.dialogOptions[StringIndex] = time.dialogOptions[StringIndex].substring(0, time.dialogOptions[StringIndex].lastIndexOf(" ") + 1) + editText.getText().toString(); //Add the Time to the string
                                }
                            }
                        }
                        if (isAllChkBoxUnchecked) {
                            for (int i = 0; i < time.selectedDialogOptions.length; i++) {
                                time.selectedDialogOptions[i] = false;
                            }
                            for (int  i = 0; i < time.dialogOptions.length; i++) {
                                time.dialogOptions[i] = time.dialogOptions[i].substring(0, time.dialogOptions[i].lastIndexOf(" ") + 1) + "HH:MM";
                                if (time.dialogOptions[i].contains("Between")) {
                                    time.dialogOptions[i] = "Between HH:MM and HH:MM";
                                }
                            }
                        }
                        if (AllOptionsUnchecked(time.selectedDialogOptions)) { //if all options are unchecked, unselect the button
                            toggleButton.setChecked(false);
                            if (allBtnsUnpressed()) {
                                makeActionBtnsUnClickable(); //No input is selected - make action buttons un-clickable
                            }
                        } else {
                            toggleButton.setChecked(true); //some triggers are marked
                        }
                    }

                });
                builder.create().show();
                break;
            //endregion
        }
    }

    private void UnselectBtnOptionsAndUpdateStrings(int aboveStringIndex, int belowStringIndex) {
        climate.selectedDialogOptions[aboveStringIndex] = false;
        climate.selectedDialogOptions[belowStringIndex] = false;
        climate.dialogOptions[belowStringIndex] = climate.dialogOptions[belowStringIndex].substring(0, climate.dialogOptions[belowStringIndex].lastIndexOf(" ") + 1) + "degrees";//Add "degrees" to the end of the string
        climate.dialogOptions[aboveStringIndex] = climate.dialogOptions[aboveStringIndex].substring(0, climate.dialogOptions[aboveStringIndex].lastIndexOf(" ") + 1) + "degrees";//Add "degrees" to the end of the string
    }

    private void updateDialogControls(CheckBox tempAbove_checkBox, CheckBox tempbelow_checkBox, EditText tempBelow_editText) {
        if (tempAbove_checkBox.isChecked()) {
            disableOtherCheckBoxAndText(tempbelow_checkBox, tempBelow_editText);
        } else {
            enableOtherCheckBoxAndText(tempbelow_checkBox, tempBelow_editText);
        }
    }

    private void buildLastStateDialog(CheckBox tempAbove_checkBox, CheckBox tempbelow_checkBox, EditText tempAbove_editText, EditText tempBelow_editText, int aboveStringIndex, int belowStringIndex) {
        final String aboveTempLastWord = climate.dialogOptions[aboveStringIndex].substring(climate.dialogOptions[aboveStringIndex].lastIndexOf(" ") + 1);
        tempAbove_editText.setText(aboveTempLastWord.equals("degrees") ? "" : aboveTempLastWord);
        tempAbove_checkBox.setChecked(climate.selectedDialogOptions[aboveStringIndex]);
        tempbelow_checkBox.setChecked(climate.selectedDialogOptions[belowStringIndex]);
        final String belowTempLastWord = climate.dialogOptions[belowStringIndex].substring(climate.dialogOptions[belowStringIndex].lastIndexOf(" ") + 1);
        tempBelow_editText.setText(belowTempLastWord.equals("degrees") ? "" : belowTempLastWord);
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

    private void enableOtherCheckBoxAndText(CheckBox tempbelow_checkBox, EditText tempBelow_editText) {
        tempbelow_checkBox.setClickable(true);
        tempbelow_checkBox.setEnabled(true);
        tempBelow_editText.setEnabled(true);
        tempBelow_editText.setClickable(true);
    }

    private void disableOtherCheckBoxAndText(CheckBox tempbelow_checkBox, EditText tempBelow_editText) {
        tempbelow_checkBox.setChecked(false);
        tempbelow_checkBox.setClickable(false);
        tempbelow_checkBox.setEnabled(false); //disable the checkbox
        tempBelow_editText.setEnabled(false);
        tempBelow_editText.setClickable(false);
    }

    protected boolean AllOptionsUnchecked(boolean[] selectedLocationOptions) {
        for (boolean bool : selectedLocationOptions) {
            if (bool == true) {
                return false;
            }
        }
        return true;
    }

    protected List getSelectedButtons() {
        List<IA> ButtonsArray = new ArrayList();
        for (Input input : inputs) {
            int id = input.getToggleButtonID();
            final ToggleButton toggleButton = (ToggleButton) findViewById(id);
            if (toggleButton.isChecked()) {
                ButtonsArray.add(input);
            }
        }
        for (Action action : actions) {
            int id = action.getToggleButtonID();
            final ToggleButton toggleButton = (ToggleButton) findViewById(id);
            if (toggleButton.isChecked()) {
                ButtonsArray.add(action);
            }
        }
        return ButtonsArray;
    }

    public void makeActionBtnsClickable() {
        for (Action action : actions) {
            int id = action.getToggleButtonID();
            final ToggleButton toggleButton = (ToggleButton) findViewById(id);
            toggleButton.setClickable(true);
        }
    }

    public void makeActionBtnsUnClickable() {
        for (Action action : actions) {
            int id = action.getToggleButtonID();
            final ToggleButton toggleButton = (ToggleButton) findViewById(id);
            toggleButton.setClickable(false);
        }
    }

    public boolean allBtnsUnpressed() {
        for (Input input : inputs) {
            int id = input.getToggleButtonID();
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
        TextView editTextView = (TextView) findViewById(R.id.editTextView);
        String name = editTextView.getText().toString();

        if (name.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter a name for the scenario",
                    Toast.LENGTH_SHORT).show();
        } else {

            //Create Intent to hold the returned message for the calling activity
            Intent returnIntent = new Intent();

            String msg = "";
            List<IA> pressedBtnsList = getSelectedButtons();

            //If we want the name of the selected buttons and their options
            for (IA ia : pressedBtnsList) {
                int id = ia.getToggleButtonID();
                final ToggleButton toggleButton = (ToggleButton) findViewById(id);

                msg += toggleButton.getTag() + ":"; //Get imageButton's tag attribute
                //msg += getResources().getResourceName(imageButton.getId()).split("/")[1] + ":"; //"Input: ";
                for (int index = 0; index < ia.getSelectedDialogOptions().length; index++) {
                    if (ia.getSelectedDialogOptions()[index] == true) {
                        msg += ia.getDialogOptions()[index] + "  ";
                    }
                }
            }

            Scenario scenario = new Scenario(name, pressedBtnsList);
            returnIntent.putExtra("result", scenario);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
    }

}


