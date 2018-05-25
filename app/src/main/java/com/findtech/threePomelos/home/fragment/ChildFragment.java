package com.findtech.threePomelos.home.fragment;


import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.home.adapter.ChildFragmentAdapter;
import com.findtech.threePomelos.home.presenter.ChildPresent;
import com.findtech.threePomelos.sdk.base.fragment.BaseCompatFragment;
import com.findtech.threePomelos.sdk.base.mvp.BaseFragmentMvp;
import com.findtech.threePomelos.sdk.base.mvp.Contract;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;

/**
 * A fragment with a Google +1 button.
 *
 * @author Administrator
 */
public class ChildFragment extends BaseFragmentMvp<ChildFragment, ChildPresent>
        implements Contract.ViewMvp<ArrayList<BaseCompatFragment>>,
        ViewPager.OnPageChangeListener {

    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    public static ChildFragment newInstance() {

        Bundle args = new Bundle();
        ChildFragment fragment = new ChildFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private ChildFragmentAdapter adapter;

    public static ChildFragment getInstance() {
        return new ChildFragment();
    }

    @Override
    protected ChildPresent createPresent() {
        return new ChildPresent();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_child;
    }

    @Override
    public void initUI(View view, @Nullable Bundle savedInstanceState) {
        adapter = new ChildFragmentAdapter(getFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        getPresent().getTabs();
    }

    @Override
    public void loadSuccess(ArrayList<BaseCompatFragment> fragments) {
        adapter.setFragmentMvps(fragments);
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                setIndicator(tabLayout, 10, 10);
            }
        });

    }

    @Override
    public void loadFailed(String s) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        if (position == 0) {
            tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.white));
            tabLayout.setTabTextColors(getResources().getColor(R.color.white), getResources().getColor(R.color.white));

        } else {
            tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.text_pink));
            tabLayout.setTabTextColors(getResources().getColor(R.color.text_color), getResources().getColor(R.color.text_pink));
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void setIndicator(TabLayout tabs, int leftDip, int rightDip) {
        Class<?> tabLayout = tabs.getClass();
        Field tabStrip = null;
        try {
            tabStrip = tabLayout.getDeclaredField("mTabStrip");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        Objects.requireNonNull(tabStrip).setAccessible(true);
        LinearLayout llTab = null;
        try {
            llTab = (LinearLayout) tabStrip.get(tabs);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        int left = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftDip, Resources.getSystem().getDisplayMetrics());
        int right = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightDip, Resources.getSystem().getDisplayMetrics());

        for (int i = 0; i < Objects.requireNonNull(llTab).getChildCount(); i++) {
            View child = llTab.getChildAt(i);
            child.setPadding(0, 0, 0, 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
            params.leftMargin = left;
            params.rightMargin = right;
            child.setLayoutParams(params);
            child.invalidate();
        }
    }
}
