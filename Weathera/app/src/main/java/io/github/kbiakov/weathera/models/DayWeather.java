package io.github.kbiakov.weathera.models;

import java.io.Serializable;
import java.util.ArrayList;

import io.github.kbiakov.weathera.models.result.CloudsData;
import io.github.kbiakov.weathera.models.result.MainData;
import io.github.kbiakov.weathera.models.result.WeatherData;
import io.github.kbiakov.weathera.models.result.WindData;


public class DayWeather implements Serializable {

    private String dt_txt;
    private MainData main;
    private WindData wind;
    private CloudsData clouds;
    private ArrayList<WeatherData> weather;

    public String getDate() {
        return dt_txt;
    }

    public WeatherData getWeather() {
        return weather.get(0);
    }

    public MainData getMain() {
        return main;
    }

    public WindData getWind() {
        return wind;
    }

    public CloudsData getClouds() {
        return clouds;
    }

}
