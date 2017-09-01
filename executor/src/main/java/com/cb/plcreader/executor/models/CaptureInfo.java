package com.cb.plcreader.executor.models;

import java.io.Serializable;

public class CaptureInfo implements Serializable
{
    private long captureTimeMS;
    private String rawData;

    public CaptureInfo() {
    }

    public CaptureInfo(long captureTimeMS, String rawData) {

        this.captureTimeMS = captureTimeMS;
        this.rawData = rawData;
    }

    public long getCaptureTimeMS ()
    {
        return captureTimeMS;
    }

    public void setCaptureTimeMS (long captureTimeMS)
    {
        this.captureTimeMS = captureTimeMS;
    }

    public String getRawData ()
    {
        return rawData;
    }

    public void setRawData (String rawData)
    {
        this.rawData = rawData;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [captureTimeMS = "+captureTimeMS+", rawData = "+rawData+"]";
    }
}
