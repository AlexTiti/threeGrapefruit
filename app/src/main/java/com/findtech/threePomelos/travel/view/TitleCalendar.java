package com.findtech.threePomelos.travel.view;


import android.content.Context;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.view.calendar.CalendarDay;
import com.findtech.threePomelos.view.calendar.format.TitleFormatter;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/03/29
 */

public class TitleCalendar implements TitleFormatter {

    private Context context;

    public TitleCalendar(Context context) {
        this.context = context;
    }

    @Override
    public CharSequence format(CalendarDay day) {
        String year = String.valueOf(day.getYear());
        String month = String.valueOf(day.getMonth() + 1);
        String builder = year +
                " " +
                month +
                context.getString(R.string.month);
        return builder;
    }
}
