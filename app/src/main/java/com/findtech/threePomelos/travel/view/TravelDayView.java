package com.findtech.threePomelos.travel.view;


import com.findtech.threePomelos.view.calendar.CalendarDay;
import com.findtech.threePomelos.view.calendar.DayViewDecorator;
import com.findtech.threePomelos.view.calendar.DayViewFacade;
import com.findtech.threePomelos.view.calendar.spans.DotSpan;

import java.util.HashSet;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/03/20
 */

public class TravelDayView implements DayViewDecorator {

    private HashSet<CalendarDay> calendarDayHashSet;
    private int color;

    public TravelDayView(HashSet<CalendarDay> calendarDayHashSet, int color) {
        this.calendarDayHashSet = calendarDayHashSet;
        this.color = color;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return calendarDayHashSet.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new DotSpan(5,color));

    }
}
