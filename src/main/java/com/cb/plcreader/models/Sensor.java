
package com.cb.plcreader.models;

public class Sensor

 {
    int portEngineRpm;
    float portEngineOilPsr;
    float portEngineFuelPsr;
    float portEngineWaterTemp;

    @Override
    public String toString() {
        return "Sensor{" +
                "portEngineRpm=" + portEngineRpm +
                ", portEngineOilPsr=" + portEngineOilPsr +
                ", portEngineFuelPsr=" + portEngineFuelPsr +
                ", portEngineWaterTemp=" + portEngineWaterTemp +
                '}';
    }

    public int getPortEngineRpm() {
        return portEngineRpm;
    }

    public void setPortEngineRpm(int portEngineRpm) {
        this.portEngineRpm = portEngineRpm;
    }

    public float getPortEngineOilPsr() {
        return portEngineOilPsr;
    }

    public void setPortEngineOilPsr(float portEngineOilPsr) {
        this.portEngineOilPsr = portEngineOilPsr;
    }

    public float getPortEngineFuelPsr() {
        return portEngineFuelPsr;
    }

    public void setPortEngineFuelPsr(float portEngineFuelPsr) {
        this.portEngineFuelPsr = portEngineFuelPsr;
    }

    public float getPortEngineWaterTemp() {
        return portEngineWaterTemp;
    }

    public void setPortEngineWaterTemp(float portEngineWaterTemp) {
        this.portEngineWaterTemp = portEngineWaterTemp;
    }

    public Sensor() {

    }

}


