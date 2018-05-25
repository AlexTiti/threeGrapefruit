package com.findtech.threePomelos.music.present;

import com.findtech.threePomelos.music.activity.SixItemMusicActivity;
import com.findtech.threePomelos.music.info.MusicInfo;
import com.findtech.threePomelos.music.model.SixItemMusicModelImpl;
import com.findtech.threePomelos.sdk.base.mvp.BasePresenterMvp;
import com.findtech.threePomelos.sdk.manger.RxHelper;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/04/08
 */

public class SixItemMusicPresent extends BasePresenterMvp<SixItemMusicActivity, SixItemMusicModelImpl> {

    private int timesPresent;

    @Override
    public SixItemMusicModelImpl createModel() {
        return new SixItemMusicModelImpl();
    }

    public void getMusic(int times, int number) {
        if (model == null || mView == null) {
            return;
        }

        mRxManager.register(model.getTypeMusic(times, number)
                .compose(RxHelper.<ArrayList<MusicInfo>>rxSchedulerHelper())
                .subscribe(new Consumer<ArrayList<MusicInfo>>() {
                    @Override
                    public void accept(ArrayList<MusicInfo> musicArray) throws Exception {
                        mView.loadSuccess(musicArray);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.loadFailed(throwable.toString());
                    }
                }));
    }

    public void loadMoreMusic(final int times, int number) {
        if (times == timesPresent) {
            return;
        }
        timesPresent = times;

        if (model == null || mView == null) {
            return;
        }
        mRxManager.register(model.getTypeMusic(times, number)
                .compose(RxHelper.<ArrayList<MusicInfo>>rxSchedulerHelper())
                .subscribe(new Consumer<ArrayList<MusicInfo>>() {
                    @Override
                    public void accept(ArrayList<MusicInfo> musicArray) throws Exception {

                        if (musicArray.size() < 15) {
                            mView.loadMoreSuccess(musicArray);
                            mView.showNoMoreData();
                        } else {
                            mView.loadMoreSuccess(musicArray);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.loadMoreFailed(throwable.toString());
                    }
                }));
    }

}
