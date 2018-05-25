package com.findtech.threePomelos.home.presenter;

import android.content.Context;
import android.graphics.Bitmap;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.database.OperateDBUtils;
import com.findtech.threePomelos.entity.BabyInfoEntity;
import com.findtech.threePomelos.home.fragment.UserFragment;
import com.findtech.threePomelos.home.model.UserFragmentModelImpl;
import com.findtech.threePomelos.music.model.MusicCollectModelImpl;
import com.findtech.threePomelos.mydevices.bean.DeviceCarBean;
import com.findtech.threePomelos.sdk.MyApplication;
import com.findtech.threePomelos.sdk.base.mvp.BasePresenterMvp;
import com.findtech.threePomelos.sdk.manger.RxHelper;
import com.findtech.threePomelos.user.bean.UserInfo;
import com.findtech.threePomelos.utils.PicOperator;
import com.findtech.threePomelos.utils.SpUtils;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/05/05
 */

public class UserFragmentPresent extends BasePresenterMvp<UserFragment, UserFragmentModelImpl> {

    @Override
    public UserFragmentModelImpl createModel() {
        return new UserFragmentModelImpl();
    }

    public void getUserDevice() {
        if (model == null || mView == null) {
            return;
        }
        mRxManager.register(model.getUserDeviceArray().compose(
                RxHelper.<ArrayList<DeviceCarBean>>rxSchedulerHelper())
                .subscribe(new Consumer<ArrayList<DeviceCarBean>>() {
                    @Override
                    public void accept(ArrayList<DeviceCarBean> beanArrayList) throws Exception {
                        mView.getDeviceSuccess(beanArrayList);
                        UserInfo.getInstance().setCarBeanArrayList(beanArrayList);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.getDeviceFailed(throwable.getMessage());
                    }
                }));
    }

    public void getBabyList() {

        if (model == null || mView == null) {
            return;
        }
        mRxManager.register(model.getBabyList().compose(
                RxHelper.<ArrayList<BabyInfoEntity>>rxSchedulerHelper()
        ).subscribe(new Consumer<ArrayList<BabyInfoEntity>>() {
            @Override
            public void accept(ArrayList<BabyInfoEntity> strings) throws Exception {
                mView.loadSuccess(strings);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                mView.loadFailed(throwable.getMessage());
            }
        }));
    }

    public void getCollectMusicCount(Context context) {

        if (model == null || mView == null) {
            return;
        }
        final MusicCollectModelImpl model = new MusicCollectModelImpl();
        mRxManager.register(model.getMusicCollectCount(context)
                .compose(RxHelper.<String>rxSchedulerHelper())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String aDouble) throws Exception {
                        mView.getCollectMusic(aDouble);
                    }
                }));
    }

    public void deleteDevice(String deviceNumAddress) {

        if (model == null || mView == null) {
            return;
        }
        mRxManager.register(model.deleteDevice(deviceNumAddress)
                .compose(RxHelper.<String>rxSchedulerHelper())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String o) throws Exception {
                        mView.deleteDeviceSuccess();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.deleteDeviceFailed(throwable.getMessage());
                    }
                }));
    }

    public void saveBabyName(final String name, final Context context) {

        if (name == null) {
            return;
        }
        if (model == null || mView == null) {
            return;
        }

        mRxManager.register(model.sendBabyName(name).compose(RxHelper.<String>rxSchedulerHelper())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        UserInfo.getInstance().setNickName(name);
                        mView.changeNickNameSuccess(s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.changeNickNameFailed(throwable.getMessage());
                    }
                }));
    }

    public void saveHeadImage(final Bitmap bitmap, final Context context) {

        if (bitmap == null) {
            return;
        }
        if (model == null || mView == null) {
            return;
        }

        mRxManager.register(model.saveFileHeadImage(bitmap).compose(RxHelper.<Bitmap>rxSchedulerHelper())
                .subscribe(new Consumer<Bitmap>() {
                    @Override
                    public void accept(Bitmap s) throws Exception {
                        mView.changePictureSuccess(bitmap);
                        PicOperator.saveToData(context, bitmap, MyApplication.getInstance().getHeadIconPath());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.changePictureFailed(throwable.getMessage());
                    }
                }));
    }

    public void deleteBaby(String baby) {

        if (model == null || mView == null) {
            return;
        }
        mRxManager.register(model.deleteBaby(baby)
                .compose(RxHelper.rxSchedulerHelper())
                .subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {
                        mView.deleteBabySuccess();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.deleteBabyFailed(throwable.getMessage());
                    }
                }));
    }

}
