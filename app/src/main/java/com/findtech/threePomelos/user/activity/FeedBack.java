package com.findtech.threePomelos.user.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.SaveCallback;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.net.NetWorkRequest;
import com.findtech.threePomelos.sdk.base.BaseCompatActivity;
import com.findtech.threePomelos.user.bean.UserInfo;
import com.findtech.threePomelos.utils.ToastUtil;
import com.findtech.threePomelos.utils.Tools;

import butterknife.BindView;


/**
 * @author Administrator
 */
public class FeedBack extends BaseCompatActivity {

    @BindView(R.id.text_feed)
    EditText feedback;
    @BindView(R.id.btn_send)
    Button btn_send;
    @BindView(R.id.includeFeed)
    View includeFeed;
    private NetWorkRequest netWorkRequest;

    private UserInfo userInfo = UserInfo.getInstance();


    @Override
    protected void showLoading() {

    }

    @Override
    protected void onErrorViewClick(View v) {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        initToolBar(includeFeed,"问题反馈");
        netWorkRequest = new NetWorkRequest(this);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = feedback.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.showToast(FeedBack.this, getResources().getString(R.string.name_not));
                    return;
                }
                netWorkRequest.sendFeedBackToServer(content, Tools.getCurrentDateHour(), userInfo.getNickName(), new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            ToastUtil.showToast(FeedBack.this, getResources().getString(R.string.send_sucess));
                            finish();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_feed_back;
    }
}
