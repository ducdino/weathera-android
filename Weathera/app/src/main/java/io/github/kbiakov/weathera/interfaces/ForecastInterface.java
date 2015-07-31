package io.github.kbiakov.weathera.interfaces;

import android.view.View;

import io.github.kbiakov.weathera.models.Forecast;


public interface ForecastInterface {

    // init hourly block views
    void initHourly(View view);

    // defines the behavior to fill forecast
    void fillForecast();

    // fills large block of the forecast for the day (today or selected)
    void fillLarge(Forecast forecast, boolean isToday);

    // fills hourly block of the forecast
    void fillHourly();

}
