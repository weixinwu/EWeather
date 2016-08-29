package com.example.wuwei.eweather;

/**
 * Created by wuwei on 8/27/2016.
 */
public class HourlyWeatherInfo {

    long time;
    String summary;
    String icon;
    double precipIntensity;
    double precipProbability;
    double temperature;
    double feels_like;
    double humidity;
    double windSpeed;
    double visibility;
    double cloud_cover;
    double pressure;
    double ozone;

    public HourlyWeatherInfo(long time, String summary, String icon, double precipIntensity, double precipProbability, double temperature, double feels_like, double humidity, double windSpeed, double visibility, double cloud_cover, double pressure, double ozone) {
        this.time = time;
        this.summary = summary;
        this.icon = icon;
        this.precipIntensity = precipIntensity;
        this.precipProbability = precipProbability;
        this.temperature = temperature;
        this.feels_like = feels_like;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.visibility = visibility;
        this.cloud_cover = cloud_cover;
        this.pressure = pressure;
        this.ozone = ozone;
    }

}
