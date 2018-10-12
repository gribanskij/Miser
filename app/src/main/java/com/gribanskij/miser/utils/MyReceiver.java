package com.gribanskij.miser.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.gribanskij.miser.R;


public class MyReceiver extends BroadcastReceiver {

    public static String ACTION_ADD_EXPENSES = "com.gribanskij.miser.ADD_EXPENSES";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_ADD_EXPENSES)) {
            NotificationUtils.remindUserAddExpenses(context);
        } else if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

            if (sharedPreferences.getBoolean(context.getResources().getString(R.string.pref_notification_key), false)) {
                NotificationUtils.setAlarm(context);
            }
        }
    }
}
