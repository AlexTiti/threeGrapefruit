package com.findtech.threePomelos.view.calendar;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.view.View;

import java.util.Calendar;
import java.util.Collection;

/**
 * Display a month of {@linkplain DayView}s and
 * seven {@linkplain WeekDayView}s.
 */
@SuppressLint("ViewConstructor")
class MonthView extends AbstractCalendarPagerView {

    private int margin = 0;
    public MonthView(@NonNull MaterialCalendarView view, CalendarDay month, int firstDayOfWeek) {
        super(view, month, firstDayOfWeek);
    }

    @Override
    protected void buildDayViews(Collection<DayView> dayViews, Calendar calendar) {
        for (int r = 0; r < DEFAULT_MAX_WEEKS; r++) {
            for (int i = 0; i < DEFAULT_DAYS_IN_WEEK; i++) {
                addDayView(dayViews, calendar);
            }
        }
    }

    public CalendarDay getMonth() {
        return getFirstViewDay();
    }

    @Override
    protected boolean isDayEnabled(CalendarDay day) {
        return day.getMonth() == getFirstViewDay().getMonth();
    }

    @Override
    protected int getRows() {
        return DEFAULT_MAX_WEEKS + DAY_NAMES_ROW;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final int specWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int specWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int specHeightSize = MeasureSpec.getSize(heightMeasureSpec);
        final int specHeightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (specHeightMode == MeasureSpec.UNSPECIFIED || specWidthMode == MeasureSpec.UNSPECIFIED) {
            throw new IllegalStateException("MonthView should never be left to decide it's size");
        }

        final int measureTileSize = specWidthSize / DEFAULT_DAYS_IN_WEEK;
        margin = (int) (measureTileSize * 0.25);
        setMeasuredDimension(specWidthSize, specHeightSize);

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                    (int) (measureTileSize * 0.75),
                    MeasureSpec.EXACTLY
            );
            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                    (int) (measureTileSize * 0.75),
                    MeasureSpec.EXACTLY
            );
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int count = getChildCount();

        final int parentLeft = margin /2;
        final int parentTop = 0;

        int childTop = parentTop;
        int childLeft = parentLeft;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);

            final int width = child.getMeasuredWidth();
            final int height = child.getMeasuredHeight();

            child.layout(childLeft, childTop, childLeft + width, childTop + height);
            childLeft = childLeft + width + margin;

            if (i % DEFAULT_DAYS_IN_WEEK == (DEFAULT_DAYS_IN_WEEK - 1)) {
                childLeft = parentLeft;
                childTop += height;
            }

        }
    }


}
