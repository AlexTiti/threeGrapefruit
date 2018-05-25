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
import com.findtech.threePomelos.travel.bean.FrequencyData;
import com.findtech.threePomelos.travel.bean.KilometerData;
import com.findtech.threePomelos.travel.iterator.FrequencyStore;
import com.findtech.threePomelos.utils.MyCalendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
 * @date : 2018/03/24
 */

public class FrequencyModelImpl implements Contract.ModeMvp {


    private Context mContext;

    public FrequencyModelImpl(Context mContext) {
        this.mContext = mContext;
    }

    public Observable<HashMap<String, Integer>> getFrequencyWeek() {

        final String userId = AVUser.getCurrentUser().getObjectId();
        final String[] conlomns = {OperateDBUtils.FREQUENCY, OperateDBUtils.DATE};
        final String selection = OperateDBUtils.USER_ID + "=?";

        Observable<HashMap<String, Integer>> observableDataBase = Observable.create(new ObservableOnSubscribe<Cursor>() {
            @Override
            public void subscribe(ObservableEmitter<Cursor> e) throws Exception {
                Cursor cursor = mContext.getContentResolver().query(OperateDBUtils.TABLE_FREQUENCY_WEEK_URI, conlomns, selection, new String[]{userId}, null);
                e.onNext(cursor);
            }
        }).filter(new Predicate<Cursor>() {
            @Override
            public boolean test(Cursor cursor) throws Exception {
                return cursor != null;
            }
        }).flatMap(new Function<Cursor, ObservableSource<HashMap<String, Integer>>>() {
            @Override
            public ObservableSource<HashMap<String, Integer>> apply(Cursor cursor) throws Exception {
                HashMap<String, Integer> frequencyStore = new HashMap<>(cursor.getCount());
                while (cursor.moveToNext()) {
                    String date = cursor.getString(cursor.getColumnIndex(OperateDBUtils.DATE));
                    int frequency = cursor.getInt(cursor.getColumnIndex(OperateDBUtils.FREQUENCY));
                    frequencyStore.put(date, frequency);
                }
                return Observable.just(frequencyStore);
            }
        });

        final Observable<HashMap<String, Integer>> observableNet = Observable.create(new ObservableOnSubscribe<HashMap<String, Integer>>() {
            @Override
            public void subscribe(final ObservableEmitter<HashMap<String, Integer>> e) throws Exception {
                AVQuery<AVObject> query = NetWorkRequest.getBaseQuery(NetWorkRequest.TABLE_NAME_WEEK);
                query.whereEqualTo("userId", AVUser.getCurrentUser());
                query.orderByAscending("travelDate");
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException exception) {
                        if (exception == null) {
                            OperateDBUtils operateDBUtils = new OperateDBUtils(mContext);
                            String dateString;
                            int frequency;
                            HashMap<String, Integer> frequencyStore = new HashMap<>(list.size());
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
                            Date date;
                            for (AVObject object : list) {
                                date = object.getDate("travelDate");
                                dateString = dateFormat.format(date.getTime());
                                frequency = object.getNumber("frequency").intValue();
                                operateDBUtils.insertFrequencyToDB(OperateDBUtils.TABLE_FREQUENCY_WEEK_URI, dateString, frequency);
                                frequencyStore.put(dateString, frequency);
                            }
                            e.onNext(frequencyStore);
                        } else {
                            e.onError(exception);
                        }
                    }
                });
            }
        });

        return observableDataBase.flatMap(new Function<HashMap<String, Integer>, ObservableSource<HashMap<String, Integer>>>() {
            @Override
            public ObservableSource<HashMap<String, Integer>> apply(HashMap<String, Integer> stringIntegerHashMap) throws Exception {
                if (stringIntegerHashMap.size() == 0) {
                    return observableNet;
                } else {
                    return Observable.just(stringIntegerHashMap);
                }

            }
        });

    }

    public Observable<HashMap<String, Integer>> getFrequencyMonth() {

        final String userId = AVUser.getCurrentUser().getObjectId();
        final String[] conlomns = {OperateDBUtils.FREQUENCY, OperateDBUtils.DATE};
        final String selection = OperateDBUtils.USER_ID + "=?";

        Observable<HashMap<String, Integer>> observableDataBase = Observable.create(new ObservableOnSubscribe<Cursor>() {
            @Override
            public void subscribe(ObservableEmitter<Cursor> e) throws Exception {
                Cursor cursor = mContext.getContentResolver().query(OperateDBUtils.TABLE_FREQUENCY_MONTH_URI, conlomns, selection, new String[]{userId}, null);
                e.onNext(cursor);
            }
        }).filter(new Predicate<Cursor>() {
            @Override
            public boolean test(Cursor cursor) throws Exception {
                return cursor != null;
            }
        }).flatMap(new Function<Cursor, ObservableSource<HashMap<String, Integer>>>() {
            @Override
            public ObservableSource<HashMap<String, Integer>> apply(Cursor cursor) throws Exception {
                HashMap<String, Integer> frequencyStore = new HashMap<>(cursor.getCount());

                while (cursor.moveToNext()) {
                    String date = cursor.getString(cursor.getColumnIndex(OperateDBUtils.DATE));
                    int frequency = cursor.getInt(cursor.getColumnIndex(OperateDBUtils.FREQUENCY));
                    frequencyStore.put(date, frequency);
                }
                return Observable.just(frequencyStore);
            }
        });

        final Observable<HashMap<String, Integer>> observableNet = Observable.create(new ObservableOnSubscribe<HashMap<String, Integer>>() {
            @Override
            public void subscribe(final ObservableEmitter<HashMap<String, Integer>> e) throws Exception {
                AVQuery<AVObject> query = NetWorkRequest.getBaseQuery(NetWorkRequest.TABLE_NAME_MONTH);
                query.whereEqualTo("userId", AVUser.getCurrentUser());
                query.orderByAscending("travelDate");
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException exception) {
                        if (exception == null) {
                            OperateDBUtils operateDBUtils = new OperateDBUtils(mContext);
                            HashMap<String, Integer> frequencyStore = new HashMap<>(list.size());
                            String dateString;
                            int frequency;
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
                            Date date;
                            for (AVObject object : list) {
                                date = object.getDate("travelDate");
                                dateString = dateFormat.format(date.getTime());
                                frequency = object.getNumber("frequency").intValue();
                                operateDBUtils.insertFrequencyToDB(OperateDBUtils.TABLE_FREQUENCY_MONTH_URI, dateString, frequency);
                                frequencyStore.put(dateString,frequency);
                            }
                            e.onNext(frequencyStore);
                        }else {
                            e.onError(exception);
                        }
                    }
                });
            }
        });
        return observableDataBase.flatMap(new Function<HashMap<String, Integer>, ObservableSource<HashMap<String, Integer>>>() {
            @Override
            public ObservableSource<HashMap<String, Integer>> apply(HashMap<String, Integer> stringIntegerHashMap) throws Exception {
                if (stringIntegerHashMap.size() == 0){
                    return observableNet;
                }else {
                    return Observable.just(stringIntegerHashMap);
                }

            }
        });

    }
}
