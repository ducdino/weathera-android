package io.github.kbiakov.weathera.helpers.db;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

import io.github.kbiakov.weathera.models.Forecast;


public class ForecastDao extends BaseDaoImpl<Forecast, Integer> {

    protected ForecastDao(ConnectionSource connectionSource, Class<Forecast> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public List<Forecast> getForecast() throws SQLException{
        return this.queryForAll();
    }

}
