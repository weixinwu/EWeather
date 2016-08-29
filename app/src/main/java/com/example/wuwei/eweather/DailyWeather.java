package com.example.wuwei.eweather;

/**
 * Created by wuwei on 8/27/2016.
 */
public class DailyWeather {
    long time;
    String summary;
    String icon;
    long sumrise;
    long sumset;
    double precipIntensity;
    double precipProbability;
    double temperature;
    double temperature_min;
    double temperature_max;
    double feels_like;
    double humidity;
    double windSpeed;
    double visibility;
    double cloud_cover;
    double pressure;
    double ozone;

    public DailyWeather(long time, String summary, String icon, long sumrise, long sumset, double precipIntensity, double precipProbability, double temperature, double temperature_min, double temperature_max, double feels_like, double humidity, double windSpeed, double visibility, double cloud_cover, double pressure, double ozone) {
        this.time = time;
        this.summary = summary;
        this.icon = icon;
        this.sumrise = sumrise;
        this.sumset = sumset;
        this.precipIntensity = precipIntensity;
        this.precipProbability = precipProbability;
        this.temperature = temperature;
        this.temperature_min = temperature_min;
        this.temperature_max = temperature_max;
        this.feels_like = feels_like;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.visibility = visibility;
        this.cloud_cover = cloud_cover;
        this.pressure = pressure;
        this.ozone = ozone;
    }
}
