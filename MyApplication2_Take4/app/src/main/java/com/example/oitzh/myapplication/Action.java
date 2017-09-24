package com.example.oitzh.myapplication;

import android.widget.ToggleButton;

/**
 * Created by oitzh on 23/09/2017.
 */

public class Action extends Input {
    public Action(ToggleButton toggleButton, String[] dialogOptions, boolean[] selectedDialogOptions) {
        super(toggleButton, dialogOptions, selectedDialogOptions);
    }

    public Action(ToggleButton toggleButton) {
        super(toggleButton);
    }
}
