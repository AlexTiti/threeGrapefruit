package com.findtech.threePomelos.music.model;

import com.findtech.threePomelos.music.info.MusicBean;
import com.findtech.threePomelos.sdk.base.mvp.Contract;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/04/10
 */

public class AllSongModel implements Contract.ModeMvp{

    public Observable<ArrayList<MusicBean>> getAllMusicList(final int times, final int number){
        return Observable.create(new ObservableOnSubscribe<ArrayList<MusicBean>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<MusicBean>> e) throws Exception {
                ArrayList<MusicBean> beanArrayList = new ArrayList<>();
                for (int i = times * 6; i < (times + 1) * 6; i++) {
                    beanArrayList.add(new MusicBean("url","AAAAAA"+i,"10000","10"));
                }
                e.onNext(beanArrayList);
            }
        });
    }

    public Observable<ArrayList<String>> getTabMusic(){
        return Observable.create(new ObservableOnSubscribe<ArrayList<String>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<String>> e) throws Exception {
                ArrayList<String> strings = new ArrayList<>();
                for (int i = 0; i <8 ; i++) {
                    strings.add("AA"+i);
                }
                e.onNext(strings);
            }
        });
    }

}
