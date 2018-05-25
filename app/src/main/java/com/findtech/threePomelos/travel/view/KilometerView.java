package com.findtech.threePomelos.travel.view;

import com.findtech.threePomelos.sdk.base.mvp.Contract;
import com.findtech.threePomelos.travel.bean.KilometerBean;

import java.util.ArrayList;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/03/29
 */

public interface KilometerView<T> extends Contract.ViewMvp<T> {

    /**
     * 获取当天里程的数据
     * @param travelBeans
     */
    void getDaySuccess(ArrayList<KilometerBean> travelBeans);

    /**
     * 获取数据为空
     */
    void getEmptyData();
}
