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

import java.util.Date;

/**
 * Decorate a day by making the text big and bold
 * @author Administrator
 */
public class OneDayDecorator implements DayViewDecorator {

    private CalendarDay date;
    private Drawable drawable;
    public OneDayDecorator(Context context) {
        drawable = context.getResources().getDrawable(R.drawable.calendar_bg);
        date = CalendarDay.today();
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return date != null && day.equals(date);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setSelectionDrawable(drawable);
    }

    public void setDate(Date date) {
        this.date = CalendarDay.from(date);
    }
}
