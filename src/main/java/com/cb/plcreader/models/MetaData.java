package com.cb.plcreader.models;

public class MetaData {
    String  responseHexString;
    long captureTime;

    public long getCaptureTime() {
        return captureTime;
    }

    public void setCaptureTime(long captureTime) {
        this.captureTime = captureTime;
    }

    public String getResponseHexString() {
        return responseHexString;
    }

    public void setResponseHexString(String responseHexString) {
        this.responseHexString = responseHexString;
    }

    public MetaData(String responseHexString, long captureTime) {
        this.responseHexString = responseHexString;
        this.captureTime = captureTime;
    }
}
