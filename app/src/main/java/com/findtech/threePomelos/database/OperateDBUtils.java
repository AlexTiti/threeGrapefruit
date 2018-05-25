package com.findtech.threePomelos.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetDataCallback;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.entity.BabyInfoEntity;
import com.findtech.threePomelos.entity.PersonDataEntity;
import com.findtech.threePomelos.entity.TravelInfoEntity;
import com.findtech.threePomelos.sdk.MyApplication;
import com.findtech.threePomelos.user.bean.UserInfo;
import com.findtech.threePomelos.utils.PicOperator;
import com.findtech.threePomelos.utils.RequestUtils;
import com.findtech.threePomelos.utils.SpUtils;
import com.findtech.threePomelos.utils.Tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 *
 * @author zhi.zhang
 * @date 1/3/16
 */
public class OperateDBUtils {

    private Context mContext;
    private final Object object = new Object();
    private final Object queryTraveLock = new Object();

    public static final String DATABASE_NAME = "threePomelos.db";
    public static final int DATABASE_VERSION = 3;
    public static final String TABLE_BABY_INFO = "babyInfo";
    public static final String TABLE_WEIGHT = "weight";
    public static final String TABLE_HEIGHT = "height";
    public static final String TABLE_TRAVEL_INFO = "travelInfo";
    public static final String ID = "_id";
    public static final String AUTOHORITY = "com.findtech.threePomelos.database.MyContentProvider";

    public static final String TABLE_TRAVEL_THREE = "travel";
    public static final String TABLE_FREQUENCY_WEEK = "frequencyWeek";
    public static final String TABLE_FREQUENCY_MONTH = "frequencyMonth";

    public static Uri TABLE_TRAVEL_THREE_URI = Uri.parse("content://" + AUTOHORITY + "/" + TABLE_TRAVEL_THREE);
    public static Uri TABLE_FREQUENCY_WEEK_URI = Uri.parse("content://" + AUTOHORITY + "/" + TABLE_FREQUENCY_WEEK);
    public static Uri TABLE_FREQUENCY_MONTH_URI = Uri.parse("content://" + AUTOHORITY + "/" + TABLE_FREQUENCY_MONTH);

    public static Uri BABYINFO_URI = Uri.parse("content://" + AUTOHORITY + "/" + TABLE_BABY_INFO);
    public static Uri HEIGHT_URI = Uri.parse("content://" + AUTOHORITY + "/" + TABLE_HEIGHT);
    public static Uri WEIGHT_URI = Uri.parse("content://" + AUTOHORITY + "/" + TABLE_WEIGHT);
    public static Uri TABLE_TRAVEL_URI = Uri.parse("content://" + AUTOHORITY + "/" + TABLE_TRAVEL_INFO);





    public static String BABYNAME = "name";
    public static String BABYSEX = "sex";
    public static String BIRTHDAY = "birthday";
    public static String HEADIMG = "header";
    public static String IS_BIND = "bluetoothBind";
    public static String BLUETOOTH_DEVICE_ID = "bluetoothDeviceId";
    public static String BABY_INFO_OBJECT_ID = "babyInfoObjectId";

    public static String BABYNATIVE = "native";
    public static String USER_ID = "uid";
    public static String NICKNAME = "nickName";
    public static String TIME = "time";
    public static String DATE = "date";
    public static String HEIGHT = "height";
    public static String WEIGHT = "weight";

    public static String TOTAL_MILEAGE = "totalMileage";
    public static String TOTAL_CALOR = "totalCalor";
    public static String TODAY_CALOR = "todayCalor";
    public static String ADULT_WEIGHT = "adult_weight";
    public static String TODAY_MILEAGE = "todayMileage";
    public static String AVERAGE_SPEED = "averageSpeed";

    public static String MILEAGE = "mileage";
    public static String FREQUENCY = "frequency";
    /**
     * 查询结束标志
     */
    public static final String QUERY_FINISH = "com.findtech.threePomelos.database.query.finish";
    /**
     * 查询标志
     */
    public static final String QUERY_DATA = "query_data";

    /**
     * 保存查询到的身高信息
     */
    private ArrayList<PersonDataEntity> timeHeightDataArray = new ArrayList<>();
    /**
     * 保存查询到的体重信息
     */
    private ArrayList<PersonDataEntity> timeWeightDataArray = new ArrayList<>();
    private SaveBabyInfoFinishListener mSaveBabyInfoFinishListener;

    public void setSaveBabyInfoFinishListener(SaveBabyInfoFinishListener saveBabyInfoFinishListener) {
        mSaveBabyInfoFinishListener = saveBabyInfoFinishListener;
    }

    public OperateDBUtils(Context context) {
        mContext = context;
    }

    public void queryUserHeightData() {

        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (object) {
                    Cursor cursor = null;
                    try {
                        String uid = AVUser.getCurrentUser().getObjectId();
                        String[] columns = new String[]{TIME, USER_ID, HEIGHT};
                        String selection = USER_ID + "=?";
                        cursor = mContext.getContentResolver().query(HEIGHT_URI, columns, selection,
                                new String[]{uid}, null);
                        timeHeightDataArray.clear();
                        Tools.SortArrayList sort = new Tools.SortArrayList();
                        if (cursor != null && cursor.getCount() > 0) {
                            while (cursor.moveToNext()) {
                                String timePoint = cursor.getString(cursor.getColumnIndex(TIME));
                                String heightNum = cursor.getString(cursor.getColumnIndex(HEIGHT));
                                timePoint = Tools.getYearFromDataPicker(timePoint) + "-" +
                                        Tools.getMonthFromDataPicker(timePoint) + "-" +
                                        Tools.getDayFromDataPicker(timePoint);
                                PersonDataEntity personDataEntity = new PersonDataEntity();
                                personDataEntity.setTime(timePoint);
                                personDataEntity.setHeight(heightNum);
                                timeHeightDataArray.add(personDataEntity);
                            }
                            //按时间排序
                            Collections.sort(timeHeightDataArray, sort);
                        }
                        if (timeHeightDataArray.size() > 0) {
                            //保存最新的身高
                            RequestUtils.getSharepreferenceEditor(mContext).putString(RequestUtils.HEIGHT,
                                    timeHeightDataArray.get(timeHeightDataArray.size() - 1).getHeight()).commit();
                        } else {
                            RequestUtils.getSharepreferenceEditor(mContext).putString(RequestUtils.HEIGHT,
                                    "0").commit();
                        }
                        //保存到MyApplication的静态对象中
                        MyApplication.getInstance().setUserHeightData(timeHeightDataArray);
                        sendBroadcast(TABLE_HEIGHT);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                }
            }
        });
    }

    /**
     * 保存身高信息
     * @param height
     * @param time
     */
    public void saveHeightToDB(String height, String time) {
        String[] columns = new String[]{OperateDBUtils.TIME, OperateDBUtils.HEIGHT};
        Uri uri = OperateDBUtils.HEIGHT_URI;
        String selection = OperateDBUtils.TIME + " = ? " + " AND " + OperateDBUtils.USER_ID + " = ?";
        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(uri, columns, selection,
                    new String[]{time, AVUser.getCurrentUser().getObjectId()}, null);
            if (cursor != null && cursor.moveToFirst()) {
                updateHeight(height, time);
            } else {
                insertHeight(height, time);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * 增加身高信息
     * @param height
     * @param time
     */
    public void insertHeight(String height, String time) {
        ContentValues values = new ContentValues();
        values.put(OperateDBUtils.TIME, time);
        values.put(OperateDBUtils.HEIGHT, height);
        values.put(OperateDBUtils.USER_ID, AVUser.getCurrentUser().getObjectId());
        mContext.getContentResolver().insert(OperateDBUtils.HEIGHT_URI, values);
    }

    /**
     * 更新数据库身高信息
     * @param height
     * @param time
     */
    public void updateHeight(String height, String time) {
        String where = OperateDBUtils.TIME + " = ? " + " AND " +OperateDBUtils.USER_ID + " = ?";
        ContentValues values = new ContentValues();
        values.put(OperateDBUtils.TIME, time);
        values.put(OperateDBUtils.HEIGHT, height);
        values.put(OperateDBUtils.USER_ID, AVUser.getCurrentUser().getObjectId());
        mContext.getContentResolver().update(OperateDBUtils.HEIGHT_URI, values, where,
                new String[]{time, AVUser.getCurrentUser().getObjectId()});
    }

    /**
     * 查询运动数据
     */
    public Observable<TravelInfoEntity> queryTravelInfoDataFromDB(final String selectAddress) {
        return Observable.create(new ObservableOnSubscribe<TravelInfoEntity>() {
            @Override
            public void subscribe(ObservableEmitter<TravelInfoEntity> e) throws Exception {
                Cursor cursor;
                String uid = AVUser.getCurrentUser().getObjectId();
                String[] columns = new String[]{TODAY_MILEAGE, TOTAL_MILEAGE, TODAY_CALOR, TOTAL_CALOR, ADULT_WEIGHT};
                String selection = OperateDBUtils.TIME + " = ? " + " AND " + USER_ID + " = ? " + " AND " + BLUETOOTH_DEVICE_ID + " = ? ";
                String time = Tools.getCurrentTime();
                cursor = mContext.getContentResolver().query(TABLE_TRAVEL_URI, columns, selection,
                        new String[]{time, uid,selectAddress}, null);
                TravelInfoEntity travelInfoEntity = TravelInfoEntity.getInstance();
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        String todayMileage = cursor.getString(cursor.getColumnIndex(TODAY_MILEAGE));
                        String totalMileage = cursor.getString(cursor.getColumnIndex(TOTAL_MILEAGE));
                        String totalCalories = cursor.getString(cursor.getColumnIndex(TOTAL_CALOR));
                        String todayCalories = cursor.getString(cursor.getColumnIndex(TODAY_CALOR));
                        String weight = cursor.getString(cursor.getColumnIndex(ADULT_WEIGHT));

                        travelInfoEntity.setTodayMileage(todayMileage);
                        travelInfoEntity.setTotalMileage(totalMileage);
                        travelInfoEntity.setTodayCalor(todayCalories);
                        travelInfoEntity.setTotalCalor(totalCalories);
                        travelInfoEntity.setAdultWeight(weight);
                    }
                }
                if ("0.0".equals(travelInfoEntity.getTodayMileage()) || "0.0".equals(travelInfoEntity.getTodayMileage()) ||
                        "0.0".equals(travelInfoEntity.getTodayCalor()) || "0.0".equals(travelInfoEntity.getTotalCalor()) ||
                        "0.0".equals(travelInfoEntity.getAdultWeight())) {
                    e.onComplete();
                } else {
                    e.onNext(travelInfoEntity);
                }

            }
        });
    }

    /**
     * 保存运动信息
     * @param travelInfoEntity
     * @param time
     */
    public void saveTravelInfoToDB(TravelInfoEntity travelInfoEntity, String time) {
        String[] columns = new String[]{OperateDBUtils.TIME, OperateDBUtils.TODAY_MILEAGE, OperateDBUtils.AVERAGE_SPEED};
        Uri uri = OperateDBUtils.TABLE_TRAVEL_URI;
        String selection = OperateDBUtils.TIME + " = ? " + " AND " + OperateDBUtils.USER_ID + " = ?";
        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(uri, columns, selection,
                    new String[]{time, AVUser.getCurrentUser().getObjectId()}, null);
            if (cursor != null && cursor.moveToFirst()) {
                updateTravelInfo(travelInfoEntity, time);
            } else {
                insertTravelInfo(travelInfoEntity, time);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * 更新运动信息
     * @param travelInfoEntity
     * @param time
     */
    private void updateTravelInfo(TravelInfoEntity travelInfoEntity, String time) {
        String where = OperateDBUtils.TIME + " = ? " + " AND " +OperateDBUtils.USER_ID + " = ?";
        ContentValues values = new ContentValues();
        values.put(OperateDBUtils.TIME, time);
        values.put(OperateDBUtils.TODAY_MILEAGE, travelInfoEntity.getTodayMileage());
        values.put(OperateDBUtils.AVERAGE_SPEED, travelInfoEntity.getAverageSpeed());
        values.put(OperateDBUtils.TOTAL_MILEAGE, travelInfoEntity.getTotalMileage());
        values.put(OperateDBUtils.TODAY_CALOR, travelInfoEntity.getTodayCalor());
        values.put(OperateDBUtils.ADULT_WEIGHT, travelInfoEntity.getAdultWeight());
        values.put(OperateDBUtils.USER_ID, AVUser.getCurrentUser().getObjectId());

        mContext.getContentResolver().update(OperateDBUtils.TABLE_TRAVEL_URI, values, where,
                new String[]{time, AVUser.getCurrentUser().getObjectId()});
    }

    /**
     * 添加运动信息
     * @param travelInfoEntity
     * @param time
     */
    private void insertTravelInfo(TravelInfoEntity travelInfoEntity, String time) {
        ContentValues values = new ContentValues();
        values.put(OperateDBUtils.TIME, time);
        values.put(OperateDBUtils.TODAY_MILEAGE, travelInfoEntity.getTodayMileage());
        values.put(OperateDBUtils.AVERAGE_SPEED, travelInfoEntity.getAverageSpeed());
        values.put(OperateDBUtils.TOTAL_MILEAGE, travelInfoEntity.getTotalMileage());
        values.put(OperateDBUtils.TODAY_CALOR, travelInfoEntity.getTodayCalor());
        values.put(OperateDBUtils.ADULT_WEIGHT, travelInfoEntity.getAdultWeight());
        values.put(OperateDBUtils.BLUETOOTH_DEVICE_ID, travelInfoEntity.getDeviceAddress());
        values.put(OperateDBUtils.USER_ID, AVUser.getCurrentUser().getObjectId());
        mContext.getContentResolver().insert(OperateDBUtils.TABLE_TRAVEL_URI, values);
    }

    public void queryUserWeightData() {

        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (object) {
                    Cursor cursor = null;
                    try {
                        String[] columns = new String[]{TIME, USER_ID, WEIGHT};
                        String selection = USER_ID + "=?";
                        cursor = mContext.getContentResolver().query(WEIGHT_URI, columns, selection,
                                new String[]{AVUser.getCurrentUser().getObjectId()}, null);
                        timeWeightDataArray.clear();
                        Tools.SortArrayList sort = new Tools.SortArrayList();
                        if (cursor != null && cursor.getCount() > 0) {
                            while (cursor.moveToNext()) {
                                String timePoint = cursor.getString(cursor.getColumnIndex(TIME));
                                String weightNum = cursor.getString(cursor.getColumnIndex(WEIGHT));
                                timePoint = Tools.getYearFromDataPicker(timePoint) + "-" +
                                        Tools.getMonthFromDataPicker(timePoint) + "-" +
                                        Tools.getDayFromDataPicker(timePoint);
                                PersonDataEntity personDataEntity = new PersonDataEntity();
                                personDataEntity.setTime(timePoint);
                                personDataEntity.setWeight(weightNum);
                                timeWeightDataArray.add(personDataEntity);
                            }
                            Collections.sort(timeWeightDataArray, sort);
                        }
                        if (timeWeightDataArray.size() > 0) {
                            RequestUtils.getSharepreferenceEditor(mContext).putString(RequestUtils.WEIGHT,
                                    timeWeightDataArray.get(timeWeightDataArray.size() - 1).getWeight()).commit();
                        } else {
                            RequestUtils.getSharepreferenceEditor(mContext).putString(RequestUtils.WEIGHT,
                                    "0").commit();
                        }

                        MyApplication.getInstance().setUserWeightData(timeWeightDataArray);
                        sendBroadcast(TABLE_WEIGHT);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                }
            }
        });
    }

    /**
     * 保存体重信息
     * @param weight
     * @param time
     */
    public void saveWeightToDB(String weight, String time) {
        String[] columns = new String[]{OperateDBUtils.TIME, OperateDBUtils.USER_ID, OperateDBUtils.WEIGHT};
        Uri uri = OperateDBUtils.WEIGHT_URI;
        String selection = OperateDBUtils.TIME + " = ? " + " AND " + OperateDBUtils.USER_ID + " = ?";
        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(uri, columns, selection,
                    new String[]{time, AVUser.getCurrentUser().getObjectId()}, null);
            if (cursor != null && cursor.moveToFirst()) {
                updateWeightToDB(weight, time);
            } else {
                insertWeightToDB(weight, time);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * 增减体重信息
     * @param weight
     * @param time
     */
    public void insertWeightToDB(String weight, String time) {
        ContentValues values = new ContentValues();
        values.put(OperateDBUtils.TIME, time);
        values.put(OperateDBUtils.WEIGHT, weight);
        values.put(OperateDBUtils.USER_ID, AVUser.getCurrentUser().getObjectId());
        mContext.getContentResolver().insert(OperateDBUtils.WEIGHT_URI, values);
    }

    /**
     * 更新体重信息
     * @param weight
     * @param time
     */
    public void updateWeightToDB(String weight, String time) {
        String where = OperateDBUtils.TIME + " = ? " + " AND " + OperateDBUtils.USER_ID + " = ?";
        ContentValues values = new ContentValues();
        values.put(OperateDBUtils.TIME, time);
        values.put(OperateDBUtils.WEIGHT, weight);
        values.put(OperateDBUtils.USER_ID, AVUser.getCurrentUser().getObjectId());
        mContext.getContentResolver().update(OperateDBUtils.WEIGHT_URI, values, where,
                new String[]{time, AVUser.getCurrentUser().getObjectId()});
    }

    public void saveMileageInfoToDB(TravelInfoEntity travelInfoEntity, String time) {
        String[] columns = new String[]{OperateDBUtils.TIME, OperateDBUtils.USER_ID, OperateDBUtils.TOTAL_MILEAGE,
                OperateDBUtils.TODAY_MILEAGE, OperateDBUtils.AVERAGE_SPEED};
        Uri uri = OperateDBUtils.TABLE_TRAVEL_URI;
        String selection = OperateDBUtils.TIME + " = ? " + " AND " + OperateDBUtils.USER_ID + " = ?";
        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(uri, columns, selection,
                    new String[]{time, AVUser.getCurrentUser().getObjectId()}, null);
            if (cursor != null && cursor.moveToFirst()) {
                updateMileageInfoToDB(travelInfoEntity, time);
            } else {
                insertMileageInfoToDB(travelInfoEntity, time);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void insertMileageInfoToDB(TravelInfoEntity travelInfoEntity, String time) {
        ContentValues values = new ContentValues();
        values.put(OperateDBUtils.TIME, time);
        values.put(OperateDBUtils.TOTAL_MILEAGE, travelInfoEntity.getTotalMileage());
        values.put(OperateDBUtils.TODAY_MILEAGE, travelInfoEntity.getTodayMileage());
        values.put(OperateDBUtils.AVERAGE_SPEED, travelInfoEntity.getAverageSpeed());
        values.put(OperateDBUtils.USER_ID, AVUser.getCurrentUser().getObjectId());
        mContext.getContentResolver().insert(OperateDBUtils.TABLE_TRAVEL_URI, values);
    }

    private void updateMileageInfoToDB(TravelInfoEntity travelInfoEntity, String time) {
        String where = OperateDBUtils.TIME + " = ? " + " AND " + OperateDBUtils.USER_ID + " = ?";
        ContentValues values = new ContentValues();
        values.put(OperateDBUtils.TIME, time);
        values.put(OperateDBUtils.TOTAL_MILEAGE, travelInfoEntity.getTotalMileage());
        values.put(OperateDBUtils.TODAY_MILEAGE, travelInfoEntity.getTodayMileage());
        values.put(OperateDBUtils.AVERAGE_SPEED, travelInfoEntity.getAverageSpeed());
        values.put(OperateDBUtils.USER_ID, AVUser.getCurrentUser().getObjectId());
        mContext.getContentResolver().update(OperateDBUtils.TABLE_TRAVEL_URI, values, where,
                new String[]{time, AVUser.getCurrentUser().getObjectId()});
    }

    public void saveBabyInfoDataToDB(final List<AVObject> AVObjects) {

        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (object) {
                    AVObject avObject = null;
                    if (AVObjects != null) {
                        if (AVObjects.size() > 0) {
                            avObject = AVObjects.get(0);
                            AVFile avFile = avObject.getAVFile(HEADIMG);
                            if (avFile != null) {
                                final AVObject finalAvObject = avObject;
                                avFile.getDataInBackground(new GetDataCallback() {
                                    @Override
                                    public void done(byte[] data, AVException e) {
                                        if (e == null) {
                                            if (PicOperator.bytes2Bitmap(data) != null) {
                                             PicOperator.saveToData(mContext, PicOperator.bytes2Bitmap(data),
                                                     MyApplication.getInstance().getBabyHeadIconPath(finalAvObject.getObjectId()));
                                            }
                                        }
                                    }
                                });
                            }

                        } else {
                            avObject = null;
                        }
                        String selection = USER_ID + " = ? ";
                        Cursor cursor = null;
                        try {
                            cursor = mContext.getContentResolver().query(BABYINFO_URI, null, selection,
                                    new String[]{AVUser.getCurrentUser().getObjectId()}, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                updateBabyInfo(avObject);
                            } else {
                                insertBabyInfo(avObject);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (cursor != null) {
                                queryBabyInfoDataFromDB();
                                cursor.close();
                            }
                        }
                    }
                }
            }
        });
    }

    private void insertBabyInfo(AVObject avObject) {
        ContentValues values = new ContentValues();
        values.put(USER_ID, AVUser.getCurrentUser().getObjectId());
        if (avObject != null) {
            values.put(BABY_INFO_OBJECT_ID, avObject.getObjectId());
            values.put(BABYNAME, avObject.getString(BABYNAME));
            values.put(BABYSEX, avObject.getString(BABYSEX));
            values.put(BIRTHDAY, avObject.getString(BIRTHDAY));
            values.put(HEADIMG, avObject.getAVFile(HEADIMG) == null ? "" : avObject.getAVFile(HEADIMG).getUrl());
            values.put(IS_BIND, avObject.getBoolean(IS_BIND) ? 1 : 0);
            values.put(BLUETOOTH_DEVICE_ID, avObject.getString(BLUETOOTH_DEVICE_ID));
            values.put(BABYNATIVE, avObject.getString(BABYNATIVE));
        }
        mContext.getContentResolver().insert(BABYINFO_URI, values);
    }

    private void updateBabyInfo(AVObject avObject) {
        String where = USER_ID + " = ? ";
        ContentValues values = new ContentValues();
        values.put(USER_ID, AVUser.getCurrentUser().getObjectId());
        if (avObject != null) {
            values.put(BABY_INFO_OBJECT_ID, avObject.getObjectId());
            values.put(BABYNAME, avObject.getString(BABYNAME));
            values.put(BABYSEX, avObject.getString(BABYSEX));
            values.put(BIRTHDAY, avObject.getString(BIRTHDAY));
            values.put(HEADIMG, avObject.getAVFile(HEADIMG) == null ? "" : avObject.getAVFile(HEADIMG).getUrl());
            values.put(IS_BIND, avObject.getBoolean(IS_BIND) ? 1 : 0);
            values.put(BLUETOOTH_DEVICE_ID, avObject.getString(BLUETOOTH_DEVICE_ID));
            values.put(BABYNATIVE, avObject.getString(BABYNATIVE));
        }
        mContext.getContentResolver().update(BABYINFO_URI, values, where,
                new String[]{AVUser.getCurrentUser().getObjectId()});
    }

    public void queryBabyInfoDataFromDB() {

        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (object) {

                    Cursor cursor = null;
                    try {
                        String selection = USER_ID + "=?";
                        cursor = mContext.getContentResolver().query(BABYINFO_URI, null, selection,
                                new String[]{AVUser.getCurrentUser().getObjectId()}, null);
                      ArrayList<BabyInfoEntity> babyInfoEntities = new ArrayList<>();
                        if (cursor != null && cursor.getCount() > 0) {
                            while (cursor.moveToNext()) {
                                BabyInfoEntity babyInfoEntity = new BabyInfoEntity();
                                babyInfoEntity.setBabyName(cursor.getString(cursor.getColumnIndex(BABYNAME)), mContext.getString(R.string.baby_niName));
                                babyInfoEntity.setBabySex(cursor.getString(cursor.getColumnIndex(OperateDBUtils.BABYSEX)),  mContext.getString(R.string.input_sex_baby));
                                babyInfoEntity.setBirthday(cursor.getString(cursor.getColumnIndex(OperateDBUtils.BIRTHDAY)),  mContext.getString(R.string.input_birth_baby));
                                babyInfoEntity.setBabyNative(cursor.getString(cursor.getColumnIndex(OperateDBUtils.BABYNATIVE)),  mContext.getString(R.string.input_address_baby));
                                babyInfoEntity.setImage(cursor.getString(cursor.getColumnIndex(OperateDBUtils.HEADIMG)), "");
                                babyInfoEntity.setIsBind(cursor.getInt(cursor.getColumnIndex(OperateDBUtils.IS_BIND)));
                                babyInfoEntity.setBluetoothDeviceId(cursor.getString(cursor.getColumnIndex(OperateDBUtils.BLUETOOTH_DEVICE_ID)));
                                babyInfoEntity.setBabyInfoObjectId(cursor.getString(cursor.getColumnIndex(OperateDBUtils.BABY_INFO_OBJECT_ID)));
                                babyInfoEntities.add(babyInfoEntity);
                            }
                        }
                        if (mSaveBabyInfoFinishListener != null) {
                            mSaveBabyInfoFinishListener.saveBabyInfoFinish();
                        }
                        UserInfo.getInstance().setBabyInfoEntities(babyInfoEntities);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }

                }
            }
        });
    }



    public void saveBlueToothIsBindToDB(final boolean isBind, final String deviceNum) {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (object) {
                    try {
                        String where = OperateDBUtils.USER_ID + " = ?";
                        ContentValues values = new ContentValues();
                        values.put(OperateDBUtils.IS_BIND, isBind);
                        values.put(OperateDBUtils.BLUETOOTH_DEVICE_ID, deviceNum);
                        mContext.getContentResolver().update(OperateDBUtils.BABYINFO_URI, values, where,
                                new String[]{AVUser.getCurrentUser().getObjectId()});
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void sendBroadcast(String data) {
        Intent intent = new Intent(QUERY_FINISH);
        intent.putExtra(QUERY_DATA, data);
        mContext.sendBroadcast(intent);
    }

    public void clearDBTable(Uri tableUri) {
        String where = USER_ID + "=?";
        mContext.getContentResolver().delete(tableUri, where, new String[]{AVUser.getCurrentUser().getObjectId()});
    }

    public void deleteTimeDBTable(Uri tableUri, String time) {
        String where = OperateDBUtils.TIME + " = ? " + " AND " + USER_ID + " = ?";
        mContext.getContentResolver().delete(tableUri, where, new String[]{time, AVUser.getCurrentUser().getObjectId()});
    }

    /**
     * 保存宝宝信息的回调接口
     */
    public interface SaveBabyInfoFinishListener {
        /**
         * 保存宝宝信息完成
         */
        void saveBabyInfoFinish();
    }

    //--------------------------------------------------------------V3.0-----------

    public void insertTravelToDB(String date ,String mileage){

        String[] columns = new String[]{OperateDBUtils.DATE, OperateDBUtils.MILEAGE};
        Uri uri = OperateDBUtils.TABLE_TRAVEL_THREE_URI;
        String selection = OperateDBUtils.DATE + " = ? " + " AND " + OperateDBUtils.USER_ID + " = ?";
        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(uri, columns, selection,
                    new String[]{date, AVUser.getCurrentUser().getObjectId()}, null);
            if (cursor != null && cursor.moveToFirst()) {
                updateTravel(mileage, date);
            } else {
                insertTravel(mileage, date);
          }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    /**
     * 增加每天的运动信息
     * @param mileage
     * @param date
     */
    private void insertTravel(String mileage, String date) {
        ContentValues values = new ContentValues();
        values.put(OperateDBUtils.DATE, date);
        values.put(OperateDBUtils.USER_ID, AVUser.getCurrentUser().getObjectId());
        values.put(OperateDBUtils.MILEAGE, mileage);
        mContext.getContentResolver().insert(OperateDBUtils.TABLE_TRAVEL_THREE_URI, values);
        if (Integer.valueOf(mileage) > SpUtils.getInt(mContext,"MaxMileage",500)) {
            SpUtils.putInt(mContext,"MaxMileage",Integer.valueOf(mileage) );
        }
    }

    /**
     * 更新数据库信息
     * @param mileage
     * @param date
     */
    private void updateTravel(String mileage, String date) {
        String where = OperateDBUtils.DATE + " = ? " + " AND " +OperateDBUtils.USER_ID + " = ?";
        ContentValues values = new ContentValues();
        values.put(OperateDBUtils.DATE, date);
        values.put(OperateDBUtils.USER_ID, AVUser.getCurrentUser().getObjectId());
        values.put(OperateDBUtils.MILEAGE, mileage);
        mContext.getContentResolver().update(OperateDBUtils.TABLE_TRAVEL_THREE_URI, values, where,
                new String[]{date, AVUser.getCurrentUser().getObjectId()});
        if (Integer.valueOf(mileage) > SpUtils.getInt(mContext,"MaxMileage",500)) {
            SpUtils.putInt(mContext,"MaxMileage",Integer.valueOf(mileage) );
        }
    }


    /**
     * 同步周频率
     * @param date
     * @param frequency
     */
    public void insertFrequencyToDB(Uri uri,String date ,int frequency){

        String[] columns = new String[]{OperateDBUtils.DATE, OperateDBUtils.FREQUENCY};

        String selection = OperateDBUtils.DATE + " = ? " + " AND " + OperateDBUtils.USER_ID + " = ?";
        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(uri, columns, selection,
                    new String[]{date, AVUser.getCurrentUser().getObjectId()}, null);
            if (cursor != null && cursor.moveToFirst()) {
                updateFrequency(uri,frequency, date);
            } else {
                insertFrequency(uri,frequency, date);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * 增加每天的运动信息
     * @param frequency
     * @param date
     */
    private void insertFrequency(Uri uri,int frequency, String date) {
        ContentValues values = new ContentValues();
        values.put(OperateDBUtils.DATE, date);
        values.put(OperateDBUtils.USER_ID, AVUser.getCurrentUser().getObjectId());
        values.put(OperateDBUtils.FREQUENCY, frequency);
        mContext.getContentResolver().insert(uri, values);
    }

    /**
     * 更新数据库信息
     * @param frequency
     * @param date
     */
    private void updateFrequency(Uri uri,int frequency, String date) {
        String where = OperateDBUtils.DATE + " = ? " + " AND " +OperateDBUtils.USER_ID + " = ?";
        ContentValues values = new ContentValues();
        values.put(OperateDBUtils.DATE, date);
        values.put(OperateDBUtils.USER_ID, AVUser.getCurrentUser().getObjectId());
        values.put(OperateDBUtils.FREQUENCY, frequency);
        mContext.getContentResolver().update(uri, values, where,
                new String[]{date, AVUser.getCurrentUser().getObjectId()});
    }


}
