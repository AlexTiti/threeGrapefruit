package com.findtech.threePomelos.user.model;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.database.OperateDBUtils;
import com.findtech.threePomelos.entity.BabyInfoEntity;
import com.findtech.threePomelos.net.NetWorkRequest;
import com.findtech.threePomelos.sdk.MyApplication;
import com.findtech.threePomelos.sdk.base.mvp.Contract;
import com.findtech.threePomelos.sdk.manger.RxHelper;
import com.findtech.threePomelos.user.activity.BabyInfoActivity;
import com.findtech.threePomelos.utils.MyCalendar;
import com.findtech.threePomelos.utils.PicOperator;

import java.text.ParseException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;


/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/05/15
 */

public class BabyInfoModel implements Contract.ModeMvp {

    public Observable<String> sendBabyName(final String name, final String objectId) {


        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> e) throws Exception {
                AVQuery<AVObject> query = new AVQuery<>(NetWorkRequest.BABY_INFO);
                query.getInBackground(objectId, new GetCallback<AVObject>() {
                    @Override
                    public void done(AVObject avObject, AVException exception) {
                        if (exception == null && avObject != null) {
                            avObject.put(OperateDBUtils.BABYNAME, name);
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


    public Observable<String> saveBabySex(final String sex, final String objectId) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> e) throws Exception {
                AVQuery<AVObject> query = new AVQuery<>(NetWorkRequest.BABY_INFO);
                query.getInBackground(objectId, new GetCallback<AVObject>() {
                    @Override
                    public void done(AVObject avObject, AVException exception) {
                        if (exception == null) {
                            avObject.put(OperateDBUtils.BABYSEX, sex);
                            avObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException exception) {
                                    if (exception == null) {
                                        e.onNext(sex);
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

    public Observable<String> saveBabyBirthDay(final String birthDay, final String objectId) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> e) throws Exception {

                AVQuery<AVObject> query = new AVQuery<>(NetWorkRequest.BABY_INFO);
                query.getInBackground(objectId, new GetCallback<AVObject>() {
                    @Override
                    public void done(AVObject avObject, AVException exception) {
                        if (exception == null) {
                            avObject.put(OperateDBUtils.BIRTHDAY, birthDay);
                            avObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException exception) {
                                    if (exception == null) {
                                        e.onNext(birthDay);
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

    public Observable<String> saveBabyNative(final String nativeBaby, final String objectId) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> e) throws Exception {
                AVQuery<AVObject> query = new AVQuery<>(NetWorkRequest.BABY_INFO);
                query.getInBackground(objectId, new GetCallback<AVObject>() {
                    @Override
                    public void done(AVObject avObject, AVException exception) {
                        if (exception == null) {
                            avObject.put(OperateDBUtils.BABYNATIVE, nativeBaby);
                            avObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException exception) {

                                    if (exception == null) {
                                        e.onNext(nativeBaby);
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

    public Observable<Bitmap> saveFileHeadImage(final Bitmap bitmap, final String objectId) {
        return Observable.create(new ObservableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(final ObservableEmitter<Bitmap> e) throws Exception {
                AVQuery<AVObject> query = new AVQuery<>(NetWorkRequest.BABY_INFO);
                query.getInBackground(objectId, new GetCallback<AVObject>() {
                    @Override
                    public void done(AVObject avObject, AVException exception ) {
                        if (exception  == null) {
                            AVFile avFile = new AVFile(MyApplication.getInstance().getBabyHeadIconPath(objectId),
                                    PicOperator.bitmap2Bytes(bitmap));
                            avFile.saveInBackground();
                            avObject.put(OperateDBUtils.HEADIMG, avFile);
                            avObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException exception ) {
                                    if (exception  == null) {
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

    public void updateBabyInfoDB(final BabyInfoEntity babyInfoEntity, final Context context) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                String where = OperateDBUtils.USER_ID + " = ? ";
                ContentValues values = new ContentValues();
                values.put(OperateDBUtils.USER_ID, AVUser.getCurrentUser().getObjectId());
                values.put(OperateDBUtils.BABY_INFO_OBJECT_ID, babyInfoEntity.getBabyInfoObjectId());
                values.put(OperateDBUtils.BABYNAME, babyInfoEntity.getBabyName());
                values.put(OperateDBUtils.BABYSEX, babyInfoEntity.getBabySex());
                values.put(OperateDBUtils.BIRTHDAY, babyInfoEntity.getBirthday());
                values.put(OperateDBUtils.BABYNATIVE, babyInfoEntity.getBabyNative());
                values.put(OperateDBUtils.HEADIMG, babyInfoEntity.getImage());
                values.put(OperateDBUtils.IS_BIND, babyInfoEntity.getIsBind() ? 1 : 0);
                values.put(OperateDBUtils.BLUETOOTH_DEVICE_ID, babyInfoEntity.getBabyInfoObjectId());
                context.getContentResolver().update(OperateDBUtils.BABYINFO_URI, values, where,
                        new String[]{AVUser.getCurrentUser().getObjectId()});
            }
        }).compose(RxHelper.<Integer>rxSchedulerHelper()
        ).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        });

    }
}
