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
    private static long sPrevWhen;

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
                continue;
            }

            Notification notification = statusBarNotification.getNotification();
            if (notification == null) {
                continue;
            }
            Bundle extras = notification.extras;
            if (extras == null) {
                continue;
            }


            //summaryチェック
            CharSequence charSummary = extras.getCharSequence(Notification.EXTRA_SUMMARY_TEXT);
            if (TextUtils.isEmpty(charSummary)) {
                continue;
            }

            if (TextUtils.isEmpty(charSummary.toString())) {
                continue;
            }

            //whenが新しいか
            if (statusBarNotification.getNotification().when <= sPrevWhen) {
                continue;
            }

            printDebug(statusBarNotification);
            printLast(statusBarNotification);
            sPrevWhen = notification.when;
        }
    }

    private void printLast(StatusBarNotification sbn) {
        Notification notification = sbn.getNotification();
        Bundle extras = notification.extras;
        long when = notification.when;

        CharSequence[] textLines = extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES);
        if (textLines == null || textLines.length < 1) {
            //single pattern
            if (isGroupTalk()) {

            } else {
                Log.d("!!!!!",
                        String.format(
                                "[Get from Single] (%s) title = %s, content = %s",
                                when,
                                extras.getCharSequence(Notification.EXTRA_TITLE),
                                extras.getCharSequence(Notification.EXTRA_TEXT)
                        ));
            }
        } else {
            //multiple pattern
            String content = textLines[textLines.length - 1].toString();
            String title = extras.getCharSequence(Notification.EXTRA_TITLE).toString();
            String user = getUser(content);
            String group = getGroup(content);

            if (TextUtils.isEmpty(group) && TextUtils.equals(title, "WhatsApp")) {
                //個人
                Log.d("!!!!!",
                        String.format(
                                "[Get from Multiple] (%s) title = %s, content = %s",
                                when,
                                user == null ? extras.getCharSequence(Notification.EXTRA_TITLE) : user,
                                getText(content)
                        ));
            } else {
                //グループ
                Log.d("!!!!!",
                        String.format(
                                "[Get from Multiple] (%s) title = %s, content = %s",
                                when,
                                group == null ? title : group,
                                user == null ? extras.getCharSequence(Notification.EXTRA_TITLE) : user + ": " +getText(content)
                        ));
            }
        }
    }

    private boolean isGroupTalk() {
        return false;
    }

    private static final String SPLITTER_USER = ": ";
    private static final String SPLITTER_GROUP = " @ ";

    private String getUser(String content) {
        int splitterIndex = content.indexOf(SPLITTER_USER);
        if (splitterIndex < 0) {
            return null;
        }

        String user = content.substring(0, splitterIndex);
        String group = getGroupFromUser(user);

        return group == null ? user : user.replaceAll(group, "");
    }

    private String getGroupFromUser(String content) {
        int splitterIndex = content.indexOf(SPLITTER_GROUP);
        if (splitterIndex < 0) {
            return null;
        }

        return content.substring(splitterIndex + 1, content.length());
    }

    private String getGroup(String content) {
        int userSplitterIndex = content.indexOf(SPLITTER_USER);
        if (userSplitterIndex < 0) {
            return null;
        }

        String user = content.substring(0, userSplitterIndex);
        int groupSplitterIndex = user.indexOf(SPLITTER_GROUP);
        if (groupSplitterIndex < 0) {
            return null;
        }

        return user.substring(groupSplitterIndex + SPLITTER_GROUP.length(), user.length());
    }

    private String getText(String content) {
        int splitterIndex = content.indexOf(SPLITTER_USER);
        if (splitterIndex < 0) {
            return content;
        }

        return content.substring(splitterIndex + 1, content.length());
    }

    private void printDebug(StatusBarNotification sbn) {
        Notification notification = sbn.getNotification();
        String packageName = sbn.getPackageName();
        Bundle extras = notification.extras;

        Log.d("!!!", String.format("============ %s ============", packageName));
        Log.d("!!!",
                String.format("[%s], value = %s (%s)",
                        packageName, "when", notification.when));
        for (String key : extras.keySet()) {
            Object value = extras.get(key);
            if (value == null) {
                continue;
            }
            Log.d("!!!",
                    String.format("[%s], key = %s, value = %s (%s)",
                            packageName, key, value.toString(), value.getClass().getName()));
            if (TextUtils.equals(key, Notification.EXTRA_TEXT_LINES)) {
                int index = 0;
                for (CharSequence charSequence : extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES)) {
                    Log.d("!!!",
                            String.format("[TextLines], key = %s, value = %s",
                                    index, charSequence.toString()));
                    index++;
                }
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
