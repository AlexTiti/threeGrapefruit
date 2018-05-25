package com.findtech.threePomelos.travel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.sdk.base.fragment.BaseCompatFragment;
import com.findtech.threePomelos.sdk.base.mvp.BaseActivityMvp;
import com.findtech.threePomelos.sdk.base.mvp.Contract;
import com.findtech.threePomelos.travel.adapter.TravelAdapter;
import com.findtech.threePomelos.travel.present.TravelDetailPresent;
import com.findtech.threePomelos.utils.IContent;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * @author Alex
 */
public class TravelDetailActivity extends BaseActivityMvp<TravelDetailActivity, TravelDetailPresent> implements Contract.ViewMvp<ArrayList<BaseCompatFragment>> {

    @BindView(R.id.vp_travel)
    ViewPager viewPager;
    @BindView(R.id.include)
    View include;

    private TravelAdapter mTravelAdapter;
    private int position = 0;

    @Override
    protected void initData() {
        super.initData();

        Intent intent = getIntent();
        if (intent != null) {
            position = intent.getIntExtra(IContent.TRAVEL_FRAGMRNT, 0);
        }
    }

    @Override
    protected void showLoading() {

    }

    @Override
    protected void onErrorViewClick(View v) {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        if (position == 0) {
            initToolBar(include, getResources().getString(R.string.kilometer));
        } else {
            initToolBar(include, getResources().getString(R.string.frequency));
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_travel_detail;
    }


    @Override
    public void loadSuccess(ArrayList<BaseCompatFragment> baseCompatFragments) {
        mTravelAdapter = new TravelAdapter(getSupportFragmentManager(), baseCompatFragments);
        viewPager.setAdapter(mTravelAdapter);
        viewPager.setCurrentItem(position);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    initToolBar(include, "日里程");
                } else {
                    initToolBar(include, "周频率");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void loadFailed(String s) {

    }

    @Override
    public void initDataFromServer() {
        present.getFragment();
    }

    @Override
    protected TravelDetailPresent createPresent() {
        return new TravelDetailPresent();
    }

}
