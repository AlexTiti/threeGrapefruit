package com.findtech.threePomelos.user.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.FindCallback;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.net.NetWorkRequest;
import com.findtech.threePomelos.sdk.base.BaseCompatActivity;
import com.findtech.threePomelos.user.adapter.ExpandListAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author Alex
 * @date 2017/11/09
 */
public class CommendProblemActivity extends BaseCompatActivity implements View.OnClickListener {

    @BindView(R.id.exListView)
    ExpandableListView mExpandableListView;
    @BindView(R.id.imageView5)
    ImageView imageView5;
    @BindView(R.id.includeCommon)
    View includeCommon;
    private List<ArrayList<String>> lists = new ArrayList<>();
    private List<String> strings = new ArrayList<>();

    private NetWorkRequest netWorkRequest;
    private String company = "3Pomelos";
    private String clickType = "Pomelos_A3";
    private int[] titleList = {R.string.bluetooth_common, R.string.bluetooth_scan_common, R.string.bluetooth_fail_common, R.string.bluetooth_data_common};
    private int[] methods = {R.string.bluetooth_common_deal, R.string.bluetooth_scan_common_deal, R.string.bluetooth_fail_common_deal, R.string.bluetooth_data_common_deal};

    @Override
    protected void showLoading() {

    }

    @Override
    protected void onErrorViewClick(View v) {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {

        initToolBar(includeCommon,"常见问题");
        imageView5.setOnClickListener(this);
        netWorkRequest = new NetWorkRequest(this);
        for (int i = 0; i < titleList.length; i++) {
            strings.add(getString(titleList[i]));
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add(getString(methods[i]));
            lists.add(arrayList);
        }

        ExpandListAdapter adapter = new ExpandListAdapter(lists, strings, this);
        mExpandableListView.setAdapter(adapter);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_commen_problem;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView5:
                break;
            default:
                break;
        }
    }



}
