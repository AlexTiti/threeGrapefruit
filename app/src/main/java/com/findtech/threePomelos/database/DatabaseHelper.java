package com.findtech.threePomelos.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;

import com.avos.avoscloud.AVUser;

import java.util.IdentityHashMap;

/**
 * 数据库
 *
 * @author Alex
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_WEIGHT_CREATE = "create table " + OperateDBUtils.TABLE_WEIGHT +
            " (" + OperateDBUtils.ID + " integer primary key autoincrement, " +
            OperateDBUtils.TIME + " text not null, " +
            OperateDBUtils.USER_ID + " text not null, " +
            OperateDBUtils.WEIGHT + " text not null);";

    private static final String DB_HEIGHT_CREATE = "create table " + OperateDBUtils.TABLE_HEIGHT +
            " (" + OperateDBUtils.ID + " integer primary key autoincrement, " +
            OperateDBUtils.TIME + " text not null, " +
            OperateDBUtils.USER_ID + " text not null, " +
            OperateDBUtils.HEIGHT + " text not null);";

    private static final String DB_BABY_INFO_CREATE = "create table " + OperateDBUtils.TABLE_BABY_INFO +
            " (" + OperateDBUtils.ID + " integer primary key autoincrement, " +
            OperateDBUtils.USER_ID + " text not null, " +
            OperateDBUtils.BABY_INFO_OBJECT_ID + " text, " +
            OperateDBUtils.BABYNAME + " text, " +
            OperateDBUtils.BABYSEX + " text, " +
            OperateDBUtils.BIRTHDAY + " text, " +
            OperateDBUtils.HEADIMG + " text, " +
            OperateDBUtils.IS_BIND + " integer, " +
            OperateDBUtils.BLUETOOTH_DEVICE_ID + " text, " +
            OperateDBUtils.BABYNATIVE + " text);";


    private static final String DB_TRAVEL_INFO_CREATE = "create table " + OperateDBUtils.TABLE_TRAVEL_INFO +
            " (" + OperateDBUtils.ID + " integer primary key autoincrement, " +
            OperateDBUtils.USER_ID + " text not null, " +
            OperateDBUtils.TIME + " text  not null, " +
            OperateDBUtils.TOTAL_MILEAGE + " text, " +
            OperateDBUtils.TODAY_MILEAGE + " text, " +
            OperateDBUtils.TOTAL_CALOR + " text, " +
            OperateDBUtils.TODAY_CALOR + " text, " +
            OperateDBUtils.ADULT_WEIGHT + " text, " +
            OperateDBUtils.BLUETOOTH_DEVICE_ID + " text, " +
            OperateDBUtils.AVERAGE_SPEED + " text);";


    /**
     * 新版运动数据库
     */
    private static final String DB_TRAVEL_CREATE = "create table " + OperateDBUtils.TABLE_TRAVEL_THREE +
            " (" + OperateDBUtils.ID + " integer primary key autoincrement, " +
            OperateDBUtils.DATE + " text not null, " +
            OperateDBUtils.USER_ID + " text not null, " +
            OperateDBUtils.MILEAGE + " text not null);";
    /**
     * 周频率数据库
     */
    private static final String DB_FREQUENCY_WEEK_CREATE = "create table " + OperateDBUtils.TABLE_FREQUENCY_WEEK +
            " (" + OperateDBUtils.ID + " integer primary key autoincrement, " +
            OperateDBUtils.DATE + " text not null, " +
            OperateDBUtils.USER_ID + " text not null, " +
            OperateDBUtils.FREQUENCY + " integer not null);";


    private static final String DB_TRAVEL_ONCE_CREATE = "create table " + OperateDBUtils.TABLE_TRAVEL_ONCE +
            " (" + OperateDBUtils.ID + " integer primary key autoincrement, " +
            OperateDBUtils.USER_ID + " text not null, " +
            OperateDBUtils.START_TIME + " text not null, " +
            OperateDBUtils.END_TIME + " text not null, " +
            OperateDBUtils.USE_TIME + " integer not null, " +
            OperateDBUtils.MILEAGE + " integer not null, " +
            OperateDBUtils.TRAVEL_DATE + " text not null);";

    /**
     * 月频率数据库
     */
    private static final String DB_FREQUENCY_WMONTH_CREATE = "create table " + OperateDBUtils.TABLE_FREQUENCY_MONTH +
            " (" + OperateDBUtils.ID + " integer primary key autoincrement, " +
            OperateDBUtils.DATE + " text not null, " +
            OperateDBUtils.USER_ID + " text not null, " +
            OperateDBUtils.FREQUENCY + " integer not null);";


    public DatabaseHelper(Context context) {
        super(context, OperateDBUtils.DATABASE_NAME, null, OperateDBUtils.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DB_BABY_INFO_CREATE);
        sqLiteDatabase.execSQL(DB_TRAVEL_INFO_CREATE);

        sqLiteDatabase.execSQL(DB_TRAVEL_CREATE);
        sqLiteDatabase.execSQL(DB_FREQUENCY_WEEK_CREATE);
        sqLiteDatabase.execSQL(DB_FREQUENCY_WMONTH_CREATE);
        sqLiteDatabase.execSQL(DB_TRAVEL_ONCE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (1 == oldVersion) {
            upgradeToVersion2(sqLiteDatabase);
            upgradeToVersion3(sqLiteDatabase);
            oldVersion = 3;
        }
        if (oldVersion == 2) {
            upgradeToVersion3(sqLiteDatabase);
            oldVersion = 3;
        }
        if (oldVersion != newVersion) {
            throw new IllegalStateException(
                    "error upgrading the database to version " + newVersion);
        }
    }

    private void upgradeToVersion3(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(DB_TRAVEL_CREATE);
        sqLiteDatabase.execSQL(DB_FREQUENCY_WEEK_CREATE);
        sqLiteDatabase.execSQL(DB_FREQUENCY_WMONTH_CREATE);
        sqLiteDatabase.execSQL(DB_TRAVEL_ONCE_CREATE);

        dropTable(DB_WEIGHT_CREATE);
        dropTable(DB_HEIGHT_CREATE);

        sqLiteDatabase.execSQL("ALTER TABLE " + OperateDBUtils.TABLE_TRAVEL_INFO
                + " ADD " + OperateDBUtils.TOTAL_CALOR + " text;");
        sqLiteDatabase.execSQL("ALTER TABLE " + OperateDBUtils.TABLE_TRAVEL_INFO
                + " ADD " + OperateDBUtils.TODAY_CALOR + " text;");
        sqLiteDatabase.execSQL("ALTER TABLE " + OperateDBUtils.TABLE_TRAVEL_INFO
                + " ADD " + OperateDBUtils.ADULT_WEIGHT + " text;");
        sqLiteDatabase.execSQL("ALTER TABLE " + OperateDBUtils.TABLE_TRAVEL_INFO
                + " ADD " + OperateDBUtils.BLUETOOTH_DEVICE_ID + " text;");

    }


    public boolean tabIsExist(String tabName) {
        boolean result = false;
        if (tabName == null) {
            return false;
        }
        SQLiteDatabase db;
        Cursor cursor;
        try {
            db = this.getReadableDatabase();
            String sql = "select count(*) as c from sqlite_master where type ='table' and name ='" + tabName.trim() + "' ";
            cursor = db.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count > 0) {
                    result = true;
                }
            }

        } catch (Exception e) {
        }
        return result;
    }

    public String dropTable(String tableName) {
        if (tableName == null) {
            return null;
        }
        SQLiteDatabase db = null;
        db = this.getReadableDatabase();
        String DROP_TABLE = "DROP TABLE IF EXISTS " + tableName;
        db.execSQL(DROP_TABLE);
        return tableName;
    }

    private void upgradeToVersion2(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE " + OperateDBUtils.TABLE_BABY_INFO
                + " ADD " + OperateDBUtils.BABYNATIVE + " text;");
    }
}