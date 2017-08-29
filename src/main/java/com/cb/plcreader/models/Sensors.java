package com.cb.plcreader.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class Sensors implements Serializable
{
    private ArrayList<Values> values;

    public Sensors(String sensorGroup) {
        this.sensorGroup = sensorGroup;
    }

    public Sensors( String sensorGroup,ArrayList<Values> values) {
        this.values = values;
        this.sensorGroup = sensorGroup;
    }

    private String sensorGroup;

    public ArrayList<Values> getValues ()
    {
        return values;
    }

    public void setValues (ArrayList<Values> values)
    {
        this.values = values;
    }

    public String getSensorGroup ()
    {
        return sensorGroup;
    }

    public void setSensorGroup (String sensorGroup)
    {
        this.sensorGroup = sensorGroup;
    }

    @Override
    public String toString() {
        return "Sensors{" +
                "values=" + Arrays.toString(new ArrayList[]{values}) +
                ", sensorGroup='" + sensorGroup + '\'' +
                '}';
    }
}