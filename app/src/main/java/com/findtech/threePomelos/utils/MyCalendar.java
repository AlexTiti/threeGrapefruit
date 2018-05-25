package com.findtech.threePomelos.utils;

import android.content.Context;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.music.utils.L;
import com.findtech.threePomelos.view.calendar.CalendarDay;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Administrator
 */
public class MyCalendar {
    private Calendar dateBegin;
    private Calendar dateEnd;
    private DateFormat df;

    private String day;
    private String month;
    private String year;


    public MyCalendar(String begin, String end, Context context) throws ParseException {

        df = new SimpleDateFormat("yyyy-MM-dd");
        dateBegin = Calendar.getInstance();
        dateEnd = Calendar.getInstance();
        dateBegin.setTime(df.parse(begin));
        dateEnd.setTime(df.parse(end));

        day = context.getResources().getString(R.string.label_date_d);
        month = context.getResources().getString(R.string.label_date__month);
        year = context.getResources().getString(R.string.label_date_year);
    }

    /**
     * 当前日比较
     *
     * @return
     */
    private boolean compareTo() {
        return dateBegin.get(Calendar.DAY_OF_MONTH) > dateEnd.get(Calendar.DAY_OF_MONTH);
    }

    private int CalculatorYear() {
        int year1 = dateBegin.get(Calendar.YEAR);
        int year2 = dateEnd.get(Calendar.YEAR);
        int month1 = dateBegin.get(Calendar.MONTH);
        int month2 = dateEnd.get(Calendar.MONTH);
        int year = year2 - year1;
        if (compareTo())
        {
            month2 -= 1;
        }
        if (month1 > month2) {
            year -= 1;
        }
        return year;
    }

    private int CalculatorMonth() {

        int month1 = dateBegin.get(Calendar.MONTH);
        int month2 = dateEnd.get(Calendar.MONTH);
        int month = 0;
        if (compareTo())
        {
            month2 -= 1;
        }
        if (month2 >= month1) {
            month = month2 - month1;
        } else if (month2 < month1)
        {
            month = 12 + month2 - month1;
        }
        return month;

    }

    private int CalculatorDay() {

        int day11 = dateBegin.get(Calendar.DAY_OF_MONTH);
        int day21 = dateEnd.get(Calendar.DAY_OF_MONTH);
        if (day21 >= day11) {
            return day21 - day11;
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateEnd.getTime());
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.add(Calendar.DAY_OF_MONTH, -1);
            return cal.getActualMaximum(Calendar.DATE) + day21 - day11;
        }
    }

    /**
     * 返回两个时间相隔的多少年
     *
     * @return
     */
    public int getYear() {
        return CalculatorYear() > 0 ? CalculatorYear() : 0;
    }

    /**
     * 返回除去整数年后的月数
     *
     * @return
     */
    public int getMonth() {
        int month = CalculatorMonth() % 12;
        return (month > 0 ? month : 0);
    }

    /**
     * 返回除去整月后的天数
     *
     * @return
     */
    private int getDay() {
        int day = CalculatorDay();
        return day;
    }

    /**
     * 返回现个日期间相差的年月天以@分隔
     *
     * @return
     */
    public String getDate() {
        String date;
        if (getYear() == 0) {
            date = getMonth() + month + getDay() + day;
            if (getMonth() == 0) {
                date = getDay() + day;
            }
        } else {
            date = getYear() + year + getMonth() + month + getDay() + day;
        }
        return date;
    }

    /**
     * 得到当天的日期
     *
     * @return
     */
    public static String getToday() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        return sdf.format(calendar.getTime());
    }

    public static Date getToday(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 获取当前月份
     * @return
     */
    public static int getCurrentMonth() {

        Calendar calendar = Calendar.getInstance();
        CalendarDay day = CalendarDay.from(calendar);
        return day.getMonth() + 1;
    }

    /**
     * 获取年份
     * @return
     */
    public static int getCurrentYear() {

        Calendar calendar = Calendar.getInstance();
        CalendarDay day = CalendarDay.from(calendar);
        return day.getYear();
    }

    /**
     * 获取周一
     * @return
     */
    public static Calendar getMondayCalendar() {
        Calendar calendar = Calendar.getInstance();
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        if (week == 1) {
            calendar.add(Calendar.DATE, -6);
        } else {
            calendar.add(Calendar.DATE, 2 - week);
        }
        return calendar;
    }

    /**
     * 获取周一日期
     * @return
     */
    public static String getMonday() {
        Calendar calendar = getMondayCalendar();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        return dateFormat.format(calendar.getTime());
    }

    public static String getStringDate(Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        return dateFormat.format(calendar.getTime());
    }

    /**
     * 获取一周第一天
     * @return
     */
    public static Calendar getMonthCalendar() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.DAY_OF_MONTH, 1 - month);
        return calendar;
    }

    /**
     * 获取一周第一天
     * @return
     */
    public static String getFirstOfMonth() {
        Calendar calendar = getMonthCalendar();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        return dateFormat.format(calendar.getTime());
    }

}
