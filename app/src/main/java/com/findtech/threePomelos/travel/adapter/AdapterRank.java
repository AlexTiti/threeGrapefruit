package com.findtech.threePomelos.travel.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.travel.bean.RankBean;
import com.findtech.threePomelos.utils.glide.GlideUtils;

import java.util.List;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/05/30
 */
public class AdapterRank extends BaseQuickAdapter<RankBean, BaseViewHolder> {

    public AdapterRank(int layoutResId, @Nullable List<RankBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, RankBean item) {
        helper.setText(R.id.tvNameRank, item.getUserName())
                .setText(R.id.tvTimeRank, item.getTime());

        GlideUtils.glideToRoundImageView(mContext, item.getImageUrl(), (ImageView) helper.getView(R.id.ivHeadRank));
    }
}
