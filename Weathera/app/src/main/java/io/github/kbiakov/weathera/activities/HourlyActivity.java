package io.github.kbiakov.weathera.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;

import io.github.kbiakov.weathera.R;
import io.github.kbiakov.weathera.fragments.HourlyFragment;
import io.github.kbiakov.weathera.models.Forecast;


public class HourlyActivity extends AppCompatActivity {

    private static final String EXTRA_HOURLY_LIST = "hourly_list";
    private static final String EXTRA_LOCATION = "location";

    private ArrayList<Forecast> mHourlyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        initToolbar(getIntent().getStringExtra(EXTRA_LOCATION));

        mHourlyList = (ArrayList<Forecast>) getIntent().getSerializableExtra(EXTRA_HOURLY_LIST);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, HourlyFragment.newInstance(mHourlyList))
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    private void initToolbar(String location) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ((TextView) findViewById(R.id.toolbarTitle)).setText(location);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationIcon(R.drawable.ic_menu_back);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
