package com.tools.spamblocker.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tools.spamblocker.CallService;
import com.tools.spamblocker.services.CallForegroundService;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, CallForegroundService.class);
            context.startForegroundService(serviceIntent);
        }
    }
}
