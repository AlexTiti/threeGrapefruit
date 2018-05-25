package com.findtech.threePomelos.music.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.music.adapter.AllSongAdapter;
import com.findtech.threePomelos.music.adapter.VaryTabAdapter;
import com.findtech.threePomelos.music.info.MusicBean;
import com.findtech.threePomelos.music.present.AllSongPresent;
import com.findtech.threePomelos.music.view.MusicVaryView;
import com.findtech.threePomelos.music.view.PopDialogUtils;
import com.findtech.threePomelos.music.view.RecyclerMusicView;
import com.findtech.threePomelos.sdk.base.mvp.BaseActivityMvp;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * @author Alex
 *         All of the song
 */
public class AllSongActivity extends BaseActivityMvp<AllSongActivity, AllSongPresent> implements
        RecyclerMusicView<ArrayList<MusicBean>>, BaseQuickAdapter.RequestLoadMoreListener,
        View.OnClickListener ,MusicVaryView<ArrayList<String>>{

    @BindView(R.id.rvAllSong)
    RecyclerView recyclerViewAllSong;
    @BindView(R.id.tvHot)
    TextView tvHot;
    @BindView(R.id.tvVary)
    TextView tvVary;
    @BindView(R.id.include)
    View  include;

    private AllSongAdapter allSongAdapter;
    private int times = 0;
    private PopupWindow mPopWindowHot;
    private PopupWindow mPopWindowVary;

    private TextView tvNew;
    private TextView tvMostLike;
    private ImageView ivNew;
    private ImageView ivMostLike;

    private VaryTabAdapter varyTabAdapter;
    private RecyclerView recyclerView;
    private ArrayList<String> stringArrayList;


    @Override
    protected void showLoading() {
        allSongAdapter.setEmptyView(loadingView);
    }

    @Override
    protected void onErrorViewClick(View v) {
        present.getAllSong(times, 0);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        initToolBar(include,getString(R.string.song_all_list));
        recyclerViewAllSong.setLayoutManager(new GridLayoutManager(this, 2));
        allSongAdapter = new AllSongAdapter(R.layout.item_all_song);

        mPopWindowHot = initPopWindow();
        mPopWindowVary = initVaryPopWindow();
        tvHot.setOnClickListener(this);
        tvVary.setOnClickListener(this);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_all_song;
    }

    @Override
    public void initDataFromServer() {
        present.getAllSong(times, 6);
    }

    @Override
    protected AllSongPresent createPresent() {
        return new AllSongPresent();
    }

    @Override
    public void loadMoreSuccess(ArrayList<MusicBean> beans) {
        allSongAdapter.addData(beans);
        allSongAdapter.loadMoreComplete();
        times++;
    }

    @Override
    public void loadMoreFailed(String message) {
        allSongAdapter.loadMoreFail();
    }

    @Override
    public void emptyDate() {
        allSongAdapter.setEmptyView(emptyView);
    }

    @Override
    public void showNoMoreData() {
        allSongAdapter.loadMoreEnd();
    }

    @Override
    public void loadSuccess(ArrayList<MusicBean> beans) {
        allSongAdapter = new AllSongAdapter(R.layout.item_all_song, beans);
        allSongAdapter.setOnLoadMoreListener(this, recyclerViewAllSong);
        recyclerViewAllSong.setAdapter(allSongAdapter);
        times++;
    }

    @Override
    public void loadFailed(String s) {
        allSongAdapter.setEmptyView(errorView);
    }

    @Override
    public void onLoadMoreRequested() {
        present.loadMoreAllSong(times, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvHot:
                if (mPopWindowVary.isShowing()) {
                    mPopWindowVary.dismiss();
                }
                if (!tvHot.isSelected()) {
                    mPopWindowHot.showAsDropDown(tvHot);
                }
                tvHot.setSelected(!tvHot.isSelected());
                tvVary.setSelected(false);
                break;
            case R.id.tvVary:
                if (mPopWindowHot.isShowing()) {
                    mPopWindowHot.dismiss();
                }
                initRecyclerTabs();
                if (!tvVary.isSelected()) {
                    mPopWindowVary.showAsDropDown(tvHot);
                }
                tvHot.setSelected(false);
                tvVary.setSelected(!tvVary.isSelected());
                break;
            case R.id.tvNew:
                tvNew.setSelected(!tvNew.isSelected());
                tvMostLike.setSelected(!tvMostLike.isSelected());
                if (ivNew.getVisibility() != View.VISIBLE) {
                    ivNew.setVisibility(View.VISIBLE);
                    ivMostLike.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.tvMostLike:
                tvMostLike.setSelected(!tvMostLike.isSelected());
                tvNew.setSelected(!tvNew.isSelected());
                if (ivMostLike.getVisibility() != View.VISIBLE) {
                    ivMostLike.setVisibility(View.VISIBLE);
                    ivNew.setVisibility(View.INVISIBLE);
                }
                break;

            default:
                break;
        }
    }

    /**
     * 初始化热度PopWindow的View
     */
    private PopupWindow initPopWindow() {
        View view = LayoutInflater.from(this).inflate(R.layout.popwindow_all_song, null);
        tvNew = view.findViewById(R.id.tvNew);
        tvMostLike = view.findViewById(R.id.tvMostLike);
        ivNew = view.findViewById(R.id.ivNew);
        ivMostLike = view.findViewById(R.id.ivMostLike);
        tvNew.setSelected(true);
        tvNew.setOnClickListener(this);
        tvMostLike.setOnClickListener(this);
        ivNew.setOnClickListener(this);
        ivMostLike.setOnClickListener(this);
        return showPopWindow(view);
    }

    private PopupWindow initVaryPopWindow() {
        View view = LayoutInflater.from(this).inflate(R.layout.popwindow_vary_song, null);
        recyclerView = view.findViewById(R.id.rvVaryPopWindow);
        recyclerView.setLayoutManager(new GridLayoutManager(this,4));
        return showPopWindow(view);
    }

    private void initRecyclerTabs(){
        if (stringArrayList == null){
            present.getTabListMusic();
        }else {
            varyTabAdapter = new VaryTabAdapter(R.layout.item_vary_music,stringArrayList);
            recyclerView.setAdapter(varyTabAdapter);
            varyTabAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    varyTabAdapter.setPositionSelect(position);
                }
            });

        }
    }

    /**
     * 显示PopWindow
     *
     * @param view
     */
    private PopupWindow showPopWindow(View view ) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        PopupWindow popupWindow = new PopupWindow(view, displayMetrics.widthPixels, displayMetrics.heightPixels, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(false);
        return popupWindow;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){

            if (mPopWindowVary.isShowing()){
                mPopWindowVary.dismiss();
                tvVary.setSelected(false);
                return true;
            }
            if (mPopWindowHot.isShowing()){
                mPopWindowHot.dismiss();
                tvHot.setSelected(false);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void loadTabFailed(String s) {

    }

    @Override
    public void loadTabSuccess(ArrayList<String> strings) {
        varyTabAdapter = new VaryTabAdapter(R.layout.item_vary_music,strings);
        recyclerView.setAdapter(varyTabAdapter);
       varyTabAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
           @Override
           public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
             varyTabAdapter.setPositionSelect(position);
           }
       });
        stringArrayList = strings;
    }


}
