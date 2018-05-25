package com.findtech.threePomelos.music.present;

import com.findtech.threePomelos.music.activity.AllSongActivity;
import com.findtech.threePomelos.music.info.MusicBean;
import com.findtech.threePomelos.music.model.AllSongModel;
import com.findtech.threePomelos.sdk.base.mvp.BasePresenterMvp;
import com.findtech.threePomelos.sdk.manger.RxHelper;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/04/10
 */

public class AllSongPresent extends BasePresenterMvp<AllSongActivity, AllSongModel> {
    private int times;

    @Override
    public AllSongModel createModel() {
        return new AllSongModel();
    }

    public void getAllSong(int times, int number) {

        if (model == null ||mView == null) {
            return;
        }
        mRxManager.register(model.getAllMusicList(times, number)
                .compose(RxHelper.<ArrayList<MusicBean>>rxSchedulerHelper())
                .subscribe(new Consumer<ArrayList<MusicBean>>() {
                    @Override
                    public void accept(ArrayList<MusicBean> beans) throws Exception {
                       mView.loadSuccess(beans);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                       mView.loadFailed(throwable.toString());
                    }
                }));
    }

    public void loadMoreAllSong(int times, int number) {

        if (this.times == times) {
            return;
        }
        this.times =  times;
        if (model == null ||mView == null) {
            return;
        }
        mRxManager.register(model.getAllMusicList(times, number)
                .compose(RxHelper.<ArrayList<MusicBean>>rxSchedulerHelper())
                .subscribe(new Consumer<ArrayList<MusicBean>>() {
                    @Override
                    public void accept(ArrayList<MusicBean> beans) throws Exception {
                       mView.loadMoreSuccess(beans);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                       mView.loadMoreFailed(throwable.toString());
                    }
                }));
    }

    public void getTabListMusic(){
        if (model == null ||mView == null) {
            return;
        }
        mRxManager.register(model.getTabMusic()
        .compose(RxHelper.<ArrayList<String>>rxSchedulerHelper())
        .subscribe(new Consumer<ArrayList<String>>() {
            @Override
            public void accept(ArrayList<String> strings) throws Exception {
               mView.loadTabSuccess(strings);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
               mView.loadTabFailed(throwable.toString());
            }
        }));
    }

}
