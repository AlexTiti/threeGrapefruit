package com.findtech.threePomelos.travel.view;


import android.content.Context;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.view.calendar.format.WeekDayFormatter;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/03/29
 */

public class WeekCalendar implements WeekDayFormatter {
    private Context context;

    public WeekCalendar(Context context) {
        this.context = context;
    }

    @Override
    public CharSequence format(int dayOfWeek) {

        String calendar = null;
        switch (dayOfWeek) {
            case 1:
                calendar = context.getString(R.string.monday);
                break;
            case 2:
                calendar = context.getString(R.string.tuesday);
                break;
            case 3:
                calendar = context.getString(R.string.wednesday);
                break;
            case 4:
                calendar = context.getString(R.string.thrsday);
                break;
            case 5:
                calendar =context.getString(R.string.friday);
                break;
            case 6:
                calendar = context.getString(R.string.saturday);
                break;
            case 7:
                calendar = context.getString(R.string.sunday);
                break;
            default:
                break;

        }
        return calendar;
    }
}
