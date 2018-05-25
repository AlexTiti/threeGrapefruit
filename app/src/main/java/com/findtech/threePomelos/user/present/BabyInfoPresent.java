package com.findtech.threePomelos.user.present;

import android.content.Context;
import android.graphics.Bitmap;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.entity.BabyInfoEntity;
import com.findtech.threePomelos.sdk.MyApplication;
import com.findtech.threePomelos.sdk.base.mvp.BasePresenterMvp;
import com.findtech.threePomelos.sdk.manger.RxHelper;
import com.findtech.threePomelos.user.activity.BabyInfoActivity;
import com.findtech.threePomelos.user.model.BabyInfoModel;
import com.findtech.threePomelos.utils.PicOperator;

import io.reactivex.functions.Consumer;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/05/15
 */

public class BabyInfoPresent extends BasePresenterMvp<BabyInfoActivity, BabyInfoModel> {

    @Override
    public BabyInfoModel createModel() {
        return new BabyInfoModel();
    }

    public void saveBabyName(final String name, String objectId, final Context context, final BabyInfoEntity babyInfoEntity) {

        if (name == null || objectId == null) {
            return;
        }
        if (model == null || mView == null) {
            return;
        }

        mRxManager.register(model.sendBabyName(name, objectId).compose(RxHelper.<String>rxSchedulerHelper())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        mView.saveNameSuccess(s);
                        babyInfoEntity.setBabyName(name,
                                context.getResources().getString(R.string.baby_niName));
                        model.updateBabyInfoDB(babyInfoEntity, context);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.saveNameFailed(throwable.getMessage());
                    }
                }));
    }

    public void saveBabySex(final String sex, String objectId, final Context context, final BabyInfoEntity babyInfoEntity) {

        if (sex == null || objectId == null) {
            return;
        }
        if (model == null || mView == null) {
            return;
        }

        mRxManager.register(model.saveBabySex(sex, objectId).compose(RxHelper.<String>rxSchedulerHelper())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        mView.saveSexSuccess(s);
                        babyInfoEntity.setBabySex(sex,
                                context.getResources().getString(R.string.princess));
                        model.updateBabyInfoDB(babyInfoEntity, context);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.saveSexFailed(throwable.getMessage());
                    }
                }));
    }

    public void saveBabyBirth(final String birthday, String objectId, final Context context, final BabyInfoEntity babyInfoEntity) {

        if (birthday == null || objectId == null) {
            return;
        }
        if (model == null || mView == null) {
            return;
        }

        mRxManager.register(model.saveBabyBirthDay(birthday, objectId).compose(RxHelper.<String>rxSchedulerHelper())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        mView.saveBirthdaySuccess(s);
                        babyInfoEntity.setBirthday(birthday, context.getString(R.string.input_birth_baby));
                        model.updateBabyInfoDB(babyInfoEntity, context);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.saveBirthdayFailed(throwable.getMessage());
                    }
                }));
    }

    public void saveBabyNative(final String babyNative, String objectId, final Context context, final BabyInfoEntity babyInfoEntity) {

        if (babyNative == null || objectId == null) {
            return;
        }
        if (model == null || mView == null) {
            return;
        }

        mRxManager.register(model.saveBabyNative(babyNative, objectId).compose(RxHelper.<String>rxSchedulerHelper())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        mView.loadSuccess(s);
                        babyInfoEntity.setBabyNative(babyNative, "");
                        model.updateBabyInfoDB(babyInfoEntity, context);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.loadFailed(throwable.getMessage());
                    }
                }));
    }

    public void saveHeadImage(final Bitmap bitmap, final String objectId, final Context context) {

        if (bitmap == null || objectId == null) {
            return;
        }
        if (model == null || mView == null) {
            return;
        }

        mRxManager.register(model.saveFileHeadImage(bitmap, objectId).compose(RxHelper.<Bitmap>rxSchedulerHelper())
                .subscribe(new Consumer<Bitmap>() {
                    @Override
                    public void accept(Bitmap s) throws Exception {
                        mView.saveHeadViewSuccess(bitmap);
                        PicOperator.saveToData(context, bitmap, MyApplication.getInstance().getBabyHeadIconPath(objectId));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.saveHeadViewFailed(throwable.getMessage());
                    }
                }));
    }
}
