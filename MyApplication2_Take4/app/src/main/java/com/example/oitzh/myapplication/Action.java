package com.example.oitzh.myapplication;

import android.widget.ToggleButton;

import java.io.Serializable;

/**
 * Created by oitzh on 23/09/2017.
 */

public class Action extends Input implements Serializable {
    public Action(int toggleButton, String[] dialogOptions, boolean[] selectedDialogOptions, String tagName) {
        super(toggleButton, dialogOptions, selectedDialogOptions,tagName);
    }

    public Action(int toggleButton) {
        super(toggleButton);
    }
}
