package io.github.kbiakov.weathera.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import io.github.kbiakov.weathera.R;
import io.github.kbiakov.weathera.models.Forecast;


public class HourlyAdapter extends ArrayAdapter<Forecast> {

    private Context mContext;
    private ArrayList<Forecast> mForecastList;
    private boolean mIsLightColor = false;
    private int mMeasureId;

    private static class ViewHolder {
        private ImageView ivPic;
        private TextView tvTemp;
        private TextView tvWind;
    }

    public HourlyAdapter(Context context, ArrayList<Forecast> forecastList, int measureId) {
        super(context, R.layout.item_hourly, forecastList);
        this.mContext = context;
        this.mForecastList = forecastList;
        this.mMeasureId = measureId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_hourly, null);

            vh = new ViewHolder();
            vh.ivPic = (ImageView) convertView.findViewById(R.id.ivPic);
            vh.tvTemp = (TextView) convertView.findViewById(R.id.tvTemp);
            vh.tvWind = (TextView) convertView.findViewById(R.id.tvWind);

            if (mIsLightColor) {
                vh.tvTemp.setTextColor(mContext.getResources().getColor(R.color.text_black));
                vh.tvWind.setTextColor(mContext.getResources().getColor(R.color.text_dark));
            }

            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        Forecast forecast = mForecastList.get(position);

        vh.ivPic.setImageResource(R.drawable.no_image);
        if (forecast.getPicUrl() != null) {
            ImageLoader.getInstance().displayImage(forecast.getPicUrl(), vh.ivPic);
        }

        vh.tvTemp.setText(getTemperature(forecast));
        vh.tvWind.setText(forecast.getWindSpeed());

        return convertView;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    public void setLightColor(boolean pred) {
        this.mIsLightColor = pred;
    }

    public void setMeasureId(int measureId) {
        mMeasureId = measureId;
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
