package com.findtech.threePomelos.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import com.findtech.threePomelos.sdk.MyApplication;


/**
 * @author Administrator
 */
public class RequestUtils {


    public static String sharepreference = "com.findtech.threePomelos.register";
    public static String USERNAME = "username";
    public static String BABYSEX = "sex";
    public static String BIRTHDAY = "birthday";
    /**
     * user info
     */
    public static String WEIGHT = "weight";
    public static String HEIGHT = "height";
    public static String TOTALE_MILEAGE = "total_mileage";
    public static String TOTALE_DAY = "total_day";
    public static String TRAVEL_INFO = "travel_info";
    /**
     * bluetooth info
     */
    public static String DEVICE = "device_num";
    public static String FIRMWARE_VERSION = "FIRMWARE_VERSION";
    public static String CURRENT_ELECTRIC = "CURRENT_ELECTRIC";
    public static String TEMPERATURE = "TEMPERATURE";
    public static String RECEIVE_TEMPERATURE_ELECTRIC_ACTION = "android.receive.temperature.electric";


    public static String IS_LOGIN = "is_login";


    public static SharedPreferences.Editor getSharepreferenceEditor(Context context) {
        SharedPreferences.Editor sharedPreferencesEditor;
        if (context != null) {
            sharedPreferencesEditor = context.getSharedPreferences(sharepreference, Context.MODE_PRIVATE).edit();
        } else {
            sharedPreferencesEditor = MyApplication.getInstance().getApplicationContext().getSharedPreferences(sharepreference, Context.MODE_PRIVATE).edit();
        }
        return sharedPreferencesEditor;
    }

    public static SharedPreferences getSharepreference(Context context) {
        SharedPreferences sharedPreferences;
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(sharepreference, Context.MODE_PRIVATE);
        } else {
            sharedPreferences = MyApplication.getInstance().getApplicationContext().getSharedPreferences(sharepreference, Context.MODE_PRIVATE);
        }
        return sharedPreferences;
    }

    public interface MyItemClickListener {
        /**
         * 点击事件
         * @param view
         * @param position
         */
        void onItemClick(View view, int position);
    }


}
