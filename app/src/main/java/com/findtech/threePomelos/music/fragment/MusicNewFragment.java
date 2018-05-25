package com.findtech.threePomelos.music.fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.music.activity.AllSongActivity;
import com.findtech.threePomelos.music.activity.SixItemMusicActivity;
import com.findtech.threePomelos.music.adapter.MusicFragmentAdapter;
import com.findtech.threePomelos.music.info.MusicBean;
import com.findtech.threePomelos.music.present.MusicFragmentPresent;
import com.findtech.threePomelos.sdk.base.mvp.BaseFragmentMvp;
import com.findtech.threePomelos.sdk.base.mvp.Contract;
import com.findtech.threePomelos.utils.IContent;
import com.findtech.threePomelos.utils.glide.GlideUtils;
import com.findtech.threePomelos.view.banner.BannerView;
import com.findtech.threePomelos.view.banner.HolderCreator;
import com.findtech.threePomelos.view.banner.ViewHolder;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/04/09
 */

public class MusicNewFragment extends BaseFragmentMvp<MusicNewFragment, MusicFragmentPresent>
        implements Contract.ViewMvp<ArrayList<MusicBean>>, View.OnClickListener,
        BannerView.BannerPageClickListener{

    @BindView(R.id.recyclerViewMusic)
    RecyclerView recyclerViewMusic;
    @BindView(R.id.bannerView)
    BannerView bannerView;
    @BindView(R.id.tvAllSong)
    TextView tvAllSong;

    @BindView(R.id.iv_fluid)
    ImageView iv_fluid;
    @BindView(R.id.iv_sleep)
    ImageView iv_sleep;
    @BindView(R.id.iv_appease)
    ImageView iv_appease;
    @BindView(R.id.iv_song)
    ImageView iv_song;
    @BindView(R.id.iv_story)
    ImageView iv_story;

    @BindView(R.id.ivFirst)
    ImageView ivFirst;
    @BindView(R.id.ivSecond)
    ImageView ivSecond;
    @BindView(R.id.ivThird)
    ImageView ivThird;
    @BindView(R.id.ivFourth)
    ImageView ivFourth;

    private MusicFragmentAdapter mMusicFragmentAdapter;
    private ArrayList<Drawable> integers;

    public static MusicNewFragment getInstance() {
        return new MusicNewFragment();
    }

    @Override
    public void loadSuccess(ArrayList<MusicBean> beans) {

        mMusicFragmentAdapter = new MusicFragmentAdapter(R.layout.item_recycler_music, beans, mContext);
        recyclerViewMusic.setAdapter(mMusicFragmentAdapter);
    }

    @Override
    public void loadFailed(String s) {

    }

    @Override
    protected MusicFragmentPresent createPresent() {
        return new MusicFragmentPresent();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_muisc;
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        getPresent().getRecyclerData();
        recyclerViewMusic.setLayoutManager(new GridLayoutManager(mActivity, 2));
        mMusicFragmentAdapter = new MusicFragmentAdapter(R.layout.item_recycler_music, mContext);
        integers = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            integers.add(getResources().getDrawable(R.drawable.img_bac_freq));
        }
        // 此处要设置在 setPages()之前
        bannerView.setBannerPageClickListener(this);
        bannerView.setPages(integers, new HolderCreator() {
            @Override
            public ViewHolderFragment createViewHolder() {
                return new ViewHolderFragment();
            }
        });
        Glide.with(mContext).load(R.drawable.img_bac_freq)
                .apply(GlideUtils.simpleOptions())
                .into(ivFirst);
        Glide.with(mContext).load(R.drawable.img_bac_freq)
                .apply(GlideUtils.simpleOptions())
                .into(ivSecond);
        Glide.with(mContext).load(R.drawable.img_bac_freq)
                .apply(GlideUtils.simpleOptions())
                .into(ivThird);
        Glide.with(mContext).load(R.drawable.img_bac_freq)
                .apply(GlideUtils.simpleOptions())
                .into(ivFourth);
    }

    @Override
    public void initUI(View view, @Nullable Bundle savedInstanceState) {

        tvAllSong.setOnClickListener(this);
        iv_fluid.setOnClickListener(this);
        iv_sleep.setOnClickListener(this);
        iv_appease.setOnClickListener(this);
        iv_song.setOnClickListener(this);
        iv_story.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        switch (v.getId()) {

            case R.id.tvAllSong:
                startNewActivity(AllSongActivity.class);
                break;
            case R.id.iv_fluid:
                bundle.putInt(IContent.NUMBER, 0);
                startNewActivity(SixItemMusicActivity.class, bundle);
                break;
            case R.id.iv_sleep:
                bundle.putInt(IContent.NUMBER, 1);
                startNewActivity(SixItemMusicActivity.class, bundle);
                break;
            case R.id.iv_appease:
                bundle.putInt(IContent.NUMBER, 2);
                startNewActivity(SixItemMusicActivity.class, bundle);
                break;
            case R.id.iv_song:
                bundle.putInt(IContent.NUMBER, 3);
                startNewActivity(SixItemMusicActivity.class, bundle);
                break;
            case R.id.iv_story:
                bundle.putInt(IContent.NUMBER, 4);
                startNewActivity(SixItemMusicActivity.class, bundle);
                break;
            default:
                break;
        }
    }

    @Override
    public void onPageClick(View view, int position) {

        Bundle bundle = new Bundle();
        bundle.putInt(IContent.NUMBER, position);
        startNewActivity(SixItemMusicActivity.class, bundle);
    }

    public static class ViewHolderFragment implements ViewHolder<Drawable> {

        private ImageView imageView;

        @Override
        public View createView(Context context) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_banner, null);
            imageView = view.findViewById(R.id.imageView);
            return view;
        }

        @Override
        public void onBind(Context context, int i, Drawable integer) {
            imageView.setImageDrawable(integer);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        bannerView.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        bannerView.start();
    }
}
