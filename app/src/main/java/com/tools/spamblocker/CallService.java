package com.tools.spamblocker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class CallService extends Service {
    private static final String TAG = "CallService";
    private TelephonyManager telephonyManager;
    private TelecomManager telecomManager;

    @Override
    public void onCreate() {
        super.onCreate();
        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        telecomManager = (TelecomManager) getSystemService(TELECOM_SERVICE);

        // Create Notification Channel for foreground service
        NotificationChannel channel = new NotificationChannel(
                "CallServiceChannel",
                "Call Service",
                NotificationManager.IMPORTANCE_LOW
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }

        // Start the foreground service
        startForeground(1, createNotification());

        // Register the PhoneStateListener
        telephonyManager.listen(new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String phoneNumber) {
                super.onCallStateChanged(state, phoneNumber);
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        // Call is ringing
                        if (shouldBlockCall(phoneNumber)) {
                            Log.d(TAG, "Blocking call from: " + phoneNumber);
                            rejectCall(); // Reject the call
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        // Call ended or no call
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        // Call answered
                        break;
                }
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private boolean shouldBlockCall(String phoneNumber) {
//        Log.d(TAG, "Incoming phone number: " + phoneNumber);
        return phoneNumber.equals("08158017398"); // Example number
    }

    private void rejectCall() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        telecomManager.endCall();
        Log.d(TAG, "Call rejected");
    }

    private Notification createNotification() {
        return new Notification.Builder(this, "CallServiceChannel")
                .setContentTitle("Call Blocker Service")
                .setContentText("Listening for incoming calls...")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        telephonyManager.listen(null, PhoneStateListener.LISTEN_NONE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
