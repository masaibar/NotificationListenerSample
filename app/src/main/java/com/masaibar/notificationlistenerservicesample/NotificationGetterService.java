package com.masaibar.notificationlistenerservicesample;

import android.app.Notification;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;

public class NotificationGetterService extends NotificationListenerService {

    private static final String TARGET_PACKAGE = "com.whatsapp";
    private static boolean isNotificationAccessEnabled = false;

    public static boolean isNotificationAccessEnabledInstance() {
        return isNotificationAccessEnabled;
    }

    public static String getEnabledNotificationListeners(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        String rawListeners = Settings.Secure.getString(contentResolver,
                "enabled_notification_listeners");

        return TextUtils.isEmpty(rawListeners) ? "none" : rawListeners;
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
            String packageName = statusBarNotification.getPackageName();
            if (!TextUtils.equals(packageName, TARGET_PACKAGE)) {
                return;
            }

            Notification notification = statusBarNotification.getNotification();
            if (notification == null) {
                continue;
            }
            Bundle extra = notification.extras;
            if (extra == null) {
                continue;
            }

            Log.d("!!!", String.format("============ %s ============", packageName));
            Log.d("!!!",
                    String.format("[%s], value = %s (%s)",
                            packageName, "when", notification.when));
            for (String key : extra.keySet()) {
                Object value = extra.get(key);
                if (value == null) {
                    continue;
                }
                Log.d("!!!",
                        String.format("[%s], key = %s, value = %s (%s)",
                                packageName, key, value.toString(), value.getClass().getName()));
            }
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
}
