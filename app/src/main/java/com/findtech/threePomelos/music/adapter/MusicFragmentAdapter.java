package com.findtech.threePomelos.music.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.music.info.MusicBean;
import com.findtech.threePomelos.sdk.base.mvp.Contract;
import com.findtech.threePomelos.sdk.base.recycler.BaseRecyclerViewAdapter;
import com.findtech.threePomelos.utils.glide.GlideUtils;

import java.util.List;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/04/09
 */

public class MusicFragmentAdapter extends BaseRecyclerViewAdapter<MusicBean, BaseViewHolder> {

    private Context mContext;

    public MusicFragmentAdapter(int layoutResId, @Nullable List data, Context mContext) {
        super(layoutResId, data);
        this.mContext = mContext;
    }

    public MusicFragmentAdapter(int layoutResId, Context mContext) {
        super(layoutResId);
        this.mContext = mContext;
    }

    @Override
    protected void convert(BaseViewHolder helper, MusicBean item) {
        helper.setText(R.id.tvMusicName, item.getName());
        Glide.with(mContext).load(R.drawable.img_bac_freq)
                .apply(GlideUtils.simpleOptions())
                .into((ImageView) helper.getView(R.id.ivMusicImage));

    }

}
