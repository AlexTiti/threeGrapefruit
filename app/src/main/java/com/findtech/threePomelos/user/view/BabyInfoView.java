package com.findtech.threePomelos.user.view;

import android.graphics.Bitmap;

import com.findtech.threePomelos.sdk.base.mvp.Contract;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/05/15
 */

public interface BabyInfoView<T> extends Contract.ViewMvp<T> {

    /**
     * 修改昵称成功
     *
     * @param name 昵称
     */
    void saveNameSuccess(String name);

    /**
     * 修改昵称失败
     *
     * @param message
     */
    void saveNameFailed(String message);

    /**
     * 保存头像成功
     *
     * @param bitmap
     */
    void saveHeadViewSuccess(Bitmap bitmap);

    /**
     * 保存失败
     *
     * @param message
     */
    void saveHeadViewFailed(String message);

    /**
     * 保存成功
     *
     * @param sex
     */
    void saveSexSuccess(String sex);

    /**
     * 保存失败
     *
     * @param message
     */
    void saveSexFailed(String message);

    /**
     * 保存成功
     *
     * @param birthDay
     */
    void saveBirthdaySuccess(String birthDay);

    /**
     * 保存失败
     *
     * @param message
     */
    void saveBirthdayFailed(String message);
}
