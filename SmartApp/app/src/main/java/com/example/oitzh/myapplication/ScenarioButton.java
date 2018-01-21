package com.example.oitzh.myapplication;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by oitzh on 21/09/2017.
 */

@Root
public class ScenarioButton implements Serializable {

    @Attribute
    public int toggleButtonID;

    @Attribute
    public String tagName;

    @Attribute
    public String selectedSpinnerItem;

    @Attribute
    public int spinnerId;

    @ElementArray
    public String[] dialogOptions;

    @ElementArray
    public boolean[] selectedDialogOptions;

    public ScenarioButton() {

    }

    public ScenarioButton(int toggleButtonID, String[] dialogOptions, boolean[] selectedDialogOptions, String tagName, int spinnerId) {
        this.toggleButtonID = toggleButtonID;
        this.dialogOptions = dialogOptions;
        this.selectedDialogOptions = selectedDialogOptions;
        this.tagName = tagName;
        this.spinnerId = spinnerId;
    }

    public ScenarioButton(int toggleButtonID) {
        this.toggleButtonID = toggleButtonID;
        this.dialogOptions = null;
        this.selectedDialogOptions = null;
    }

    public int getToggleButtonID() {
        return this.toggleButtonID;
    }

    public String[] getDialogOptions() {
        return this.dialogOptions;
    }

    public boolean[] getSelectedDialogOptions() {
        return this.selectedDialogOptions;
    }

    public String getTagName() {
        return tagName;
    }

}
