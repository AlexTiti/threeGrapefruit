package com.findtech.threePomelos.user.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.home.MainActivity;
import com.findtech.threePomelos.sdk.base.BaseCompatActivity;
import com.findtech.threePomelos.sdk.base.mvp.BaseActivityMvp;
import com.findtech.threePomelos.sdk.base.mvp.Contract;
import com.findtech.threePomelos.user.present.SetNickNamePresent;
import com.findtech.threePomelos.utils.RequestUtils;
import com.findtech.threePomelos.utils.ToastUtil;

import butterknife.BindView;

/**
 * @author Administrator
 */
public class SetBabyNickNameActivity extends BaseActivityMvp<SetBabyNickNameActivity, SetNickNamePresent>
        implements View.OnClickListener, Contract.ViewMvp<String> {

    @BindView(R.id.etBabyName)
    EditText etBabyName;
    @BindView(R.id.btnNext)
    Button btnNext;
    @BindView(R.id.include)
    View include;

    private String birthdayBaby;
    private String nickNameBaby;


    @Override
    protected void initData() {
        super.initData();
        Intent intent = getIntent();
        birthdayBaby = intent.getStringExtra("Baby_Birthday");
    }

    @Override
    protected void showLoading() {

    }

    @Override
    protected void onErrorViewClick(View v) {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        initToolBarMain(include,"");
        btnNext.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_set_baby_nick_name;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnNext) {
            nickNameBaby = etBabyName.getText().toString().trim();
            if (TextUtils.isEmpty(nickNameBaby)) {
                ToastUtil.showToast(SetBabyNickNameActivity.this, getResources().getString(R.string.baby_birth_none));
                return;
            }
            if (TextUtils.isEmpty(birthdayBaby)) {
                ToastUtil.showToast(SetBabyNickNameActivity.this, getResources().getString(R.string.baby_birth_none));
                return;
            }
                saveBabyInfo();
        }
    }

    private void saveBabyInfo() {
        present.saveNickName(RequestUtils.getSharepreference(this).getString(RequestUtils.BABYSEX, "")
                , birthdayBaby, nickNameBaby, this);

    }

    @Override
    public void loadSuccess(String s) {
        if (!RequestUtils.getSharepreference(SetBabyNickNameActivity.this).getBoolean(RequestUtils.IS_LOGIN, false)) {
            RequestUtils.getSharepreferenceEditor(SetBabyNickNameActivity.this)
                    .putBoolean(RequestUtils.IS_LOGIN,true).apply();
            startActivity(MainActivity.class);
        }
        finish();
    }

    @Override
    public void loadFailed(String s) {
        checkNetWork();
    }

    @Override
    public void initDataFromServer() {

    }

    @Override
    protected SetNickNamePresent createPresent() {
        return new SetNickNamePresent();
    }
}
