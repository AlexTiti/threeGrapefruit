package com.findtech.threePomelos.travel.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.findtech.threePomelos.sdk.base.fragment.BaseCompatFragment;

import java.util.ArrayList;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/03/23
 */

public class TravelAdapter extends FragmentStatePagerAdapter{

    ArrayList<BaseCompatFragment> fragmentArrayList;

    public TravelAdapter(FragmentManager fm,ArrayList<BaseCompatFragment> fragmentArrayList) {
        super(fm);
        this.fragmentArrayList = fragmentArrayList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentArrayList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentArrayList == null ? 0 : fragmentArrayList.size();
    }
}
