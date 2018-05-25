package com.findtech.threePomelos.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.avos.avoscloud.UpdatePasswordCallback;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.home.MainActivity;
import com.findtech.threePomelos.music.utils.L;
import com.findtech.threePomelos.sdk.base.BaseCompatActivity;
import com.findtech.threePomelos.utils.ToastUtil;

/**
 * @author Alex
 */
public class ForgetPasswordActivity extends BaseCompatActivity implements View.OnClickListener {

    private EditText phoneTextView, passwordTextView, identifyTextView;
    private Button nextButton;
    String phoneStr, passwordStr;
    TextView sendIdentify;



    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        phoneStr = phoneTextView.getText().toString();
        passwordStr = passwordTextView.getText().toString();
        switch (view.getId()) {
            case R.id.send_identify:
                if (TextUtils.isEmpty(phoneStr)) {
                    Toast toast = Toast.makeText(this, R.string.input_phone_number, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                } else if (!phoneStr.isEmpty() && !phoneStr.matches("^(13|14|15|17|18)\\d{9}$")) {
                    Toast toast = Toast.makeText(this, R.string.input_legal_phone_number, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                } else {
                    requestPassword(phoneStr);
                }
                break;
            case R.id.next_step:
                String identifyStr = identifyTextView.getText().toString();
                if (TextUtils.isEmpty(phoneStr)) {
                    ToastUtil.showToast(ForgetPasswordActivity.this, R.string.input_phone_number);
                    return;
                }
                String regex = "^(13|14|15|17|18)\\d{9}$";
                if (!TextUtils.isEmpty(phoneStr) && !phoneStr.matches(regex)) {
                    ToastUtil.showToast(ForgetPasswordActivity.this, R.string.input_legal_phone_number);
                    return;
                }
                if (TextUtils.isEmpty(passwordStr)) {
                    ToastUtil.showToast(ForgetPasswordActivity.this, R.string.input_password);
                    return;
                }
                if (TextUtils.isEmpty(identifyStr)) {
                    ToastUtil.showToast(ForgetPasswordActivity.this, R.string.input_identify);
                    return;
                }
                checkIdentifyAndResetPassword(identifyStr, passwordStr);
                break;
                default:
                    break;
        }
    }

    void requestPassword(final String phone) {

        AVUser.requestPasswordResetBySmsCodeInBackground(phone, new RequestMobileCodeCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    timer.start();
                    Toast toast = Toast.makeText(ForgetPasswordActivity.this, getString(R.string.checkNum_send), Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    L.e("======", e.getMessage()+"==="+e.getCode());
                    if (e.getCode() == 213) {
                        Toast toast = Toast.makeText(ForgetPasswordActivity.this, R.string.noFoundUser, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    if (e.getCode() == 601){
                        Toast toast = Toast.makeText(ForgetPasswordActivity.this, R.string.checkNumberRate, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            }
        });
    }

    private void checkIdentifyAndResetPassword(String identify, String newPassword) {

        L.e("=======", Thread.currentThread().getName());
        AVUser.resetPasswordBySmsCodeInBackground(identify, newPassword, new UpdatePasswordCallback() {
            @Override
            public void done(AVException e) {

                if (e == null) {
                    if (AVUser.getCurrentUser() != null) {
                        Intent nextIntent = new Intent(ForgetPasswordActivity.this, MainActivity.class);
                        startActivity(nextIntent);
                    } else {
                        Intent nextIntent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                        startActivity(nextIntent);
                    }
                    ToastUtil.showToast(ForgetPasswordActivity.this, R.string.title_activity_forget_password_success);
                } else {
                    if (e.getCode() == 603) {
                        Toast toast = Toast.makeText(ForgetPasswordActivity.this, R.string.smsCodeError, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }

            }
        });
    }

    CountDownTimer timer = new CountDownTimer(60000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            sendIdentify.setBackground(getResources().getDrawable(R.drawable.forget_psd_bac));
            sendIdentify.setEnabled(false);
            sendIdentify.setText(millisUntilFinished / 1000 + "s");
        }

        @Override
        public void onFinish() {
            sendIdentify.setBackground(getResources().getDrawable(R.drawable.login_button_selector));
            sendIdentify.setEnabled(true);
            sendIdentify.setText(getString(R.string.identify));
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    @Override
    protected void showLoading() {

    }

    @Override
    protected void onErrorViewClick(View v) {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {

        phoneTextView = findViewById(R.id.input_phone);
        passwordTextView = findViewById(R.id.input_password);
        identifyTextView = findViewById(R.id.identify_num);
        nextButton = findViewById(R.id.next_step);
        sendIdentify = findViewById(R.id.send_identify);

        sendIdentify.setOnClickListener(this);
        nextButton.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_forget_password;
    }
}
