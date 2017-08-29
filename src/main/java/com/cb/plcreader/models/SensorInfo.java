package com.cb.plcreader.models;

import java.io.Serializable;

public class SensorInfo implements Serializable {

    private String _id;
    private String sensorName;
    private String sensorGroup;
    private String dataRegister;
    private String dataType;
    private char  dataRegisterType;
    private final String docType = "sensor_info";

    @Override
    public String toString() {
        return "SensorInfo{" +
                "_id='" + _id + '\'' +
                ", sensorName='" + sensorName + '\'' +
                ", sensorGroup='" + sensorGroup + '\'' +
                ", dataRegister='" + dataRegister + '\'' +
                ", dataType='" + dataType + '\'' +
                ", docType='" + docType + '\'' +
                '}';
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public String getDataRegister() {
        return dataRegister;
    }

    public void setDataRegister(String dataRegister) {
        this.dataRegister = dataRegister;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public char getDataRegisterType() {
        return dataRegisterType;
    }

    public void setDataRegisterType(char dataRegisterType) {
        this.dataRegisterType = dataRegisterType;
    }

    public String getSensorGroup() {
        return sensorGroup;
    }

    public void setSensorGroup(String sensorGroup) {
        this.sensorGroup = sensorGroup;
    }

    public SensorInfo() {

    }

    public SensorInfo(String _id, String sensorName, String sensorGroup, String dataRegister, String dataType, char dataRegisterType) {
        this._id = _id;
        this.sensorName = sensorName;
        this.sensorGroup = sensorGroup;
        this.dataRegister = dataRegister;
        this.dataType = dataType;
        this.dataRegisterType = dataRegisterType;
    }
}
