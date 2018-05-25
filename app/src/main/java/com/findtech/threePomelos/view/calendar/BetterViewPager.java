package com.findtech.threePomelos.view.calendar;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * does some reflection that isn't needed.
 * And was making view creation time rather large. So lets override it and make it better!
 * @author Administrator
 */
public class BetterViewPager extends ViewPager {

    public BetterViewPager(Context context) {
        super(context);
    }

    public BetterViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


}
