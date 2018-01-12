package com.example.oitzh.myapplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by oitzh on 11/11/2017.
 */

public class ScenarioAction implements Serializable {
    public HashMap<ScenarioAction.Name, String> sensorsInfo = new HashMap<>();
    List<Name> names = new ArrayList<>();
    Action action = new Action();

    public enum Name {
        Light,
        Ac,
        Tv,
        Boiler,
        Security;

        public static Object stringToActionName(String name) {
            try {
                return valueOf(name.substring(0, 1).toUpperCase() + name.substring(1));
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    public static class Action implements Serializable {
        public Light light;
        public Ac ac;
        public Tv tv;
        public Boiler boiler;
        public Security security;

        public void setParameter(Object o) {
            if (o instanceof Light) light = (Light) o;
            if (o instanceof Ac) ac = (Ac) o;
            if (o instanceof Tv) tv = (Tv) o;
            if (o instanceof Boiler) boiler = (Boiler) o;
            if (o instanceof Security) security = (Security) o;
        }

        public enum Light {
            On,
            Off;

            public static String[] enumToDescriptionArray() {
                return Arrays.stream(ScenarioAction.Action.Light.values()).map(val -> val.getClass().toString().substring(val.getClass().toString().lastIndexOf("$") + 1) + " " + val.name()).toArray(String[]::new);
            }

            public static Light descriptionToEnum(String str) {
                String trigger = str.split(" ")[1];
                return Arrays.stream(Light.values()).filter(val -> val.name().contains(trigger)).toArray(Light[]::new)[0];
            }
        }

        public enum Ac {
            On,
            Off;

            public static String[] enumToDescriptionArray() {
                return Arrays.stream(Ac.values()).map(val -> val.getClass().toString().substring(val.getClass().toString().lastIndexOf("$") + 1) + " " + val.name()).toArray(String[]::new);
            }

            public static Ac descriptionToEnum(String str) {
                String trigger = str.split(" ")[1];
                return Arrays.stream(Ac.values()).filter(val -> val.name().contains(trigger)).toArray(Ac[]::new)[0];
            }
        }

        public enum Tv {
            On,
            Off;

            public static String[] enumToDescriptionArray() {
                return Arrays.stream(Tv.values()).map(val -> val.getClass().toString().substring(val.getClass().toString().lastIndexOf("$") + 1) + " " + val.name()).toArray(String[]::new);
            }

            public static Tv descriptionToEnum(String str) {
                String trigger = str.split(" ")[1];
                return Arrays.stream(Tv.values()).filter(val -> val.name().contains(trigger)).toArray(Tv[]::new)[0];
            }
        }

        public enum Boiler {
            On,
            Off;

            public static String[] enumToDescriptionArray() {
                return Arrays.stream(ScenarioAction.Action.Boiler.values()).map(val -> val.getClass().toString().substring(val.getClass().toString().lastIndexOf("$") + 1) + " " + val.name()).toArray(String[]::new);
            }

            public static Boiler descriptionToEnum(String str) {
                String trigger = str.split(" ")[1];
                return Arrays.stream(Boiler.values()).filter(val -> val.name().contains(trigger)).toArray(Boiler[]::new)[0];
            }
        }

        public enum Security {
            On,
            Off;

            public static String[] enumToDescriptionArray() {
                return Arrays.stream(ScenarioAction.Action.Security.values()).map(val -> val.getClass().toString().substring(val.getClass().toString().lastIndexOf("$") + 1) + " " + val.name()).toArray(String[]::new);
            }

            public static Security descriptionToEnum(String str) {
                String trigger = str.split(" ")[1];
                return Arrays.stream(Security.values()).filter(val -> val.name().contains(trigger)).toArray(Security[]::new)[0];
            }
        }
    }
}
