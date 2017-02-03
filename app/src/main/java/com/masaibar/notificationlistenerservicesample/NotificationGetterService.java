package com.masaibar.notificationlistenerservicesample;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;

public class NotificationGetterService extends NotificationListenerService {

    private static final String KEY_TITLE = "android.title";
    private static final String KEY_BODY = "android.text";
    private static final String KEY_SUMMARY_TEXT = "android.summaryText";
    private static boolean isNotificationAccessEnabled = false;

    public static boolean isIsNotificationAccessEnabled() {
        return isNotificationAccessEnabled;
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        try {
            super.onNotificationPosted(sbn);
        } catch (AbstractMethodError ignored) {

        }

        StatusBarNotification[] statusBarNotifications = getActiveNotifications();
        if (statusBarNotifications == null) {
            return;
        }

        for (StatusBarNotification statusBarNotification : statusBarNotifications) {
            Notification notification = statusBarNotification.getNotification();
            if (notification == null) {
                continue;
            }
            Bundle extra = notification.extras;
            if (extra == null) {
                continue;
            }

            Log.d("!!!",
                    String.format(
                            "packageName = %s, title = %s, body = %s",
                            statusBarNotification.getPackageName(),
                            getStringByKey(extra, KEY_TITLE),
                            getStringByKey(extra, KEY_BODY)
                    ));
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }

    @Override
    public IBinder onBind(Intent intent) {
        isNotificationAccessEnabled = true;
        Log.d("!!!", "onBind");
        return super.onBind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        isNotificationAccessEnabled = false;
        Log.d("!!!", "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onListenerConnected() {
        isNotificationAccessEnabled = true;
        Log.d("!!!", "onListenerConnected");
        super.onListenerConnected();
    }

    @Override
    public void onListenerDisconnected() {
        isNotificationAccessEnabled = false;
        Log.d("!!!", "onListenerDisconnected");
        super.onListenerDisconnected();
    }

    public String getStringByKey(Bundle bundle, String key) {
        if (bundle == null) {
            return null;
        }

        if (!bundle.containsKey(key)) {
            return null;
        }

        CharSequence c = bundle.getCharSequence(key);
        return TextUtils.isEmpty(c) ? null : c.toString();
    }
}
