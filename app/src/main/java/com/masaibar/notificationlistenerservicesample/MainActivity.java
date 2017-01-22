package com.masaibar.notificationlistenerservicesample;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((TextView) findViewById(R.id.text_status)).setText(
                NotificationGetterService.isIsNotificationAccessEnabled() ?
                        "enabled" : "disabled"
        );
    }

    public void openNotificationAccessSettings(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
