package com.findtech.threePomelos.user.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.home.MainActivity;
import com.findtech.threePomelos.login.LoginActivity;
import com.findtech.threePomelos.login.ThirdPartyController;
import com.findtech.threePomelos.sdk.AppUtils;
import com.findtech.threePomelos.sdk.base.BaseCompatActivity;
import com.findtech.threePomelos.sdk.manger.AppManager;
import com.findtech.threePomelos.sdk.manger.RxHelper;
import com.findtech.threePomelos.utils.RequestUtils;
import com.findtech.threePomelos.view.dialog.CustomDialog;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.listener.SocializeListeners;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * @author Administrator
 */
public class SettingActivity extends BaseCompatActivity implements View.OnClickListener {

    @BindView(R.id.tvAboutUs)
    TextView tvAboutUs;
    @BindView(R.id.tvUserProtect)
    TextView tvUserProtect;
    @BindView(R.id.btnLoginOut)
    Button btnLoginOut;
    @BindView(R.id.includeToolbar)
    View includeToolbar;

    private ThirdPartyController mThirdPartyController;
    public final int TOAST_NUMB = 1001;

    @Override
    protected void showLoading() {

    }

    @Override
    protected void onErrorViewClick(View v) {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        initToolBar(includeToolbar, "设置");
        mThirdPartyController = new ThirdPartyController(this);
        tvAboutUs.setOnClickListener(this);
        tvUserProtect.setOnClickListener(this);
        btnLoginOut.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tvAboutUs:
                startActivity(AboutUsActivity.class);
                break;
            case R.id.btnLoginOut:
                showVerifyLogoutDialog();
                break;
            case R.id.tvUserProtect:
                startActivity(GetUserProtocolActivity.class);
                break;
            default:
                break;
        }
    }

    private void showVerifyLogoutDialog() {
        final CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setTitle(getString(R.string.notice))
                .setNotifyInfo(getString(R.string.logout_confirm))
                .setShowButton(true)
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logOut();
                        dialog.dismiss();
                        startActivity(new Intent(SettingActivity.this, LoginActivity.class));
                        finish();
                    }
                });

        builder.setNegativeButton(getString(R.string.cancle), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void logOut() {
        AVUser.logOut();
        if (!mThirdPartyController.getUMSocialServiceLoginInstance().getEntity().mInitialized) {
            final Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
                @Override
                public void subscribe(final ObservableEmitter<Integer> e) throws Exception {
                    mThirdPartyController.getUMSocialServiceLoginInstance().deleteOauth(
                            mContext, SHARE_MEDIA.WEIXIN,
                            new SocializeListeners.SocializeClientListener() {
                                @Override
                                public void onStart() {
                                }

                                @Override
                                public void onComplete(int status, SocializeEntity entity) {
                                    e.onNext(status);
                                }
                            }
                    );
                }
            });
            Observable.create(new ObservableOnSubscribe<Integer>() {
                @Override
                public void subscribe(final ObservableEmitter<Integer> e) throws Exception {
                    mThirdPartyController.getUMSocialServiceLoginInstance().initEntity(mContext,
                            new SocializeListeners.SocializeClientListener() {
                                @Override
                                public void onStart() {
                                }

                                @Override
                                public void onComplete(int status, SocializeEntity entity) {
                                    e.onNext(status);
                                }
                            });
                }
            }).filter(new Predicate<Integer>() {
                @Override
                public boolean test(Integer integer) throws Exception {
                    return integer == 200;
                }
            }).flatMap(new Function<Integer, ObservableSource<Integer>>() {
                @Override
                public ObservableSource<Integer> apply(Integer integer) throws Exception {
                    return observable;
                }
            }).filter(new Predicate<Integer>() {
                @Override
                public boolean test(Integer integer) throws Exception {
                    return integer == 200;
                }
            }).compose(RxHelper.<Integer>rxSchedulerHelper())
                    .subscribe(new Consumer<Integer>() {
                        @Override
                        public void accept(Integer integer) throws Exception {
                            Toast.makeText(mContext, getString(R.string.logout_success), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {

            Observable.create(new ObservableOnSubscribe<Integer>() {
                @Override
                public void subscribe(final ObservableEmitter<Integer> e) throws Exception {
                    mThirdPartyController.getUMSocialServiceLoginInstance().deleteOauth(mContext, SHARE_MEDIA.WEIXIN,
                            new SocializeListeners.SocializeClientListener() {
                                @Override
                                public void onStart() {
                                }
                                @Override
                                public void onComplete(int status, SocializeEntity entity) {
                                    e.onNext(status);
                                }
                            }
                    );
                }
            }).compose(RxHelper.<Integer>rxSchedulerHelper())
                    .subscribe(new Consumer<Integer>() {
                        @Override
                        public void accept(Integer integer) throws Exception {
                            Toast.makeText(mContext, getString(R.string.logout_success), Toast.LENGTH_SHORT).show();
                        }
                    });

        }


    }

}
