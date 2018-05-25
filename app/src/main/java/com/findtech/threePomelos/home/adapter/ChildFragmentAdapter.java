package com.findtech.threePomelos.home.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


import com.findtech.threePomelos.sdk.base.fragment.BaseCompatFragment;
import com.findtech.threePomelos.sdk.base.mvp.BaseFragmentMvp;

import java.util.ArrayList;

/**
 * <pre>
 *
 *   @author   :   Alex
 *   @e_mail   :   18238818283@sina.cn
 *   @time     :   2018/03/14
 *   @desc     :
 *   @version  :   V 1.0.9
 */

public class ChildFragmentAdapter extends FragmentStatePagerAdapter {

    private ArrayList<BaseCompatFragment> fragmentMvps;
    private String[] string = {"出行", "音乐", "亲子"};
    public ChildFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setFragmentMvps(ArrayList<BaseCompatFragment> fragmentMvps) {
        this.fragmentMvps = fragmentMvps;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentMvps.get(position);
    }

    @Override
    public int getCount() {
        return fragmentMvps == null ? 0 : fragmentMvps.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return string[position];
    }
}
