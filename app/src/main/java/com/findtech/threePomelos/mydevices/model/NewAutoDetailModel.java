package com.findtech.threePomelos.mydevices.model;

import android.content.Context;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.findtech.threePomelos.database.OperateDBUtils;
import com.findtech.threePomelos.entity.TravelInfoEntity;
import com.findtech.threePomelos.mydevices.bean.DeviceCarBean;
import com.findtech.threePomelos.net.NetWorkRequest;
import com.findtech.threePomelos.sdk.base.mvp.Contract;
import com.findtech.threePomelos.utils.IContent;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;

import static com.findtech.threePomelos.net.NetWorkRequest.DEVICE_UUID;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/05/14
 */

public class NewAutoDetailModel implements Contract.ModeMvp {

    /**
     * 获取用户绑定的设备列表
     *
     * @return
     */
    public Observable<ArrayList<DeviceCarBean>> getUserDeviceArray() {

        IContent.getInstacne().addressList.clear();
        return Observable.create(new ObservableOnSubscribe<ArrayList<DeviceCarBean>>() {
            @Override
            public void subscribe(final ObservableEmitter<ArrayList<DeviceCarBean>> e) throws Exception {

                AVQuery<AVObject> query = NetWorkRequest.getBaseQuery(DEVICE_UUID);
                query.whereEqualTo("post", AVUser.getCurrentUser())
                        .orderByDescending("updatedAt")
                        .findInBackground(new FindCallback<AVObject>() {
                            @Override
                            public void done(List<AVObject> list, AVException exception) {
                                if (exception == null) {
                                    IContent.getInstacne().addressList.clear();
                                    for (AVObject avObject : list) {
                                        String address = avObject.getString("bluetoothDeviceId");
                                        String name = avObject.getString("bluetoothName");
                                        String deviceType = avObject.getString(NetWorkRequest.DEVICEIDENTIFITER);
                                        String functionType = avObject.getString(NetWorkRequest.FUNCTION_TYPE);
                                        String company = avObject.getString(NetWorkRequest.COMPANY);
                                        if (address != null && address.length() != 0 && address != "") {
                                            IContent.getInstacne().addressList.add(new DeviceCarBean(name, address, deviceType, functionType, company));
                                        }
                                    }
                                    e.onNext(IContent.getInstacne().addressList);
                                } else {
                                    e.onError(exception);
                                }
                            }

                        });
            }
        });
    }

    /**
     * 获取用户卡路里
     *
     * @return
     */

    public Observable<TravelInfoEntity> getCalories() {

        Observable<TravelInfoEntity> observableNet = getCaloriesFromNet();
        Observable<TravelInfoEntity> observableMemory = Observable.create(new ObservableOnSubscribe<TravelInfoEntity>() {
            @Override
            public void subscribe(final ObservableEmitter<TravelInfoEntity> e) throws Exception {
                //查询数据库.设置完所有信息
                final TravelInfoEntity bean = TravelInfoEntity.getInstance();
                if ("0.0".equals(bean.getAdultWeight()) || "0.0".equals(bean.getTodayCalor())) {
                    e.onComplete();
                } else {
                    e.onNext(bean);
                }
            }
        });

        return Observable.concat(observableMemory,  observableNet);
    }

    public Observable<TravelInfoEntity> getCaloriesFromNet() {
        return Observable.create(new ObservableOnSubscribe<TravelInfoEntity>() {
            @Override
            public void subscribe(final ObservableEmitter<TravelInfoEntity> e) throws Exception {
                NetWorkRequest.getCalorFromServer(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException exception) {
                        if (exception == null) {
                            TravelInfoEntity bean = TravelInfoEntity.getInstance();
                            for (int i = 0; i < list.size(); i++) {
                                AVObject avObject = list.get(i);
                                String calories = avObject.getString("totalCaloriesValue");
                                String adultWeight = avObject.getString("adultWeight");
                                bean.setAdultWeight(adultWeight);
                                bean.setTotalCalor(calories);
                            }
                            e.onNext(bean);
                        } else {
                            e.onError(exception);
                        }
                    }
                });
            }
        });
    }

    /**
     * 获取运动信息
     *
     * @param selectAddress
     * @return
     */
    public Observable<TravelInfoEntity> getTravelInfo(final String selectAddress, Context context) {

        final TravelInfoEntity bean = TravelInfoEntity.getInstance();

        Observable<TravelInfoEntity> observableMemory = Observable.create(new ObservableOnSubscribe<TravelInfoEntity>() {
            @Override
            public void subscribe(final ObservableEmitter<TravelInfoEntity> e) throws Exception {
                //查询数据库.设置完所有信息
                if ("0.0".equals(bean.getTodayMileage()) || "0.0".equals(bean.getTotalMileage())) {
                    e.onComplete();
                } else {
                    e.onNext(bean);
                }
            }
        });


        Observable<TravelInfoEntity> observableNet = getTravelFromNet(selectAddress);
        return Observable.concat(observableMemory,  observableNet);
    }

    public Observable<TravelInfoEntity> getTravelFromNet(final String selectAddress) {
        return Observable.create(new ObservableOnSubscribe<TravelInfoEntity>() {
            @Override
            public void subscribe(final ObservableEmitter<TravelInfoEntity> e) throws Exception {
                NetWorkRequest.getTravelInfo(selectAddress, new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException exception) {
                        if (exception == null) {
                            TravelInfoEntity bean = TravelInfoEntity.getInstance();
                            if (list.size() != 0) {
                                AVObject avObject = list.get(0);
                                bean.setTodayMileage(avObject.getString(OperateDBUtils.TODAY_MILEAGE));
                                bean.setTotalMileage(avObject.getString(OperateDBUtils.TOTAL_MILEAGE));
                                bean.setAverageSpeed(avObject.getString(OperateDBUtils.AVERAGE_SPEED));
                                String todayCalorie = avObject.getString("calorieValue");
                                bean.setTodayCalor(todayCalorie);
                                bean.setDeviceAddress(selectAddress);
                                e.onNext(bean);
                            }
                        } else {
                            e.onError(exception);
                        }
                    }
                });
            }
        });
    }

    /**
     * 保存数据到服务端
     *
     * @param selectAddress
     * @param today
     * @param total
     * @param speed
     * @param type
     */
    public Observable<TravelInfoEntity> sendTravelInfoToServer(final String selectAddress, final double today, final double total, final String speed, final String type) {

        final TravelInfoEntity bean = TravelInfoEntity.getInstance();
        bean.setTodayMileage(String.valueOf(today));
        bean.setTodayMileage(String.valueOf(total));

        Observable<TravelInfoEntity> sendNet = Observable.create(new ObservableOnSubscribe<TravelInfoEntity>() {

            @Override
            public void subscribe(final ObservableEmitter<TravelInfoEntity> e) throws Exception {
                NetWorkRequest.saveDataToServerWithDay(selectAddress, today, total, speed, type, new SaveCallback() {
                    @Override
                    public void done(AVException exception) {
                        if (exception == null) {
                            e.onNext(bean);
                        } else {
                            e.onError(exception);
                        }
                    }
                });
            }
        });

        final Observable<TravelInfoEntity> newTravelInfo = Observable.zip(getCaloriesFromNet(), getTravelFromNet(selectAddress),
                new BiFunction<TravelInfoEntity, TravelInfoEntity, TravelInfoEntity>() {
                    @Override
                    public TravelInfoEntity apply(TravelInfoEntity entity, TravelInfoEntity entity2) throws Exception {
                        entity2.setAdultWeight(entity.getAdultWeight());
                        entity2.setTotalCalor(entity.getTotalCalor());
                        return entity2;
                    }
                });

        return sendNet.flatMap(new Function<TravelInfoEntity, ObservableSource<TravelInfoEntity>>() {
            @Override
            public ObservableSource<TravelInfoEntity> apply(TravelInfoEntity entity) throws Exception {
                return newTravelInfo;
            }
        });
    }


    /**
     * 解除绑定
     *
     * @param deviceNum
     * @return
     */
    public Observable<String> deleteDeviceBind(final String deviceNum) {

        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> e) throws Exception {

                NetWorkRequest.deleteBlueToothIsBind(deviceNum, new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, final AVException exception) {
                        if (exception == null) {
                            for (int i = 0; i < list.size(); i++) {
                                AVObject avObjects = list.get(i);
                                avObjects.deleteInBackground(new DeleteCallback() {
                                    @Override
                                    public void done(AVException exception) {
                                        if (exception == null) {
                                            e.onNext(deviceNum);
                                        } else {
                                            e.onError(exception);
                                        }
                                    }
                                });
                            }
                        } else {
                            e.onError(exception);
                        }
                    }
                });
            }
        });
    }

}
