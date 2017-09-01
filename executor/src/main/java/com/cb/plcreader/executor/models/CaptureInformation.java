
package com.cb.plcreader.executor.models;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;


public class CaptureInformation implements Serializable {

    private String _id;
    private String _rev;
    private final String docType = "capture_info";
    private String assetName;
    private String assetAbbrevation;
    private String officialNo;
    private String ipAddress;
    @JsonFormat
            (shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy hh:mm:ss")
    private Date date;
    private boolean isOnline;
    private ArrayList<Sensors> sensors;
    private CaptureInfo captureInfo;
    private String errMsg;


    @Override
    public String toString() {
        return "CaptureInformation{" +
                "sensors=" + Arrays.toString(new ArrayList[]{sensors}) +
                ", isOnline='" + isOnline + '\'' +
                ", assetName='" + assetName + '\'' +
                ", _rev='" + _rev + '\'' +
                ", captureInfo=" + captureInfo +
                ", docType='" + docType + '\'' +
                ", officialNo='" + officialNo + '\'' +
                ", _id='" + _id + '\'' +
                ", assetAbbrevation='" + assetAbbrevation + '\'' +
                ", errMsg='" + errMsg + '\'' +
                ", date=" + date +
                ", ipAddress='" + ipAddress + '\'' +
                '}';
    }

    public CaptureInformation(String assetName, String officialNo, String assetAbbrevation, Date date, String ipAddress, boolean isOnline) {
        this.assetName = assetName;
        this.officialNo = officialNo;
        this.assetAbbrevation = assetAbbrevation;
        this.date = date;
        this.ipAddress = ipAddress;
        this.isOnline = isOnline;
    }

    public ArrayList<Sensors> getSensors() {
        return sensors;
    }

    public void setSensors(ArrayList<Sensors> sensors) {
        this.sensors = sensors;
    }

    public boolean getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String get_rev() {
        return _rev;
    }

    public void set_rev(String _rev) {
        this._rev = _rev;
    }

    public CaptureInfo getCaptureInfo() {
        return captureInfo;
    }

    public void setCaptureInfo(CaptureInfo captureInfo) {
        this.captureInfo = captureInfo;
    }

    public String getDocType() {
        return docType;
    }

    public String getOfficialNo() {
        return officialNo;
    }

    public void setOfficialNo(String officialNo) {
        this.officialNo = officialNo;
    }

    public String get_id() {
        return _id;
    }


    public String getAssetAbbrevation() {
        return assetAbbrevation;
    }

    public void setAssetAbbrevation(String assetAbbrevation) {
        this.assetAbbrevation = assetAbbrevation;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

}

