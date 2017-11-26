package com.example.oitzh.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oitzh on 24/09/2017.
 */

public class Event implements Parcelable {
    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
    private List<ScenarioButton> toggledButtons = new ArrayList<>();

    public Event(List<ScenarioButton> list) {
        toggledButtons = list;
    }

    private Event(Parcel in) {
        //in.readList(toggledButtons,ElementType.class.getClassLoader());
        in.readList(toggledButtons, null);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeList(toggledButtons);
    }
/*
    public String printString() {
        String msg = "";
        for (IA ia : toggledButtons) {
            msg += ia.getToggleButtonID().getTag() + ":"; //Get imageButton's tag attribute
            //msg += getResources().getResourceName(imageButton.getId()).split("/")[1] + ":"; //"ScenarioButton: ";
            for (int index = 0; index < ia.getSelectedDialogOptions().length; index++) {
                if (ia.getSelectedDialogOptions()[index] == true) {
                    msg += ia.getDialogOptions()[index] + "  ";
                }
            }
        }
        return msg;
    }
    */
}
