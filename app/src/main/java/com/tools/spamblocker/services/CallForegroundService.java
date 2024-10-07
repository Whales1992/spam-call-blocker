package com.tools.spamblocker.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import com.tools.spamblocker.R;

public class CallForegroundService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(1, getNotification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification getNotification() {
        NotificationChannel channel = new NotificationChannel(
            "call_service_channel",
            "Call Service",
            NotificationManager.IMPORTANCE_DEFAULT
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);

        Notification.Builder notificationBuilder = new Notification.Builder(this, "call_service_channel")
            .setContentTitle("Spam Call Monitor")
            .setContentText("Managing spam calls in the background")
            .setSmallIcon(R.drawable.icons_8);  // Provide your own icon

        return notificationBuilder.build();
    }
}
