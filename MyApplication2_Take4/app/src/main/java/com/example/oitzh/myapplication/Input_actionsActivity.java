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


        location = new Input((ToggleButton) findViewById(R.id.gpsBtn), new String[]{"When leaving home", "When arriving home"}, new boolean[]{false, false});
        inputs = new ArrayList<>(Arrays.asList(location));

        lights = new Action((ToggleButton) findViewById(R.id.lightsBtn));
        ac = new Action((ToggleButton) findViewById(R.id.acBtn));
        tv = new Action((ToggleButton) findViewById(R.id.tvBtn));
        actions = new ArrayList<>(Arrays.asList(lights, tv, ac));

        //make sure no action btn is clickable before any input was chosen;
        for (Action action : actions) {
            action.toggleButton.setClickable(false);
        }


        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Set Toolbar title
        ab.setTitle("Configure");
        ab.setDisplayHomeAsUpEnabled(true);


    }

    protected void onClickFunc(View view) {
        int id = view.getId();
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
                            location.toggleButton.setChecked(false);
                            if (allBtnsUnpressed()) {
                                makeActionBtnsUnClickable(); //No input is selected - make action buttons un-clickable
                            }
                        } else {
                            location.toggleButton.setChecked(true); //some triggers are marked
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
                            location.toggleButton.setChecked(true);
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
                                location.toggleButton.setChecked(false);
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
        List<IO> PressedBtns = getSelectedButtons();
        for (IO io : PressedBtns) {
            msg += io.getToggleButton().getTag() + ":"; //Get imageButton's tag attribute
            //msg += getResources().getResourceName(imageButton.getId()).split("/")[1] + ":"; //"Input: ";
            for (int index = 0; index < io.getSelectedDialogOptions().length; index++) {
                if (io.getSelectedDialogOptions()[index] == true) {
                    msg += io.getDialogOptions()[index] + "  ";
                }
            }
        }

        returnIntent.putExtra("result", msg);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    protected List getSelectedButtons() {
        List<IO> ButtonsArray = new ArrayList();
        for (Input input : inputs) {
            if (input.toggleButton.isChecked()) {
                ButtonsArray.add(location);
            }
        }
        for (Action action : actions) {
            if (action.toggleButton.isChecked()) {
                ButtonsArray.add(location);
            }
        }
        return ButtonsArray;
    }

    public void makeActionBtnsClickable() {
        for (Action action : actions) {
            action.toggleButton.setClickable(true);
        }
    }

    public void makeActionBtnsUnClickable() {
        for (Action action : actions) {
            action.toggleButton.setClickable(false);
        }
    }

    public boolean allBtnsUnpressed() {
        for (Input input : inputs) {
            if (input.toggleButton.isChecked()) {
                return false;
            }
        }
        return true;
    }

}


