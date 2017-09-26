package com.example.oitzh.myapplication;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ToggleButton;

import java.io.Serializable;

/**
 * Created by oitzh on 21/09/2017.
 */

public class Input implements IA, Serializable {

    public int toggleButton;
    public String[] dialogOptions;
    public boolean[] selectedDialogOptions;


    public String tagName;

    public Input(int toggleButton, String[] dialogOptions, boolean[] selectedDialogOptions,String tagName) {
        this.toggleButton = toggleButton;
        this.dialogOptions = dialogOptions;
        this.selectedDialogOptions = selectedDialogOptions;
        this.tagName = tagName;
    }

    public Input(int toggleButton){
        this.toggleButton = toggleButton;
        this.dialogOptions = null;
        this.selectedDialogOptions = null;
    }

    @Override
    public int getToggleButton() {
        return this.toggleButton;
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
