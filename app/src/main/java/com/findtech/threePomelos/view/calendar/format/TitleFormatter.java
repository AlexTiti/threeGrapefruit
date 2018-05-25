package com.findtech.threePomelos.view.calendar.format;


import com.findtech.threePomelos.view.calendar.CalendarDay;

/**
 * Used to format a to a string for the month/year title
 * @author Administrator
 */
public interface TitleFormatter {

    /**
     * Converts the supplied day to a suitable month/year title
     *
     * @param day the day containing relevant month and year information
     * @return a label to display for the given month/year
     */
    CharSequence format(CalendarDay day);
}
