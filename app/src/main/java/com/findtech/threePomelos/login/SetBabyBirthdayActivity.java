package com.findtech.threePomelos.login;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SignUpCallback;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.home.MainActivity;
import com.findtech.threePomelos.music.utils.L;
import com.findtech.threePomelos.net.NetWorkRequest;
import com.findtech.threePomelos.net.QueryBabyInfoCallBack;
import com.findtech.threePomelos.sdk.MyApplication;
import com.findtech.threePomelos.sdk.base.BaseCompatActivity;
import com.findtech.threePomelos.utils.RequestUtils;
import com.findtech.threePomelos.utils.ToastUtil;
import com.findtech.threePomelos.view.datepicker.DatepickerDialog;


/**
 * @author Administrator
 */
public class SetBabyBirthdayActivity extends BaseCompatActivity implements View.OnClickListener {


    private Button btnNext = null;
    private Button btnCancel = null;
    private Button btnComfirm = null;
    private TextView txtBirthdate = null;
    private LinearLayout layoutDatepicker = null;
    private DatepickerDialog mChangeBirthDialog;
    private ProgressDialog progressDialog;




    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void showLoading() {

    }

    @Override
    protected void onErrorViewClick(View v) {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        init();
        initProgressDialog();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.set_baby_birthday;
    }

    void init() {
        txtBirthdate = findViewById(R.id.txt_birthdate);
        btnNext = findViewById(R.id.btn_birthdate_next);
        btnCancel = findViewById(R.id.btn_datepicker_cancel);
        btnComfirm = findViewById(R.id.btn_datepicker_confirm);
        layoutDatepicker = findViewById(R.id.datapicker_birthday_layout);
        txtBirthdate.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnComfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_birthdate_next:
                registerUserAccount();
                break;
            case R.id.txt_birthdate:
                if (mChangeBirthDialog != null && mChangeBirthDialog.isShowing()) {
                    return;
                }
                mChangeBirthDialog = new DatepickerDialog(
                        SetBabyBirthdayActivity.this);
                setWindowSize(mChangeBirthDialog);
                mChangeBirthDialog.show();
                mChangeBirthDialog.setBirthdayListener(new DatepickerDialog.OnBirthListener() {
                    @Override
                    public void onClick(String year, String month, String day) {
                        txtBirthdate.setText(year + "-" + month + "-" + day);
                    }
                });
                break;
            case R.id.btn_datepicker_cancel:
                layoutDatepicker.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    void registerUserAccount() {
        progressDialog.show();
        AVUser user = new AVUser();
        user.setUsername(RequestUtils.getSharepreference(this).getString(RequestUtils.USERNAME, null));
        user.setPassword(MyApplication.getPassword());
        user.setMobilePhoneNumber(RequestUtils.getSharepreference(this).getString(RequestUtils.USERNAME, null));

        if (TextUtils.isEmpty(txtBirthdate.getText().toString())) {
            progressDialog.dismiss();
            ToastUtil.showToast(SetBabyBirthdayActivity.this, getResources().getString(R.string.baby_birth_none));
            return;
        }

        if (!TextUtils.isEmpty(MyApplication.getPassword()) &&
                !TextUtils.isEmpty(RequestUtils.getSharepreference(this).getString(RequestUtils.USERNAME, null))) {

            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        setBabyInfo();
                    } else {
                        L.e("===",e.toString());
                        progressDialog.dismiss();
                        if (e.getCode() == 214) {
                            ToastUtil.showToast(SetBabyBirthdayActivity.this, getResources().getString(R.string.phone_number_useless));
                        }
                    }
                }
            });
        } else {
            progressDialog.dismiss();
            ToastUtil.showToast(SetBabyBirthdayActivity.this, getResources().getString(R.string.pasw_empty));
        }
    }

    void setBabyInfo() {
        AVObject post = new AVObject(NetWorkRequest.BABY_INFO);
        post.put(RequestUtils.BABYSEX, RequestUtils.getSharepreference(this).getString(RequestUtils.BABYSEX, ""));
        post.put(RequestUtils.BIRTHDAY, txtBirthdate.getText().toString());
        post.put("post", AVUser.getCurrentUser());
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                progressDialog.dismiss();
                if (e == null) {
                    ToastUtil.showToast(getApplicationContext(), getResources().getString(R.string.regist_sucess));

                    AVUser.logInInBackground(RequestUtils.getSharepreference(SetBabyBirthdayActivity.this).getString(RequestUtils.USERNAME,null), MyApplication.getPassword(), new LogInCallback<AVUser>() {
                        @Override
                        public void done(AVUser avUser, AVException e) {
                            if (e == null) {
                                NetWorkRequest netWorkRequest = new NetWorkRequest(SetBabyBirthdayActivity.this);
                                NetWorkRequest.getUserInfo(SetBabyBirthdayActivity.this);
                                netWorkRequest.getBabyInfoDataAndSaveToDB(new QueryBabyInfoCallBack.QueryIsBind() {

                                    @Override
                                    public void finishQueryIsBind(boolean isBind, String deviceId) {


                                        RequestUtils.getSharepreferenceEditor(SetBabyBirthdayActivity.this)
                                                .putBoolean(RequestUtils.IS_LOGIN, true).apply();
                                        if (isBind) {
                                            startActivity(new Intent(SetBabyBirthdayActivity.this, MainActivity.class));
                                            progressDialog.dismiss();
                                        } else {
                                            startActivity(new Intent(SetBabyBirthdayActivity.this, MainActivity.class));
                                            progressDialog.dismiss();
                                        }
                                    }
                                });
                            } else {
                                progressDialog.dismiss();
                                Intent intent = new Intent(SetBabyBirthdayActivity.this, LoginActivity.class);
                                startActivity(intent);
                                switch (e.getCode()) {
                                    case 210:
                                        ToastUtil.showToast(SetBabyBirthdayActivity.this, getResources().getString(R.string.login_notice));
                                        break;
                                    case 211:
                                        ToastUtil.showToast(SetBabyBirthdayActivity.this, getResources().getString(R.string.name_useless));
                                        break;
                                    default:
                                        break;
                                }
                                if ("java.net.UnknownHostException".equals(e.getMessage())) {
                                    checkNetWork();
                                }
                                e.printStackTrace();
                            }
                        }
                    });





                } else {

                    ToastUtil.showToast(getApplicationContext(), getResources().getString(R.string.save_data_failed));
                    e.printStackTrace();
                }
            }
        });
    }

    private void setWindowSize(Dialog dialog) {
        DisplayMetrics dm = new DisplayMetrics();
        Window dialogWindow = dialog.getWindow();
        WindowManager m = dialogWindow.getWindowManager();
        m.getDefaultDisplay().getMetrics(dm);
        // 为获取屏幕宽、高
        WindowManager.LayoutParams p = dialogWindow.getAttributes();
        // 获取对话框当前的参数值
        dialogWindow.getDecorView().setPadding(0, 0, 0, 0);
        p.width = dm.widthPixels;
        p.alpha = 1.0f;
        p.dimAmount = 0.6f;
        p.gravity = Gravity.BOTTOM;
        dialogWindow.setAttributes(p);
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getResources().getString(R.string.registraing));
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
    }
}
