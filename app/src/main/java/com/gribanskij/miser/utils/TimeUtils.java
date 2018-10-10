package com.gribanskij.miser.utils;

import java.util.Calendar;

/**
 * Created by sesa175711 on 31.10.2016.
 */
public class TimeUtils {

    public static final String DEFAULT_CURRENCY = "RUB";
    public static final float DEFAULT_SUM = 0;
    public static final int TYPE_COST = 0;
    public static final int TYPE_INCOM = 1;
    public static final int TYPE_ACCOUNTS = 2;
    public static final int MAX_COST_CATEGORIES = 14;
    public static final int MAX_INCOM_CATEGORIES = 4;
    public static final int MAX_ACCOUNTS = 4;
    public static final String TYPE = "type";
    public static final String DETAIL = "detail";
    public static final String DATE_FROM = "from";
    public static final String DATE_TO = "to";
    public static final String CATEGORY = "category";
    public static final String CURRENCY = "currency";
    public static final int DEFAULT_CATEGORY = 0;

    private final static long DURATION_DAY_MS = 86400000; //(24 * 60 * 60 * 1000);
    private final static long DURATION_WEEK_MS = 604800000;// 7 * DURATION_DAY_MS;
    private final static int[] CALENDAR_TIME_FIELDS = {Calendar.HOUR_OF_DAY, Calendar.HOUR, Calendar.AM_PM, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND};


    private TimeUtils() {
    }

    public static long getBegin_month() {
        Calendar mCalendar = Calendar.getInstance();
        setZeroTime(mCalendar);
        mCalendar.set(Calendar.DAY_OF_MONTH, 1);
        return mCalendar.getTimeInMillis();
    }

    public static long getEnd_month() {
        Calendar mCalendar = Calendar.getInstance();
        return getBegin_month() + (DURATION_DAY_MS * mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
    }

    public static long getBegin_week() {
        Calendar mCalendar = Calendar.getInstance();
        setZeroTime(mCalendar);
        if (mCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            mCalendar.add(Calendar.DAY_OF_MONTH, -6);
        } else {
            mCalendar.add(Calendar.DAY_OF_MONTH, -(mCalendar.get(Calendar.DAY_OF_WEEK) - 2));
        }
        return mCalendar.getTimeInMillis();
    }

    public static long getBegin_day() {
        Calendar mCalendar = Calendar.getInstance();
        setZeroTime(mCalendar);
        return mCalendar.getTimeInMillis();
    }

    public static long getEnd_week() {
        return getBegin_week() + DURATION_WEEK_MS;
    }

    public static long getEnd_day() {
        return getBegin_day() + DURATION_DAY_MS;
    }

    private static void setZeroTime(Calendar c) {
        for (int i : CALENDAR_TIME_FIELDS) {
            c.clear(i);
        }
    }
}
