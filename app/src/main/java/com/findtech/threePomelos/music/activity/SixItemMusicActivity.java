package com.findtech.threePomelos.music.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.ProgressCallback;
import com.avos.avoscloud.SaveCallback;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.music.adapter.MusicAdapter;
import com.findtech.threePomelos.music.adapter.SixMusicAdapter;
import com.findtech.threePomelos.music.info.MusicInfo;
import com.findtech.threePomelos.music.present.SixItemMusicPresent;
import com.findtech.threePomelos.music.utils.DownFileUtils;
import com.findtech.threePomelos.music.utils.HandlerUtil;
import com.findtech.threePomelos.music.utils.MusicUtils;
import com.findtech.threePomelos.music.view.AbstractAppBarStateChangeListener;
import com.findtech.threePomelos.music.view.RecyclerMusicView;
import com.findtech.threePomelos.musicserver.Nammu;
import com.findtech.threePomelos.net.NetWorkRequest;
import com.findtech.threePomelos.sdk.MyApplication;
import com.findtech.threePomelos.sdk.base.mvp.BaseActivityMvp;
import com.findtech.threePomelos.utils.IContent;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;


/**
 * @author Administrator
 */
public class SixItemMusicActivity extends BaseActivityMvp<SixItemMusicActivity, SixItemMusicPresent> implements
        MusicAdapter.DownClick, RecyclerMusicView<ArrayList<MusicInfo>>, BaseQuickAdapter.RequestLoadMoreListener,
        BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemChildClickListener {

    private ArrayList<MusicInfo> musicInfosArray = new ArrayList<>();
    Handler handler;
    PlayMusicBase playMusic;
    private String title;
    private int number;

    private int times = 0;
    private MyApplication app;

    private int position;
    int down_position;
    private String content;
    private IContent icontent = IContent.getInstacne();

    private String[] proj_music = new String[]{
            MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ARTIST_ID, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.SIZE};

    private SixMusicAdapter mSixMusicAdapter;
    private RecyclerView mRecyclerView;
    @BindView(R.id.collapsingToolbar)
    CollapsingToolbarLayout layout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.iv_music)
    ImageView imageView;
    @BindView(R.id.appLayout)
    AppBarLayout appBarLayout;
    @BindView(R.id.iv_toolbar)
    ImageView iv_toolbar;
    @BindView(R.id.tv_toolbar)
    TextView tv_toolbar;
    @BindView(R.id.tv_expendTitle)
    TextView tv_expendTitle;
    private int[] drawablesBg = {R.drawable.fluid_music_bg, R.drawable.sleep_music_bg, R.drawable.appease_music_bg, R.drawable.song_music_bg, R.drawable.stroy_music_bg};
    private int[] drawables = {R.drawable.fluid, R.drawable.sleep, R.drawable.appease, R.drawable.song, R.drawable.stroy};
    private int[] strings = {R.string.fluid_music, R.string.sleep_music, R.string.appease_music, R.string.song_music, R.string.stroy_music};


    public void click(int position) {
        this.position = position;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Nammu.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Nammu.requestPermission(this, Nammu.PERMISSIONS_STORAGE, 1, Manifest.permission.WRITE_EXTERNAL_STORAGE, getResources().getString(R.string.primess_notice));
        } else {
            goMusic();
        }
        if (IContent.getInstacne().SD_Mode) {
            if (app.manager.cubicBLEDevice != null) {
                app.manager.cubicBLEDevice.writeValue(IContent.SERVERUUID_BLE, IContent.WRITEUUID_BLE, IContent.BLUEMODE);
            }
        }
    }

    public void goMusic() {
        if (playMusic != null) {
            handler.removeCallbacks(playMusic);
        }
        if (position > -1) {
            playMusic = new PlayMusicBase(position, musicInfosArray);
            handler.postDelayed(playMusic, 70);
        }
        startFloat();
    }

    @Override
    public void downClick(int position) {
        down_position = position;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Nammu.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Nammu.requestPermission(this, Nammu.PERMISSIONS_STORAGE, 100, Manifest.permission.WRITE_EXTERNAL_STORAGE, getResources().getString(R.string.primess_notice));
        } else {
            downMusic(musicInfosArray.get(position));
        }
    }

    @Override
    public void loadMoreSuccess(ArrayList<MusicInfo> musicInfos) {
        mSixMusicAdapter.addData(musicInfos);
        mSixMusicAdapter.loadMoreComplete();
        times++;
        musicInfos.addAll(musicInfosArray);

    }

    @Override
    public void loadMoreFailed(String message) {
        mSixMusicAdapter.loadMoreFail();

    }

    @Override
    public void emptyDate() {
        mSixMusicAdapter.loadMoreComplete();
        mSixMusicAdapter.setEmptyView(emptyView);
    }

    @Override
    public void showNoMoreData() {
        mSixMusicAdapter.loadMoreEnd();
    }

    @Override
    public void loadSuccess(ArrayList<MusicInfo> musicInfos) {
        musicInfosArray = musicInfos;
        mSixMusicAdapter = new SixMusicAdapter(R.layout.music_item, musicInfos);
        mSixMusicAdapter.setOnLoadMoreListener(this, mRecyclerView);
        mSixMusicAdapter.setOnItemClickListener(this);
        mSixMusicAdapter.setOnItemChildClickListener(this);
        mRecyclerView.setAdapter(mSixMusicAdapter);
        times++;
    }

    @Override
    public void loadFailed(String s) {

    }

    @Override
    public void onLoadMoreRequested() {
        present.loadMoreMusic(times, 0);
        mSixMusicAdapter.loadMoreComplete();
    }


    @Override
    protected void initData() {
        super.initData();
        Intent intent = getIntent();
        if (intent != null) {
            title = intent.getStringExtra(IContent.TITLE);
            number = intent.getIntExtra(IContent.NUMBER, 0);
            content = intent.getStringExtra(IContent.CONTENT);
        }
    }

    @Override
    public void initDataFromServer() {
        present.getMusic(times, 0);
    }

    @Override
    protected SixItemMusicPresent createPresent() {
        return new SixItemMusicPresent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void showLoading() {
    }

    @Override
    protected void onErrorViewClick(View v) {
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        app = (MyApplication) getApplication();
        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mSixMusicAdapter = new SixMusicAdapter(R.layout.music_item);
        layout.setBackgroundDrawable(getResources().getDrawable(drawablesBg[number]));
        tv_toolbar.setText(strings[number]);
        tv_expendTitle.setText(strings[number]);
        appBarLayout.addOnOffsetChangedListener(new AbstractAppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state == State.COLLAPSED) {
                    iv_toolbar.setImageDrawable(getResources().getDrawable(R.drawable.back_play_detail));
                    tv_toolbar.setAlpha(1f);

                } else if (state == State.EXPANDED) {
                    iv_toolbar.setImageDrawable(getResources().getDrawable(R.drawable.icon_back));
                    tv_toolbar.setAlpha(0f);
                }
            }

            @Override
            public void onScale(float f) {
                tv_toolbar.setAlpha(f);
            }
        });
        imageView.setImageDrawable(getResources().getDrawable(drawables[number]));

        Nammu.init(getApplicationContext());
        handler = HandlerUtil.getInstance(this);
        iv_toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SixItemMusicActivity.this.finish();
            }
        });
        // toolbar.setNavigationIcon(R.drawable.icon_back);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_baby_story;
    }


    @Override
    public void updateTrack() {
        super.updateTrack();
        mSixMusicAdapter.notifyDataSetChanged();
    }

    private void downMusic(final MusicInfo info) {

        mSixMusicAdapter.getItem(down_position).artist = "downing";
        mSixMusicAdapter.notifyItemChanged(down_position);

        final NetWorkRequest netWorkRequest = new NetWorkRequest(this);
        NetWorkRequest.downMusicFromNet(this, info, new ProgressCallback() {
            @Override
            public void done(Integer integer) {
                if (integer == 100) {
                    File file = DownFileUtils.creatFile(SixItemMusicActivity.this, IContent.FILEM_USIC, info.musicName + ".mp3");
                    if (!file.exists()) {
                        return;
                    }
                    netWorkRequest.sendMusicDown(info.musicName, new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                            } else {
                                checkNetWork();
                            }
                        }
                    });
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        Intent mediaScanIntent = new Intent(
                                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        Uri contentUri = Uri.fromFile(file);
                        mediaScanIntent.setData(contentUri);
                        SixItemMusicActivity.this.sendBroadcast(mediaScanIntent);
                    } else {
                        sendBroadcast(new Intent(
                                Intent.ACTION_MEDIA_MOUNTED,
                                Uri.parse("file://"
                                        + Environment.getExternalStorageDirectory())));
                    }
                    CountDownTimer timer = new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {
                            File fileca = DownFileUtils.creatFileDir(SixItemMusicActivity.this, IContent.FILEM_USIC);
                            if (!fileca.exists()) {
                                return;
                            }
                            MusicInfo info = musicInfosArray.get(down_position);
                            ContentResolver cr = SixItemMusicActivity.this.getContentResolver();
                            Cursor cursor = cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj_music,
                                    MediaStore.Audio.Media.TITLE + "=?", new String[]{info.musicName},
                                    null);
                            ArrayList<MusicInfo> infoArray = MusicUtils.getMusicListCursor(cursor);
                            if (infoArray.size() > 0) {
                                for (MusicInfo info1 : infoArray) {
                                    icontent.map.put(info1.musicName, info1);
                                }
                                musicInfosArray.set(down_position, icontent.map.get(info.musicName));
                            }
                            mSixMusicAdapter.notifyItemChanged(down_position, icontent.map.get(info.musicName));
                        }
                    };
                    timer.start();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (int i = 0; i < musicInfosArray.size(); i++) {
            MusicInfo info = musicInfosArray.get(i);
            if (icontent.map.containsKey(info.musicName)) {
                musicInfosArray.set(i, icontent.map.get(info.musicName));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    goMusic();
                }
                break;
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    downMusic(musicInfosArray.get(down_position));
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        click(position);
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        downClick(position);
    }
}
