package com.example.oitzh.myapplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by oitzh on 11/11/2017.
 */

public class ScenarioInput implements Serializable {
    public List<Name> names = new ArrayList<>();
    public Trigger trigger = new Trigger();

    public enum Name {
        Location,
        Time,
        Climate,
        Motion
    }

    public static class Trigger implements Serializable {
        public void setParameter(Object o){
            if(o instanceof Time) time = (Time)o;
            if(o instanceof Location) location = (Location)o;
            if(o instanceof Climate) climate = (Climate)o;
            if(o instanceof Motion) motion = (Motion)o;
        }
        public Time time;
        public Location location;
        public Climate climate;
        public Motion motion;

        public enum Time {
            From_HHMM,
            Until_HHMM,
            At_HHMM,
            Between_HHMM_and_HHMM;

            public ScenarioTime first;
            public ScenarioTime second;

            public static String[] enumToDescriptionArray(){
                return Arrays.stream(Time.values()).map(Object::toString).map(string -> string.replace("_", " ").replace("HHMM", "HH:MM")).toArray(String[]::new);
            }

            public static Time descriptionToEnum(String str){
                String trigger =  str.split(" ")[0];
                Time t = Arrays.stream(Time.values()).filter(val-> val.name().contains(trigger)).toArray(Time[]::new)[0];
                t.first = new ScenarioTime(str.split(" ")[1]);
                t.second = str.split(" ").length > 3 ? new ScenarioTime(str.split(" ")[3]) : null;
                return t;
            }
        }

        public enum Location {
            When_Leaving,
            When_Arriving;

            public static String[] enumToDescriptionArray(){
               return Arrays.stream(ScenarioInput.Trigger.Location.values()).map(Object::toString).map(string -> string.replace('_', ' ')).toArray(String[]::new);
            }

            public static Location descriptionToEnum(String str){
                String trigger =  str.split(" ")[1];
                return Arrays.stream(Location.values()).filter(val-> val.name().contains(trigger)).toArray(Location[]::new)[0];
            }
        }

        public enum Climate {
            Above_Degrees,
            Below_Degrees;
//            Between;

            public int first;
            //public int second;

            public static String[] enumToDescriptionArray(){
                return Arrays.stream(Climate.values()).map(Object::toString).map(string -> string.replace("_", " ")).toArray(String[]::new);
            }

            public static Climate descriptionToEnum(String str){
                String trigger =  str.split(" ")[0];
                Climate c = Arrays.stream(Climate.values()).filter(val-> val.name().contains(trigger)).toArray(Climate[]::new)[0];
                c.first = Integer.parseInt(str.split(" ")[1]);
                return c;
            }
        }

        public enum Motion {
            When_Leaving,
            When_Arriving;

            public static String[] enumToDescriptionArray(){
                return Arrays.stream(ScenarioInput.Trigger.Motion.values()).map(Object::toString).map(string -> string.replace('_', ' ')).toArray(String[]::new);
            }

            public static Motion descriptionToEnum(String str){
                String trigger =  str.split(" ")[0];
                return Arrays.stream(Motion.values()).filter(val-> val.name().contains(trigger)).toArray(Motion[]::new)[0];
            }
        }
    }
}
