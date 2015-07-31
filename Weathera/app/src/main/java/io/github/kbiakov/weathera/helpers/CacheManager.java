package io.github.kbiakov.weathera.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import io.github.kbiakov.weathera.helpers.db.DatabaseHelper;
import io.github.kbiakov.weathera.helpers.db.HelperFactory;
import io.github.kbiakov.weathera.models.Forecast;


public class CacheManager {

    private final String PREF = "pref_cache";
    private final String FIELD_OUTDATE_DAY = "outdate_day";
    private final String FIELD_COUNTRY_ID = "country_id";
    private final String FIELD_CITY_ID = "city_id";
    private final String FIELD_MEASURE_ID = "measure_id";

    private static CacheManager sManager;
    private SharedPreferences mPref;

    private CacheManager(Context appContext) {
        mPref = appContext.getSharedPreferences(PREF, Activity.MODE_PRIVATE);
    }

    public static synchronized CacheManager getInstance(Context c) {
        if (sManager == null) {
            sManager = new CacheManager(c);
        }
        return sManager;
    }

    public boolean isEmpty() {
        return getOutdateDay() == -1;
    }

    /**
     * Saves cache. Cleanup of old cached forecast before saving.
     * @param forecastList
     * @param countryId
     * @param cityId
     * @param measureId
     */
    public void saveCache(ArrayList<Forecast> forecastList, int countryId, int cityId, int measureId) {
        clearForecast();

        saveForecast(forecastList);
        saveLocation(countryId, cityId);
        saveMeasureId(measureId);
    }

    public void clearCache() {
        clearForecast();
        clearPreferences();
    }

    private void saveForecast(ArrayList<Forecast> forecastList) {
        for (int i = 0; i < forecastList.size(); i++) {
            Forecast forecast = forecastList.get(i);

            DatabaseHelper dbHelper = HelperFactory.getHelper();
            RuntimeExceptionDao<Forecast, Integer> dao = dbHelper.getForecastDao();
            dao.create(forecast);
        }

        setOutdateDay(forecastList.get(forecastList.size() - 1).getDayOfYear() + 1);
    }

    public ArrayList<Forecast> getForecast() {
        DatabaseHelper dbHelper = HelperFactory.getHelper();
        RuntimeExceptionDao<Forecast, Integer> dao = dbHelper.getForecastDao();
        List<Forecast> forecastList = dao.queryForAll();

        ArrayList<Forecast> forecasts = new ArrayList<>();

        int outdateDay = getOutdateDay();
        if (outdateDay != -1) {
            TimeZone timeZone = TimeZone.getTimeZone("UTC");
            Calendar today = Calendar.getInstance(timeZone);
            today.set(Calendar.HOUR_OF_DAY, 0);
            int todayDay = today.get(Calendar.DAY_OF_YEAR);
            int offset = outdateDay - todayDay;

            // ignore outdated forecast
            if (offset > 0) {
                for (Forecast forecast : forecastList) {
                    if (forecast.getDayOfYear() >= todayDay) {
                        forecasts.add(forecast);
                    }
                }
            }
        }

        return forecasts;
    }

    private void clearForecast() {
        if (!isEmpty()) {
            DatabaseHelper dbHelper = HelperFactory.getHelper();
            RuntimeExceptionDao<Forecast, Integer> dao = dbHelper.getForecastDao();
            dao.delete(getForecast());

            setOutdateDay(-1);
        }
    }

    private void saveLocation(int countryId, int cityId) {
        mPref.edit()
                .putInt(FIELD_COUNTRY_ID, countryId)
                .putInt(FIELD_CITY_ID, cityId)
                .apply();
    }

    public int getCountryId() {
        return mPref.getInt(FIELD_COUNTRY_ID, 0);
    }

    public int getCityId() {
        return mPref.getInt(FIELD_CITY_ID, 0);
    }

    public void saveMeasureId(int measureId) {
        mPref.edit().putInt(FIELD_MEASURE_ID, measureId).apply();
    }

    public int getMeasureId() {
        return mPref.getInt(FIELD_MEASURE_ID, 0);
    }

    private void setOutdateDay(int day) {
        mPref.edit().putInt(FIELD_OUTDATE_DAY, day).apply();
    }

    private int getOutdateDay() {
        return mPref.getInt(FIELD_OUTDATE_DAY, -1);
    }

    private void clearPreferences() {
        mPref.edit().clear().apply();
    }

}
