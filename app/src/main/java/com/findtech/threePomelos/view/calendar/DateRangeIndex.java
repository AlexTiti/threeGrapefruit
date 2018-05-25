package com.findtech.threePomelos.view.calendar;

/**
 * Use math to calculate first days of months by postion from a minium date
 */
interface DateRangeIndex {

    /**
     * 返回数量
     * @return
     */
    int getCount();

    /**
     * 获取下表
     * @param day
     * @return
     */
    int indexOf(CalendarDay day);

    /**
     * 获取CalendarDay
     * @param position
     * @return
     */
    CalendarDay getItem(int position);
}
