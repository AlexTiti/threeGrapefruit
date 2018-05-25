package com.findtech.threePomelos.travel.view;

import com.findtech.threePomelos.sdk.base.mvp.Contract;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/03/24
 */

public interface FrequentyMvpView<T> extends Contract.ViewMvp<T> {

    /**
     * 获取月频率
     * @param t
     */
    void getFrequencyMonth(T t);

    /**
     * 获取月频率失败
     * @param message
     */
    void getFrequencyMonthFaild(String message);

    /**
     * 获取数据为空
     */
    void getFrequencyMonthNetError();
}
