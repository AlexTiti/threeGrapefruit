package com.findtech.threePomelos.travel.view;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;


import com.findtech.threePomelos.R;
import com.findtech.threePomelos.view.calendar.CalendarDay;
import com.findtech.threePomelos.view.calendar.DayViewDecorator;
import com.findtech.threePomelos.view.calendar.DayViewFacade;

import java.util.ArrayList;
import java.util.Date;

/**
 * Decorate a day by making the text big and bold
 *
 * @author Administrator
 */
public class OneDayDecorator implements DayViewDecorator {

    private CalendarDay date;
    private Drawable drawable;
    private Drawable drawableActient;
    private ArrayList<CalendarDay> calendarDayHashSet;

    public OneDayDecorator(Context context) {
        drawable = context.getResources().getDrawable(R.drawable.calendar_bg);
        drawableActient = context.getResources().getDrawable(R.drawable.inset_travel_select);
        date = CalendarDay.today();
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return date != null && day.equals(date);
    }

    @Override
    public void decorate(DayViewFacade view) {
        if ( calendarDayHashSet != null && calendarDayHashSet.contains(date)) {
            view.setSelectionDrawable(drawableActient);
        } else {
            view.setSelectionDrawable(drawable);
        }

    }

    public void setCalendarDayHashSet(ArrayList<CalendarDay> calendarDayHashSet) {
        this.calendarDayHashSet = calendarDayHashSet;
    }

    public void setDate(Date date) {
        this.date = CalendarDay.from(date);
    }
}
