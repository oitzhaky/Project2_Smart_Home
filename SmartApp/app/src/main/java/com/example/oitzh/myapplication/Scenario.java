package com.example.oitzh.myapplication;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oitzh on 25/09/2017.
 */

@Root
public class Scenario implements Serializable {

    public ScenarioInput scenarioInput = new ScenarioInput();
    public ScenarioAction scenarioAction = new ScenarioAction();
    @Attribute
    private String scenarioName;
    @ElementList
    private List<ScenarioButton> inputButtons = new ArrayList<>();
    @ElementList
    private List<ScenarioButton> actionButtons = new ArrayList<>();

    public Scenario() {
    }

    public Scenario(String name) {
        this.scenarioName = name;
    }

    public Scenario(List<ScenarioButton> inputs, List<ScenarioButton> actions) {
        this.inputButtons = inputs;
        this.actionButtons = actions;
    }

    public Scenario(String scenarioName, List<ScenarioButton> inputs, List<ScenarioButton> actions) {
        this.scenarioName = scenarioName;
        this.inputButtons = inputs;
        this.actionButtons = actions;

        for (ScenarioButton scenarioButton : inputButtons) {
            ScenarioInput.Name sensorType = ScenarioInput.Name.valueOf(scenarioButton.getTagName());
            scenarioInput.names.add(sensorType);
            scenarioInput.sensorsInfo.put(sensorType, scenarioButton.selectedSpinnerItem);
            for (int i = 0; i < scenarioButton.dialogOptions.length; i++) {
                if (scenarioButton.selectedDialogOptions[i]) {
                    Class<?> clazz = null;
                    Method method = null;
                    try {
                        clazz = Class.forName(ScenarioInput.Trigger.class.getName() + "$" + scenarioButton.getTagName());
                        method = clazz.getMethod("descriptionToEnum", String.class);
                        Object o = method.invoke(null, scenarioButton.dialogOptions[i]);
                        this.scenarioInput.trigger.setParameter(o);
                    } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        for (ScenarioButton scenarioButton : actionButtons) {
            ScenarioAction.Name sensorType = ScenarioAction.Name.valueOf(scenarioButton.getTagName());
            scenarioAction.names.add(sensorType);
            scenarioAction.sensorsInfo.put(sensorType, scenarioButton.selectedSpinnerItem);
            scenarioAction.names.add(sensorType);
            for (int i = 0; i < scenarioButton.dialogOptions.length; i++) {
                if (scenarioButton.selectedDialogOptions[i]) {
                    Class<?> clazz = null;
                    Method method = null;
                    try {
                        clazz = Class.forName(ScenarioAction.Action.class.getName() + "$" + scenarioButton.getTagName());
                        method = clazz.getMethod("descriptionToEnum", String.class);
                        Object o = method.invoke(null, scenarioButton.dialogOptions[i]);
                        this.scenarioAction.action.setParameter(o);
                    } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
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

    public void setScenarioName(String newScenarioName) {
        this.scenarioName = newScenarioName;
    }

    public void setInputButtons(List<ScenarioButton> inputButtons) {
        this.inputButtons = inputButtons;
    }

    public void setActionButtons(List<ScenarioButton> actionButtons) {
        this.actionButtons = actionButtons;
    }

}
