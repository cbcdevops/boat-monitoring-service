package com.cb.plcreader.executor.models;

import java.io.Serializable;

public class Sensors implements Serializable {

    private String sensorGroup;
    private String sensorName;
    private float value;
    private String uom;

    public Sensors() {
    }

    @Override
    public String toString() {
        return "Sensors{" +
                "sensorGroup='" + sensorGroup + '\'' +
                ", sensorName='" + sensorName + '\'' +
                ", value=" + value +
                ", uom='" + uom + '\'' +
                '}';
    }

    public String getSensorGroup() {
        return sensorGroup;
    }

    public void setSensorGroup(String sensorGroup) {
        this.sensorGroup = sensorGroup;
    }

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public Sensors(String sensorGroup,  float value, String sensorName, String uom) {

        this.sensorGroup = sensorGroup;
        this.sensorName = sensorName;
        this.value = value;
        this.uom = uom;
    }
}
