package io.github.kbiakov.weathera.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.DateFormatSymbols;


public class Utils {

    public static boolean isNetAvailable(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static String getMonthFromInt(int month) {
        return new DateFormatSymbols().getMonths()[month];
    }

}
