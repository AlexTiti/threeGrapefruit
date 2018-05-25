package com.findtech.threePomelos.sdk;

import android.app.Activity;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.bluetooth.BlueManager;
import com.findtech.threePomelos.entity.PersonDataEntity;
import com.findtech.threePomelos.music.utils.IConstants;
import com.findtech.threePomelos.music.utils.PreferencesUtility;
import com.findtech.threePomelos.musicserver.info.PlaylistInfo;
import com.findtech.threePomelos.sdk.manger.GlobalApplication;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Administrator
 */
public class MyApplication extends GlobalApplication {

    public static Context mContext;
    public BlueManager manager = null;
    private List<Activity> activitys = null;
    private static MyApplication instance;
    private static final String APPID = "1R2oS0W0U9dJGveftbxOHGy3-gzGzoHsz";
    private static final String APPKEY = "Pf2Gper3lCPI0neKo0EKWdLN";

    private static final String APPID_TEST = "prnPRaln6v5xwNqUkQu5sFUA-gzGzoHsz";
    private static final String APPKEY_TEST = "zQodN3qR7OOVjizMfqTI3LXE";
    public static final boolean DEBUG = false;
    public static String passwordStr;
    private ArrayList<PersonDataEntity> timeHeightDataArray = new ArrayList<>();
    private ArrayList<PersonDataEntity> timeWeightDataArray = new ArrayList<>();
    private long favPlaylist = IConstants.FAV_PLAYLIST;

    public MyApplication() {
        activitys = new LinkedList<>();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AVOSCloud.initialize(this, APPID_TEST, APPKEY_TEST);
        AVOSCloud.setDebugLogEnabled(true);
        manager = new BlueManager(getApplicationContext());
        mContext = this;
        if (!PreferencesUtility.getInstance(this).getFavriateMusicPlaylist()) {
            PlaylistInfo.getInstance(this).addPlaylist(favPlaylist, getResources().getString(R.string.baby_like),
                    0, "res:/" + null, "local");
            PreferencesUtility.getInstance(this).setFavriateMusicPlaylist(true);
        }
        MultiDex.install(this);

    }

    private static Gson gson;

    public static Gson gsonInstance() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static void setPassword(String passwordStr) {
        MyApplication.passwordStr = passwordStr;
    }

    public static String getPassword() {
        return passwordStr;
    }

    public static MyApplication getInstance() {
        if (null == instance) {
            synchronized (MyApplication.class) {
                if (null == instance) {
                    instance = new MyApplication();
                }
            }
        }
        return instance;
    }

    public void exit() {
        if (activitys != null && activitys.size() > 0) {
            for (Activity activity : activitys) {
                activity.finish();
            }
        }
    }

    public void setUserHeightData(ArrayList<PersonDataEntity> arrayList) {
        timeHeightDataArray = arrayList;
    }

    public ArrayList<PersonDataEntity> getUserHeightData() {
        return timeHeightDataArray;
    }

    public void setUserWeightData(ArrayList<PersonDataEntity> arrayList) {
        timeWeightDataArray = arrayList;
    }

    public ArrayList<PersonDataEntity> getUserWeightData() {
        return timeWeightDataArray;
    }

    public String getHeadIconPath() {
        String path = "default_head_icon.png";
        if (AVUser.getCurrentUser().getObjectId() != null) {
            path = AVUser.getCurrentUser().getObjectId() + "_head_icon.png";
        }
        return path;
    }

    public String getBabyHeadIconPath(String objectId) {
        String path = "default_baby_icon.png";
        if (objectId != null) {
            path = objectId + "baby_head_icon.png";
        }
        return path;
    }

}
