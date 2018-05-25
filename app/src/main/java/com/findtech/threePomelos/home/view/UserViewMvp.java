package com.findtech.threePomelos.home.view;

import android.graphics.Bitmap;

import com.findtech.threePomelos.mydevices.bean.DeviceCarBean;
import com.findtech.threePomelos.sdk.base.mvp.Contract;
import java.util.ArrayList;
/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/05/05
 */

public interface UserViewMvp<T> extends Contract.ViewMvp<T>{

    /**
     * 获取设备列表
     * @param beanArrayList
     */
    void getDeviceSuccess(ArrayList<DeviceCarBean> beanArrayList);

    /**
     * 获取失败
     * @param message
     */
    void getDeviceFailed(String message);

    /**
     * 修改昵称成功
     * @param nickName
     */
    void changeNickNameSuccess(String nickName);

    /**
     * 修改昵称失败
     * @param message
     */
    void changeNickNameFailed(String message);

    /**
     * 修改头像成功
     * @param bitmap
     */
    void changePictureSuccess(Bitmap bitmap);

    /**
     * 修改头像失败
     * @param message
     */
    void changePictureFailed(String message);

    /**
     * 查询收藏歌曲
     * @param s
     */
    void getCollectMusic(String s);

    /**
     * 删除设备成功
     */
    void deleteDeviceSuccess();

    /**
     * 删除设备失败
     * @param message
     */
    void deleteDeviceFailed(String message);
    /**
     * 删除宝宝成功
     */
    void deleteBabySuccess();

    /**
     *  删除宝宝失败
     * @param message
     */
    void deleteBabyFailed(String message);

}
