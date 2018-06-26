package com.findtech.threePomelos.music.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.animation.ScaleInAnimation;
import com.chad.library.adapter.base.animation.SlideInRightAnimation;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.music.info.MusicBean;
import com.findtech.threePomelos.sdk.base.recycler.BaseRecyclerViewAdapter;
import com.findtech.threePomelos.utils.glide.GlideUtils;

import java.util.List;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/04/11
 */

public class AllSongAdapter extends BaseRecyclerViewAdapter<MusicBean,BaseViewHolder> {

    public AllSongAdapter(int layoutResId, @Nullable List<MusicBean> data) {
        super(layoutResId, data);
        init();
    }

    public AllSongAdapter(int layoutResId) {
        super(layoutResId);
        init();
    }

    @Override
    protected void convert(BaseViewHolder helper, MusicBean item) {
        helper.setText(R.id.tvMusicName,item.getTitle());
        GlideUtils.glideToImageView(mContext,item.getImageFace(),(ImageView) helper.getView(R.id.ivMusicImage));
    }

    private void init() {
        openLoadAnimation(new ScaleInAnimation(0.8f));
        isFirstOnly(true);
    }
}
