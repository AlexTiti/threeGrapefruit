package com.findtech.threePomelos.mydevices.present;

import android.content.Context;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.SaveCallback;
import com.findtech.threePomelos.database.OperateDBUtils;
import com.findtech.threePomelos.entity.TravelInfoEntity;
import com.findtech.threePomelos.mydevices.activity.DeviceDetailActivity;
import com.findtech.threePomelos.mydevices.bean.DeviceCarBean;
import com.findtech.threePomelos.mydevices.model.NewAutoDetailModel;
import com.findtech.threePomelos.net.NetWorkRequest;
import com.findtech.threePomelos.sdk.base.mvp.BasePresenterMvp;
import com.findtech.threePomelos.sdk.manger.RxHelper;
import com.findtech.threePomelos.user.bean.UserInfo;
import com.findtech.threePomelos.utils.MyCalendar;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/06/02
 */
public class DeviceDetailPresent extends BasePresenterMvp<DeviceDetailActivity, NewAutoDetailModel> {
    @Override
    public NewAutoDetailModel createModel() {
        return new NewAutoDetailModel();
    }

    public void getAdultWeight() {
        if (model == null || mView == null) {
            return;
        }
        mRxManager.register(model.getAdultWeight()
                .firstElement()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<TravelInfoEntity>() {
                    @Override
                    public void accept(TravelInfoEntity travelInfoEntity) throws Exception {
                        mView.getCaloriesSuccess(travelInfoEntity);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.getCaloriesFailed(throwable.getMessage());
                    }
                }));
    }

    public void sendNameToServer(final String name, String selectAddress) {
        if (mView == null) {
            return;
        }

        NetWorkRequest.sendNameToServer(name, selectAddress, new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    mView.loadSuccess(name);
                } else {
                    mView.loadFailed(e.getMessage());
                }
            }
        });
    }

    public void sendWeightToServer(final String weight) {
        if (model == null || mView == null) {
            return;
        }
        mRxManager.register(model.saveAdultWeight(weight).compose(RxHelper.<String>rxSchedulerHelper())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        mView.sendWeightSuccess(s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.sendWeightFailed(throwable.getMessage());
                    }
                }));


    }

    public void updateDeviceList() {
        if (model == null || mView == null) {
            return;
        }
        mRxManager.register(model.getUserDeviceArray()
                .compose(RxHelper.<ArrayList<DeviceCarBean>>rxSchedulerHelper())
                .subscribe(new Consumer<ArrayList<DeviceCarBean>>() {
                    @Override
                    public void accept(ArrayList<DeviceCarBean> beanArrayList) throws Exception {
                        UserInfo.getInstance().setCarBeanArrayList(beanArrayList);
                    }
                }));
    }

    public void getTravelInfo(String selectAddress, Context context) {

        if (model == null || mView == null) {
            return;
        }

        mRxManager.register(model.getTravelInfo(selectAddress, context).
                firstElement()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<TravelInfoEntity>() {
                    @Override
                    public void accept(TravelInfoEntity travelInfoEntity) throws Exception {
                        mView.getTravelInfoSuccess(travelInfoEntity);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.getTravelInfoFailed(throwable.getMessage());
                    }
                }));
    }

    public void senTravelInfoToServer(final double today, final double total, final double speed,final String type) {

        if (model == null || mView == null) {
            return;
        }
        mRxManager.register(model.sendTravelInfoToServer(today, total,speed, type)
                .compose(RxHelper.<TravelInfoEntity>rxSchedulerHelper())
                .subscribe(new Consumer<TravelInfoEntity>() {
                    @Override
                    public void accept(TravelInfoEntity travelInfoEntity) throws Exception {
                        mView.sendTravelInfoToServerSuccess(travelInfoEntity);
                        model.updateTravelInfo();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.sendTravelInfoToServerFailed(throwable.getMessage());
                    }
                }));
    }

    /**
     * 保存到数据库
     *
     * @param today
     * @param context
     */
    public void addServerToDB(final String today, final Context context) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                String travelDate = MyCalendar.getToday();
                OperateDBUtils operateDBUtils = new OperateDBUtils(context);
                operateDBUtils.insertTravelToDB(travelDate, today);
            }
        }).compose(RxHelper.<String>rxSchedulerHelper())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {

                    }
                });

    }

    public void deleteBindDevice(String address) {

        if (model == null || mView == null) {
            return;
        }
        mRxManager.register(model.deleteDeviceBind(address)
                .compose(RxHelper.<String>rxSchedulerHelper())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        mView.unBindSuccess(s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.unBindFailed(throwable.getMessage());
                    }
                }));
    }
}
