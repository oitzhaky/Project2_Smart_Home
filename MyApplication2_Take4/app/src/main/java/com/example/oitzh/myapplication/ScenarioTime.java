package com.example.oitzh.myapplication;

/**
 * Created by oitzh on 11/11/2017.
 */

public class ScenarioTime {
    public int hour;
    public int minutes;

    ScenarioTime(String time){
        this.hour =Integer.parseInt(time.split(":")[0]);
        this.minutes=Integer.parseInt(time.split(":")[1]);
    }

    @Override
    public boolean equals(Object time) {
        return time instanceof ScenarioTime && ((this.hour == ((ScenarioTime) time).hour) && (this.minutes == ((ScenarioTime) time).minutes));
    }

    public Double toDouble(){
        return Double.parseDouble(toString());
    }

    @Override
    public String toString(){
        return String.format("%02d",hour)+ "." + String.format("%02d",minutes);
    }
}