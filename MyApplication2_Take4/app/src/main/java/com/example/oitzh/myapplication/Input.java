package com.example.oitzh.myapplication;

import java.io.Serializable;

/**
 * Created by oitzh on 21/09/2017.
 */

public class Input implements IA, Serializable {

    public int toggleButtonID;
    public String[] dialogOptions;
    public boolean[] selectedDialogOptions;


    public String tagName;

    public Input(int toggleButtonID, String[] dialogOptions, boolean[] selectedDialogOptions, String tagName) {
        this.toggleButtonID = toggleButtonID;
        this.dialogOptions = dialogOptions;
        this.selectedDialogOptions = selectedDialogOptions;
        this.tagName = tagName;
    }

    public Input(int toggleButtonID){
        this.toggleButtonID = toggleButtonID;
        this.dialogOptions = null;
        this.selectedDialogOptions = null;
    }

    public int getToggleButtonID() {
        return this.toggleButtonID;
    }

    @Override
    public String[] getDialogOptions() {
        return this.dialogOptions;
    }

    @Override
    public boolean[] getSelectedDialogOptions() {
        return this.selectedDialogOptions;
    }

    public String getTagName() {
        return tagName;
    }
}
