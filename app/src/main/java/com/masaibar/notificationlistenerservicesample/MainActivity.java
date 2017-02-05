package com.masaibar.notificationlistenerservicesample;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button_open_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNotificationAccessSettings(getApplicationContext());
            }
        });

        findViewById(R.id.button_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotification();
            }
        });

        findViewById(R.id.button_get_listeners).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView) findViewById(R.id.text_enabled_listeners))
                        .setText(NotificationGetterService.getEnabledNotificationListeners(getApplicationContext()));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((TextView) findViewById(R.id.text_status)).setText(
                NotificationGetterService.isNotificationAccessEnabledInstance() ?
                        "enabled" : "disabled"
        );
    }

    private void sendNotification() {
        Notification notification = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle("title")
                .setContentText("content")
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();

        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(0, notification);
    }

    public void openNotificationAccessSettings(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
