package io.github.kbiakov.weathera.models.result;

import java.io.Serializable;

import io.github.kbiakov.weathera.helpers.Consts;


public class MainData implements Serializable {

    private double temp;
    private double temp_min;
    private double temp_max;
    private double pressure;
    private double humidity;

    public double getTemp() {
        return temp;
    }

    public double getPressure() {
        return pressure;
    }

    public double getHumidity() {
        return humidity;
    }

}
