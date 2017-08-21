
package com.cb.plcreader.models;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;


public class CaptureInformation {


    private String Id;
    @JsonFormat
            (shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private Date date;
    private String assertName;
    private boolean isOnline;
    private String ipAddress;
    private int port;

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    private Sensor sensor;
    private MetaData metaData;
    private String errMsg;

    public CaptureInformation(Date date, String assertName, boolean isOnline, String ipAddress, int port) {
        this.date = date;
        this.assertName = assertName;
        this.isOnline = isOnline;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public CaptureInformation() {
    }

    @Override
    public String toString() {
        return "CaptureInformation{" +
                "Id='" + Id + '\'' +
                ", date=" + date +
                ", assertName='" + assertName + '\'' +
                ", isOnline=" + isOnline +
                ", ipAddress='" + ipAddress + '\'' +
                ", port=" + port +
                ", sensor=" + sensor +
                ", metaData=" + metaData +
                ", errMsg='" + errMsg + '\'' +
                '}';
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getAssertName() {
        return assertName;
    }

    public void setAssertName(String assertName) {
        this.assertName = assertName;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Sensor getSensors() {
        return sensor;
    }

    public void setSensors(Sensor sensor) {
        this.sensor = sensor;
    }

    public MetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
    }

    public CaptureInformation(Date date, String assertName, boolean isOnline, String ipAddress, int port, List<Sensor> sensors, MetaData metaData) {
        this.date = date;
        this.assertName = assertName;
        this.isOnline = isOnline;
        this.ipAddress = ipAddress;
        this.port = port;
        this.sensor = sensor;
        this.metaData = metaData;

    }
}

