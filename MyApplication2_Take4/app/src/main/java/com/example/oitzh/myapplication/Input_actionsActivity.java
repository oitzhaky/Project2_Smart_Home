package com.example.oitzh.myapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
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
        inputs = new ArrayList<>(Arrays.asList(location));

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
        if (scenarioToEdit != null) { //Initialize current activity
            for (IA toggleButton : scenarioToEdit.getToggledButtonsArray()) {
                //Find buttons by name
                String currentToggleBtnName = toggleButton.getTagName();
                Iterator<Input> it = inputs.iterator(); //TODO: the same for outputs array!!
                while (it.hasNext()) {
                    Input curr = it.next();
                    if (curr.getTagName().equals(currentToggleBtnName)) {
                        curr.selectedDialogOptions = toggleButton.getSelectedDialogOptions(); //TODO:should use NEW?
                        int id = curr.getToggleButton();
                        ToggleButton toggleBtn = (ToggleButton) findViewById(id);
                        toggleBtn.setChecked(true);

                    }
                }

            }
        }

        //make sure no action btn is clickable before any input was chosen;
        //Pay attention to do it after extracting scenario if exists!
        for (Action action : actions) {
            int id = action.getToggleButton();
            ToggleButton toggleButton = (ToggleButton) findViewById(id);
            toggleButton.setClickable(false);
        }

    }


    protected void onClickFunc(View view) {
        int id = view.getId();
        final ToggleButton toggleButton = (ToggleButton) findViewById(id);
        switch (id) {
            case R.id.gpsBtn:
                //make action buttons clickable!
                //TODO: replace with a class
                makeActionBtnsClickable();

                AlertDialog.Builder builder = new AlertDialog.Builder(Input_actionsActivity.this);
                builder.setTitle("Choose Triggers");
                builder.setIcon(R.drawable.ic_event_black_48px);
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
                        //ToggleButton imageButton= (ToggleButton)findViewById(R.id.gpsBtn);
                        final AlertDialog alertDialog = (AlertDialog) dialog;
                        final ListView alertDialogList = alertDialog.getListView();

                        if (isChecked) {
                            toggleButton.setChecked(true);
                            //imageButton.setPressed(true);

                            if (location.dialogOptions[which].equals("When leaving home")) {
                                int stringIndex = Arrays.asList(location.dialogOptions).indexOf("When arriving home");
                                alertDialogList.getChildAt(stringIndex).setEnabled(false); //UI-wise: disable the other option
                                location.selectedDialogOptions[stringIndex] = false; //Update the other option as unselected so when the dialog will be opened, options will be marked correctly
                            } else {
                                int stringIndex = Arrays.asList(location.dialogOptions).indexOf("When leaving home");
                                alertDialogList.getChildAt(stringIndex).setEnabled(false);
                                location.selectedDialogOptions[stringIndex] = false;
                            }

                            Toast.makeText(Input_actionsActivity.this, "You chose " + Input_actionsActivity.this.location.dialogOptions[which], Toast.LENGTH_SHORT).show();

                        } else {
                            if (location.dialogOptions[which].equals("When leaving home")) {
                                int stringIndex = Arrays.asList(location.dialogOptions).indexOf("When arriving home");
                                alertDialogList.getChildAt(stringIndex).setEnabled(true);
                                //location.selectedDialogOptions[Arrays.asList(location.dialogOptions).indexOf("When arriving home")]=false;
                            } else {
                                int stringIndex = Arrays.asList(location.dialogOptions).indexOf("When leaving home");
                                alertDialogList.getChildAt(stringIndex).setEnabled(true);
                                //location.selectedDialogOptions[Arrays.asList(location.dialogOptions).indexOf("When leaving home")]=false;
                            }

                            if (AllOptionsUnchecked(location.selectedDialogOptions)) { //if all options are unchecked, unselect the Togglebutton
                                toggleButton.setChecked(false);
                                //imageButton.setPressed(false);
                            }
                            Toast.makeText(Input_actionsActivity.this, "You UN-chose " + Input_actionsActivity.this.location.dialogOptions[which], Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builder.create().show();
                break;

            case R.id.climateBtn:
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

        //Create Intent to hold the returned message for the calling activity
        Intent returnIntent = new Intent();

        String msg = "";
        List<IA> pressedBtnsList = getSelectedButtons();
        //List<String> buttonsNameList = getSelectedButtonsName(pressedBtnsList);
        Scenario scenario = new Scenario(pressedBtnsList);


        for (IA ia : pressedBtnsList) {
            int id = ia.getToggleButton();
            final ToggleButton toggleButton = (ToggleButton) findViewById(id);

            msg += toggleButton.getTag() + ":"; //Get imageButton's tag attribute
            //msg += getResources().getResourceName(imageButton.getId()).split("/")[1] + ":"; //"Input: ";
            for (int index = 0; index < ia.getSelectedDialogOptions().length; index++) {
                if (ia.getSelectedDialogOptions()[index] == true) {
                    msg += ia.getDialogOptions()[index] + "  ";
                }
            }
        }


        returnIntent.putExtra("result", scenario);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    /*
    private List<String> getSelectedButtonsName(List<IA> pressedBtns) {
        List<String> buttonsName = new ArrayList<>();
        for (IA ia : pressedBtns) {
            int id = ia.getToggleButton();
            ToggleButton toggleButton = (ToggleButton)findViewById(id);
            buttonsName.add((String)toggleButton.getTag()); //Get imageButton's tag attribute
        }
        return buttonsName;

    }
    */

    protected List getSelectedButtons() {
        List<IA> ButtonsArray = new ArrayList();
        for (Input input : inputs) {
            int id = input.getToggleButton();
            final ToggleButton toggleButton = (ToggleButton) findViewById(id);
            if (toggleButton.isChecked()) {
                ButtonsArray.add(input);
            }
        }
        for (Action action : actions) {
            int id = action.getToggleButton();
            final ToggleButton toggleButton = (ToggleButton) findViewById(id);
            if (toggleButton.isChecked()) {
                ButtonsArray.add(action);
            }
        }
        return ButtonsArray;
    }

    public void makeActionBtnsClickable() {
        for (Action action : actions) {
            int id = action.getToggleButton();
            final ToggleButton toggleButton = (ToggleButton) findViewById(id);
            toggleButton.setClickable(true);
        }
    }

    public void makeActionBtnsUnClickable() {
        for (Action action : actions) {
            int id = action.getToggleButton();
            final ToggleButton toggleButton = (ToggleButton) findViewById(id);
            toggleButton.setClickable(false);
        }
    }

    public boolean allBtnsUnpressed() {
        for (Input input : inputs) {
            int id = input.getToggleButton();
            final ToggleButton toggleButton = (ToggleButton) findViewById(id);
            if (toggleButton.isChecked()) {
                return false;
            }
        }
        return true;
    }

}


