package com.findtech.threePomelos.travel.view;

import com.findtech.threePomelos.sdk.base.mvp.Contract;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/04/19
 */

public interface CalendarViewMvp<T> extends Contract.ViewMvp<T>{

    /**
     * 加载出行分析成功
     * @param s
     */
    void loadTravelAnalysisSuccess(String s);

    /**
     * 加载出行分析失败
     * @param message
     */
    void loadTravelAnalysisFailed(String message);



}
