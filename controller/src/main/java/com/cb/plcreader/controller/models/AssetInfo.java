package com.cb.plcreader.controller.models;

import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Arrays;

@Component
public class AssetInfo implements Serializable {

    private String _id;
    private String assetName;
    private String assetAbbreviation;
    private String hostAddress1;
    private String hostAddress2;
    private int port;
    private String runFreqInSec;
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
                ", runFreqInSec='" + runFreqInSec + '\'' +
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

    public String getRunFreqInSec() {
        return runFreqInSec;
    }

    public void setRunFreqInSec(String runFreqInSec) {
        this.runFreqInSec = runFreqInSec;
    }

    public String[] getReadSensorList() {
        return readSensorList;
    }

    public void setReadSensorList(String[] readSensorList) {
        this.readSensorList = readSensorList;
    }

    public AssetInfo(String assetName, String _id, String assetAbbreviation, String hostAddress1, String hostAddress2, int port, String runFreqInSec, String[] readSensorList) {

        this.assetName = assetName;
        this._id = _id;
        this.assetAbbreviation = assetAbbreviation;
        this.hostAddress1 = hostAddress1;
        this.hostAddress2 = hostAddress2;
        this.port = port;
        this.runFreqInSec = runFreqInSec;
        this.readSensorList = readSensorList;
    }

    public AssetInfo() {

    }
}
