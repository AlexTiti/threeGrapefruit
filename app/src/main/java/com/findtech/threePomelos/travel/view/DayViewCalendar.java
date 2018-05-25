package com.findtech.threePomelos.travel.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.sdk.base.mvp.Contract;
import com.findtech.threePomelos.view.calendar.CalendarDay;
import com.findtech.threePomelos.view.calendar.DayViewDecorator;
import com.findtech.threePomelos.view.calendar.DayViewFacade;


import java.util.ArrayList;
import java.util.HashSet;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/03/29
 */

public class DayViewCalendar implements DayViewDecorator {

    private Drawable drawable;
    private ArrayList<CalendarDay> calendarDayHashSet;

    public DayViewCalendar(Context context,ArrayList<CalendarDay> calendarDayHashSet) {
        this.calendarDayHashSet = calendarDayHashSet;
        drawable = context.getResources().getDrawable(R.drawable.inset_travel);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return calendarDayHashSet.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setSelectionDrawable(drawable);
    }
}
