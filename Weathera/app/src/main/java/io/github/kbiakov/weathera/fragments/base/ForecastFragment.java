package io.github.kbiakov.weathera.fragments.base;

import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import io.github.kbiakov.weathera.R;
import io.github.kbiakov.weathera.adapters.HourlyAdapter;
import io.github.kbiakov.weathera.helpers.CacheManager;
import io.github.kbiakov.weathera.interfaces.ForecastInterface;
import io.github.kbiakov.weathera.models.Forecast;


/**
 * Fragments of this type contains a large weather forecast for the day.
 */
public abstract class ForecastFragment extends BaseFragment implements ForecastInterface {

    protected ArrayList<Forecast> mHourlyList;
    protected HourlyAdapter mHourlyAdapter;

    protected GridView gvHourly;
    protected ImageView ivPic;
    protected TextView tvDate;
    protected TextView tvTemp;
    protected TextView tvDescription;
    protected TextView tvWind;
    protected TextView tvClouds;
    protected TextView tvTempLabel;
    protected TextView tvDescriptionLabel;
    protected TextView tvWindLabel;
    protected TextView tvCloudsLabel;

    private int mMeasureId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMeasureId = CacheManager.getInstance(getActivity()).getMeasureId();

        mHourlyList = new ArrayList<>();
        mHourlyAdapter = new HourlyAdapter(getActivity(), mHourlyList, mMeasureId);
    }

    @Override
    public void initHourly(View view) {
        ivPic = (ImageView) view.findViewById(R.id.ivPic);
        tvDate = (TextView) view.findViewById(R.id.tvDate);
        tvTemp = (TextView) view.findViewById(R.id.tvTemp);
        tvDescription = (TextView) view.findViewById(R.id.tvDescription);
        tvWind = (TextView) view.findViewById(R.id.tvWind);
        tvClouds = (TextView) view.findViewById(R.id.tvClouds);
        tvTempLabel = (TextView) view.findViewById(R.id.tvTempLabel);
        tvDescriptionLabel = (TextView) view.findViewById(R.id.tvDescriptionLabel);
        tvWindLabel = (TextView) view.findViewById(R.id.tvWindLabel);
        tvCloudsLabel = (TextView) view.findViewById(R.id.tvCloudsLabel);

        gvHourly = (GridView) view.findViewById(R.id.gvHourly);
        gvHourly.setAdapter(mHourlyAdapter);
    }

    @Override
    public abstract void fillForecast();

    @Override
    public void fillLarge(Forecast forecast, boolean isToday) {
        ivPic.setImageResource(R.drawable.no_image);
        if (forecast.getPicUrl() != null) {
            ImageLoader.getInstance().displayImage(forecast.getPicUrl(), ivPic);
        }

        tvDate.setText((isToday ? getString(R.string.today) + ", " : "") + forecast.getMonth() + " " + forecast.getDay());
        tvTemp.setText(getTemperature(forecast));
        tvDescription.setText(forecast.getDescription());
        tvWind.setText(forecast.getWindSpeed() + ", " + forecast.getWindDegree());
        tvClouds.setText(forecast.getCloudiness());
    }

    @Override
    public void fillHourly() {
        gvHourly.setNumColumns(mHourlyList.size());
    }

    // sets the temperature measurement at this fragment
    public void setMeasureId(int measureId) {
        mMeasureId = measureId;

        CacheManager.getInstance(getActivity()).saveMeasureId(mMeasureId);

        fillLarge(mHourlyList.get(0), false);

        mHourlyAdapter.setMeasureId(mMeasureId);
        mHourlyAdapter.notifyDataSetChanged();
    }

    protected int getMeasureId() {
        return CacheManager.getInstance(getActivity()).getMeasureId();
    }

    private String getTemperature(Forecast forecast) {
        String temperature;
        switch (mMeasureId) {
            case 0:
                temperature = forecast.getTemperatureK();
                break;
            case 1:
                temperature = forecast.getTemperatureC();
                break;
            case 2: default:
                temperature = forecast.getTemperatureF();
                break;
        }
        return temperature;
    }

}
