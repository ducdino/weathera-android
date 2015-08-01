package io.github.kbiakov.weathera.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import io.github.kbiakov.weathera.activities.DailyActivity;
import io.github.kbiakov.weathera.fragments.base.ForecastFragment;
import io.github.kbiakov.weathera.helpers.CacheManager;
import io.github.kbiakov.weathera.helpers.Consts;
import io.github.kbiakov.weathera.R;
import io.github.kbiakov.weathera.adapters.DailyAdapter;
import io.github.kbiakov.weathera.helpers.Utils;
import io.github.kbiakov.weathera.interfaces.RequestService;
import io.github.kbiakov.weathera.models.DayWeather;
import io.github.kbiakov.weathera.models.Forecast;
import io.github.kbiakov.weathera.models.result.ForecastResult;
import io.github.kbiakov.weathera.server.ServiceGenerator;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class DailyFragment extends ForecastFragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "DailyFragment";

    private ArrayList<Forecast> mForecastList = new ArrayList<>();
    private ArrayList<Forecast> mDailyList = new ArrayList<>();
    private DailyAdapter mDailyAdapter;
    private CacheManager mCacheManager;
    private Callbacks mCallbacks;
    private WeakReference<DBSaveTask> mDbSaveWeakRef;

    private Spinner spnCountry;
    private Spinner spnCity;
    private ListView lvDays;
    private SwipeRefreshLayout vRefresh;

    private String mCountry = "RU";
    private String mCity = "Moscow";

    private boolean isOnline;
    private boolean isCached;

    public interface Callbacks {
        void onDaySelected(ArrayList<Forecast> hourlyList, String location);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        mCacheManager = CacheManager.getInstance(getActivity());

        isOnline = isOnline();
        isCached = isCached();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View headerLocation = LayoutInflater.from(getActivity()).inflate(R.layout.view_location, null);
        View headerHourly = LayoutInflater.from(getActivity()).inflate(R.layout.view_hourly, null);
        View root = inflater.inflate(R.layout.fragment_daily, container, false);

        mDailyAdapter = new DailyAdapter(getActivity(), mDailyList, getMeasureId());

        initLocation(headerLocation);
        initHourly(headerHourly);

        lvDays = (ListView) root.findViewById(R.id.lvDays);
        lvDays.addHeaderView(headerLocation, "location", false);
        lvDays.addHeaderView(headerHourly, "hourly", false);
        lvDays.setAdapter(mDailyAdapter);
        lvDays.setOnItemClickListener(onDayClickListener);

        vRefresh = (SwipeRefreshLayout) root.findViewById(R.id.vRefresh);
        vRefresh.setColorSchemeResources(R.color.main);
        vRefresh.setOnRefreshListener(this);

        return root;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onRefresh() {
        getForecast();
    }

    private AdapterView.OnItemClickListener onDayClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            int selectedDay = mDailyList.get(position - lvDays.getHeaderViewsCount()).getDayOfYear();

            // packs hourly forecast for the selected day
            ArrayList<Forecast> hourlyList = new ArrayList<>();
            for (Forecast forecast : mForecastList) {
                int day = forecast.getDayOfYear();

                if (day == selectedDay) {
                    hourlyList.add(forecast);
                } else if (day > selectedDay) {
                    break;
                }
            }

            mCallbacks.onDaySelected(hourlyList, mCity + ", " + mCountry);
        }
    };

    private void getForecast() {
        // UI controls (spinners) disabled in offline mode, only cached data or default values
        spnCountry.setEnabled(isOnline());
        spnCity.setEnabled(isOnline());

        /** offline & empty cache */
        if (!isOnline && !isCached) {
            showErrorDialog(getString(R.string.no_internet_and_cache), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getActivity().finish();
                }
            });
            return;
        }

        /** get the weather forecast from cache immediately at start */
        if (isCached) {
            getForecastCache();
        } else { /** online & empty cache, get forecast from server */
            getForecastServer();
        }
    }

    private void getForecastDelayed() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getForecast();
            }
        }, 2000);
    }

    /**
     * Server request using Weather API provided OpenWeatherMap. Returns the weather
     * for 5 days, divided into 3-hour intervals. The parameter is the location in
     * the format "%city_name%,%COUNTRY_CODE%".
     */
    private void getForecastServer() {
        showRefresh();

        // disable spinners when server request
        spnCountry.setEnabled(false);
        spnCity.setEnabled(false);

        Log.i(TAG, "Forecast request: " + mCity + ", " + mCountry);

        String url = RequestService.OWEATHER_API_URL;
        RequestService service = ServiceGenerator.createService(RequestService.class, url);
        service.getForecast(mCity + ',' + mCountry, new Callback<ForecastResult>() {
            @Override
            public void success(ForecastResult res, Response response) {
                if (res.getResponseCode() != 200) {
                    showErrorDialog("Response error " + res.getResponseCode() + ".", null);
                    return;
                }

                mForecastList.clear();
                for (DayWeather dayWeather : res.getForecast()) {
                    mForecastList.add(new Forecast(
                            dayWeather.getDate(),
                            dayWeather.getWeather().getIcon(),
                            dayWeather.getWeather().getDescription(),
                            dayWeather.getMain().getTemp(),
                            dayWeather.getWind().getSpeed(),
                            dayWeather.getWind().getDegree(),
                            dayWeather.getClouds().getCloudiness()));
                }

                fillForecast();
                saveForecastCache();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                hideRefresh();
                showErrorDialog(retrofitError.getMessage(), null);
            }
        });
    }

    private void getForecastCache() {
        showRefresh();

        new AsyncTask<Void, ArrayList<Forecast>, ArrayList<Forecast>>() {
            @Override
            protected ArrayList<Forecast> doInBackground(Void... params) {
                return mCacheManager.getForecast();
            }

            @Override
            protected void onPostExecute(ArrayList<Forecast> forecastList) {
                // outdated cache
                if (!isOnline && forecastList.size() == 0) {
                    mCacheManager.clearCache();
                    hideRefresh();

                    showErrorDialog(getString(R.string.no_internet_and_actual_cache), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getActivity().finish();
                        }
                    });
                    return;
                }

                mForecastList.clear();
                mForecastList.addAll(forecastList);

                if (isOnline) {
                    fillForecast();
                    getForecastServer();
                } else {
                    onForecastLoaded();
                }
            }
        }.execute();
    }
    
    public void saveForecastCache() {
        int countryId = spnCountry.getSelectedItemPosition();
        int cityId = spnCity.getSelectedItemPosition();

        DBSaveTask dbSaveTask = new DBSaveTask(this);
        mDbSaveWeakRef = new WeakReference<>(dbSaveTask);
        dbSaveTask.execute(countryId, cityId, getMeasureId());
    }

    private class DBSaveTask extends AsyncTask<Integer, Void, Void> {

        private WeakReference<DailyFragment> mFragmentWeakRef;

        private DBSaveTask(DailyFragment fragment) {
            this.mFragmentWeakRef = new WeakReference<>(fragment);
        }

        @Override
        protected Void doInBackground(Integer... params) {
            int countryId = params[0];
            int cityId = params[1];
            int measureId = params[2];
            mCacheManager.saveCache(mForecastList, countryId, cityId, measureId);
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            if (this.mFragmentWeakRef.get() != null) {
                hideRefresh();
            }
        }
    }

    private boolean isCacheSavingInProcess() {
        return this.mDbSaveWeakRef != null && this.mDbSaveWeakRef.get() != null &&
                !this.mDbSaveWeakRef.get().getStatus().equals(AsyncTask.Status.FINISHED);
    }

    private void onForecastLoaded() {
        fillForecast();
        hideRefresh();
    }

    private void initLocation(View view) {
        spnCountry = (Spinner) view.findViewById(R.id.spnCountry);
        spnCity = (Spinner) view.findViewById(R.id.spnCity);

        ArrayAdapter<CharSequence> countriesAdapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.countries_array, android.R.layout.simple_spinner_item);
        countriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnCountry.setAdapter(countriesAdapter);
        spnCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final int citiesArrayId;
                switch (position) {
                    case 0:
                    default: // Russia
                        mCountry = "RU";
                        citiesArrayId = R.array.ru_cities_array;
                        break;
                    case 1: // USA
                        mCountry = "US";
                        citiesArrayId = R.array.us_cities_array;
                        break;
                    case 2: // Netherlands
                        mCountry = "NL";
                        citiesArrayId = R.array.nl_cities_array;
                        break;
                }

                ArrayAdapter<CharSequence> citiesAdapter = ArrayAdapter.createFromResource(
                        getActivity(), citiesArrayId, android.R.layout.simple_spinner_item);
                citiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spnCity.setAdapter(citiesAdapter);
                spnCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mCity = getResources().getStringArray(citiesArrayId)[position];

                        if (isCacheSavingInProcess()) {
                            getForecastDelayed();
                        } else {
                            getForecast();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });

                // cached location (not called when changing country; then the city is set to 0 pos.)
                if (spnCountry.getSelectedItemPosition() == mCacheManager.getCountryId()) {
                    spnCity.setSelection(mCacheManager.getCityId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // cached location
        spnCountry.setSelection(mCacheManager.getCountryId());
    }

    @Override
    public void fillForecast() {
        Forecast nowForecast = mForecastList.get(0);
        fillLarge(nowForecast, true);
        fillHourly(nowForecast);
        fillDaily(nowForecast);
    }

    private void fillHourly(Forecast nowForecast) {
        mHourlyList.clear();

        for (Forecast dayForecast : mForecastList) {
            if (dayForecast.getDay() == nowForecast.getDay()) {
                mHourlyList.add(dayForecast);
            } else break;
        }

        mHourlyAdapter.notifyDataSetChanged();

        super.fillHourly();
    }

    /**
     * Fills the daily forecast choosing midday temperature.
     * @param nowForecast
     */
    private void fillDaily(Forecast nowForecast) {
        int currentDay = nowForecast.getDay();
        int daysNum = 0;
        int pos = 0;

        mDailyList.clear();
        while (daysNum < 4) {
            if (mForecastList.get(pos).getDay() == currentDay) {
                pos++;
                continue;
            }

            mDailyList.add(mForecastList.get(pos + Consts.MIDDAY_HOUR));
            currentDay = mForecastList.get(pos).getDay();
            daysNum++;
            pos += 8;
        }

        // last day "tail" (temperature of the last day can be presented not fully)
        int lastIndex = mForecastList.size() - 1;
        int rem = lastIndex - pos;
        if (rem > 0) {
            mDailyList.add(mForecastList.get(rem > Consts.MIDDAY_HOUR ? pos + Consts.MIDDAY_HOUR : lastIndex));
        }

        mDailyAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_daily, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_other) {
            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.set_measure)
                    .setSingleChoiceItems(R.array.temp_measure_array, getMeasureId(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setMeasureId(which);
                            dialog.dismiss();
                        }
                    }).show();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * At change measurement then change it in the daily forecast.
     * @param measureId
     */
    @Override
    public void setMeasureId(int measureId) {
        super.setMeasureId(measureId);

        fillLarge(mHourlyList.get(0), true);

        mDailyAdapter.setMeasureId(measureId);
        mDailyAdapter.notifyDataSetChanged();

        ((DailyActivity) getActivity()).setMeasureId(measureId);
    }

    /**
     * Boolean variables isOnline & isCached are used to reduce the function calls where
     * this is not necessary. Their values should not be changed. In other cases, invoked
     * the function with the same name, see below.
     */

    private boolean isOnline() {
        isOnline = Utils.isNetAvailable(getActivity());
        return isOnline;
    }

    private boolean isCached() {
        isCached = !mCacheManager.isEmpty();
        return isCached;
    }

    // show "pull to refresh", some actions are executed
    private void showRefresh() {
        vRefresh.post(new Runnable() {
            @Override
            public void run() {
                vRefresh.setRefreshing(true);
            }
        });
    }

    // hide "pull to refresh" & update location controls
    private void hideRefresh() {
        spnCountry.setEnabled(isOnline());
        spnCity.setEnabled(isOnline());

        vRefresh.setRefreshing(false);
    }

}
