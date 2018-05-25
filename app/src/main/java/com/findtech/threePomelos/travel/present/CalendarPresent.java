package com.findtech.threePomelos.travel.present;

import android.content.Context;

import com.findtech.threePomelos.sdk.base.mvp.BasePresenterMvp;
import com.findtech.threePomelos.sdk.manger.RxHelper;
import com.findtech.threePomelos.travel.activity.CalendarActivity;
import com.findtech.threePomelos.travel.model.CalendarModelImpl;
import com.findtech.threePomelos.view.calendar.CalendarDay;


import java.util.ArrayList;
import java.util.HashSet;

import io.reactivex.functions.Consumer;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/03/29
 */

public class CalendarPresent extends BasePresenterMvp<CalendarActivity, CalendarModelImpl> {
    private Context context;

    public CalendarPresent(Context context) {
        this.context = context;
    }

    @Override
    public CalendarModelImpl createModel() {
        return new CalendarModelImpl(context);
    }

    public void getDatesPresent(CalendarDay calendarDay) {

        if (model == null || mView == null) {
            return;
        }
        mRxManager.register(model.getDates(calendarDay).compose(
                RxHelper.<ArrayList<CalendarDay>>rxSchedulerHelper())
                .subscribe(new Consumer<ArrayList<CalendarDay>>() {
                    @Override
                    public void accept(ArrayList<CalendarDay> calendarDays) throws Exception {
                        mView.loadSuccess(calendarDays);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.loadFailed(throwable.getMessage());
                    }
                }));
    }

    public void loadTravelAnalysis(CalendarDay calendarDay){
        if (model == null || mView == null) {
            return;
        }
        mRxManager.register(model.loadTravelAnalysis(calendarDay)
        .compose(RxHelper.<String>rxSchedulerHelper())
        .subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                mView.loadTravelAnalysisSuccess(s);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                mView.loadTravelAnalysisFailed(throwable.getMessage());
            }
        }));
    }
}
