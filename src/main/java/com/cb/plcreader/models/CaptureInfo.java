package com.cb.plcreader.models;

import java.io.Serializable;

public class CaptureInfo implements Serializable
{
    private String captureTimeMS;

    private String rawData;

    public String getCaptureTimeMS ()
    {
        return captureTimeMS;
    }

    public void setCaptureTimeMS (String captureTimeMS)
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
