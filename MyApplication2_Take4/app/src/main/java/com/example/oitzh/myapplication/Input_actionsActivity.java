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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static com.example.oitzh.myapplication.R.id.editTextView;
import static com.example.oitzh.myapplication.R.id.tempAbove_checkBox;
import static com.example.oitzh.myapplication.R.id.tempAbove_text;
import static com.example.oitzh.myapplication.R.id.tempBelow_checkBox;


public class Input_actionsActivity extends AppCompatActivity {

    public List<Action> actions;
    public List<Input> inputs;
    Input location;
    Input climate;
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


        location = new Input((R.id.gpsBtn), new String[]{"When leaving home", "When arriving home"}, new boolean[]{false, false}, (String) findViewById(R.id.gpsBtn).getTag());
        climate = new Input((R.id.climateBtn), new String[]{"When Temp is above degrees", "When Temp is below degrees", "When Temp is above degrees and below degrees"}, new boolean[]{false, false, false}, (String) findViewById(R.id.climateBtn).getTag());
        inputs = new ArrayList<>(Arrays.asList(location, climate));

        lights = new Action(R.id.lightsBtn);
        ac = new Action(R.id.acBtn);
        tv = new Action(R.id.tvBtn);
        actions = new ArrayList<>(Arrays.asList(lights, tv, ac));

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Set Toolbar title
        ab.setTitle("Configure");
        ab.setDisplayHomeAsUpEnabled(true);

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
                            toggleButton.setChecked(true); //make the main button checked

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
                            } else {
                                int stringIndex = Arrays.asList(location.dialogOptions).indexOf("When leaving home");
                                alertDialogList.getChildAt(stringIndex).setEnabled(true);
                            }

                            if (AllOptionsUnchecked(location.selectedDialogOptions)) { //if all options are unchecked, unselect the Togglebutton
                                toggleButton.setChecked(false);

                            }
                            //Toast.makeText(Input_actionsActivity.this, "You UN-chose " + Input_actionsActivity.this.location.dialogOptions[which], Toast.LENGTH_SHORT).show(); //print comment
                        }
                    }
                });
                builder.create().show();
                break;
            //endregion
            case R.id.climateBtn:

                final View viewDialog = inflater.inflate(R.layout.climate_alert, null);
                builder.setView(viewDialog);
                final CheckBox tempAbove_checkBox = (CheckBox) viewDialog.findViewById(R.id.tempAbove_checkBox);
                final CheckBox tempbelow_checkBox = (CheckBox) viewDialog.findViewById(R.id.tempBelow_checkBox); // what id do you have?
                final EditText tempAbove_editText = (EditText) viewDialog.findViewById(R.id.tempAbove_text);
                final EditText tempBelow_editText = (EditText) viewDialog.findViewById(R.id.tempBelow_text);
                final int aboveStringIndex = findIndexInArray(Arrays.asList(climate.dialogOptions), "above");
                final int belowStringIndex = findIndexInArray(Arrays.asList(climate.dialogOptions), "below");

                //Build last state of dialog controls before showing it
                final String aboveTempLastWord = climate.dialogOptions[aboveStringIndex].substring(climate.dialogOptions[aboveStringIndex].lastIndexOf(" ") + 1);
                tempAbove_editText.setText(aboveTempLastWord.equals("degrees") ? "" : aboveTempLastWord);
                tempAbove_checkBox.setChecked(climate.selectedDialogOptions[aboveStringIndex]);
                tempbelow_checkBox.setChecked(climate.selectedDialogOptions[belowStringIndex]);
                final String belowTempLastWord = climate.dialogOptions[belowStringIndex].substring(climate.dialogOptions[belowStringIndex].lastIndexOf(" ") + 1);
                tempBelow_editText.setText(belowTempLastWord.equals("degrees") ? "" : belowTempLastWord);


                tempAbove_checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (tempAbove_checkBox.isChecked()) {
                            tempbelow_checkBox.setChecked(false);
                            tempbelow_checkBox.setClickable(false);
                            tempbelow_checkBox.setEnabled(false); //disable the checkbox
                            tempBelow_editText.setEnabled(false);
                            tempBelow_editText.setClickable(false);
                        } else {
                            tempbelow_checkBox.setClickable(true);
                            tempbelow_checkBox.setEnabled(true);
                            tempBelow_editText.setEnabled(true);
                            tempBelow_editText.setClickable(true);
                        }
                    }
                });
                tempbelow_checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (tempbelow_checkBox.isChecked()) {
                            tempAbove_checkBox.setChecked(false);
                            tempAbove_checkBox.setClickable(false);
                            tempAbove_checkBox.setEnabled(false);
                            tempAbove_editText.setEnabled(false);
                            tempAbove_editText.setClickable(false);
                        } else {
                            tempAbove_checkBox.setClickable(true);
                            tempAbove_checkBox.setEnabled(true);
                            tempAbove_editText.setEnabled(true);
                            tempAbove_editText.setClickable(true);
                        }
                    }
                });


                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (tempAbove_checkBox.isChecked()) {
                            if (!tempAbove_editText.getText().toString().equals("")) {
                                climate.selectedDialogOptions[aboveStringIndex] = true;
                                climate.dialogOptions[aboveStringIndex] = climate.dialogOptions[aboveStringIndex].substring(0, climate.dialogOptions[aboveStringIndex].lastIndexOf(" ")+1) + tempAbove_editText.getText().toString();
                                climate.dialogOptions[belowStringIndex] = climate.dialogOptions[belowStringIndex].substring(0, climate.dialogOptions[belowStringIndex].lastIndexOf(" ")+1) + "degrees";
                                climate.selectedDialogOptions[belowStringIndex] = false;
                            } else {
                                climate.selectedDialogOptions[aboveStringIndex] = false;
                                climate.selectedDialogOptions[belowStringIndex] = false;
                            }
                        } else if (tempbelow_checkBox.isChecked()) {
                            if (!tempBelow_editText.getText().toString().equals("")) {//temp below is checked
                                climate.selectedDialogOptions[belowStringIndex] = true;
                                climate.dialogOptions[belowStringIndex] = climate.dialogOptions[belowStringIndex].substring(0, climate.dialogOptions[belowStringIndex].lastIndexOf(" ")+1) + tempBelow_editText.getText().toString();
                                climate.dialogOptions[aboveStringIndex] = climate.dialogOptions[aboveStringIndex].substring(0, climate.dialogOptions[aboveStringIndex].lastIndexOf(" ")+1) + "degrees";
                                climate.selectedDialogOptions[aboveStringIndex] = false;
                            } else {
                                climate.selectedDialogOptions[belowStringIndex] = false;
                                climate.selectedDialogOptions[aboveStringIndex] = false;
                            }
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
//                builder.setMultiChoiceItems(climate.dialogOptions, climate.selectedDialogOptions, new DialogInterface.OnMultiChoiceClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//                        final AlertDialog alertDialog = (AlertDialog) dialog;
//                        final ListView alertDialogList = alertDialog.getListView();
//
//                        if (isChecked) {
//                            toggleButton.setChecked(true); //make the main button checked
//
//                            if (climate.dialogOptions[which].contains("above")) {
//                                int stringIndex = findIndexInArray(Arrays.asList(climate.dialogOptions),"below");
//                                alertDialogList.getChildAt(stringIndex).setEnabled(false); //UI-wise: disable the other option
//                                climate.selectedDialogOptions[stringIndex] = false; //Update the other option as unselected so when the dialog will be opened, options will be marked correctly
//                            } else {
//                                int stringIndex = findIndexInArray(Arrays.asList(climate.dialogOptions),"above");
//                                alertDialogList.getChildAt(stringIndex).setEnabled(false);
//                                climate.selectedDialogOptions[stringIndex] = false;
//                            }
//                            //Toast.makeText(Input_actionsActivity.this, "You chose " + Input_actionsActivity.this.location.dialogOptions[which], Toast.LENGTH_SHORT).show(); //print comment
//
//                        } else {
//                            if (climate.dialogOptions[which].equals("above")) {
//                                int stringIndex = findIndexInArray(Arrays.asList(climate.dialogOptions),"below");
//                                alertDialogList.getChildAt(stringIndex).setEnabled(true);
//                            } else {
//                                int stringIndex = findIndexInArray(Arrays.asList(climate.dialogOptions),"above");;
//                                alertDialogList.getChildAt(stringIndex).setEnabled(true);
//                            }
//
//                            if (AllOptionsUnchecked(climate.selectedDialogOptions)) { //if all options are unchecked, unselect the Togglebutton
//                                toggleButton.setChecked(false);
//
//                            }
//                            //Toast.makeText(Input_actionsActivity.this, "You UN-chose " + Input_actionsActivity.this.location.dialogOptions[which], Toast.LENGTH_SHORT).show(); //print comment
//                        }
//                    }
//                });

                builder.create().show();
                break;

            case R.id.timeBtn:
                break;

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

    public int findIndexInArray(List<String> arr, String partLine) {
        for (int i = 0; i < arr.size(); i++) {
            if (arr.get(i).contains(partLine)) {
                return i;
            }
        }
        return 0;
    }


}


