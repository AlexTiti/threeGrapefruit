package com.findtech.threePomelos.music.present;

import android.content.Context;

import com.findtech.threePomelos.music.activity.MusicLocalActivity;
import com.findtech.threePomelos.music.info.MusicInfo;
import com.findtech.threePomelos.music.model.MusicModelImpl;
import com.findtech.threePomelos.sdk.base.mvp.BasePresenterMvp;
import com.findtech.threePomelos.sdk.manger.RxHelper;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/05/07
 */

public class MusicLocalPresent extends BasePresenterMvp<MusicLocalActivity, MusicModelImpl> {
    @Override
    public MusicModelImpl createModel() {
        return new MusicModelImpl();
    }

    public void getMusicLocal(Context mContext) {
        if (model == null || mView == null) {
            return;
        }
        mRxManager.register(model.getLocalMusicArray(mContext)
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
