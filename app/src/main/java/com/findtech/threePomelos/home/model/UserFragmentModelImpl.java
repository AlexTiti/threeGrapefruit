package com.findtech.threePomelos.home.model;

import android.graphics.Bitmap;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.findtech.threePomelos.database.OperateDBUtils;
import com.findtech.threePomelos.entity.BabyInfoEntity;
import com.findtech.threePomelos.mydevices.bean.DeviceCarBean;
import com.findtech.threePomelos.net.NetWorkRequest;
import com.findtech.threePomelos.sdk.MyApplication;
import com.findtech.threePomelos.sdk.base.mvp.Contract;
import com.findtech.threePomelos.user.bean.UserInfo;
import com.findtech.threePomelos.utils.IContent;
import com.findtech.threePomelos.utils.PicOperator;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

import static com.findtech.threePomelos.net.NetWorkRequest.DEVICE_UUID;


/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/05/05
 */

public class UserFragmentModelImpl implements Contract.ModeMvp {

    public Observable<ArrayList<DeviceCarBean>> getUserDeviceArray() {

        IContent.getInstacne().addressList.clear();
        return Observable.create(new ObservableOnSubscribe<ArrayList<DeviceCarBean>>() {
            @Override
            public void subscribe(final ObservableEmitter<ArrayList<DeviceCarBean>> e) throws Exception {

                AVQuery<AVObject> query = NetWorkRequest.getBaseQuery(DEVICE_UUID);
                query.whereEqualTo("post", AVUser.getCurrentUser());
                query.orderByDescending("updatedAt");
                query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
                query.setMaxCacheAge(24 * 3600 * 7 * 1000);
                query.findInBackground(new FindCallback<AVObject>() {
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
                                if (address != null && address.length() != 0) {
                                    IContent.getInstacne().addressList.add(new DeviceCarBean(name, address, deviceType, functionType, company));
                                }
                            }
                            UserInfo.getInstance().setCarBeanArrayList(IContent.getInstacne().addressList);
                            e.onNext(IContent.getInstacne().addressList);
                        } else {
                            e.onError(exception);
                        }
                    }

                });
            }
        });
    }

    public Observable<ArrayList<BabyInfoEntity>> getBabyList() {
        return Observable.create(new ObservableOnSubscribe<ArrayList<BabyInfoEntity>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<BabyInfoEntity>> e) throws Exception {
                e.onNext(UserInfo.getInstance().getBabyInfoEntities());
            }
        });
    }

    public Observable<String> deleteDevice(final String deviceNumAddress) {

        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> e) throws Exception {
                NetWorkRequest.deleteBlueToothIsBind(deviceNumAddress, new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException exception) {
                        if (exception == null) {
                            for (int i = 0; i < list.size(); i++) {
                                AVObject avObjects = list.get(i);
                                avObjects.deleteInBackground(new DeleteCallback() {
                                    @Override
                                    public void done(AVException exception) {
                                        if (exception == null) {
                                            e.onNext("A");
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });
    }

    public Observable<String> sendBabyName(final String name) {


        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> e) throws Exception {
                AVQuery<AVObject> query = new AVQuery<>(NetWorkRequest.USER_INFO);
                query.getInBackground(AVUser.getCurrentUser().getObjectId(), new GetCallback<AVObject>() {
                    @Override
                    public void done(AVObject avObject, AVException exception) {
                        if (exception == null && avObject != null) {
                            avObject.put(OperateDBUtils.NICKNAME, name);
                            avObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException exception) {
                                    if (exception == null) {
                                        e.onNext(name);
                                    } else {
                                        e.onError(exception);
                                    }
                                }
                            });
                        } else {
                            e.onError(exception);
                        }
                    }
                });
            }
        });
    }

    public Observable<Bitmap> saveFileHeadImage(final Bitmap bitmap) {
        return Observable.create(new ObservableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(final ObservableEmitter<Bitmap> e) throws Exception {
                AVQuery<AVObject> query = new AVQuery<>(NetWorkRequest.USER_INFO);
                query.getInBackground(AVUser.getCurrentUser().getObjectId(), new GetCallback<AVObject>() {
                    @Override
                    public void done(AVObject avObject, AVException exception) {
                        if (exception == null) {
                            AVFile avFile = new AVFile(MyApplication.getInstance().getHeadIconPath(),
                                    PicOperator.bitmap2Bytes(bitmap));
                            avFile.saveInBackground();
                            avObject.put(OperateDBUtils.HEADIMG, avFile);
                            avObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException exception) {
                                    if (exception == null) {
                                        e.onNext(bitmap);
                                    } else {
                                        e.onError(exception);
                                    }
                                }
                            });
                        } else {
                            e.onError(exception);
                        }
                    }
                });
            }
        });
    }

    public Observable deleteBaby(final String baby) {

        return Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(final ObservableEmitter e) throws Exception {
                e.onNext(null);
            }
        });
    }

}
