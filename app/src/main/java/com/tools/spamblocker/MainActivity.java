package com.tools.spamblocker;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.telecom.TelecomManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tools.spamblocker.services.CallForegroundService;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_CODE_PERMISSIONS = 100;
    private static final String TAG = "MainActivity";

    private EditText phoneNumberEditText;
    private SharedPreferences sharedPreferences;
    public static final String BLOCK_LIST_KEY = "block_list_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("CallBlockerPrefs", Context.MODE_PRIVATE);

        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        Button addNumberButton = findViewById(R.id.addNumberButton);
        Button startServiceButton = findViewById(R.id.startServiceButton);

        startServiceButton.setOnClickListener(v -> {
            startForegroundService(new Intent(this, CallService.class));
            ContextCompat.startForegroundService(this, new Intent(this, CallForegroundService.class));
            finishAndRemoveTask();
        });

        addNumberButton.setOnClickListener(v -> {
            String phoneNumber = phoneNumberEditText.getText().toString().trim();
            if (!phoneNumber.isEmpty()) {
                addNumberToBlockList(phoneNumber);
                Toast.makeText(MainActivity.this, "Number added to block list", Toast.LENGTH_SHORT).show();
                phoneNumberEditText.setText("");  // Clear the input field
            } else {
                Toast.makeText(MainActivity.this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            }
        });

        checkPermissions();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG}, PERMISSION_REQUEST_CODE);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    PERMISSION_REQUEST_CODE);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED) {
            // Request the permission if it hasn't been granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_CALL_LOG},
                    PERMISSION_REQUEST_CODE);
        }

        requestSetDefaultDialer(this);

        startForegroundService(new Intent(this, CallService.class));
        ContextCompat.startForegroundService(this, new Intent(this, CallForegroundService.class));
    }

    private void addNumberToBlockList(String phoneNumber) {
        Set<String> blockList = sharedPreferences.getStringSet(BLOCK_LIST_KEY, new HashSet<>());
        Set<String> updatedBlockList = new HashSet<>(blockList);
        updatedBlockList.add(phoneNumber);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(BLOCK_LIST_KEY, updatedBlockList);
        editor.apply();  // Apply changes asynchronously
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted
            } else {
                // Handle permission denial
            }
        }else if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission granted!");
            } else {
                Log.e(TAG, "Permission denied!");
                // Handle the case where the user denied the permission
            }
        }
    }

    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ANSWER_PHONE_CALLS}, REQUEST_CODE_PERMISSIONS);
        } else {
            // Permission is already granted
            Log.d(TAG, "Permission granted");
        }
    }

    public void requestSetDefaultDialer(Context context) {
        TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
        if (telecomManager != null) {
            Intent intent = new Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER);
            intent.putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, context.getPackageName());
            context.startActivity(intent);
        }
    }

}