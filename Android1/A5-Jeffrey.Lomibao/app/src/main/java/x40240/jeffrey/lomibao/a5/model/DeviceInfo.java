package x40240.jeffrey.lomibao.a5.model;

import android.content.Context;
import android.content.res.Resources;

import java.io.Serializable;
import java.util.ArrayList;

public final class DeviceInfo
    implements Serializable
{
    public enum TemperatureCompensationEnum { TC_AUTO, TC_MANUAL }

    private TemperatureCompensationEnum temperatureCompensation;
    private int id;
    private String tag;
    private double lowerRangeValue;
    private double upperRangeValue;
    private double manualTemperature;

    public DeviceInfo() {
        id = 1;
        tag = "1066_pH_Device";
        lowerRangeValue = 0.0;
        upperRangeValue = 14.0;
        manualTemperature = 25.0;
        temperatureCompensation = TemperatureCompensationEnum.TC_AUTO;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TemperatureCompensationEnum getTemperatureCompensation() {
        return temperatureCompensation;
    }

    public void setTemperatureCompensation(TemperatureCompensationEnum temperatureCompensation) {
        this.temperatureCompensation = temperatureCompensation;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public double getLowerRangeValue() {
        return lowerRangeValue;
    }

    public void setLowerRangeValue(double lowerRangeValue) {
        this.lowerRangeValue = lowerRangeValue;
    }

    public double getUpperRangeValue() {
        return upperRangeValue;
    }

    public void setUpperRangeValue(double upperRangeValue) {
        this.upperRangeValue = upperRangeValue;
    }

    public double getManualTemperature() {
        return manualTemperature;
    }

    public void setManualTemperature(double manualTemperature) {
        this.manualTemperature = manualTemperature;
    }

}
