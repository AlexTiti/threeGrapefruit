package com.findtech.threePomelos.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.avos.avoscloud.AVUser;
import com.findtech.threePomelos.database.OperateDBUtils;
import com.findtech.threePomelos.entity.BabyInfoEntity;
import com.findtech.threePomelos.home.MainActivity;
import com.findtech.threePomelos.login.LoginActivity;
import com.findtech.threePomelos.music.utils.HandlerUtil;
import com.findtech.threePomelos.net.NetWorkRequest;
import com.findtech.threePomelos.utils.IContent;
import com.findtech.threePomelos.utils.NetUtils;


/**
 *
 * @author zhi.zhang
 * @date 3/16/16
 */
public class LaunchActivity extends AppCompatActivity implements OperateDBUtils.SaveBabyInfoFinishListener {

    private OperateDBUtils operateDBUtils;
    private NetWorkRequest netWorkRequest;
    private boolean isShowOnce = false;
    SharedPreferences sp;
    Handler mHandle ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION );
        }
        mHandle = HandlerUtil.getInstance(this);
        sp = getSharedPreferences(IContent.IS_FIRST_USE, MODE_PRIVATE);

        operateDBUtils = new OperateDBUtils(this);
        operateDBUtils.setSaveBabyInfoFinishListener(this);

        netWorkRequest = new NetWorkRequest(getApplicationContext());
        if (AVUser.getCurrentUser() != null && NetUtils.isConnectInternet(this)) {
            netWorkRequest.selectDeviceTypeAndIdentifier();
        }
        mHandle.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sp.getBoolean(IContent.IS_FIRST_USE, true)) {
                    startActivity(new Intent(LaunchActivity.this, GuideActivity.class));
                    finish();
                    return;
                }
                if (AVUser.getCurrentUser() != null) {
                    operateDBUtils.queryBabyInfoDataFromDB();
                    NetWorkRequest.getUserInfo(LaunchActivity.this);
                } else {
                    startActivity(new Intent(LaunchActivity.this, LoginActivity.class));
                    finish();
                }
            }
        },1000);

    }

    @Override
    public void saveBabyInfoFinish() {
                startActivity(new Intent(LaunchActivity.this, MainActivity.class));
                finish();
    }
}
