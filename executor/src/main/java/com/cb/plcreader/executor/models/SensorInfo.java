package com.cb.plcreader.executor.models;

import java.io.Serializable;

public class SensorInfo implements Serializable {

    private String _id;
    private String sensorName; //Name of the Sensor
    private String sensorGroup; //Group to which the sensor belongs
    private int dataRegister; // Data Register value E.g., 10000, 20000
    private String dataType; //Data Type
    private char  dataRegisterType; // Type of the data register
    private String uom; //Unit of Measure
    private final String docType = "sensor_info";

    @Override
    public String toString() {
        return "SensorInfo{" +
                "_id='" + _id + '\'' +
                ", sensorName='" + sensorName + '\'' +
                ", sensorGroup='" + sensorGroup + '\'' +
                ", dataRegister=" + dataRegister +
                ", dataType='" + dataType + '\'' +
                ", dataRegisterType=" + dataRegisterType +
                ", uom='" + uom + '\'' +
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

    public String getSensorGroup() {
        return sensorGroup;
    }

    public void setSensorGroup(String sensorGroup) {
        this.sensorGroup = sensorGroup;
    }

    public int getDataRegister() {
        return dataRegister;
    }

    public void setDataRegister(int dataRegister) {
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

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public SensorInfo() {

    }

    public SensorInfo(String _id, String sensorName, String sensorGroup, int dataRegister, String dataType, char dataRegisterType, String uom) {

        this._id = _id;
        this.sensorName = sensorName;
        this.sensorGroup = sensorGroup;
        this.dataRegister = dataRegister;
        this.dataType = dataType;
        this.dataRegisterType = dataRegisterType;
        this.uom = uom;
    }
}
