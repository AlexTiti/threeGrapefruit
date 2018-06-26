package com.findtech.threePomelos.music.present;

import android.content.Context;

import com.findtech.threePomelos.music.activity.MusicBabyActivity;
import com.findtech.threePomelos.music.info.MusicInfo;
import com.findtech.threePomelos.music.model.MusicCollectModelImpl;
import com.findtech.threePomelos.sdk.base.mvp.BasePresenterMvp;
import com.findtech.threePomelos.sdk.manger.RxHelper;
import com.findtech.threePomelos.user.bean.UserInfo;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/05/07
 */

public class MusicCollectPresent extends BasePresenterMvp<MusicBabyActivity, MusicCollectModelImpl> {

    @Override
    public MusicCollectModelImpl createModel() {
        return new MusicCollectModelImpl();
    }

    public void getCollectArray(Context context) {
        if (model == null || mView == null) {
            return;
        }
        mRxManager.register(model.getMusicCollectArray(context)
                .compose(RxHelper.<ArrayList<MusicInfo>>rxSchedulerHelper())
                .subscribe(new Consumer<ArrayList<MusicInfo>>() {
                    @Override
                    public void accept(ArrayList<MusicInfo> musicInfos) throws Exception {
                        mView.loadSuccess(musicInfos);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.loadFailed(null);
                    }
                }));
    }

}
