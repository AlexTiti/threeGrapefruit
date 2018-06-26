package com.findtech.threePomelos.music.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.music.adapter.SixMusicAdapter;
import com.findtech.threePomelos.music.info.MusicBean;
import com.findtech.threePomelos.music.info.MusicInfo;
import com.findtech.threePomelos.music.present.BannerAlbumPresent;
import com.findtech.threePomelos.music.utils.HandlerUtil;
import com.findtech.threePomelos.music.view.AbstractAppBarStateChangeListener;
import com.findtech.threePomelos.music.view.BannerAibumView;
import com.findtech.threePomelos.musicserver.Nammu;
import com.findtech.threePomelos.net.NetWorkRequest;
import com.findtech.threePomelos.sdk.MyApplication;
import com.findtech.threePomelos.sdk.base.mvp.BaseActivityMvp;
import com.findtech.threePomelos.utils.IContent;
import com.findtech.threePomelos.utils.glide.BlureTransformation;
import com.findtech.threePomelos.utils.glide.GlideApp;
import com.findtech.threePomelos.utils.glide.GlideUtils;

import java.util.ArrayList;

import butterknife.BindView;


/**
 * @author Administrator
 */
public class BannerAlbumActivity extends BaseActivityMvp<BannerAlbumActivity, BannerAlbumPresent> implements
        BannerAibumView<ArrayList<MusicInfo>>, BaseQuickAdapter.RequestLoadMoreListener,
        BaseQuickAdapter.OnItemClickListener {

    private ArrayList<MusicInfo> musicInfosArray = new ArrayList<>();
    Handler handler;
    PlayMusicBase playMusic;

    private String albumAddress;
    private String albumObjectId;
    private int musicType;
    private int times = 0;
    private MyApplication app;
    private int position;

    private SixMusicAdapter mSixMusicAdapter;
    private RecyclerView mRecyclerView;
    @BindView(R.id.collapsingToolbar)
    CollapsingToolbarLayout layout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appLayout)
    AppBarLayout appBarLayout;
    @BindView(R.id.iv_toolbar)
    ImageView iv_toolbar;
    @BindView(R.id.image_bac)
    ImageView image_bac;
    @BindView(R.id.image_pic)
    ImageView image_pic;
    @BindView(R.id.tv_toolbar)
    TextView tv_toolbar;
    @BindView(R.id.tv_expendTitle)
    TextView tv_expendTitle;
    @BindView(R.id.title_below)
    TextView title_below;

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
            handler.postDelayed(playMusic, 0);
        }
        startFloat();
    }

//    public void downClick(int position) {
//        down_position = position;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Nammu.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//            Nammu.requestPermission(this, Nammu.PERMISSIONS_STORAGE, 100, Manifest.permission.WRITE_EXTERNAL_STORAGE, getResources().getString(R.string.primess_notice));
//        } else {
//            downMusic(musicInfosArray.get(position));
//        }
//    }

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
        mRecyclerView.setAdapter(mSixMusicAdapter);
        times++;
    }

    @Override
    public void loadFailed(String s) {
        checkNetWork();
        ViewStub viewStub = findViewById(R.id.viewStub);
        viewStub.inflate();
        viewStub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                present.getMusicCommon(albumAddress, 0, musicType);
            }
        });
    }

    @Override
    public void onLoadMoreRequested() {
        present.loadMoreMusicCommon(albumAddress, times, 1);
        mSixMusicAdapter.loadMoreComplete();
    }

    @Override
    protected void initData() {
        super.initData();
        Intent intent = getIntent();
        if (intent != null) {
            albumAddress = intent.getStringExtra(NetWorkRequest.URL);
            albumObjectId = intent.getStringExtra(NetWorkRequest.ALBUM_OBJECT);
            musicType = intent.getIntExtra(NetWorkRequest.MUSIC_TYPE, 1);
        }
    }

    @Override
    public void initDataFromServer() {
        present.getMusicCommon(albumAddress, 0, musicType);
        present.getAlbumInfo(albumObjectId);
    }

    @Override
    protected BannerAlbumPresent createPresent() {
        return new BannerAlbumPresent();
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

        Nammu.init(getApplicationContext());
        handler = HandlerUtil.getInstance(this);
        iv_toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BannerAlbumActivity.this.finish();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_common_music;
    }


    @Override
    public void updateTrack() {
        super.updateTrack();
        mSixMusicAdapter.notifyDataSetChanged();
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

            default:
                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        click(position);
    }


    @Override
    public void getAlbumInfoSuccess(MusicBean bean) {
        tv_toolbar.setText(bean.getTitle());
        tv_expendTitle.setText(bean.getTitle());
        title_below.setText(bean.getDescribe());
        if (bean.getColor() != null) {
            layout.setBackgroundColor(Color.parseColor(bean.getColor()));
        }

//        BlureTransformation transformation = new BlureTransformation.Builder(this).build();
//        GlideApp.with(this).asBitmap().load(bean.getImageFace()).transform(transformation).into(image_bac);

//        Glide.with(this).load(bean.getImageFace()).apply(RequestOptions.bitmapTransform(new BlurTransformation(23, 3))).into(image_bac);

        GlideApp.with(this).asBitmap().load(bean.getImageFace()).transform(new BlureTransformation(23, 3)).into(image_bac);
        GlideUtils.glideToImageView(this, bean.getImageFace(), image_pic);

    }

    @Override
    public void getAlbumInfoFailed() {
        checkNetWork();
    }
}
