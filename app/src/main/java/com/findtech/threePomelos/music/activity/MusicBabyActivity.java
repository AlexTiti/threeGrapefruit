package com.findtech.threePomelos.music.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.music.adapter.ShowMusicAdapter;
import com.findtech.threePomelos.music.info.MusicInfo;
import com.findtech.threePomelos.music.model.ItemClickListtener;
import com.findtech.threePomelos.music.present.MusicCollectPresent;
import com.findtech.threePomelos.music.utils.HandlerUtil;
import com.findtech.threePomelos.musicserver.control.MusicPlayer;
import com.findtech.threePomelos.sdk.base.mvp.BaseActivityMvp;
import com.findtech.threePomelos.sdk.base.mvp.Contract;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;

/**
 * @author Administrator
 */
public class MusicBabyActivity extends BaseActivityMvp<MusicBabyActivity,MusicCollectPresent>
        implements ItemClickListtener, Contract.ViewMvp<ArrayList<MusicInfo>> {

    @BindView(R.id.show_music)
    RecyclerView recyclerView;
    @BindView(R.id.nodata_layout)
    LinearLayout nodata_layout;
    @BindView(R.id.include)
    View include;

    ArrayList<MusicInfo> musicInfos = new ArrayList<>();
    private ShowMusicAdapter showMusicAdapter;
    PlayMusic playMusic;
    Handler handler;
    private int position;


    @Override
    public void click(int position) {
        this.position = position;
        goMusic();
    }



    @Override
    public void updateTrack() {
        super.updateTrack();
        showMusicAdapter.notifyDataSetChanged();
    }

    public void goMusic() {
        if (playMusic != null) {
            handler.removeCallbacks(playMusic);
        }
        if (position > -1) {
            playMusic = new PlayMusic(position);
            handler.postDelayed(playMusic, 70);
        }
        startFloat();
    }


    @Override
    protected void showLoading() {

    }

    @Override
    protected void onErrorViewClick(View v) {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        showMusicAdapter = new ShowMusicAdapter();
        showMusicAdapter.setItemCliclListener(this);
        recyclerView.setAdapter(showMusicAdapter);
        handler = HandlerUtil.getInstance(this);
        initToolBar(include,getResources().getString(R.string.collect_music));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_show_music;
    }

    @Override
    public void loadSuccess(ArrayList<MusicInfo> musicInfos) {
        this.musicInfos = musicInfos;
        showMusicAdapter.setMusicInfos(musicInfos);
        showMusicAdapter.notifyDataSetChanged();
    }

    @Override
    public void loadFailed(String s) {
        nodata_layout.setVisibility(View.VISIBLE);
        ImageView image =  findViewById(R.id.net_fail_image);
        image.setImageResource(R.drawable.babay_like);
        TextView textView =  findViewById(R.id.net_fail_text);
        textView.setText(getResources().getString(R.string.network_babylike));
    }

    @Override
    public void initDataFromServer() {
        getPresent().getCollectArray(this);
    }

    @Override
    protected MusicCollectPresent createPresent() {
        return new MusicCollectPresent();
    }

    class PlayMusic implements Runnable {
        int position;

        public PlayMusic(int position) {
            this.position = position;
        }

        @Override
        public void run() {
            long[] list = new long[musicInfos.size()];
            HashMap<Long, MusicInfo> infos = new HashMap(musicInfos.size());
            for (int i = 0; i < musicInfos.size(); i++) {
                MusicInfo info = musicInfos.get(i);
                list[i] = info.songId;
                infos.put(list[i], musicInfos.get(i));
            }
            if (position > -1) {
                MusicPlayer.playAll(infos, list, position, false);
            }
        }
    }
}
