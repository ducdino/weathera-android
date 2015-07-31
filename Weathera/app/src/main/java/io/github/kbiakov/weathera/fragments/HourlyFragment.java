package io.github.kbiakov.weathera.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import io.github.kbiakov.weathera.fragments.base.ForecastFragment;
import io.github.kbiakov.weathera.helpers.Consts;
import io.github.kbiakov.weathera.R;
import io.github.kbiakov.weathera.interfaces.ForecastInterface;
import io.github.kbiakov.weathera.models.Forecast;


public class HourlyFragment extends ForecastFragment implements ForecastInterface {

    private static final String EXTRA_HOURLY_LIST = "hourly_list";

    public static HourlyFragment newInstance(ArrayList<Forecast> hourlyList) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_HOURLY_LIST, hourlyList);

        HourlyFragment f = new HourlyFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHourlyAdapter.setLightColor(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_hourly, container, false);

        mHourlyAdapter.addAll((ArrayList<Forecast>) getArguments().getSerializable(EXTRA_HOURLY_LIST));

        initHourly(root);
        fillForecast();

        return root;
    }

    /**
     * This fragment requires other colors.
     * @param view
     */
    @Override
    public void initHourly(View view) {
        super.initHourly(view);

        int white = getColorByRes(android.R.color.white);
        int textBlack = getColorByRes(R.color.text_black);
        int textDark = getColorByRes(R.color.text_dark);

        view.findViewById(R.id.vHourly).setBackgroundColor(white);

        tvDate.setTextColor(textBlack);
        tvTempLabel.setTextColor(textBlack);
        tvDescriptionLabel.setTextColor(textBlack);
        tvWindLabel.setTextColor(textBlack);
        tvCloudsLabel.setTextColor(textBlack);
        tvTemp.setTextColor(textDark);
        tvDescription.setTextColor(textDark);
        tvWind.setTextColor(textDark);
        tvClouds.setTextColor(textDark);
    }

    /**
     * Selects the index for large displaying the weather forecast. The server response
     * contains a set of a maximum of 8 points (00:00, 03:00, ..., 21:00). In this way
     * select the temperature for the midday or the end of the day.
     */
    @Override
    public void fillForecast() {
        int lastIndex = mHourlyList.size() - 1;
        int lastPos = Consts.MIDDAY_HOUR < lastIndex ? Consts.MIDDAY_HOUR : lastIndex;
        fillLarge(mHourlyList.get(lastPos), false);
        fillHourly();
    }

    private int getColorByRes(int resId) {
        return getResources().getColor(resId);
    }

}
