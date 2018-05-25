package com.findtech.threePomelos.utils.weather;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/05/09
 */

public class WeatherBean {

    private static WeatherBean mWeatherBean;

    private String codeWeather;
    private String textWeather;
    private String temperature = "0";

    private String quality = " ";
    private WeatherBean() {
    }

    public static WeatherBean getInstance() {
        if (mWeatherBean == null){
            synchronized (WeatherBean.class){
                if (mWeatherBean == null){
                    mWeatherBean = new WeatherBean();
                }
            }
        }
        return mWeatherBean;
    }

    public String getCodeWeather() {
        return codeWeather;
    }

    public void setCodeWeather(String codeWeather) {
        this.codeWeather = codeWeather;
    }

    public String getTextWeather() {
        return textWeather;
    }

    public void setTextWeather(String textWeather) {
        this.textWeather = textWeather;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }
}
