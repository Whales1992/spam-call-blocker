package com.tools.spamblocker.services;

import android.telecom.Call;
import android.telecom.CallScreeningService;
import androidx.annotation.NonNull;

public class CallService extends CallScreeningService {
    private static final String TAG = "CallService";

    @Override
    public void onScreenCall(@NonNull Call.Details callDetails) {
    }
}
