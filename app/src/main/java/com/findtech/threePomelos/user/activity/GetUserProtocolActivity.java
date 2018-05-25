package com.findtech.threePomelos.user.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.FindCallback;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.net.NetWorkRequest;
import com.findtech.threePomelos.sdk.base.BaseCompatActivity;
import com.findtech.threePomelos.utils.ToastUtil;

import java.util.List;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhi.zhang
 * @date 5/3/16
 */
public class GetUserProtocolActivity extends BaseCompatActivity {

    @BindView(R.id.webView_user_protect)
    WebView webView_user_protect;
    @BindView(R.id.pb_user_protect)
    ProgressBar progressBar;
    @BindView(R.id.container)
    ConstraintLayout container;
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
        initToolBar(include,"用户协议");
        final NetWorkRequest netWorkRequest = new NetWorkRequest(this);
        initWebView();
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> e) throws Exception {

                netWorkRequest.getUserProtect(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException exception) {
                        if (exception == null) {
                            if (list.size() > 0) {
                                AVFile avFile = list.get(1).getAVFile("userProtocol");
                                if (avFile == null) {
                                    ToastUtil.showToast(GetUserProtocolActivity.this, getString(R.string.data_exception));
                                    return;
                                } else {
                                    e.onNext(avFile.getUrl());
                                }
                            }
                        } else {
                            e.onError(exception);
                        }
                    }
                });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        webView_user_protect.loadUrl(s);
                    }
                });

        progressBar.setBackgroundColor(getResources().getColor(R.color.white));
        progressBar.setMax(100);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_protocol;
    }

    private void initWebView() {

        webView_user_protect = findViewById(R.id.webView_user_protect);
        webView_user_protect.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.loadUrl(request.getUrl().toString());
                } else {
                    view.loadUrl(request.toString());
                }
                return true;
            }
        });

        webView_user_protect.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        container.removeView(webView_user_protect);
        webView_user_protect.removeAllViews();
        webView_user_protect.destroy();
    }

}
