package com.gribanskij.miser.sql_base;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by SESA175711 on 01.06.2017.
 */

public class Provider extends ContentProvider {

    public static final String LOG_TAG = Provider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int DATA_SUM_URI = 100;
    private static final int DATA_ID = 110;
    private static final int DATA_CATEGORY_SUM_URI = 120;
    private static final int ACCOUNTS_URI = 200;
    private static final int ACCOUNTS_ID = 210;
    private static final int ACCOUNTS_CATEGORY_ID = 220;
    private static final int CATEGORIES_URI = 300;
    private static final int CATEGORIES_ID = 310;

    static {
        sUriMatcher.addURI(MiserContract.CONTENT_AUTHORITY, MiserContract.PATH_DATA, DATA_SUM_URI);
        sUriMatcher.addURI(MiserContract.CONTENT_AUTHORITY, MiserContract.PATH_DATA + "/#", DATA_ID);
        sUriMatcher.addURI(MiserContract.CONTENT_AUTHORITY, MiserContract.PATH_DATA + "/category", DATA_CATEGORY_SUM_URI);
        sUriMatcher.addURI(MiserContract.CONTENT_AUTHORITY, MiserContract.PATH_ACCOUNTS, ACCOUNTS_URI);
        sUriMatcher.addURI(MiserContract.CONTENT_AUTHORITY, MiserContract.PATH_ACCOUNTS + "/#", ACCOUNTS_ID);
        sUriMatcher.addURI(MiserContract.CONTENT_AUTHORITY, MiserContract.PATH_ACCOUNTS + "/category/#", ACCOUNTS_CATEGORY_ID);
        sUriMatcher.addURI(MiserContract.CONTENT_AUTHORITY, MiserContract.PATH_CATEGORIES, CATEGORIES_URI);
        sUriMatcher.addURI(MiserContract.CONTENT_AUTHORITY, MiserContract.PATH_CATEGORIES + "/#", CATEGORIES_ID);
    }

    private MiserDbHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new MiserDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteQueryBuilder queryBuilderb = new SQLiteQueryBuilder();
        Cursor cursor = null;
        String groupBy = null;
        String having = null;


        switch (sUriMatcher.match(uri)) {

            case DATA_SUM_URI:
                queryBuilderb.setTables(MiserContract.DataTable.NAME);
                Log.i(LOG_TAG, "DATA_URI - OK!");
                break;

            case DATA_ID:
                queryBuilderb.setTables(MiserContract.DataTable.NAME);
                Log.i(LOG_TAG, "DATA_ID_URI - OK!");
                break;

            case DATA_CATEGORY_SUM_URI:
                queryBuilderb.setTables(MiserContract.DataTable.NAME);
                groupBy = MiserContract.DataTable.Cols.CATEGORY_ID;
                Log.i(LOG_TAG, "DATA_CATEGORY_SUM_URI - OK!");
                break;

            case ACCOUNTS_URI:
                queryBuilderb.setTables(MiserContract.AccountTable.NAME);
                Log.i(LOG_TAG, "ACCOUNTS_URI - OK!");
                break;
            case ACCOUNTS_ID:
                queryBuilderb.setTables(MiserContract.AccountTable.NAME);

                break;
            case ACCOUNTS_CATEGORY_ID:
                queryBuilderb.setTables(MiserContract.AccountTable.NAME);

                break;
            case CATEGORIES_URI:
                queryBuilderb.setTables(MiserContract.CategoryTable.NAME);
                Log.i(LOG_TAG, "CATEGORIES_URI - OK!");
                break;
            case CATEGORIES_ID:
                queryBuilderb.setTables(MiserContract.CategoryTable.NAME);

                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }


        ContentResolver cr;
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        cursor = queryBuilderb.query(database, projection, selection, selectionArgs, groupBy, having, sortOrder);

        try {
            cr = getContext().getContentResolver();
        } catch (Exception ex) {
            Log.i(LOG_TAG, "Database query - notification - ERROR");
            cr = null;
        }

        if (cursor != null) {
            cursor.setNotificationUri(cr, uri);
            Log.i(LOG_TAG, "DATABASE query - OK");
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        long num = database.insert(MiserContract.DataTable.NAME, null, contentValues);
        if (num == 0) return null;

        ContentResolver cr;
        try {
            cr = getContext().getContentResolver();
        } catch (Exception ex) {
            Log.i(LOG_TAG, "Database insert - notifyChange - ERROR");
            return uri;
        }
        cr.notifyChange(MiserContract.DataTable.DATA_URI, null);
        cr.notifyChange(MiserContract.DataTable.DATA_CATEGORY_SUM_URI, null);
        return uri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int num = database.delete(MiserContract.DataTable.NAME, s, strings);
        ContentResolver cr;
        try {
            cr = getContext().getContentResolver();
        } catch (Exception ex) {
            Log.i(LOG_TAG, "Database delete - notifyChange - ERROR");
            return num;
        }
        if (num != 0) {
            cr.notifyChange(MiserContract.DataTable.DATA_URI, null);
            cr.notifyChange(MiserContract.DataTable.DATA_CATEGORY_SUM_URI, null);
        }
        return num;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {

        String table;
        Uri uri_group_sum = null;

        switch (sUriMatcher.match(uri)) {
            case ACCOUNTS_URI: {
                table = MiserContract.AccountTable.NAME;
                break;

            }
            case CATEGORIES_URI: {
                table = MiserContract.CategoryTable.NAME;
                break;
            }

            case DATA_SUM_URI: {
                table = MiserContract.DataTable.NAME;
                uri_group_sum = MiserContract.DataTable.DATA_URI.buildUpon().
                        appendPath(MiserContract.DataTable.Cols.CATEGORY_ID).build();
                break;
            }

            default: {
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
            }
        }
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        int count = database.update(table, contentValues, s, strings);

        ContentResolver cr;
        try {
            cr = getContext().getContentResolver();
        } catch (Exception ex) {
            Log.i(LOG_TAG, "Database update - notifyChange - ERROR");
            return count;
        }
        if (count != 0) {
            cr.notifyChange(uri, null);

            if (uri_group_sum != null)
                cr.notifyChange(MiserContract.DataTable.DATA_CATEGORY_SUM_URI, null);
        }

        return count;
    }
}
