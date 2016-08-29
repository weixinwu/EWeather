package com.example.wuwei.eweather;

import android.text.format.DateUtils;

import java.util.ArrayList;

/**
 * Created by wuwei on 8/27/2016.
 */
public class ResponseBody {
    ArrayList<HourlyWeatherInfo> hourlyWeatherInfos;
    ArrayList<DailyWeather> dailyWeathers;
    String today_summary;
    HourlyWeatherInfo currentWeather;

    public ResponseBody(ArrayList<HourlyWeatherInfo> hourlyWeatherInfos, ArrayList<DailyWeather> dailyWeathers, String today_summary,HourlyWeatherInfo currentWeather) {
        this.hourlyWeatherInfos = hourlyWeatherInfos;
        this.dailyWeathers = dailyWeathers;
        this.today_summary = today_summary;
        this.currentWeather = currentWeather;
    }

}
