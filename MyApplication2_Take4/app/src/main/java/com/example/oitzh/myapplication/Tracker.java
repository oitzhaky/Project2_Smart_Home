package com.example.oitzh.myapplication;

import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oitzh on 10/11/2017.
 */

public class Tracker {
    List<Tuple<ScenarioTime,ScenarioButton>> fromArray = new ArrayList<>();
    List<Tuple<ScenarioTime,ScenarioButton>> untilArray = new ArrayList<>();
    String currentTime;

    public Tracker() {
        DateFormat df = new SimpleDateFormat("HH:mm");
        df.setTimeZone(TimeZone.getTimeZone("GMT+2:00"));
        currentTime = df.format(Calendar.getInstance(TimeZone.getTimeZone("GMT+2:00")).getTime());
    }

    public void updateTimeTracker(Scenario scenario){
        List<ScenarioButton> toggleButtons = scenario.getInputToggledButtonsArray();
        for(ScenarioButton button: toggleButtons){
            if(button.getToggleButtonID()== R.id.timeBtn){
                boolean[] selectedDialogOptions = button.getSelectedDialogOptions();
                String[] dialogOptions = button.getDialogOptions();
                int length = selectedDialogOptions.length;
                for(int i=0; i< length; i++){
                    if(selectedDialogOptions[i]){
                        if (dialogOptions[i].contains("Between")) {
                            String fromTime = dialogOptions[i].substring(dialogOptions[i].indexOf(" ") + 1, dialogOptions[i].indexOf("a"));
                            String untilTime = dialogOptions[i].substring(dialogOptions[i].lastIndexOf(" ") + 1);
                            fromArray.add(new Tuple<>(new ScenarioTime(fromTime),scenario.getActionToggledButtonsArray()));
                            untilArray.add(new Tuple<>(new ScenarioTime(untilTime),scenario.getActionToggledButtonsArray()));

                        } else {
                            String time = dialogOptions[i].substring(dialogOptions[i].lastIndexOf(" ") + 1);
                            if(dialogOptions[i].contains("From")){
                                fromArray.add(new Tuple<>(new ScenarioTime(time),scenario.getActionToggledButtonsArray()));
                            }else{ //Until
                                untilArray.add(new Tuple<>(new ScenarioTime(time),scenario.getActionToggledButtonsArray()));
                                fromArray.add(new Tuple<>(new ScenarioTime(currentTime),scenario.getActionToggledButtonsArray()));
                            }
                        }

                    }
                }
            }
        }
    }

    public void checkTime(){


        for (Tuple tuple:fromArray){
           if(tuple.time.equals(new ScenarioTime(currentTime))){
                //publish to aws
               for(Object scenarioButton: tuple.outputs){
                   List<String> awsString = getAwsString((ScenarioButton) scenarioButton,"FROM");
                    //publish string
               }
            }
        }
        for (Tuple tuple:untilArray){
            if(tuple.time.equals(new ScenarioTime(currentTime))){
                //publish to aws
                for(Object scenarioButton: tuple.outputs){
                    List<String> awsString = getAwsString((ScenarioButton) scenarioButton,"UNTIL");
                    //publish string
                }
            }
        }
    }

    public class Tuple<Time,ScenarioButton>{
        Time time;
        List<ScenarioButton> outputs = new ArrayList<>();

        Tuple(Time time, List<ScenarioButton> outputs){
            this.time=time;
            this.outputs=outputs;
        }
    }

    private List<String> getAwsString(ScenarioButton scenarioButton,String string){
        List<String> stringArray=new ArrayList<>();
        String[] dialogOptions = scenarioButton.getDialogOptions();
        int id = scenarioButton.toggleButtonID;
            if ( id == R.id.lightsBtn) {
                if(string.equals("FROM")){
                    for(int i=0; i< scenarioButton.selectedDialogOptions.length;i++){
                        if(scenarioButton.selectedDialogOptions[i]){
                            stringArray.add(scenarioButton.getTagName()+ "/" + dialogOptions[i].split(" ")[1] + "--" + dialogOptions[i].substring(dialogOptions[i].lastIndexOf(" ") + 1) );
                        }
                    }
                }else{//Until
                    for(int i=0; i< scenarioButton.selectedDialogOptions.length;i++){
                        if(scenarioButton.selectedDialogOptions[i]){
                            stringArray.add(scenarioButton.getTagName()+ "/" + dialogOptions[(i+2)%4].split(" ")[1] +"--"+ dialogOptions[(i+2)%4].substring(dialogOptions[(i+2)%4].lastIndexOf(" ") + 1) );
                        }
                    }
                }
            }else{
                if(string.equals("FROM")){
                    for(int i=0; i< scenarioButton.selectedDialogOptions.length;i++) {
                        stringArray.add(scenarioButton.getTagName() + "--"  + dialogOptions[i].substring(dialogOptions[i].lastIndexOf(" ") + 1));
                    }
                }else{//until
                    for(int i=0; i< scenarioButton.selectedDialogOptions.length;i++) {
                        stringArray.add(scenarioButton.getTagName() + "--" + dialogOptions[i+1].substring(dialogOptions[i+1].lastIndexOf(" ") + 1));
                    }
                }
            }
         return stringArray;
    }
}
