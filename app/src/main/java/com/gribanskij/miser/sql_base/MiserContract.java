package com.gribanskij.miser.sql_base;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by santy on 23.07.2016.
 */
public final class MiserContract {
    public static final String CONTENT_AUTHORITY = "com.gribanskij.miser";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_DATA = "data";
    public static final String PATH_ACCOUNTS = "accounts";
    public static final String PATH_CATEGORIES = "category";
    public static final String PATH_DATA_ID = "data/#";
    public static final String PATH_DATA_SUM_CATEGORY_ID = "data/category";
    public static final String PATH_ACCOUNT_CATEGORY_ID = "accounts/category/#";
    public static final String PATH_ACCOUNTS_ID = "accounts/#";
    public static final String PATH_CATEGORIES_ID = "category/#";
    /**
     * Possible values for the TYPE of the DataTable and CategoryTable.
     */
    public static final int TYPE_COST = 0;
    public static final int TYPE_INCOM = 1;
    public static final int TYPE_ACCOUNTS = 2;

    private MiserContract() {
    }

    public static final class DataTable {
        /**
         * The content URI to access the data in the provider
         */
        public static final Uri DATA_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_DATA);
        public static final Uri DATA_ID_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_DATA_ID);
        public static final Uri DATA_CATEGORY_SUM_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_DATA_SUM_CATEGORY_ID);
        public static final String NAME = "data";


        private DataTable() {
        }

        public static class Cols implements BaseColumns {
            public static final String _ID = BaseColumns._ID;
            public static final String TYPE = "type";
            public static final String CATEGORY_ID = "category";
            public static final String DESCRIPTION = "description";
            public static final String AMOUNT = "amount";
            public static final String DATE = "date";
            public static final String RESERVE_1 = "add1";
            public static final String RESERVE_2 = "add2";
        }
    }

    public static final class AccountTable {
        /**
         * The content URI to access the data in the provider
         */
        public static final Uri ACCOUNTS_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ACCOUNTS);
        public static final Uri ACCOUNTS_ID_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ACCOUNTS_ID);
        public static final Uri ACCOUNTS_CATEGORY_ID_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ACCOUNT_CATEGORY_ID);
        public static final String NAME = "accounts";


        private AccountTable() {
        }

        public static class Cols implements BaseColumns {
            public static final String _ID = BaseColumns._ID;
            public static final String CATEGORY_ID = "id";
            public static final String ACCOUNT_AMOUNT = "amount";
            public static final String RESERVE_1 = "add1";
            public static final String RESERVE_2 = "add2";
        }
    }

    public static final class CategoryTable {
        /**
         * The content URI to access the data in the provider
         */
        public static final Uri CATEGORIES_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CATEGORIES);
        public static final Uri CATEGORIES_ID_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CATEGORIES_ID);
        public static final String NAME = "category";


        private CategoryTable() {
        }

        public static class Cols implements BaseColumns {
            public static final String _ID = BaseColumns._ID;
            public static final String TYPE = "type";
            public static final String CATEGORY_ID = "id";
            public static final String CATEGORY_NAME = "name";
            public static final String SYSTEM_CURRENCY = "currency";
            public static final String RESERVE_1 = "reserve1";
            public static final String RESERVE_2 = "reserve2";
        }
    }
}
