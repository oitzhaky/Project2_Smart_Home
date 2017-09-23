package com.example.oitzh.myapplication;

import android.widget.ToggleButton;

/**
 * Created by oitzh on 23/09/2017.
 */

public class Output extends Input {
    public Output(ToggleButton toggleButton, String[] dialogOptions, boolean[] selectedDialogOptions) {
        super(toggleButton, dialogOptions, selectedDialogOptions);
    }

    public Output(ToggleButton toggleButton) {
        super(toggleButton);
    }
}
