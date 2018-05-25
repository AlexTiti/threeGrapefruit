package com.findtech.threePomelos.music.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseViewHolder;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.sdk.base.recycler.BaseRecyclerViewAdapter;

import java.util.List;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/04/11
 */

public class VaryTabAdapter extends BaseRecyclerViewAdapter<String,BaseViewHolder> {

    private int positionSelect = -1;
    public VaryTabAdapter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tvTabMusic,item);
        if (helper.getAdapterPosition() == positionSelect){
            helper.getView(R.id.tvTabMusic).setSelected(true);
        }else {
            helper.getView(R.id.tvTabMusic).setSelected(false);
        }

    }

    public void setPositionSelect(int positionSelect) {
        this.positionSelect = positionSelect;
        notifyDataSetChanged();

    }
}
