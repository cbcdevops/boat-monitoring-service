package com.cb.plcreader.models;

import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Arrays;

@Component
public class AssetInformation implements Serializable {


    private String _id;
    private String assetName;
    private String assetAbbreviation;
    private String hostAddress1;
    private String hostAddress2;
    private int port;
    private String cronScheduleString;
    private String[] readSensorList;
    private final String docType = "asset_info";

    @Override
    public String toString() {
        return "AssetInformation{" +
                "assetName='" + assetName + '\'' +
                ", _id=" + _id +
                ", assetAbbreviation='" + assetAbbreviation + '\'' +
                ", hostAddress1='" + hostAddress1 + '\'' +
                ", hostAddress2='" + hostAddress2 + '\'' +
                ", port=" + port +
                ", cronScheduleString='" + cronScheduleString + '\'' +
                ", readSensorList=" + Arrays.toString(readSensorList) +
                ", docType='" + docType + '\'' +
                '}';
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getAssetAbbreviation() {
        return assetAbbreviation;
    }

    public void setAssetAbbreviation(String assetAbbreviation) {
        this.assetAbbreviation = assetAbbreviation;
    }

    public String getHostAddress1() {
        return hostAddress1;
    }

    public void setHostAddress1(String hostAddress1) {
        this.hostAddress1 = hostAddress1;
    }

    public String getHostAddress2() {
        return hostAddress2;
    }

    public void setHostAddress2(String hostAddress2) {
        this.hostAddress2 = hostAddress2;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getCronScheduleString() {
        return cronScheduleString;
    }

    public void setCronScheduleString(String cronScheduleString) {
        this.cronScheduleString = cronScheduleString;
    }

    public String[] getReadSensorList() {
        return readSensorList;
    }

    public void setReadSensorList(String[] readSensorList) {
        this.readSensorList = readSensorList;
    }

    public AssetInformation(String assetName, String _id, String assetAbbreviation, String hostAddress1, String hostAddress2, int port, String cronScheduleString, String[] readSensorList) {

        this.assetName = assetName;
        this._id = _id;
        this.assetAbbreviation = assetAbbreviation;
        this.hostAddress1 = hostAddress1;
        this.hostAddress2 = hostAddress2;
        this.port = port;
        this.cronScheduleString = cronScheduleString;
        this.readSensorList = readSensorList;
    }

    public AssetInformation() {

    }
}
