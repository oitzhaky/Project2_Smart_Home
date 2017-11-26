package com.example.oitzh.myapplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by oitzh on 11/11/2017.
 */

public class ScenarioAction implements Serializable {
    List<Name> names = new ArrayList<>();
    Action action = new Action();

    public enum Name {
        Light,
        AC,
        TV,
        Boiler,
        Security
    }

    public static class Action implements Serializable {
        public void setParameter(Object o){
            if(o instanceof Light) light = (Light)o;
            if(o instanceof AC) ac = (AC)o;
            if(o instanceof TV) tv = (TV)o;
            if(o instanceof Boiler) boiler = (Boiler)o;
            if(o instanceof Security) security = (Security)o;
        }

        public Light light;
        public AC ac;
        public TV tv;
        public Boiler boiler;
        public Security security;

        public enum Light{
            On,
            Off;

            public static String[] enumToDescriptionArray(){
                return Arrays.stream(ScenarioAction.Action.Light.values()).map(val -> val.getClass().toString().substring(val.getClass().toString().lastIndexOf("$") + 1)+ " " + val.name()).toArray(String[]::new);
            }

            public static Light descriptionToEnum(String str){
                String trigger =  str.split(" ")[1];
                return Arrays.stream(Light.values()).filter(val-> val.name().contains(trigger)).toArray(Light[]::new)[0];
            }
        }
        public enum AC{
            On,
            Off;

            public static String[] enumToDescriptionArray(){
                return Arrays.stream(ScenarioAction.Action.AC.values()).map(val -> val.getClass().toString().substring(val.getClass().toString().lastIndexOf("$") + 1)+ " " + val.name()).toArray(String[]::new);
            }

            public static AC descriptionToEnum(String str){
                String trigger =  str.split(" ")[1];
                return Arrays.stream(AC.values()).filter(val-> val.name().contains(trigger)).toArray(AC[]::new)[0];
            }
        }
        public enum TV{
            On,
            Off;

            public static String[] enumToDescriptionArray(){
                return Arrays.stream(ScenarioAction.Action.TV.values()).map(val -> val.getClass().toString().substring(val.getClass().toString().lastIndexOf("$") + 1)+ " " + val.name()).toArray(String[]::new);
            }

            public static TV descriptionToEnum(String str){
                String trigger =  str.split(" ")[1];
                return Arrays.stream(TV.values()).filter(val-> val.name().contains(trigger)).toArray(TV[]::new)[0];
            }
        }
        public enum Boiler{
            On,
            Off;

            public static String[] enumToDescriptionArray(){
                return Arrays.stream(ScenarioAction.Action.Boiler.values()).map(val -> val.getClass().toString().substring(val.getClass().toString().lastIndexOf("$") + 1)+ " " + val.name()).toArray(String[]::new);
            }

            public static Boiler descriptionToEnum(String str){
                String trigger =  str.split(" ")[1];
                return Arrays.stream(Boiler.values()).filter(val-> val.name().contains(trigger)).toArray(Boiler[]::new)[0];
            }
        }
        public enum Security{
            On,
            Off;

            public static String[] enumToDescriptionArray(){
                return Arrays.stream(ScenarioAction.Action.Security.values()).map(val -> val.getClass().toString().substring(val.getClass().toString().lastIndexOf("$") + 1)+ " " + val.name()).toArray(String[]::new);
            }

            public static Security descriptionToEnum(String str){
                String trigger =  str.split(" ")[1];
                return Arrays.stream(Security.values()).filter(val-> val.name().contains(trigger)).toArray(Security[]::new)[0];
            }
        }
    }
}
