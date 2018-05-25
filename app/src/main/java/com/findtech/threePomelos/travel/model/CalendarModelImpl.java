package com.findtech.threePomelos.travel.model;

import android.content.Context;
import android.database.Cursor;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.findtech.threePomelos.database.OperateDBUtils;
import com.findtech.threePomelos.net.NetWorkRequest;
import com.findtech.threePomelos.sdk.base.mvp.Contract;
import com.findtech.threePomelos.utils.MyCalendar;
import com.findtech.threePomelos.view.calendar.CalendarDay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;


/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/03/29
 */

public class CalendarModelImpl implements Contract.ModeMvp {

    private Context context;
    final String userId = AVUser.getCurrentUser().getObjectId();
    final String[] conlomns = {OperateDBUtils.DATE};
    final String selection = OperateDBUtils.USER_ID + "=?" + " and " + OperateDBUtils.DATE + ">=?" + " and " + OperateDBUtils.DATE + "<=?";

    String date;
    String dateMax;

    public CalendarModelImpl(Context context) {
        this.context = context;
    }

    public Observable<ArrayList<CalendarDay>> getDates(CalendarDay calendarDay) {

        Calendar calendar = calendarDay.getCalendar();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.DATE, -day);
        date = MyCalendar.getStringDate(calendar);
        calendar.add(Calendar.MONTH, 1);
        dateMax = MyCalendar.getStringDate(calendar);

        return Observable.create(new ObservableOnSubscribe<Cursor>() {
            @Override
            public void subscribe(ObservableEmitter<Cursor> e) throws Exception {
                Cursor cursor = context.getContentResolver().query(OperateDBUtils.TABLE_TRAVEL_THREE_URI,
                        conlomns, selection, new String[]{userId, date, dateMax}, null);
                e.onNext(cursor);
            }
        }).filter(new Predicate<Cursor>() {
            @Override
            public boolean test(Cursor cursor) throws Exception {
                return cursor != null;
            }
        }).flatMap(new Function<Cursor, ObservableSource<ArrayList<CalendarDay>>>() {
            @Override
            public ObservableSource<ArrayList<CalendarDay>> apply(Cursor cursor) throws Exception {
                ArrayList<CalendarDay> calendarDays = new ArrayList<>();
                while (cursor.moveToNext()) {
                    String date = cursor.getString(cursor.getColumnIndex(OperateDBUtils.DATE));
                    Date d = MyCalendar.getToday(date + " 00:00:00");
                    calendarDays.add(CalendarDay.from(d));
                }
                return Observable.just(calendarDays);
            }
        });
    }

    public  Observable<String>  loadTravelAnalysis(final CalendarDay day){
       final CalendarDay calendarDayNew = CalendarDay.from(day.getYear(),day.getMonth(),day.getDay());

        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> e) throws Exception {

                AVQuery<AVObject> query = NetWorkRequest.getBaseQuery("TravelDataWithDay");
                query.whereEqualTo("userId", AVUser.getCurrentUser());
                query.whereEqualTo("travelDate",calendarDayNew.getDate());
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException exception) {
                        if (exception == null) {
                            if (list.size() > 0) {
                                e.onNext(calendarDayNew.toString());
                            }else {
                                e.onNext("No Data!");
                            }
                        }else {
                            e.onError(exception);
                        }
                    }
                });
            }
        });
    }
}
