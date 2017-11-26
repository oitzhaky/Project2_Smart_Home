package com.example.oitzh.myapplication;

import android.renderscript.ScriptGroup;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oitzh on 25/09/2017.
 */

public class Scenario implements Serializable {

    private List<ScenarioButton> inputButtons = new ArrayList<>();
    private List<ScenarioButton> actionButtons = new ArrayList<>();
    public ScenarioInput scenarioInput = new ScenarioInput();
    public ScenarioAction scenarioAction = new ScenarioAction();
    private String scenarioName;

    public Scenario(List<ScenarioButton> inputs, List<ScenarioButton> actions) {
        this.inputButtons = inputs;
        this.actionButtons = actions;
    }

    public Scenario(String scenarioName, List<ScenarioButton> inputs, List<ScenarioButton> actions) {
        this.scenarioName = scenarioName;
        this.inputButtons = inputs;
        this.actionButtons = actions;

        for (ScenarioButton scenarioButton : inputButtons) {
                    scenarioInput.names.add(ScenarioInput.Name.valueOf(scenarioButton.getTagName()));
                    for (int i = 0; i < scenarioButton.dialogOptions.length; i++) {
                        if (scenarioButton.selectedDialogOptions[i]) {
                            Class<?> clazz = null;
                            Method method = null;
                            try {
                                clazz = Class.forName(ScenarioInput.Trigger.class.getName() + "$" + scenarioButton.getTagName());
                                method = clazz.getMethod("descriptionToEnum", String.class);
                                Object o = method.invoke(null, scenarioButton.dialogOptions[i]);
                                this.scenarioInput.trigger.setParameter(o);
                            } catch (ClassNotFoundException|NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
        }

        for (ScenarioButton scenarioButton : actionButtons) {
            scenarioAction.names.add(ScenarioAction.Name.valueOf(scenarioButton.getTagName()));
            for (int i = 0; i < scenarioButton.dialogOptions.length; i++) {
                if (scenarioButton.selectedDialogOptions[i]) {
                    Class<?> clazz = null;
                    Method method = null;
                    try {
                        clazz = Class.forName(ScenarioAction.Action.class.getName() + "$" + scenarioButton.getTagName());
                        method = clazz.getMethod("descriptionToEnum", String.class);
                        Object o = method.invoke(null, scenarioButton.dialogOptions[i]);
                        this.scenarioAction.action.setParameter(o);
                    } catch (ClassNotFoundException|NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public Scenario(Scenario scenario) {
        this.scenarioName = new String(scenario.getScenarioName());
        this.inputButtons = new ArrayList<>(scenario.getInputToggledButtonsArray());
        this.actionButtons = new ArrayList<>(scenario.getActionToggledButtonsArray());
    }

    public List<ScenarioButton> getInputToggledButtonsArray() {
        return inputButtons;
    }

    public List<ScenarioButton> getActionToggledButtonsArray() {
        return actionButtons;
    }

    public String getScenarioName() {
        return scenarioName;
    }

//    public String printString() {
//
//        String msg = "";
//
//
//        for (ScenarioButton scenarioButton : toggledButtons) {
//int id = scenarioButton.getToggleButtonID();
//    final ToggleButton toggleButton = (ToggleButton) findViewById(id);
//            msg += scenarioButton.getTagName() + ":"; //Get imageButton's tag attribute
//            //msg += getResources().getResourceName(imageButton.getId()).split("/")[1] + ":"; //"ScenarioButton: ";
//            for (int index = 0; index < scenarioButton.getSelectedDialogOptions().length; index++) {
//                if (scenarioButton.getSelectedDialogOptions()[index] == true) {
//                    msg += scenarioButton.getDialogOptions()[index] + "  ";
//                }
//            }
//        }
//
//        return msg;
//    }
}
