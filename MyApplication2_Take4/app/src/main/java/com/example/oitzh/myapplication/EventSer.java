package com.example.oitzh.myapplication;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oitzh on 25/09/2017.
 */

public class EventSer implements Serializable {

    private List<IA> toggledButtons = new ArrayList<>();

    public EventSer(List<IA> list) {
        toggledButtons = list;
    }

    public String printString() {

        String msg = "";


        for (IA ia : toggledButtons) {
            msg += ia.getTagName() + ":"; //Get imageButton's tag attribute
            //msg += getResources().getResourceName(imageButton.getId()).split("/")[1] + ":"; //"Input: ";
            for (int index = 0; index < ia.getSelectedDialogOptions().length; index++) {
                if (ia.getSelectedDialogOptions()[index] == true) {
                    msg += ia.getDialogOptions()[index] + "  ";
                }
            }
        }

        return msg;
    }
}
