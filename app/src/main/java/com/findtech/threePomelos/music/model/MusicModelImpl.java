package com.findtech.threePomelos.music.model;

import android.content.Context;

import com.findtech.threePomelos.music.info.MusicInfo;
import com.findtech.threePomelos.music.utils.DownFileUtils;
import com.findtech.threePomelos.music.utils.IConstants;
import com.findtech.threePomelos.music.utils.MusicComparator;
import com.findtech.threePomelos.music.utils.MusicUtils;
import com.findtech.threePomelos.music.utils.PreferencesUtility;
import com.findtech.threePomelos.music.utils.SortOrder;
import com.findtech.threePomelos.sdk.base.mvp.Contract;
import com.findtech.threePomelos.utils.IContent;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;


/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/05/07
 */

public class MusicModelImpl implements Contract.ModeMvp {

    private PreferencesUtility mPreferences;
    private boolean isAZSort = true;
    private HashMap<String, Integer> positionMap = new HashMap<>();

    public Observable<ArrayList<MusicInfo>> getLocalMusicArray(final Context mContext){
        mPreferences = PreferencesUtility.getInstance(mContext);
        return Observable.create(new ObservableOnSubscribe<ArrayList<MusicInfo>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<MusicInfo>> e) throws Exception {
                ArrayList<MusicInfo> songList = new ArrayList<>();
                isAZSort = mPreferences.getSongSortOrder().equals(SortOrder.SongSortOrder.SONG_A_Z);
                boolean hasFolder = false;
                File file = DownFileUtils.creatFileDir(mContext, IContent.FILEM_USIC);
                if (!file.exists()) {
                    hasFolder = file.mkdirs();
                } else {
                    hasFolder = true;
                }
                if (hasFolder) {
                    songList = MusicUtils.queryMusic(mContext, file.getAbsolutePath(), IConstants.START_FROM_FOLDER);
                }
                if (songList == null) {
                    songList = new ArrayList<>();
                }
                if (isAZSort) {
                    Collections.sort(songList, new MusicComparator());
                    for (int i = 0; i < songList.size(); i++) {
                        if (positionMap.get(songList.get(i).sort) == null) {
                            positionMap.put(songList.get(i).sort, i);
                        }
                    }
                }
                if (songList.size() > 0) {
                    e.onNext(songList);
                }else {
                    e.onError(new Exception());
                }
            }
        });
    }
}
