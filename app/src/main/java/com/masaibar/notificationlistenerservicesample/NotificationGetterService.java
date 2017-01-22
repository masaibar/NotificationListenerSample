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

    private static boolean isNotificationAccessEnabled = false;
    private static String TARGET_PACKAGE = "com.whatsapp";

    public static boolean isIsNotificationAccessEnabled() {
        return isNotificationAccessEnabled;
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        handleNotification(sbn);
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


    private static final String KEY_TITLE = "android.title";
    private static final String KEY_BODY = "android.text";
    private static final String KEY_SUMMARY_TEXT = "android.summaryText";

    private void handleNotification(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();

        if (!TextUtils.equals(packageName, TARGET_PACKAGE)) {
            return;
        }

        long postedTime = sbn.getPostTime();
        String tag = sbn.getTag();

        Notification notification = sbn.getNotification();
        Bundle extra = notification.extras;

        if (TextUtils.isEmpty(tag)) {
            return;
        }

        if (!TextUtils.isEmpty(getStringByKey(extra, KEY_SUMMARY_TEXT))) {
            return;
        }

        String title = getStringByKey(extra, KEY_TITLE);
        String body = getStringByKey(extra, KEY_BODY);

        Log.d(
                "!!!",
                String.format(
                        "postedTime = %s, tag = %s, title = %s, body = %s",
                        postedTime, tag, title, body)
        );
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
