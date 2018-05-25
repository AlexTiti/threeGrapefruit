package com.findtech.threePomelos.user.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.sdk.base.BaseCompatActivity;

import butterknife.BindView;

/**
 * @author Administrator
 *
 */
public class AboutUsActivity extends BaseCompatActivity {

    @BindView(R.id.include)
    View include;

    @Override
    protected void showLoading() {

    }

    @Override
    protected void onErrorViewClick(View v) {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        initToolBar(include, "关于三爸");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about_us;
    }

}
