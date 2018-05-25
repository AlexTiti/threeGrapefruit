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
import com.findtech.threePomelos.travel.bean.KilometerBean;
import com.findtech.threePomelos.travel.bean.KilometerData;
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
 * @author :   Alex
 * @version :   V 1.0.9
 * @e_mail :   18238818283@sina.cn
 * @time :   2018/03/20
 * @desc :
 */

public class KilometerModelImpl implements Contract.ModeMvp {

   private Context mContext;
    private final String TABLE_NAME = "TravelDataWithDay";
    private final String TABLE_NAME_ONCE = "TravelInfoOnce";

    public KilometerModelImpl(Context mContext) {
        this.mContext = mContext;
    }

    public Observable<HashMap<String,Float>> getKiloMeterList() {

        final String userId = AVUser.getCurrentUser().getObjectId();
        final String[] conlomns = {OperateDBUtils.MILEAGE, OperateDBUtils.DATE};
        final String selection = OperateDBUtils.USER_ID + "=?";

        Observable<HashMap<String,Float>> observableDataBase =   Observable.create(new ObservableOnSubscribe<Cursor>() {
            @Override
            public void subscribe(ObservableEmitter<Cursor> e) throws Exception {
                Cursor cursor = mContext.getContentResolver().query(OperateDBUtils.TABLE_TRAVEL_THREE_URI, conlomns, selection, new String[]{userId}, null);
                e.onNext(cursor);
            }
        }).filter(new Predicate<Cursor>() {
            @Override
            public boolean test(Cursor cursor) throws Exception {
                return cursor != null;
            }
        }).flatMap(new Function<Cursor, ObservableSource<HashMap<String,Float>>>() {
            @Override
            public ObservableSource<HashMap<String,Float>> apply(Cursor cursor) throws Exception {
                HashMap<String,Float> hashMap = new HashMap<>(cursor.getCount());
                {
                    while (cursor.moveToNext()) {
                        String date = cursor.getString(cursor.getColumnIndex(OperateDBUtils.DATE));
                        float mileage = Float.valueOf(cursor.getString(cursor.getColumnIndex(OperateDBUtils.MILEAGE)));
                        hashMap.put(date,mileage);
                    }
                }
                return Observable.just(hashMap);
            }
        });

        final Observable<HashMap<String,Float>> observableNet = Observable.create(new ObservableOnSubscribe<HashMap<String, Float>>() {
            @Override
            public void subscribe(final ObservableEmitter<HashMap<String, Float>> e) throws Exception {
                final AVQuery<AVObject> query = NetWorkRequest.getBaseQuery(NetWorkRequest.TABLE_NAME);
                query.whereEqualTo("userId", AVUser.getCurrentUser());
                query.orderByAscending("travelDate");
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException exception) {
                        if (exception == null ) {
                            OperateDBUtils operateDBUtils = new OperateDBUtils(mContext);
                            String dateString;
                            String kilometer;
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
                            Date date;
                            HashMap<String,Float> hashMap = new HashMap<>(list.size());
                            for (AVObject object : list) {
                                date = object.getDate("travelDate");
                                dateString = dateFormat.format(date.getTime());
                                kilometer = object.getNumber("mileage").toString();
                                operateDBUtils.insertTravelToDB(dateString, kilometer);
                                hashMap.put(dateString,Float.valueOf(kilometer));
                            }
                            e.onNext(hashMap);
                        }else {
                            e.onError(exception);
                        }
                    }
                });
            }
        });

       return observableDataBase.flatMap(new Function<HashMap<String, Float>, ObservableSource<HashMap<String, Float>>>() {
            @Override
            public ObservableSource<HashMap<String, Float>> apply(HashMap<String, Float> stringFloatHashMap) throws Exception {
                if (stringFloatHashMap.size() == 0){
                    return observableNet;
                }else {
                  return   Observable.just(stringFloatHashMap);
                }
            }
        });

    }

    public Observable<ArrayList<KilometerBean>> getTravelBeans(final Date date) {

        return Observable.create(new ObservableOnSubscribe<ArrayList<KilometerBean>>() {
            @Override
            public void subscribe(final ObservableEmitter<ArrayList<KilometerBean>> e) throws Exception {
                AVQuery<AVObject> query = AVQuery.getQuery(TABLE_NAME_ONCE);

                query.whereEqualTo("userId", AVUser.getCurrentUser());
                query.whereEqualTo("travelDate",date);
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException exception) {
                        if (exception == null) {
                            ArrayList<KilometerBean> beans = new ArrayList<>();
                            for (AVObject object : list) {
                                beans.add(new KilometerBean(object.getString("startTime"),
                                        object.getString("endTime"),
                                        object.getNumber("mileage").intValue(),
                                        object.getNumber("calories").intValue()));
                            }
                            e.onNext(beans);
                        } else {
                            e.onError(exception);
                        }
                    }
                });
            }
        });
    }
}
