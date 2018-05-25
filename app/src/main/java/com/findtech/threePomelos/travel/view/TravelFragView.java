package com.findtech.threePomelos.travel.view;

import com.findtech.threePomelos.utils.weather.WeatherBean;
import com.findtech.threePomelos.sdk.base.mvp.Contract;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/05/14
 */

public interface TravelFragView<T> extends Contract.ViewMvp<T> {

    /**
     * 加载成功
     * @param bean
     */
    void getWeatherSuccess(WeatherBean bean);

    /**
     * 加载失败
     * @param message
     */
    void getWeatherFailed(String message);

    /**
     * 发送天气成功
     */
    void sendWeatherSuccess();

    /**
     * 发送天气失败
     */
    void sendWeatherFailed();

}
