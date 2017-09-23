package com.example.oitzh.myapplication;

import android.widget.ImageButton;
import android.widget.ToggleButton;

/**
 * Created by oitzh on 21/09/2017.
 */

public class Input {

    public ToggleButton toggleButton;
    public String[] dialogOptions;
    public boolean[] selectedDialogOptions;

    public Input(ToggleButton toggleButton, String[] dialogOptions, boolean[] selectedDialogOptions) {
        this.toggleButton = toggleButton;
        this.dialogOptions = dialogOptions;
        this.selectedDialogOptions = selectedDialogOptions;
    }

    public Input(ToggleButton toggleButton){
        this.toggleButton = toggleButton;
        this.dialogOptions = null;
        this.selectedDialogOptions = null;
    }
}
