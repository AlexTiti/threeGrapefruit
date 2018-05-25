package com.findtech.threePomelos.mydevices.view;

import com.findtech.threePomelos.entity.TravelInfoEntity;
import com.findtech.threePomelos.sdk.base.mvp.Contract;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/05/15
 */

public interface NewAutoDeviceView<T> extends Contract.ViewMvp<T> {

    /**
     * 获取卡路里成功
     * @param entity
     */
    void getCaloriesSuccess(TravelInfoEntity entity);

    /**
     * 获取卡路里失败
     * @param message
     */
    void getCaloriesFailed(String message);

    /**
     * 获取出行数据成功
     * @param entity
     */
    void getTravelInfoSuccess(TravelInfoEntity entity);

    /**
     * 获取出行数据失败
     * @param message
     */
    void getTravelInfoFailed(String message);

    /**
     * 上传数据成功
     * @param entity
     */
    void sendTravelInfoToServerSuccess(TravelInfoEntity entity);

    /**
     * 上传数据失败
     * @param message
     */
    void sendTravelInfoToServerFailed(String message);

    /**
     * 上传体重成功
     * @param weight
     */
    void sendWeightSuccess(String weight);

    /**
     * 上传体重失败
     * @param message
     */
    void sendWeightFailed(String message);

    /**
     * 解除成功
     * @param address
     */
    void unBindSuccess(String address);

    /**
     * 解除失败
     * @param message
     */
    void unBindFailed(String message);

}
