package com.findtech.threePomelos.music.model;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.findtech.threePomelos.music.info.MusicInfo;
import com.findtech.threePomelos.sdk.base.mvp.Contract;
import com.findtech.threePomelos.utils.IContent;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/04/08
 */

public class SixItemMusicModelImpl implements Contract.ModeMvp {

    private IContent icontent = IContent.getInstacne();
    private long i = 0;

    public Observable<ArrayList<MusicInfo>> getTypeMusic(final int times, final int number) {
        final ArrayList<MusicInfo> musicInfoArray = new ArrayList<>();
        i = times * 15;
        return Observable.create(new ObservableOnSubscribe<ArrayList<MusicInfo>>() {
            @Override
            public void subscribe(final ObservableEmitter<ArrayList<MusicInfo>> e) throws Exception {
                AVQuery<AVObject> query = new AVQuery<>("MusicList");
                query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE)
                        .setMaxCacheAge(24 * 3600)
                        .whereEqualTo("typeNumber", number + 1)
                        .skip(times * 15)
                        .limit(15).orderByAscending("musicOrder")
                        .findInBackground(new FindCallback<AVObject>() {
                            @Override
                            public void done(List<AVObject> list, AVException exception) {
                                if (exception == null) {
                                    for (AVObject avObject : list) {
                                        String musicName = avObject.getString("musicName");
                                        if (icontent.map.containsKey(musicName)) {
//                                    AVFile imageFile = avObject.getAVFile("musicImage");
//                                    icontent.map.get(musicName).faceImage = imageFile.getUrl();
                                            musicInfoArray.add(icontent.map.get(musicName));
                                        } else {
                                            MusicInfo musicInfo = new MusicInfo();
                                            musicInfo.musicName = musicName;
                                            musicInfo.artist = null;
                                            musicInfo.type = avObject.getNumber("typeNumber");
                                            AVFile avFile = avObject.getAVFile("musicFiles");
                                            musicInfo.lrc = avFile.getUrl();
//                                    AVFile imageFile = avObject.getAVFile("musicImage");
//                                    musicInfo.faceImage = imageFile.getUrl();
                                            musicInfo.avObject = avObject.toString();
                                            musicInfo.islocal = false;
                                            musicInfo.songId = number * 1000 + i;
                                            musicInfoArray.add(musicInfo);
                                        }
                                        i++;
                                    }
                                    e.onNext(musicInfoArray);
                                } else {
                                    e.onError(exception);
                                }
                            }
                        });
            }
        });
    }
}
