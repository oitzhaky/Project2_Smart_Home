package com.example.oitzh.myapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Input_actionsActivity extends AppCompatActivity {

    String[] locationOptions = {"When leaving home","When arriving home"};
    boolean[] selectedLocationOptions = {false,false};
    final int NUMBER_OF_INPUTS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_actions);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Set Toolbar title
        ab.setTitle("Configure");
        ab.setDisplayHomeAsUpEnabled(true);

    }

    protected void onClickFunc(View view) {
        int id = view.getId();
        Intent intent;
        switch (id) {
            case R.id.quickActionBtn:
                break;

            case R.id.gpsBtn:
                //make button selected
                ImageButton imageButton= (ImageButton)view;
                imageButton.setSelected(!imageButton.isSelected());

                //open dialog window
                AlertDialog.Builder builder = new AlertDialog.Builder(Input_actionsActivity.this);
                builder.setMultiChoiceItems(locationOptions, selectedLocationOptions, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            selectedLocationOptions[which] = true;
                            Toast.makeText(Input_actionsActivity.this, "You chose " + Input_actionsActivity.this.locationOptions[which], Toast.LENGTH_SHORT).show();
                        }else{
                            selectedLocationOptions[which] = false;
                            Toast.makeText(Input_actionsActivity.this, "You UN-chose " + Input_actionsActivity.this.locationOptions[which], Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                builder.create().show();


               /* Intent returnIntent = new Intent();
                TextView textView = (TextView) findViewById(R.id.gpsText);
                String message = textView.getText().toString();
                returnIntent.putExtra("result",message);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();*/

                break;

            case R.id.climateBtn:

                break;

            case R.id.lightsBtn:

                break;

        }


    }

    protected  void submitData(View view){
        //Create Intent to hold the returned message for the calling activity
        Intent returnIntent = new Intent();

        String msg = "";
        List<ImageButton> list = getSelectedImageButtons();
        for(ImageButton imageButton: list) {
            msg += imageButton.getTag() + ":"; //Get tag attribute
            //msg += getResources().getResourceName(imageButton.getId()).split("/")[1] + ":"; //"Location: ";
            if (selectedLocationOptions[0]) {
                msg += locationOptions[0] + "  ";
            }
            if (selectedLocationOptions[1]) {
                msg += locationOptions[1];
            }
        }

        returnIntent.putExtra("result",msg);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    protected List getSelectedImageButtons(){
        List<ImageButton> imageButtonsArray = new ArrayList();
        //Should iterate over all buttons!
        ImageButton imageButton = (ImageButton) findViewById(R.id.gpsBtn);
        if (imageButton.isSelected()){
            imageButtonsArray.add(imageButton);
        }
        return imageButtonsArray;
    }
}


