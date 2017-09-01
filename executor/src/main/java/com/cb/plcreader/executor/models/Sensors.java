package com.cb.plcreader.executor.models;

import java.io.Serializable;

public class Sensors implements Serializable {

    private String sensorGroup;
    private String sensorName;
    private float value;

    public Sensors() {
    }

    @Override
    public String toString() {
        return "Sensors{" +
                "value='" + value + '\'' +
                ", sensorName='" + sensorName + '\'' +
                ", sensorGroup='" + sensorGroup + '\'' +
                '}';
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public String getSensorGroup() {
        return sensorGroup;
    }

    public void setSensorGroup(String sensorGroup) {
        this.sensorGroup = sensorGroup;
    }

    public Sensors(String sensorName, float value, String sensorGroup) {
        this.value = value;
        this.sensorName = sensorName;
        this.sensorGroup = sensorGroup;
    }
}
