package io.github.kbiakov.weathera.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

import io.github.kbiakov.weathera.R;
import io.github.kbiakov.weathera.fragments.DailyFragment;
import io.github.kbiakov.weathera.fragments.HourlyFragment;
import io.github.kbiakov.weathera.models.Forecast;


public class DailyActivity extends MasterDetailActivity implements DailyFragment.Callbacks {

    private static final String EXTRA_HOURLY_LIST = "hourly_list";
    private static final String EXTRA_LOCATION = "location";

    @Override
    protected Fragment createFragment() {
        return new DailyFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initToolbar();
    }

    @Override
    public void onDaySelected(ArrayList<Forecast> hourlyList, String location) {
        if (!isTablet()) {
            Intent i = new Intent(this, HourlyActivity.class);
            i.putExtra(EXTRA_HOURLY_LIST, hourlyList);
            i.putExtra(EXTRA_LOCATION, location);
            startActivity(i);
        } else {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment oldDetail = fm.findFragmentById(R.id.detailFragmentContainer);
            Fragment newDetail = HourlyFragment.newInstance(hourlyList);
            if (oldDetail != null) {
                ft.remove(oldDetail);
            }
            ft.add(R.id.detailFragmentContainer, newDetail);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.commit();
        }
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
    }

    private boolean isTablet() {
        return findViewById(R.id.detailFragmentContainer) != null;
    }

    public void setMeasureId(int measureId) {
        if (isTablet()) {
            FragmentManager fm = getSupportFragmentManager();
            HourlyFragment f = (HourlyFragment) fm.findFragmentById(R.id.detailFragmentContainer);
            if (f != null) {
                f.setMeasureId(measureId);
            }
        }
    }

}
