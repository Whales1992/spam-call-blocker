package com.tools.spamblocker.services;

import static com.tools.spamblocker.MainActivity.BLOCK_LIST_KEY;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.provider.CallLog;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import java.util.HashSet;
import java.util.Set;

public class PhoneCallReceiver extends BroadcastReceiver {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
        SharedPreferences sharedPreferences = context.getSharedPreferences("CallBlockerPrefs", Context.MODE_PRIVATE);
        Set<String> blockList = sharedPreferences.getStringSet(BLOCK_LIST_KEY, new HashSet<>());

        if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {

            if(incomingNumber!=null){
                if(incomingNumber.startsWith("020") || incomingNumber.startsWith("0700") ||
                        incomingNumber.startsWith("7080642") ||
                        incomingNumber.startsWith("07080642") ||
                        incomingNumber.startsWith("+23407080642") ||
                        incomingNumber.startsWith("23407080642")){
                    endCall(incomingNumber, context);
                }else{
                    for (String blackListedPhone:blockList) {
                        if(incomingNumber.equals(blackListedPhone)){
                            endCall(incomingNumber, context);
                        }
                    }
                }
            }
        } else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)) {
//            Log.d(TAG, "Call answered");
            // Handle call answered logic here
        } else if (TelephonyManager.EXTRA_STATE_IDLE.equals(state)) {
//            Log.d(TAG, "Call ended");
            // Handle call ended logic here
        }
    }

    @SuppressLint("MissingPermission")
    public void endCall(String incomingNumber, Context cx) {
        TelecomManager telecomManager = (TelecomManager) cx.getSystemService(Context.TELECOM_SERVICE);
        telecomManager.endCall(); // Call can only be ended if your app is the default phone app
        new Handler().postDelayed(() -> {
            // Code to execute after the delay
            deleteCallLogByNumber(cx, incomingNumber);
        }, 2000);
    }

    public void deleteCallLogByNumber(Context context, String phoneNumber) {
        ContentResolver contentResolver = context.getContentResolver();

        String where = CallLog.Calls.NUMBER + " = ?";
        String[] params = new String[]{phoneNumber};

        // Delete the call log entry
        int rowsDeleted = contentResolver.delete(CallLog.Calls.CONTENT_URI, where, params);
        if (rowsDeleted > 0) {
            Log.d("DeleteCallLog", "Call log entry deleted successfully.");
        } else {
            Log.d("DeleteCallLog", "No call log entry found with that number."+phoneNumber);
        }
    }

}
