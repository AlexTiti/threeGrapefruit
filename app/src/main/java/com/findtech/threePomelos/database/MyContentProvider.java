package com.findtech.threePomelos.database;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * @author zhi.zhang
 * @date 1/3/16
 */
public class MyContentProvider extends ContentProvider {

    private DatabaseHelper dbHelper = null;
    private ContentResolver resolver = null;
    private Context mContext;

    private static final int WEIGHT = 1;
    private static final int HEIGHT = 2;
    private static final int BABY_INFO = 3;
    private static final int TRAVEL_INFO = 4;

    private static final int TRAVEL_VERSION_THREE = 5;
    private static final int TRAVEL_FREQUENCY_WEEK = 6;
    private static final int TRAVEL_FREQUENCY_MONTH = 7;
    private static final UriMatcher sMatcher;

    static {
        sMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sMatcher.addURI(OperateDBUtils.AUTOHORITY, OperateDBUtils.TABLE_WEIGHT, WEIGHT);
        sMatcher.addURI(OperateDBUtils.AUTOHORITY, OperateDBUtils.TABLE_HEIGHT, HEIGHT);
        sMatcher.addURI(OperateDBUtils.AUTOHORITY, OperateDBUtils.TABLE_BABY_INFO, BABY_INFO);
        sMatcher.addURI(OperateDBUtils.AUTOHORITY, OperateDBUtils.TABLE_TRAVEL_INFO, TRAVEL_INFO);

        sMatcher.addURI(OperateDBUtils.AUTOHORITY, OperateDBUtils.TABLE_TRAVEL_THREE, TRAVEL_VERSION_THREE);
        sMatcher.addURI(OperateDBUtils.AUTOHORITY, OperateDBUtils.TABLE_FREQUENCY_WEEK, TRAVEL_FREQUENCY_WEEK);
        sMatcher.addURI(OperateDBUtils.AUTOHORITY, OperateDBUtils.TABLE_FREQUENCY_MONTH, TRAVEL_FREQUENCY_MONTH);
    }


    public MyContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int id;
        String table = getTable(uri);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        id = db.delete(table, selection, selectionArgs);
        if (id < 0) {
            throw new SQLiteException("Unable to delete " + selection + " for " + uri);
        }
        resolver.notifyChange(uri, null);
        return id;
    }

    /**
     * 获取操作的数据表
     *
     * @param uri
     * @return
     */
    private String getTable(Uri uri) {
        String table;
        switch (sMatcher.match(uri)) {
            case WEIGHT:
                table = OperateDBUtils.TABLE_WEIGHT;
                break;
            case HEIGHT:
                table = OperateDBUtils.TABLE_HEIGHT;
                break;
            case BABY_INFO:
                table = OperateDBUtils.TABLE_BABY_INFO;
                break;
            case TRAVEL_INFO:
                table = OperateDBUtils.TABLE_TRAVEL_INFO;
                break;
            case TRAVEL_VERSION_THREE:
                table = OperateDBUtils.TABLE_TRAVEL_THREE;
                break;
            case TRAVEL_FREQUENCY_WEEK:
                table = OperateDBUtils.TABLE_FREQUENCY_WEEK;
                break;
            case TRAVEL_FREQUENCY_MONTH:
                table = OperateDBUtils.TABLE_FREQUENCY_MONTH;
                break;
            default:
                throw new IllegalArgumentException("this is unkown uri:" + uri);
        }
        return table;
    }

    @Override
    public String getType(Uri uri) {

        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String table = getTable(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = db.insert(table, OperateDBUtils.ID, values);
        if (id < 0) {
            throw new SQLiteException("Unable to insert " + values + " for " + uri);
        }
        Uri newUri = ContentUris.withAppendedId(uri, id);
        resolver.notifyChange(newUri, null);
        return newUri;
    }

    @Override
    public boolean onCreate() {
        mContext = getContext();
        resolver = mContext.getContentResolver();
        dbHelper = new DatabaseHelper(mContext);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
        Cursor cursor = null;
        String table = getTable(uri);
        try {
            sqlBuilder.setTables(table);
            cursor = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder, null);
            cursor.setNotificationUri(resolver, uri);
        } catch (Exception e) {

        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String table = getTable(uri);

        int id = db.update(table, values, selection, selectionArgs);
        if (id < 0) {
            throw new SQLiteException("Unable to insert " + values + " for " + uri);
        }
        Uri wUri = ContentUris.withAppendedId(uri, id);
        resolver.notifyChange(wUri, null);
        return id;

//        switch (sMatcher.match(uri)) {
//            case WEIGHT:
//                id = db.update(OperateDBUtils.TABLE_WEIGHT, values, selection, selectionArgs);
//                if (id < 0) {
//                    throw new SQLiteException("Unable to insert " + values + " for " + uri);
//                }
//                Uri wUri = ContentUris.withAppendedId(uri, id);
//                resolver.notifyChange(wUri, null);
//                return id;
//            case HEIGHT:
//                id = db.update(OperateDBUtils.TABLE_HEIGHT, values, selection, selectionArgs);
//                if (id < 0) {
//                    throw new SQLiteException("Unable to insert " + values + " for " + uri);
//                }
//                Uri hUri = ContentUris.withAppendedId(uri, id);
//                resolver.notifyChange(hUri, null);
//                return id;
//            case BABY_INFO:
//                id = db.update(OperateDBUtils.TABLE_BABY_INFO, values, selection, selectionArgs);
//                if (id < 0) {
//                    throw new SQLiteException("Unable to insert " + values + " for " + uri);
//                }
//                Uri babyInfoUri = ContentUris.withAppendedId(uri, id);
//                resolver.notifyChange(babyInfoUri, null);
//                return id;
//            case TRAVEL_INFO:
//                id = db.update(OperateDBUtils.TABLE_TRAVEL_INFO, values, selection, selectionArgs);
//                if (id < 0) {
//                    throw new SQLiteException("Unable to insert " + values + " for " + uri);
//                }
//                Uri travelInfoUri = ContentUris.withAppendedId(uri, id);
//                resolver.notifyChange(travelInfoUri, null);
//                return id;
//            case TRAVEL_VERSION_THREE:
//                id = db.update(OperateDBUtils.TABLE_TRAVEL_THREE, values, selection, selectionArgs);
//                if (id < 0) {
//                    throw new SQLiteException("Unable to insert " + values + " for " + uri);
//                }
//                Uri travelUri = ContentUris.withAppendedId(uri, id);
//                resolver.notifyChange(travelUri, null);
//                return id;
//            default:
//                throw new IllegalArgumentException("this is unkown uri:" + uri);
//        }
    }

}
