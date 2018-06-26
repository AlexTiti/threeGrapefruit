package com.findtech.threePomelos.user.model;

import android.content.Context;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.findtech.threePomelos.database.OperateDBUtils;
import com.findtech.threePomelos.net.NetWorkRequest;
import com.findtech.threePomelos.sdk.base.mvp.Contract;
import com.findtech.threePomelos.utils.RequestUtils;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

import static com.findtech.threePomelos.net.NetWorkRequest.BABY_INFO;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/06/01
 */
public class SetNickNameModel implements Contract.ModeMvp {

    public Observable<String> saveNewBaby(final String babySex, final String birthdayBaby,final String name,final Context context) {

      return   Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> e) throws Exception {
                final AVObject post = new AVObject(NetWorkRequest.BABY_INFO);
                post.put(RequestUtils.BABYSEX, babySex);
                post.put(RequestUtils.BIRTHDAY, birthdayBaby);
                post.put(OperateDBUtils.BABYNAME, name);
                post.put("post", AVUser.getCurrentUser());
                post.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException exception) {
                        if (exception == null) {
                            AVQuery<AVObject> query = AVQuery.getQuery(BABY_INFO);
                            query.whereEqualTo("post", AVUser.getCurrentUser())
                                    .whereEqualTo(RequestUtils.BABYSEX, babySex)
                                    .whereEqualTo(RequestUtils.BIRTHDAY, birthdayBaby)
                                    .whereEqualTo(OperateDBUtils.BABYNAME, name)
                                    .findInBackground(new FindCallback<AVObject>() {
                                        @Override
                                        public void done(List<AVObject> list, AVException exception) {
                                            if (exception == null && list.size() >0) {
                                                OperateDBUtils operateDBUtils = new OperateDBUtils(context);
                                                operateDBUtils.saveBabyInfoDataToDB(list);
                                                e.onNext("A");
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


    public void getBabyInfoDataAndSaveToDB(final String babySex, final String birthdayBaby) {

    }

}
