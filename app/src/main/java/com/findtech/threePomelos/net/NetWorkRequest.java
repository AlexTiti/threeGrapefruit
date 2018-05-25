package com.findtech.threePomelos.net;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;
import com.avos.avoscloud.SaveCallback;
import com.findtech.threePomelos.database.OperateDBUtils;
import com.findtech.threePomelos.entity.TravelInfoEntity;
import com.findtech.threePomelos.music.info.MusicInfo;
import com.findtech.threePomelos.music.utils.DownFileUtils;
import com.findtech.threePomelos.music.utils.L;
import com.findtech.threePomelos.mydevices.bean.BluetoothLinkBean;
import com.findtech.threePomelos.sdk.MyApplication;
import com.findtech.threePomelos.user.bean.UserInfo;
import com.findtech.threePomelos.utils.IContent;
import com.findtech.threePomelos.utils.PicOperator;
import com.findtech.threePomelos.utils.RequestUtils;
import com.findtech.threePomelos.utils.Tools;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author zhi.zhang
 * @date 3/9/16
 */
public class NetWorkRequest {

    private final String TAG = "NetWorkRequest";

    /* 表名 */
    /**
     * 用户信息
     */
    public static final String USER_INFO = "_User";
    /**
     * 宝贝信息
     */
    public static final String BABY_INFO = "BabyInfo";
    /**
     * 宝贝体重
     */
    public static final String BABY_WEIGHT = "BabyWeight";
    /**
     * 宝贝身高
     */
    public static final String BABY_HEIGHT = "BabyHeight";
    /**
     * 推车今日里程、今日时速、总里程
     */
    public static final String TRAVEL_INFO = "TravelInfo";
    /**
     * 推车总里程
     */
    public static final String TOTAL_MILEAGE = "TotalMileage";
    /**
     * 健康合理区间表
     */
    public static final String HEALTH_STATE = "HealthState";
    /**
     * 健康贴士
     */
    public static final String HEALTH_TIPS = "HealthTips";
    /**
     * 用户协议
     */
    public static final String USER_PROTOCOL = "UserProtocol";


    /**
     * 昵称
     */
    public static final String NICKNAME = "nickName";

    /**
     * 头像
     */
    public static final String HEADER = "header";


    /**
     * 设备列表
     */
    public static final String DEVICE_UUID = "DeviceUUIDList";
    public static final String CART_LIST_DETAILS = "Cart_List_Details";
    /**
     * 卡路里
     */
    public static final String CALORIES = "totalCalories";

    /**
     * 一键修复
     */
    public static final String REPAIR_DATA = "DeviceHardwareInfo";
    /**
     * 从机数据
     */
    public static final String DEVICE_DATA = "DeviceData";

    /**
     * 使用说明
     */
    public static final String INSTRUCTIONS = "Instruction";

    public static String BLUETOOTH_NAME = "bluetoothName";
    public static String DEVICEIDENTIFITER = "deviceIdentifier";
    public static String FUNCTION_TYPE = "fuctionType";
    public static String COMPANY = "company";
    private Context mContext;
    private OperateDBUtils mOperateDBUtils;
    public static final String TABLE_NAME = "TravelDataWithDay";
    public static final String TABLE_NAME_WEEK = "FrequencyWeeked";
    public static final String TABLE_NAME_MONTH = "FrequencyMonthed";

    public NetWorkRequest(Context context) {
        mContext = context;
        mOperateDBUtils = new OperateDBUtils(mContext);

    }


    public void getBabyInfoDataAndSaveToDB() {

        AVQuery<AVObject> query = AVQuery.getQuery(BABY_INFO);
        query.whereEqualTo("post", AVUser.getCurrentUser());
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        AVObject post = new AVObject(NetWorkRequest.BABY_INFO);
                        post.put("post", AVUser.getCurrentUser());
                        post.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    getBabyInfoDataAndSaveToDB();
                                } else {
                                    mOperateDBUtils.queryBabyInfoDataFromDB();
                                }
                            }
                        });
                    } else {
                        mOperateDBUtils.saveBabyInfoDataToDB(list);
                    }
                } else {
                    mOperateDBUtils.queryBabyInfoDataFromDB();
                }
            }
        });
    }

    public static void getUserInfo(final Context mContext) {
        AVQuery<AVObject> query = new AVQuery<>(USER_INFO);
        query.getInBackground(AVUser.getCurrentUser().getObjectId(),new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException exception) {
                UserInfo userInfo = UserInfo.getInstance();
                if (exception == null) {
                    userInfo.setNickName(avObject.getString(NICKNAME));
                    AVFile avFile = avObject.getAVFile(HEADER);
                    if (avFile != null) {
                        avFile.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, AVException e) {
                                if (e == null) {
                                    if (PicOperator.bytes2Bitmap(data) != null) {
                                        PicOperator.saveToData(mContext, PicOperator.bytes2Bitmap(data),MyApplication.getInstance().getHeadIconPath());
                                    }
                                }
                            }
                        });
                    }
                } else {

                }

            }
        });


    }

    public void getBabyInfoDataAndSaveToDB(final QueryBabyInfoCallBack.QueryIsBind queryIsBind) {
        AVQuery<AVObject> query = AVQuery.getQuery(BABY_INFO);
        query.whereEqualTo("post", AVUser.getCurrentUser());
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        AVObject post = new AVObject(NetWorkRequest.BABY_INFO);
                        post.put("post", AVUser.getCurrentUser());
                        post.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    getBabyInfoDataAndSaveToDB(queryIsBind);
                                } else {
                                    mOperateDBUtils.queryBabyInfoDataFromDB();
                                }
                            }
                        });
                    } else {
                        mOperateDBUtils.saveBabyInfoDataToDB(list);
                        if (list.size() > 0) {
                            queryIsBind.finishQueryIsBind(list.get(0).getBoolean(OperateDBUtils.IS_BIND),
                                    list.get(0).getString(OperateDBUtils.BLUETOOTH_DEVICE_ID));
                        } else {
                            queryIsBind.finishQueryIsBind(false, "");
                        }
                    }
                } else {
                    mOperateDBUtils.queryBabyInfoDataFromDB();
                }
            }
        });
    }

    public void getBabyWeightDataAndSaveToDB() {
        AVQuery<AVObject> query = AVQuery.getQuery(BABY_WEIGHT);
        query.whereEqualTo("post", AVUser.getCurrentUser());
        query.orderByAscending(OperateDBUtils.DATE);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    mOperateDBUtils.clearDBTable(OperateDBUtils.WEIGHT_URI);
                    if (list.size() > 0) {
                        for (AVObject avObject : list) {
                            String weight = avObject.getString(OperateDBUtils.WEIGHT);
                            Date date = avObject.getDate(OperateDBUtils.DATE);
                            String time = Tools.getTimeFromDate(date);
                            if (!TextUtils.isEmpty(weight) && !TextUtils.isEmpty(time)) {
                                mOperateDBUtils.saveWeightToDB(weight, time);
                            }
                        }
                    }
                    mOperateDBUtils.queryUserWeightData();
                } else {
                    mOperateDBUtils.queryUserWeightData();
                }
            }
        });
    }

    public void getAllHealthDate(FindCallback findCallback) {
        AVQuery<AVObject> query = new AVQuery<>(HEALTH_TIPS);
        query.orderByAscending("createdAt");
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.setMaxCacheAge(24 * 3600 * 7 * 1000);
        query.findInBackground(findCallback);
    }

    public void getMusicDownList(FindCallback findCallback) {
        AVQuery<AVObject> query = new AVQuery<>(MUSIC_USER);
        query.whereEqualTo("post_user", AVUser.getCurrentUser());
        query.whereEqualTo("is_down", "1");
        query.findInBackground(findCallback);
    }

    public static void sendAadultWeight(final String weight, final SaveCallback saveCallback) {

        AVQuery<AVObject> query = new AVQuery<>(CALORIES);
        query.whereEqualTo("post", AVUser.getCurrentUser());
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {

                if (e == null) {
                    if (list.size() != 0) {
                        AVObject avObject = list.get(0);
                        avObject.put("adultWeight", weight);
                        avObject.saveInBackground(saveCallback);
                    } else {
                        AVObject avObject = new AVObject(CALORIES);
                        avObject.put("post", AVUser.getCurrentUser());
                        avObject.put("adultWeight", weight);
                        avObject.saveInBackground(saveCallback);
                    }
                }
            }
        });
    }

    public void sendDeleteDownMusic(final String name, final SaveCallback saveCallback) {
        AVQuery<AVObject> query = new AVQuery<>(MUSIC_USER);
        query.whereEqualTo("post_user", AVUser.getCurrentUser());
        query.whereEqualTo("musicName", name);
        query.whereEqualTo("is_down", "1");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null && list.size() != 0) {
                    for (AVObject avObject : list) {
                        avObject.put("is_down", "0");
                        avObject.saveInBackground(saveCallback);
                    }
                }
            }
        });
    }

    public static void sendNameToServer(final String name, final String address, final SaveCallback saveCallback) {
        AVQuery<AVObject> query = new AVQuery<>(DEVICE_UUID);
        query.whereEqualTo("post", AVUser.getCurrentUser());
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    for (int i = 0; i < Objects.requireNonNull(list).size(); i++) {
                        AVObject avObject = list.get(i);
                        if (avObject.getString("bluetoothDeviceId") != null && avObject.getString("bluetoothDeviceId").equals(address)) {
                            avObject.put("bluetoothName", name);
                            avObject.saveInBackground(saveCallback);
                        }
                    }
                } else {
                    L.e("======", e.toString());
                }
            }
        });
    }

    public void updateUUIDCreatAt(final String address, final SaveCallback saveCallback) {
        AVQuery<AVObject> query = new AVQuery<>(DEVICE_UUID);
        query.whereEqualTo("post", AVUser.getCurrentUser());
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    for (int i = 0; i < list.size(); i++) {
                        AVObject avObject = list.get(i);
                        if (address.equals(avObject.getString("bluetoothDeviceId"))) {
                            avObject.put("bluetoothDeviceId", address);
                            avObject.saveInBackground(saveCallback);
                        }
                    }
                }
            }
        });
    }

    public void sendRepairMessage(String repairMessage, Date date, String address, String type, SaveCallback callback) {
        AVObject avObject = new AVObject(REPAIR_DATA);
        avObject.put("user", AVUser.getCurrentUser());
        avObject.put(OperateDBUtils.DATE, date);
        avObject.put("deviceAddress", address);
        avObject.put(DEVICEIDENTIFITER, type);
        avObject.put("repairInfo", repairMessage);
        avObject.saveInBackground(callback);
    }

    public void sendDataMessage(String dataMessage, Date date, String address, String type, SaveCallback callback) {

        AVObject avObject = new AVObject(DEVICE_DATA);
        avObject.put("user", AVUser.getCurrentUser());
        avObject.put(OperateDBUtils.DATE, date);
        avObject.put("deviceAddress", address);
        avObject.put(DEVICEIDENTIFITER, type);
        avObject.put("deviceData", dataMessage);
        avObject.saveInBackground(callback);
    }

    public void getBabyHeightDataAndSaveToDB() {
        AVQuery<AVObject> query = AVQuery.getQuery(BABY_HEIGHT);
        query.whereEqualTo("post", AVUser.getCurrentUser());
        query.orderByAscending(OperateDBUtils.DATE);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    mOperateDBUtils.clearDBTable(OperateDBUtils.HEIGHT_URI);
                    if (list.size() > 0) {
                        for (AVObject avObject : list) {
                            String height = avObject.getString(OperateDBUtils.HEIGHT);
                            Date curDate = avObject.getDate(OperateDBUtils.DATE);
                            String time = Tools.getTimeFromDate(curDate);
                            if (!TextUtils.isEmpty(height) && !TextUtils.isEmpty(time)) {
                                mOperateDBUtils.saveHeightToDB(height, time);
                            }
                        }
                    }
                    mOperateDBUtils.queryUserHeightData();
                } else {
                    mOperateDBUtils.queryUserHeightData();

                }
            }
        });
    }

    public void getTotalMileageDataAndSaveToSP() {
        AVQuery<AVObject> query = AVQuery.getQuery(TOTAL_MILEAGE);
        query.whereEqualTo("post", AVUser.getCurrentUser());
        query.findInBackground(new FindCallback<AVObject>() {
            TravelInfoEntity travelInfoEntity = TravelInfoEntity.getInstance();

            @Override
            public void done(List<AVObject> list, AVException e) {
                String todayMileage = "0.0";
                if (e == null) {
                    if (list.size() > 0) {
                        for (AVObject avObject : list) {
                            todayMileage = avObject.getString(OperateDBUtils.TOTAL_MILEAGE);
                        }
                    }
                    RequestUtils.getSharepreferenceEditor(mContext).putString(OperateDBUtils.TOTAL_MILEAGE,
                            todayMileage).commit();
                } else {
                    todayMileage = RequestUtils.getSharepreference(mContext).getString(OperateDBUtils.TOTAL_MILEAGE, "0.0");

                }
                travelInfoEntity.setTotalMileage(todayMileage);

                mHandle.sendEmptyMessage(0x99);
            }
        });
    }


    Handler mHandle = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            Intent intent = new Intent(OperateDBUtils.QUERY_FINISH);
            mContext.sendBroadcast(intent);
            return true;
        }
    });


    public void getTravelInfoDataAndSaveToDB() {
        AVQuery<AVObject> query = getBaseQuery(TRAVEL_INFO);
        query.whereEqualTo("post", AVUser.getCurrentUser());
        final Date curDate = Tools.getCurrentDate();
        query.whereEqualTo(OperateDBUtils.DATE, curDate);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    // 删除数据信息
                    mOperateDBUtils.deleteTimeDBTable(OperateDBUtils.TABLE_TRAVEL_URI, Tools.getTimeFromDate(curDate));
                    if (list.size() > 0) {
                        for (AVObject avObject : list) {
                            TravelInfoEntity travelInfoEntity = TravelInfoEntity.getInstance();
                            travelInfoEntity.setTodayMileage(avObject.getString(OperateDBUtils.TODAY_MILEAGE));
                            travelInfoEntity.setAverageSpeed(avObject.getString(OperateDBUtils.AVERAGE_SPEED));
                            String todayMileage = avObject.getString(OperateDBUtils.TOTAL_MILEAGE);
                            travelInfoEntity.setTotalMileage(todayMileage);
                            Date date = avObject.getDate(OperateDBUtils.DATE);
                            mOperateDBUtils.saveTravelInfoToDB(travelInfoEntity, Tools.getTimeFromDate(date));
                        }
                    }
                }
            }
        });
    }


    public static void getTravelInfo(String address, FindCallback<AVObject> findCallback) {
        AVQuery<AVObject> query = getBaseQuery(TRAVEL_INFO);
        query.whereEqualTo("post", AVUser.getCurrentUser());
        query.whereEqualTo("bluetoothDeviceId", address);
        query.orderByDescending("updatedAt");
        query.findInBackground(findCallback);

    }


    /**
     * @param tableName BABY_WEIGHT
     *                  BABY_HEIGHT
     *                  TRAVEL_INFO
     * @param date
     * @return
     */

    public void isExistTheTimeOnTable(String tableName, final Date date, final String address, final FindCallback findCallback) {
        final AVQuery<AVObject> query = AVQuery.getQuery(tableName);
        query.whereEqualTo("post", AVUser.getCurrentUser());
        query.whereEqualTo(OperateDBUtils.BLUETOOTH_DEVICE_ID, address);
        query.whereEqualTo(OperateDBUtils.DATE, date);
        query.findInBackground(findCallback);
    }

    public void isExistTheTimeOnTable(String tableName, final Date date, final FindCallback findCallback) {
        final AVQuery<AVObject> query = AVQuery.getQuery(tableName);
        query.whereEqualTo("post", AVUser.getCurrentUser());
        query.whereEqualTo(OperateDBUtils.DATE, date);
        query.findInBackground(findCallback);
    }

    public void isExistTheUserOnTable(String tableName, final FindCallback<AVObject> findCallback) {
        final AVQuery<AVObject> query = AVQuery.getQuery(tableName);
        query.whereEqualTo("post", AVUser.getCurrentUser());
        query.findInBackground(findCallback);
    }

    public void updateWeightAndTimeToServer(final String weight, final Date date, final SaveCallback saveCallback) {
        if (TextUtils.isEmpty(weight) || date == null) {

            return;
        }
        final AVQuery<AVObject> query = AVQuery.getQuery(BABY_WEIGHT);
        query.whereEqualTo("post", AVUser.getCurrentUser());
        query.whereEqualTo(OperateDBUtils.DATE, date);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            AVObject avObjects = list.get(i);
                            avObjects.put(OperateDBUtils.WEIGHT, weight);
                            avObjects.saveInBackground(saveCallback);
                        }
                    }
                }
            }
        });
    }

    public void addWeightAndTimeToServer(final String weight, final Date date, final SaveCallback saveCallback) {
        if (TextUtils.isEmpty(weight) || date == null) {

            return;
        }

        AVObject postWeight = new AVObject(BABY_WEIGHT);
        postWeight.put("post", AVUser.getCurrentUser());
        postWeight.put(OperateDBUtils.DATE, date);
        postWeight.put(OperateDBUtils.WEIGHT, weight);
        postWeight.saveInBackground(saveCallback);
    }

    public void updateHeightAndTimeToServer(final String height, final Date date, final SaveCallback saveCallback) {
        if (TextUtils.isEmpty(height) || date == null) {

            return;
        }
        final AVQuery<AVObject> query = AVQuery.getQuery(BABY_HEIGHT);
        query.whereEqualTo("post", AVUser.getCurrentUser());
        query.whereEqualTo(OperateDBUtils.DATE, date);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            AVObject avObjects = list.get(i);
                            avObjects.put(OperateDBUtils.HEIGHT, height);
                            avObjects.saveInBackground(saveCallback);
                        }
                    }
                }
            }
        });
    }

    public void addHeightAndTimeToServer(final String height, final Date date, final SaveCallback saveCallback) {
        if (TextUtils.isEmpty(height) || date == null) {

            return;
        }

        AVObject postHeight = new AVObject(BABY_HEIGHT);
        postHeight.put("post", AVUser.getCurrentUser());
        postHeight.put(OperateDBUtils.DATE, date);
        postHeight.put(OperateDBUtils.HEIGHT, height);
        postHeight.saveInBackground(saveCallback);
    }

    public void updateTotalMileageAndTimeToServer(final String totalMileage, final SaveCallback saveCallback) {
        if (TextUtils.isEmpty(totalMileage)) {

            return;
        }
        final AVQuery<AVObject> query = AVQuery.getQuery(TOTAL_MILEAGE);
        query.whereEqualTo("post", AVUser.getCurrentUser());
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            AVObject avObjects = list.get(i);
                            avObjects.put(OperateDBUtils.TOTAL_MILEAGE, totalMileage);
                            avObjects.saveInBackground(saveCallback);
                        }
                    }
                }
            }
        });
    }

    public void addTotalMileageAndTimeToServer(final String totalMileage, final SaveCallback saveCallback) {
        if (TextUtils.isEmpty(totalMileage)) {

            return;
        }
        AVObject postTotalMileage = new AVObject(TOTAL_MILEAGE);
        postTotalMileage.put("post", AVUser.getCurrentUser());
        postTotalMileage.put(OperateDBUtils.TOTAL_MILEAGE, totalMileage);
        postTotalMileage.saveInBackground(saveCallback);
    }

    public void updateTravelInfoAndTimeToServer(final TravelInfoEntity travelInfoEntity, final Date date, final SaveCallback saveCallback) {
        if (travelInfoEntity == null || date == null) {
            return;
        }
        final AVQuery<AVObject> query = AVQuery.getQuery(TRAVEL_INFO);
        query.whereEqualTo("post", AVUser.getCurrentUser());
        query.whereEqualTo(OperateDBUtils.DATE, date);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            AVObject avObjects = list.get(i);
                            avObjects.put(OperateDBUtils.TOTAL_MILEAGE, travelInfoEntity.getTotalMileage());
                            avObjects.put(OperateDBUtils.TODAY_MILEAGE, travelInfoEntity.getTodayMileage());
                            avObjects.put(OperateDBUtils.AVERAGE_SPEED, travelInfoEntity.getAverageSpeed());
                            avObjects.put(DEVICEIDENTIFITER, IContent.getInstacne().clickPositionType);
                            avObjects.saveInBackground(saveCallback);
                        }
                    }
                }
            }
        });
    }

    public void addTravelInfoAndTimeToServer(final TravelInfoEntity travelInfoEntity, final Date date, final SaveCallback saveCallback) {
        if (travelInfoEntity == null || date == null) {

            return;
        }
        AVObject postTotalMileage = new AVObject(TRAVEL_INFO);
        postTotalMileage.put("post", AVUser.getCurrentUser());
        postTotalMileage.put(OperateDBUtils.DATE, date);
        postTotalMileage.put(DEVICEIDENTIFITER, IContent.getInstacne().clickPositionType);
        postTotalMileage.put("bluetoothDeviceId", IContent.getInstacne().address);
        postTotalMileage.put(OperateDBUtils.TOTAL_MILEAGE, travelInfoEntity.getTotalMileage());
        postTotalMileage.put(OperateDBUtils.TODAY_MILEAGE, travelInfoEntity.getTodayMileage());
        postTotalMileage.put(OperateDBUtils.AVERAGE_SPEED, travelInfoEntity.getAverageSpeed());
        postTotalMileage.saveInBackground(saveCallback);
    }

    public void selectDeviceTypeAndIdentifier() {
        AVQuery<AVObject> query = new AVQuery<>(CART_LIST_DETAILS);
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.setMaxCacheAge(24 * 3600 * 7 * 1000);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    for (AVObject avObject : list) {
                        BluetoothLinkBean bean = new BluetoothLinkBean();
                        bean.setDeviceIndentifier(avObject.getString(NetWorkRequest.DEVICEIDENTIFITER));
                        bean.setName(avObject.getString(NetWorkRequest.BLUETOOTH_NAME));
                        bean.setType(avObject.getString(NetWorkRequest.FUNCTION_TYPE));
                        bean.setCompany(avObject.getString(NetWorkRequest.COMPANY));
                        IContent.getInstacne().bluetoothLinkBeen.add(bean);
                    }
                }
            }
        });


    }

    public void selectDeviceTypeAndIdentifier(FindCallback<AVObject> findCallback) {
        AVQuery<AVObject> query = new AVQuery<>(CART_LIST_DETAILS);
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.setMaxCacheAge(24 * 3600 * 7 * 1000);
        query.findInBackground(findCallback);


    }

    public void thisBlueToothIsBinded(final String deviceNum, FindCallback<AVObject> findCallback) {
        AVQuery<AVObject> query = new AVQuery<>(DEVICE_UUID);
        query.whereEqualTo("post", AVUser.getCurrentUser());
        query.whereEqualTo(OperateDBUtils.BLUETOOTH_DEVICE_ID, deviceNum);
        query.findInBackground(findCallback);

    }

    public void updateBlueTooth(final boolean isBind, final String deviceNum, final String deviceName, final String functype, final String deviceIndentifier, final SaveCallback saveCallback) {


    }

    public void addBlueTooth(final boolean isBind, final String deviceNum, final String deviceName, final String functype, final String deviceIndentifier, String company, final SaveCallback saveCallback) {
        AVObject object_bind = new AVObject(DEVICE_UUID);
        object_bind.put(OperateDBUtils.BLUETOOTH_DEVICE_ID, deviceNum);
        object_bind.put("bluetoothName", deviceName);
        object_bind.put("bluetoothBind", isBind);
        object_bind.put(DEVICEIDENTIFITER, deviceIndentifier);
        object_bind.put(FUNCTION_TYPE, functype);
        object_bind.put(COMPANY, company);
        object_bind.put("post", AVUser.getCurrentUser());
        object_bind.saveInBackground(saveCallback);
    }

    public static void deleteBlueToothIsBind(final String deviceNum, final FindCallback<AVObject> findCallback) {
        AVQuery<AVObject> query = new AVQuery<>(DEVICE_UUID);
        query.whereEqualTo("post", AVUser.getCurrentUser());
        query.whereEqualTo(OperateDBUtils.BLUETOOTH_DEVICE_ID, deviceNum);
        query.findInBackground(findCallback);
    }

    /**
     * 上传Server device 的运动信息
     *
     * @param today
     * @param total
     * @param speed
     */

    public static void saveDataToServer(final String address, final String today, final String total, final String speed, final String type, final SaveCallback saveCallback) {

        AVQuery<AVObject> query = new AVQuery<>(TRAVEL_INFO);
        query.whereEqualTo("post", AVUser.getCurrentUser());
        query.whereEqualTo("date", Tools.getCurrentDate());
        query.whereEqualTo(OperateDBUtils.BLUETOOTH_DEVICE_ID, IContent.getInstacne().address);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            AVObject avObjects = list.get(i);
                            String deviceAddress = avObjects.getString(OperateDBUtils.BLUETOOTH_DEVICE_ID);
                            if (deviceAddress != null && deviceAddress.equals(IContent.getInstacne().address)) {
                                avObjects.put(OperateDBUtils.AVERAGE_SPEED, speed);
                                avObjects.put(OperateDBUtils.TODAY_MILEAGE, today);
                                avObjects.put(OperateDBUtils.TOTAL_MILEAGE, total);
                                avObjects.put("date", Tools.getCurrentDate());
                                avObjects.put(DEVICEIDENTIFITER, type);
                                avObjects.saveInBackground(saveCallback);
                            }
                        }
                    } else {
                        AVObject avObjects = new AVObject(TRAVEL_INFO);
                        avObjects.put(OperateDBUtils.AVERAGE_SPEED, speed);
                        avObjects.put(OperateDBUtils.TODAY_MILEAGE, today);
                        avObjects.put(OperateDBUtils.TOTAL_MILEAGE, total);
                        avObjects.put(DEVICEIDENTIFITER, type);
                        avObjects.put("date", Tools.getCurrentDate());
                        avObjects.put(OperateDBUtils.BLUETOOTH_DEVICE_ID, address);
                        avObjects.put("post", AVUser.getCurrentUser());
                        avObjects.saveInBackground(saveCallback);
                    }

                }

            }
        });

    }

    /**
     * 上传推车数据 （New）
     *
     * @param address
     * @param today
     * @param total
     * @param speed
     * @param type
     * @param saveCallback
     */
    public static void saveDataToServerWithDay(final String address, final double today, final double total, final String speed, final String type, final SaveCallback saveCallback) {

        AVQuery<AVObject> query = new AVQuery<>("TravelDataWithDay");
        query.whereEqualTo("userId", AVUser.getCurrentUser());
        query.whereEqualTo("travelDate", Tools.getCurrentDate());
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            AVObject avObjects = list.get(i);
                            avObjects.put("mileage", today);
                            avObjects.saveInBackground(saveCallback);
                        }
                    } else {
                        AVObject avObjects = new AVObject("TravelDataWithDay");
                        avObjects.put("mileage", today);
                        avObjects.put("travelDate", Tools.getCurrentDate());
                        avObjects.put("userId", AVUser.getCurrentUser());
                        avObjects.saveInBackground(saveCallback);
                    }
                }

            }
        });
    }

    /**
     * 上传每次出行
     *
     * @param mileage
     * @param startTime
     * @param endTime
     * @param useTime
     * @param saveCallback
     */
    public static void saveDataToServerWithOnce(final double mileage, final Date startTime, final Date endTime, final double useTime, final SaveCallback saveCallback) {

        AVQuery<AVObject> query = new AVQuery<>("TravelInfoOnce");
        query.whereEqualTo("userId", AVUser.getCurrentUser());
        query.whereEqualTo("travelDate", Tools.getCurrentDate());
        query.whereEqualTo("startTime", startTime);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            AVObject avObjects = list.get(i);
                            avObjects.put("mileage", mileage);
                            avObjects.put("startTime", startTime);
                            avObjects.put("endTime", endTime);
                            avObjects.put("useTime", useTime);
                            avObjects.saveInBackground(saveCallback);
                        }
                    } else {
                        AVObject avObjects = new AVObject("TravelInfoOnce");
                        avObjects.put("mileage", mileage);
                        avObjects.put("startTime", startTime);
                        avObjects.put("endTime", endTime);
                        avObjects.put("useTime", useTime);
                        avObjects.put("travelDate", Tools.getCurrentDate());
                        avObjects.put("userId", AVUser.getCurrentUser());
                        avObjects.saveInBackground(saveCallback);
                    }
                }

            }
        });
    }

    public static void getLastTime(FindCallback<AVObject> findCallback) {
        AVQuery<AVObject> query = new AVQuery<>("TravelInfoOnce");
        query.whereEqualTo("userId", AVUser.getCurrentUser())
                .orderByDescending("endTime")
                .findInBackground(findCallback);

    }

    public void getHealthStateDataFromServer(String sex, String age, FindCallback<AVObject> findCallback) {
        AVQuery<AVObject> query = AVQuery.getQuery(HEALTH_STATE);
        query.whereEqualTo("sex", sex);
        query.whereEqualTo("age", age);
        query.findInBackground(findCallback);
    }

    public static void getCalorFromServer(FindCallback<AVObject> findCallback) {
        AVQuery<AVObject> query = getBaseQuery(CALORIES);
        query.whereEqualTo("post", AVUser.getCurrentUser());
        query.findInBackground(findCallback);

    }

    public void sendFeedBackToServer(String content, Date date, String name, SaveCallback saveCallback) {
        AVObject avObject = new AVObject("FeedBack");
        avObject.put("userName", name);
        avObject.put("feedBackContent", content);
        avObject.put("date", date);
        avObject.put("user", AVUser.getCurrentUser());
        avObject.saveInBackground(saveCallback);

    }


    public void getMusicCollect(final FindCallback<AVObject> findCallback) {
        AVQuery<AVObject> query = new AVQuery<>(MUSIC_USER);
        query.whereEqualTo("post_user", AVUser.getCurrentUser());
        query.whereEqualTo("is_collected", "1");
        query.findInBackground(findCallback);
    }

    public void deleteMusicCollect(final String name, final FindCallback<AVObject> findCallback) {
        AVQuery<AVObject> query = new AVQuery<>("MusicPrefer");
        query.whereEqualTo("user", AVUser.getCurrentUser());
        query.whereEqualTo("musicName", name);
        query.findInBackground(findCallback);


    }

    public static void downMusicFromNet(Context context, MusicInfo info, ProgressCallback progressCallback) {

        final File file = DownFileUtils.creatFile(context, IContent.FILEM_USIC, info.musicName + ".mp3");

        if (Objects.requireNonNull(file).exists()) {
            file.delete();
        }

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        AVFile avFile = new AVFile(info.musicName + ".mp3", info.lrc, new HashMap<String, Object>());
        avFile.getDataInBackground(new GetDataCallback() {

            FileOutputStream fileOutputStream;
            BufferedOutputStream outputStream;

            @Override
            public void done(byte[] bytes, AVException e) {
                try {
                    fileOutputStream = new FileOutputStream(file);
                    outputStream = new BufferedOutputStream(fileOutputStream);
                    outputStream.write(bytes);
                    outputStream.flush();
                } catch (Exception e1) {
                    e1.printStackTrace();
                } finally {
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }


            }
        }, progressCallback);
    }

    public static void sendMusicDownLoad(final MusicInfo info, final SaveCallback saveCallback) {

        AVQuery<AVObject> query = new AVQuery<>("MusicDownLoad");
        query.whereEqualTo("user", AVUser.getCurrentUser());
        query.whereEqualTo("musicName", info.musicName);
        query.whereEqualTo("typeNumber", info.type);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    L.e("==============", "=============" + list.size());
                    if (list.size() > 0) {
                        AVObject avObject = list.get(0);
                        avObject.put("musicName", info.musicName);
                        avObject.put("typeNumber", info.type);
                        avObject.put("user", AVUser.getCurrentUser());
                        avObject.saveInBackground();

                    } else {
                        AVObject avObject = new AVObject("MusicDownLoad");
                        try {
                            AVObject avObject_info = AVObject.parseAVObject(info.avObject);
                            avObject.put("musicName", info.musicName);
                            avObject.put("typeNumber", info.type);
                            avObject.put("user", AVUser.getCurrentUser());
                            avObject.saveInBackground(saveCallback);
                        } catch (Exception e0) {
                            e0.printStackTrace();
                        }
                    }
                }

            }
        });
    }

    public void getInstruction(FindCallback<AVObject> findCallback) {
        AVQuery<AVObject> query = new AVQuery<>(INSTRUCTIONS);
        query.findInBackground(findCallback);


    }

    public void downUpDateOr(FindCallback<AVObject> findCallback) {
        AVQuery<AVObject> query = new AVQuery<>("DeviceUpdate");
        query.orderByDescending("createdAt");
        query.limit(1);
        query.findInBackground(findCallback);
    }


    public void getDeviceUser(FindCallback<AVObject> findCallback) {
        IContent.getInstacne().addressList.clear();
        AVQuery<AVObject> query = AVQuery.getQuery(DEVICE_UUID);
        query.whereEqualTo("post", AVUser.getCurrentUser());
        query.orderByDescending("updatedAt");
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.setMaxCacheAge(24 * 3600 * 7 * 1000);
        query.findInBackground(findCallback);

    }


    public void getUserProtect(FindCallback<AVObject> findCallback) {

        AVQuery<AVObject> query = new AVQuery<>(NetWorkRequest.USER_PROTOCOL);
        query.findInBackground(findCallback);

    }

    public void downUpdateFile(AVFile avFile, final File file, ProgressCallback progressCallback) {

        avFile.getDataInBackground(new GetDataCallback() {
            FileOutputStream fileOutputStream;
            BufferedOutputStream outputStream;

            @Override
            public void done(byte[] bytes, AVException e) {
                try {
                    fileOutputStream = new FileOutputStream(file);
                    outputStream = new BufferedOutputStream(fileOutputStream);
                    outputStream.write(bytes);
                    outputStream.flush();
                } catch (Exception e1) {
                    e1.printStackTrace();
                } finally {
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }


            }

        }, progressCallback);

    }

    /**
     * 优化统计接口
     */
    private static final String MUSIC_USER = "Music_relate_user";


    public void sendMusicDown(final String musicName, final SaveCallback saveCallback) {
        AVQuery<AVObject> query = new AVQuery<>(MUSIC_USER);
        query.whereEqualTo("post_user", AVUser.getCurrentUser());
        query.whereEqualTo("musicName", musicName);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        AVObject avObject = list.get(0);
                        avObject.put("is_down", "1");
                        avObject.saveInBackground(saveCallback);
                    } else {
                        AVObject avObject = new AVObject(MUSIC_USER);
                        avObject.put("musicName", musicName);
                        avObject.put("post_user", AVUser.getCurrentUser());
                        avObject.put("is_down", "1");
                        avObject.saveInBackground(saveCallback);
                    }
                }
            }
        });
    }

    public void sendMusicCollecting(final String musicName, final SaveCallback saveCallback) {

        AVQuery<AVObject> query = new AVQuery<>(MUSIC_USER);
        query.whereEqualTo("post_user", AVUser.getCurrentUser());
        query.whereEqualTo("musicName", musicName);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        AVObject avObject = list.get(0);
                        avObject.put("is_collected", "1");
                        avObject.saveInBackground(saveCallback);
                    } else {

                        AVObject avObject = new AVObject(MUSIC_USER);
                        avObject.put("musicName", musicName);
                        avObject.put("post_user", AVUser.getCurrentUser());
                        avObject.put("is_collected", "1");
                        avObject.saveInBackground(saveCallback);
                    }
                }
            }
        });
    }

    public void deleteMusicCollecting(final String musicName, final SaveCallback saveCallback) {
        AVQuery<AVObject> query = new AVQuery<>(MUSIC_USER);
        query.whereEqualTo("post_user", AVUser.getCurrentUser());
        query.whereEqualTo("musicName", musicName);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        AVObject avObject = list.get(0);
                        avObject.put("is_collected", "0");
                        avObject.saveInBackground(saveCallback);
                    }
                }
            }
        });
    }

    public static void setPlayCount(final String musicName, final int count) {
        AVQuery<AVObject> query = new AVQuery<>(MUSIC_USER);
        query.whereEqualTo("post_user", AVUser.getCurrentUser());
        query.whereEqualTo("musicName", musicName);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        AVObject avObject = list.get(0);
                        avObject.put("play_count", count);
                        avObject.saveInBackground();
                    } else {
                        AVObject avObject = new AVObject(MUSIC_USER);
                        avObject.put("musicName", musicName);
                        avObject.put("post_user", AVUser.getCurrentUser());
                        avObject.put("play_count", count);
                        avObject.saveInBackground();
                    }
                }
            }
        });
    }

    public static void getAPPVersion() {
        AVQuery<AVObject> query = new AVQuery<>("AndroidAPPVersion");
        query.orderByDescending("createdAt");
        query.limit(1);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null && list.size() > 0) {
                    IContent.getInstacne().newVersion = list.get(0).getString("version");
                }
            }
        });
    }

    //------------------------------ App -------------------------------------

    public static AVQuery<AVObject> getBaseQuery(String table) {
        AVQuery<AVObject> query = AVQuery.getQuery(table);
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.setMaxCacheAge(24 * 3600 * 7 * 1000);
        return query;
    }


    /**
     * 同步服务端数据
     */
    public void getTravelInfoSaveToDB() {

        final AVQuery<AVObject> query = getBaseQuery(TABLE_NAME);
        query.whereEqualTo("userId", AVUser.getCurrentUser());
        query.orderByAscending("travelDate");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException exception) {
                if (exception == null && list.size() > 0) {
                    String dateString;
                    String kilometer;
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
                    Date date;
                    for (AVObject object : list) {
                        date = object.getDate("travelDate");
                        dateString = dateFormat.format(date.getTime());
                        kilometer = object.getNumber("mileage").toString();
                        mOperateDBUtils.insertTravelToDB(dateString, kilometer);
                    }
                }
            }
        });
    }

    public void getFrequencyWeekToDB() {

        AVQuery<AVObject> query = getBaseQuery(TABLE_NAME_WEEK);
        query.whereEqualTo("userId", AVUser.getCurrentUser());
        query.orderByAscending("travelDate");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException exception) {
                if (exception == null) {
                    String dateString;
                    int frequency;
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
                    Date date;
                    for (AVObject object : list) {
                        date = object.getDate("travelDate");
                        dateString = dateFormat.format(date.getTime());
                        frequency = object.getNumber("frequency").intValue();
                        mOperateDBUtils.insertFrequencyToDB(OperateDBUtils.TABLE_FREQUENCY_WEEK_URI, dateString, frequency);
                    }
                }
            }
        });
    }

    public void getFrequencyMonthToDB() {

        AVQuery<AVObject> query = getBaseQuery(TABLE_NAME_MONTH);
        query.whereEqualTo("userId", AVUser.getCurrentUser());
        query.orderByAscending("travelDate");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException exception) {
                if (exception == null) {
                    String dateString;
                    int frequency;
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
                    Date date;
                    for (AVObject object : list) {
                        date = object.getDate("travelDate");
                        dateString = dateFormat.format(date.getTime());
                        frequency = object.getNumber("frequency").intValue();
                        mOperateDBUtils.insertFrequencyToDB(OperateDBUtils.TABLE_FREQUENCY_MONTH_URI, dateString, frequency);
                    }
                }
            }
        });
    }

}
