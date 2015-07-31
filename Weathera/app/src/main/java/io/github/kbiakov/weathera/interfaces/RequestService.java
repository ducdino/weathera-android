package io.github.kbiakov.weathera.interfaces;

import io.github.kbiakov.weathera.models.result.ForecastResult;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface RequestService {

    String OWEATHER_API_URL = "http://api.openweathermap.org/data/2.5";

    @GET("/forecast")
    void getForecast(@Query("q") String location, Callback<ForecastResult> cb);

}
