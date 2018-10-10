package com.gribanskij.miser.sql_base;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gribanskij.miser.R;
import com.gribanskij.miser.utils.TimeUtils;


/**
 * Created by SESA175711 on 22.07.2016.
 */
class MiserDbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "miserBase.db";
    private static final int DB_VERSION = 1;
    private Context context;

    public MiserDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateMyDatabase(db, 0, DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateMyDatabase(db, oldVersion, newVersion);
    }

    private void updateMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < 1) {
            db.execSQL("CREATE TABLE " + MiserContract.DataTable.NAME + "(" + MiserContract.DataTable.Cols._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    MiserContract.DataTable.Cols.TYPE + " INTEGER" + ", " +
                    MiserContract.DataTable.Cols.CATEGORY_ID + " INTEGER" + ", " +
                    MiserContract.DataTable.Cols.DESCRIPTION + " TEXT" + ", " +
                    MiserContract.DataTable.Cols.AMOUNT + " REAL" + ", " +
                    MiserContract.DataTable.Cols.DATE + " INTEGER" + ", " +
                    MiserContract.DataTable.Cols.RESERVE_1 + " REAL" + ", " +
                    MiserContract.DataTable.Cols.RESERVE_2 + " TEXT" + " )");

            db.execSQL("CREATE TABLE " + MiserContract.AccountTable.NAME + "(" + MiserContract.AccountTable.Cols._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    MiserContract.AccountTable.Cols.CATEGORY_ID + " INTEGER" + ", " +
                    MiserContract.AccountTable.Cols.ACCOUNT_AMOUNT + " REAL" + ", " +
                    MiserContract.AccountTable.Cols.RESERVE_1 + " REAL" + ", " +
                    MiserContract.AccountTable.Cols.RESERVE_2 + " TEXT" + " )");

            db.execSQL("CREATE TABLE " + MiserContract.CategoryTable.NAME + "(" + MiserContract.CategoryTable.Cols._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    MiserContract.CategoryTable.Cols.TYPE + " INTEGER" + ", " +
                    MiserContract.CategoryTable.Cols.CATEGORY_ID + " INTEGER" + ", " +
                    MiserContract.CategoryTable.Cols.CATEGORY_NAME + " TEXT" + ", " +
                    MiserContract.CategoryTable.Cols.SYSTEM_CURRENCY + " TEXT" + ", " +
                    MiserContract.CategoryTable.Cols.RESERVE_1 + " REAL" + ", " +
                    MiserContract.CategoryTable.Cols.RESERVE_2 + " TEXT" + " )");
            initDB(db);
        }
        if (oldVersion < 2) {

        }
    }

    //Fill category table and accounts table names by default (RUS/ENG)

    private void initDB(SQLiteDatabase db) {

        ContentValues contentValues = new ContentValues();
        String[] income_category = context.getResources().getStringArray(R.array.income_category);
        String[] cost_category = context.getResources().getStringArray(R.array.cost_category);
        String[] account = context.getResources().getStringArray(R.array.accounts);

        for (int i = 0; i < TimeUtils.MAX_ACCOUNTS; i++) {
            contentValues.put(MiserContract.AccountTable.Cols.CATEGORY_ID, i);
            contentValues.put(MiserContract.AccountTable.Cols.ACCOUNT_AMOUNT, TimeUtils.DEFAULT_SUM);

            db.insert(MiserContract.AccountTable.NAME, null, contentValues);
            contentValues.clear();
        }

        int a = 0;
        for (String str : cost_category) {
            contentValues.put(MiserContract.CategoryTable.Cols.TYPE, MiserContract.TYPE_COST);
            contentValues.put(MiserContract.CategoryTable.Cols.CATEGORY_ID, a++);
            contentValues.put(MiserContract.CategoryTable.Cols.CATEGORY_NAME, str);
            contentValues.put(MiserContract.CategoryTable.Cols.SYSTEM_CURRENCY, TimeUtils.DEFAULT_CURRENCY);

            db.insert(MiserContract.CategoryTable.NAME, null, contentValues);
            contentValues.clear();
        }

        a = 0;
        for (String str : income_category) {
            contentValues.put(MiserContract.CategoryTable.Cols.TYPE, MiserContract.TYPE_INCOM);
            contentValues.put(MiserContract.CategoryTable.Cols.CATEGORY_ID, a++);
            contentValues.put(MiserContract.CategoryTable.Cols.CATEGORY_NAME, str);
            contentValues.put(MiserContract.CategoryTable.Cols.SYSTEM_CURRENCY, TimeUtils.DEFAULT_CURRENCY);

            db.insert(MiserContract.CategoryTable.NAME, null, contentValues);
            contentValues.clear();
        }

        a = 0;
        for (String str : account) {
            contentValues.put(MiserContract.CategoryTable.Cols.TYPE, MiserContract.TYPE_ACCOUNTS);
            contentValues.put(MiserContract.CategoryTable.Cols.CATEGORY_ID, a++);
            contentValues.put(MiserContract.CategoryTable.Cols.CATEGORY_NAME, str);
            contentValues.put(MiserContract.CategoryTable.Cols.SYSTEM_CURRENCY, TimeUtils.DEFAULT_CURRENCY);

            db.insert(MiserContract.CategoryTable.NAME, null, contentValues);
            contentValues.clear();
        }
    }
}
