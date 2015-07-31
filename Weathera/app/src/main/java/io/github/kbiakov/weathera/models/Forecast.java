package io.github.kbiakov.weathera.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import io.github.kbiakov.weathera.helpers.Consts;
import io.github.kbiakov.weathera.helpers.Utils;


@DatabaseTable(tableName = "Forecasts")
public class Forecast implements Serializable {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String date;

    @DatabaseField(canBeNull = false)
    private String picUrl;

    @DatabaseField(canBeNull = false)
    private String description;

    @DatabaseField(canBeNull = false)
    private double temperature;

    @DatabaseField(canBeNull = false)
    private double windSpeed;

    @DatabaseField(canBeNull = false)
    private double windDegree;

    @DatabaseField(canBeNull = false)
    private int cloudiness;

    public Forecast() {}

    public Forecast(String date, String picUrl, String description,
                    double temperature, double windSpeed, double windDegree, int cloudiness) {

        this.date = date;
        this.picUrl = picUrl;
        this.description = description;
        this.temperature = temperature;
        this.windSpeed = windSpeed;
        this.windDegree = windDegree;
        this.cloudiness = cloudiness;
    }

    public int getId() {
        return id;
    }

    private Calendar getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();

        try {
            calendar.setTime(format.parse(date));
        } catch (ParseException ignored) {}

        return calendar;
    }

    public int getDay() {
        return getDate().get(Calendar.DAY_OF_MONTH);
    }

    public String getMonth() {
        return Utils.getMonthFromInt(getDate().get(Calendar.MONTH));
    }

    public int getDayOfYear() {
        return getDate().get(Calendar.DAY_OF_YEAR);
    }

    public String getPicUrl() {
        return picUrl != null ? Consts.OWEATHER_IMG_URL + picUrl + ".png" : null;
    }

    public String getDescription() {
        return description;
    }

    public String getTemperatureK() {
        return (int) temperature + "°K";
    }

    public String getTemperatureC() {
        String tempStr = String.valueOf(kelvinToCelsius(temperature));
        return tempStr.substring(0, tempStr.indexOf('.') + 2) + "°C";
    }

    public String getTemperatureF() {
        return (int) (kelvinToFahrenheit(temperature)) + "°F";
    }

    public String getWindSpeed() {
        return windSpeed + " m/s";
    }

    public String getWindDegree() {
        return (int) (windDegree) + " dg";
    }

    public String getCloudiness() {
        return cloudiness + "%";
    }

    private double kelvinToCelsius(double temp) {
        return temp - Consts.KEL_CEL_ODDS;
    }

    private double kelvinToFahrenheit(double temp) {
        return 1.8 * (temp - Consts.KEL_FAH_ODDS) + 32;
    }

}
