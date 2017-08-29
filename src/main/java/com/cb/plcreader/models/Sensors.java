package com.cb.plcreader.models;

import java.io.Serializable;
import java.util.Arrays;

public class Sensors implements Serializable
{
    private Values[] values;

    private String sensorGroup;

    public Values[] getValues ()
    {
        return values;
    }

    public void setValues (Values[] values)
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
                "values=" + Arrays.toString(values) +
                ", sensorGroup='" + sensorGroup + '\'' +
                '}';
    }
}