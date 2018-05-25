package com.findtech.threePomelos.user.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.sdk.base.BaseCompatActivity;

import butterknife.BindView;

/**
 * @author Alex
 */
public class InstructionsMainActivity extends BaseCompatActivity implements View.OnClickListener {


    @BindView(R.id.pb_user_protect)
    ProgressBar progressBar;
    @BindView(R.id.webView)
    WebView webView;
    String instructions, url;
    @BindView(R.id.text_common)
    TextView text_common;
    @BindView(R.id.text_instruction)
    TextView text_instruction;
    @BindView(R.id.container)
    ConstraintLayout layout;
    @BindView(R.id.includeInstructions)
    View includeInstructions;


    @Override
    protected void showLoading() {

    }

    @Override
    protected void onErrorViewClick(View v) {

    }

    @Override
    protected void initData() {
        super.initData();

        Intent intent = getIntent();
        if (intent != null) {
            instructions = intent.getStringExtra("url0");
            url = intent.getStringExtra("url1");
        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        initToolBar(includeInstructions,"使用说明");
        progressBar.setBackgroundColor(getResources().getColor(R.color.white));
        progressBar.setMax(100);
        text_common.setOnClickListener(this);
        text_instruction.setOnClickListener(this);


        webView.setWebViewClient(new WebViewClient() {
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

        webView.setWebChromeClient(new WebChromeClient() {
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

        webView.loadUrl(instructions);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_instructions_main;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_common:
                text_common.setTextColor(getResources().getColor(R.color.text_pink));
                text_instruction.setTextColor(getResources().getColor(R.color.text_grey));
                webView.loadUrl(instructions);
                break;
            case R.id.text_instruction:
                text_common.setTextColor(getResources().getColor(R.color.text_grey));
                text_instruction.setTextColor(getResources().getColor(R.color.text_pink));
                webView.loadUrl(url);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        layout.removeView(webView);
        webView.destroy();


    }
}
