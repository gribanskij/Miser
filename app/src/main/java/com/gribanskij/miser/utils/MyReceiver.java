package com.gribanskij.miser.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.gribanskij.miser.R;

import java.util.Calendar;

public class MyReceiver extends BroadcastReceiver {

    public static String ACTION_ADD_EXPENSES = "com.gribanskij.miser.ADD_EXPENSES";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_ADD_EXPENSES)) {
            NotificationUtils.remindUserAddExpenses(context);
        } else if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

            if (sharedPreferences.getBoolean(context.getResources().getString(R.string.pref_notification_key), true)) {

                Intent mIntent = new Intent();
                intent.setAction(MyReceiver.ACTION_ADD_EXPENSES);
                intent.setClass(context, MyReceiver.class);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                        179, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, 21);
                calendar.set(Calendar.MINUTE, 0);
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pendingIntent);

            }
        }
    }
}
