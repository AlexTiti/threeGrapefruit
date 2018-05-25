package com.findtech.threePomelos.music.model;

import android.content.Context;

import com.findtech.threePomelos.music.info.MusicInfo;
import com.findtech.threePomelos.music.info.Playlist;
import com.findtech.threePomelos.musicserver.control.PlaylistsManager;
import com.findtech.threePomelos.musicserver.info.PlaylistInfo;
import com.findtech.threePomelos.sdk.base.mvp.Contract;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/05/07
 */

public class MusicCollectModelImpl implements Contract.ModeMvp {

    private PlaylistInfo playlistInfo;
    private Playlist playList;
    private ArrayList playLists = new ArrayList();


    public Observable<ArrayList<MusicInfo>> getMusicCollectArray(final Context context) {
        playlistInfo = PlaylistInfo.getInstance(context);
        playLists.addAll(playlistInfo.getPlaylist());

        if (playLists.size() > 0) {
            playList = (Playlist) playLists.get(0);
        }
        return Observable.create(new ObservableOnSubscribe<ArrayList<MusicInfo>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<MusicInfo>> e) throws Exception {
                ArrayList<MusicInfo> infoArrayList = PlaylistsManager.getInstance(context).getMusicInfos(playList.id);
                if (infoArrayList.size() > 0) {
                    e.onNext(infoArrayList);
                }else {
                    e.onError(null);
                }
            }
        });
    }

    public Observable<String> getMusicCollectCount(final Context context) {
        playlistInfo = PlaylistInfo.getInstance(context);
        playLists.addAll(playlistInfo.getPlaylist());

        if (playLists.size() > 0) {
            playList = (Playlist) playLists.get(0);
        }
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                int d = PlaylistsManager.getInstance(context).getMusicCount(playList.id);
                    e.onNext(String.valueOf(d) );
            }
        });
    }
}
