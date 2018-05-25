package com.findtech.threePomelos.travel.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.sdk.base.mvp.BaseActivityMvp;
import com.findtech.threePomelos.travel.present.CalendarPresent;
import com.findtech.threePomelos.travel.view.CalendarViewMvp;
import com.findtech.threePomelos.travel.view.DayViewCalendar;
import com.findtech.threePomelos.travel.view.OneDayDecorator;
import com.findtech.threePomelos.travel.view.WeekCalendar;
import com.findtech.threePomelos.view.calendar.CalendarBtnListener;
import com.findtech.threePomelos.view.calendar.CalendarDay;
import com.findtech.threePomelos.view.calendar.MaterialCalendarView;
import com.findtech.threePomelos.view.calendar.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;

import butterknife.BindView;


/**
 * @author Alex
 */
public class CalendarActivity extends BaseActivityMvp<CalendarActivity, CalendarPresent> implements
        OnDateSelectedListener, CalendarViewMvp<ArrayList<CalendarDay>>, View.OnClickListener, CalendarBtnListener {

    @BindView(R.id.cv_travel)
    MaterialCalendarView materialCalendarView;
    @BindView(R.id.iv_date)
    ImageView iv_date;
    @BindView(R.id.rightClick)
    ImageView rightClick;
    @BindView(R.id.include)
    View include;

    private OneDayDecorator dayDecorator;
    private ArrayList<CalendarDay> dayArrayList;


    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

        dayDecorator.setDate(date.getDate());
        materialCalendarView.addDecorator(dayDecorator);
        if (dayArrayList != null) {
            materialCalendarView.setDatesListSelected(dayArrayList);
        }
        materialCalendarView.invalidateDecorators();
        if (date.equals(CalendarDay.today())) {
            iv_date.setImageDrawable(getResources().getDrawable(R.drawable.img_doing));
        } else if (dayArrayList.contains(date)) {
            iv_date.setImageDrawable(getResources().getDrawable(R.drawable.img_done));
        }else{
            iv_date.setImageDrawable(getResources().getDrawable(R.drawable.img_undo));
        }

        if (date.getDate().getTime() <= System.currentTimeMillis()) {
            present.loadTravelAnalysis(date);
        }else {
            iv_date.setImageDrawable(getResources().getDrawable(R.drawable.img_undo));
        }
    }

    @Override
    protected void showLoading() {

    }

    @Override
    protected void onErrorViewClick(View v) {

    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        dayDecorator = new OneDayDecorator(mContext);
        materialCalendarView.setWeekDayFormatter(new WeekCalendar(this));
        rightClick.setOnClickListener(this);
        materialCalendarView.setBtnListener(this);
        Calendar instance = Calendar.getInstance();
        materialCalendarView.setSelectedDate(instance.getTime());
        materialCalendarView.addDecorator(dayDecorator);
        CalendarDay day = materialCalendarView.getCurrentDate();
        initToolBar(include, getString(R.string.current_tv, day.getYear(), day.getMonth() + 1));
        materialCalendarView.setOnDateChangedListener(this);
        iv_date.setImageDrawable(getResources().getDrawable(R.drawable.img_doing));

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_calendar;
    }

    @Override
    public void loadSuccess(ArrayList<CalendarDay> calendarDays) {
        materialCalendarView.addDecorator(new DayViewCalendar(CalendarActivity.this, calendarDays));
        dayArrayList = calendarDays;
        materialCalendarView.setDatesListSelected(calendarDays);
    }

    @Override
    public void loadFailed(String s) {

    }

    @Override
    public void initDataFromServer() {
        present.getDatesPresent(CalendarDay.today());
    }

    @Override
    protected CalendarPresent createPresent() {
        return new CalendarPresent(this);
    }


    public void rightArrowClick() {
        Calendar instance = Calendar.getInstance();
        materialCalendarView.setDataSelectReturn(CalendarDay.from(instance));
        materialCalendarView.setCurrentDate(instance);
        dayDecorator.setDate(instance.getTime());
        materialCalendarView.addDecorator(dayDecorator);
        if (dayArrayList != null) {
            materialCalendarView.setDatesListSelected(dayArrayList);
        }
        iv_date.setImageDrawable(getResources().getDrawable(R.drawable.img_doing));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rightClick:
                rightArrowClick();
                break;
            default:
                break;
        }
    }

    @Override
    public void selectMonth(CalendarDay calendarDay) {
        initToolBar(include, getString(R.string.current_tv, calendarDay.getYear(), calendarDay.getMonth() + 1));
        if (present != null) {
            present.getDatesPresent(calendarDay);
        }
    }

    @Override
    public void loadTravelAnalysisSuccess(String s) {

    }

    @Override
    public void loadTravelAnalysisFailed(String message) {

    }


}
