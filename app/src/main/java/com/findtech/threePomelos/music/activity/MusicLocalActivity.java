package com.findtech.threePomelos.music.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.SaveCallback;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.music.adapter.ShowMusicAdapter;
import com.findtech.threePomelos.music.info.MusicInfo;
import com.findtech.threePomelos.music.model.ItemClickListtener;
import com.findtech.threePomelos.music.present.MusicLocalPresent;
import com.findtech.threePomelos.music.utils.DownFileUtils;
import com.findtech.threePomelos.music.utils.HandlerUtil;
import com.findtech.threePomelos.music.utils.IConstants;
import com.findtech.threePomelos.music.utils.L;
import com.findtech.threePomelos.music.utils.MusicComparator;
import com.findtech.threePomelos.music.utils.MusicUtils;
import com.findtech.threePomelos.music.utils.PreferencesUtility;
import com.findtech.threePomelos.music.utils.SideBar;
import com.findtech.threePomelos.music.utils.SortOrder;
import com.findtech.threePomelos.musicserver.Nammu;
import com.findtech.threePomelos.musicserver.control.MusicPlayer;
import com.findtech.threePomelos.net.NetWorkRequest;
import com.findtech.threePomelos.sdk.base.BaseCompatActivity;
import com.findtech.threePomelos.sdk.base.mvp.BaseActivityMvp;
import com.findtech.threePomelos.sdk.base.mvp.Contract;
import com.findtech.threePomelos.utils.IContent;
import com.findtech.threePomelos.view.dialog.CustomDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * @author Administrator
 */
public class MusicLocalActivity extends BaseActivityMvp<MusicLocalActivity,MusicLocalPresent> implements ItemClickListtener,
        ShowMusicAdapter.LongClickListener ,Contract.ViewMvp<ArrayList<MusicInfo>> {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.sidebar)
    SideBar sideBar;
    @BindView(R.id.dialog_text)
    TextView dialogText;
    @BindView(R.id.viewstub)
    ViewStub viewStub;
    @BindView(R.id.include)
    View include;

    private HashMap<String, Integer> positionMap = new HashMap<>();
    private boolean isAZSort = true;
    ArrayList<MusicInfo> songList;
    PlayMusic playMusic;
    Handler handler;
    public Activity mContext = this;
    private int position;
    private NetWorkRequest netWorkRequest;
    File file_music;
    private ShowMusicAdapter musicAdapter;
    private LinearLayoutManager layoutManager;
    private PreferencesUtility mPreferences;
    private boolean isFirstLoad = true;

    private void loadView() {

        layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        musicAdapter = new ShowMusicAdapter();
        recyclerView.setAdapter(musicAdapter);
        recyclerView.setHasFixedSize(true);
        musicAdapter.setItemCliclListener(this);
        musicAdapter.setLongClickListener(this);

        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                dialogText.setText(s);
                sideBar.setView(dialogText);
                if (positionMap.get(s) != null) {
                    int i = positionMap.get(s);
                    ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(i + 1, 0);
                }
            }
        });

        file_music = DownFileUtils.creatFileDir(this, IContent.FILEM_USIC);
        netWorkRequest = new NetWorkRequest(this);
    }

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                sideBar.setVisibility(View.VISIBLE);
            }
        }
    };


    @Override
    protected void showLoading() {

    }

    @Override
    protected void onErrorViewClick(View v) {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mPreferences = PreferencesUtility.getInstance(mContext);
        handler = HandlerUtil.getInstance(mContext);
        isFirstLoad = true;
        isAZSort = mPreferences.getSongSortOrder().equals(SortOrder.SongSortOrder.SONG_A_Z);
        Nammu.init(getApplicationContext());
        loadView();
        initToolBar(include,getResources().getString(R.string.down_music));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.load_framelayout;
    }



    @Override
    public void click(int position) {

        this.position = position;
        goMusic();
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
    public void longClick(final int position) {

        final CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.notice))
                .setShowBindInfo(getResources().getString(R.string.delete_notice))
                .setShowButton(true)
                .setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        final MusicInfo info = songList.get(position);
                        Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, info.songId);
                        mContext.getContentResolver().delete(uri, null, null);
                        final File file = DownFileUtils.creatFile(MusicLocalActivity.this, IContent.FILEM_USIC, info.musicName + ".mp3");
                        if (file.exists()) {
                            file.delete();
                        }
                        netWorkRequest.sendDeleteDownMusic(info.musicName, new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    Map map = IContent.getInstacne().map;
                                    if (map.containsKey(info.musicName)) {
                                        IContent.getInstacne().map.remove(info.musicName);
                                    }
                                }
                            }
                        });
                        songList.remove(position);

                        if (songList == null || songList.size() <= 0) {
                            View view = viewStub.inflate();
                            ImageView image = view.findViewById(R.id.net_fail_image);
                            image.setImageResource(R.drawable.down_list);
                            TextView textView = view.findViewById(R.id.net_fail_text);
                            textView.setText(getResources().getString(R.string.network_local));
                        }
                        musicAdapter.setMusicInfos(songList);
                        musicAdapter.notifyDataSetChanged();

                        if (MusicPlayer.getCurrentAudioId() == info.songId) {
                            if (MusicPlayer.getQueueSize() == 0) {
                                MusicPlayer.stop();
                            } else {
                                MusicPlayer.next();
                            }
                        }

                        dialog.dismiss();
                    }
                });
        builder.setNegativeButton(getResources().getString(R.string.cancle),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();


    }

    @Override
    public void loadSuccess(ArrayList<MusicInfo> musicArray) {

        viewStub.setVisibility(View.GONE);
        musicAdapter.setMusicInfos(musicArray);
        musicAdapter.notifyDataSetChanged();

        if (isAZSort) {
            recyclerView.addOnScrollListener(scrollListener);
        } else {
            sideBar.setVisibility(View.INVISIBLE);
            recyclerView.removeOnScrollListener(scrollListener);
        }
    }

    @Override
    public void loadFailed(String s) {

        View view = viewStub.inflate();
        ImageView image =  view.findViewById(R.id.net_fail_image);
        image.setImageResource(R.drawable.down_list);
        TextView textView =  view.findViewById(R.id.net_fail_text);
        textView.setText(getResources().getString(R.string.network_local));
    }

    @Override
    public void initDataFromServer() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Nammu.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Nammu.requestPermission(this, Nammu.PERMISSIONS_STORAGE, 100, Manifest.permission.READ_EXTERNAL_STORAGE, getResources().getString(R.string.primess_notice));
        } else {
            getPresent().getMusicLocal(this);
        }

    }

    @Override
    protected MusicLocalPresent createPresent() {
        return new MusicLocalPresent();
    }



    class PlayMusic implements Runnable {
        int position;

        public PlayMusic(int position) {
            this.position = position;
        }

        @Override
        public void run() {
            long[] list = new long[songList.size()];
            HashMap<Long, MusicInfo> infos = new HashMap(songList.size());
            for (int i = 0; i < songList.size(); i++) {
                MusicInfo info = songList.get(i);
                list[i] = info.songId;
                info.islocal = true;
                infos.put(list[i], songList.get(i));
            }
            if (position > -1) {
                MusicPlayer.playAll(infos, list, position, false);
            }
        }
    }

    @Override
    public void updateTrack() {
        super.updateTrack();
        musicAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    file_music = DownFileUtils.creatFileDir(this, IContent.FILEM_USIC);
                    getPresent().getMusicLocal(this);
                }
                break;
            default:
                break;
        }
    }
}
