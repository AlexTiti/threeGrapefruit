package com.findtech.threePomelos.home.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.mydevices.bean.DeviceCarBean;

import java.util.List;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/05/05
 */

public class UserDeviceAdapter extends BaseQuickAdapter<DeviceCarBean,BaseViewHolder> {

    public UserDeviceAdapter(int layoutResId, @Nullable List<DeviceCarBean> data) {
        super(layoutResId, data);
    }

    public UserDeviceAdapter(int layoutResId) {
        super(layoutResId);
    }




    @Override
    protected void convert(BaseViewHolder helper, DeviceCarBean item) {
        helper.setText(R.id.tvTitle,item.getDeviceName())
                .setText(R.id.tvRight,item.getCompany());
    }
}
