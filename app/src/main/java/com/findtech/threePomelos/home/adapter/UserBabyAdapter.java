package com.findtech.threePomelos.home.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.entity.BabyInfoEntity;

import java.util.List;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/05/05
 */

public class UserBabyAdapter extends BaseQuickAdapter<BabyInfoEntity, BaseViewHolder> {

    public UserBabyAdapter(int layoutResId, @Nullable List<BabyInfoEntity> data) {
        super(layoutResId, data);
    }

    public UserBabyAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, BabyInfoEntity item) {
        helper.setText(R.id.tvTitle, item.getBabyName())
                .setText(R.id.tvRight, item.getBabySex());
    }
}
