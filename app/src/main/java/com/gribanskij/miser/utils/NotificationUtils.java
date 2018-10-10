package com.gribanskij.miser.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.gribanskij.miser.R;
import com.gribanskij.miser.dashboard.DashboardActivity;


/**
 * Created by SESA175711 on 21.11.2017.
 */

public class NotificationUtils {


    private final static int MISER_REMINDER_NOTIFICATION_ID = 1139;
    private final static int MISER_REMINDEER_PENDING_INTENT_ID = 3418;
    private final static String MISER_REMINDER_NOTIFICATION_CHANNEL_ID = "reminder_notification_channel_miser";


    public static void clearAllNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }


    public static void remindUserAddExpenses(Context context) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(MISER_REMINDER_NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.main_notification_channel), NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                context, MISER_REMINDER_NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.mipmap.ic_launcher_miser)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(context.getString(R.string.addExpenses_notification_title))
                .setContentText(context.getString(R.string.addExpenses_notification_main_text))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString
                        (R.string.addExpenses_notification_main_text)))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }
        notificationManager.notify(MISER_REMINDER_NOTIFICATION_ID, notificationBuilder.build());
    }

    private static PendingIntent contentIntent(Context context) {
        Intent startActivityIntent = new Intent(context, DashboardActivity.class);
        return PendingIntent.getActivity(context, MISER_REMINDEER_PENDING_INTENT_ID,
                startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static Bitmap largeIcon(Context context) {
        Resources resource = context.getResources();
        return BitmapFactory.decodeResource(resource, R.mipmap.ic_launcher_miser);
    }
}
