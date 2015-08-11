package io.github.kbiakov.weathera.helpers.db;

import android.content.Context;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import io.github.kbiakov.weathera.R;
import io.github.kbiakov.weathera.models.Forecast;


public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String TAG = DatabaseHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "Forecasts.db";
    private static final int DATABASE_VERSION = 1;

    private ForecastDao forecastDao = null;
    private RuntimeExceptionDao<Forecast, Integer> forecastRuntimeDao = null;

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource){
        try {
            TableUtils.createTable(connectionSource, Forecast.class);
        } catch (SQLException e) {
            Log.e(TAG, "Error creating database " + DATABASE_NAME);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVer, int newVer){
        try {
            TableUtils.dropTable(connectionSource, Forecast.class, true);
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(TAG, "Error upgrading database " + DATABASE_NAME + " from version " + oldVer);
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the Database Access Object (DAO) for our SimpleData class. It will create it or just give the cached value.
     */
    public Dao<Forecast, Integer> getDao() throws SQLException {
        if (forecastDao == null) {
            forecastDao = getDao(Forecast.class);
        }
        return forecastDao;
    }

    /**
     * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our SimpleData class. It will
     * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
     */
    public RuntimeExceptionDao<Forecast, Integer> getForecastDao() {
        if (forecastRuntimeDao == null) {
            forecastRuntimeDao = getRuntimeExceptionDao(Forecast.class);
        }
        return forecastRuntimeDao;
    }

    @Override
    public void close(){
        super.close();
        forecastDao = null;
        forecastRuntimeDao = null;
    }

}
