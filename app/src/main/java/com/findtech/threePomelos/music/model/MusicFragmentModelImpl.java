package com.findtech.threePomelos.music.model;

import com.findtech.threePomelos.music.info.MusicBean;
import com.findtech.threePomelos.music.info.MusicInfo;
import com.findtech.threePomelos.sdk.base.mvp.Contract;

import java.util.ArrayList;


import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/04/09
 */

public class MusicFragmentModelImpl implements Contract.ModeMvp {

    public Observable<ArrayList<MusicBean>> getRecyclerViewData(){
        return Observable.create(new ObservableOnSubscribe<ArrayList<MusicBean>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<MusicBean>> e) throws Exception {
                ArrayList<MusicBean> beans = new ArrayList<>();
                for (int i=0;i<6;i++){
                    beans.add(new MusicBean("url","AA","1000","10"));
                }
                e.onNext(beans);
            }
        });
    }
}
