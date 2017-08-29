package com.cb.plcreader.models;

import java.io.Serializable;

public class Values implements Serializable
{
    private String value;

    private String sensorName;

    public String getValue ()
    {
        return value;
    }

    public void setValue (String value)
    {
        this.value = value;
    }

    public String getSensorName ()
    {
        return sensorName;
    }

    public void setSensorName (String sensorName)
    {
        this.sensorName = sensorName;
    }

    @Override
    public String toString() {
        return "Values{" +
                "value='" + value + '\'' +
                ", sensorName='" + sensorName + '\'' +
                '}';
    }
}
