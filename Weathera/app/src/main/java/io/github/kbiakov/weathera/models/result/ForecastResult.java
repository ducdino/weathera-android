package io.github.kbiakov.weathera.models.result;

import java.io.Serializable;
import java.util.ArrayList;

import io.github.kbiakov.weathera.models.DayWeather;


public class ForecastResult implements Serializable {

    private int cod;
    private ArrayList<DayWeather> list;

    public int getResponseCode() {
        return cod;
    }

    public ArrayList<DayWeather> getForecast() {
        return list;
    }

}
