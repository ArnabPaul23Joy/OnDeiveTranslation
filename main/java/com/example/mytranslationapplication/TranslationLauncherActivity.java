package com.example.mytranslationapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import android.preference.PreferenceManager;

public class TranslationLauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this); // getActivity() for Fragment
        Boolean yourLocked = prefs.getBoolean("modelDownload", false);
        if(yourLocked){
            Intent intent = new Intent(TranslationLauncherActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return;
        }
        setContentView(R.layout.activity_translation_launcher);
        requestPermissions(new String[]{"android.permission.INTERNET", "android.permission.POST_NOTIFICATIONS", "android.permission.read_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.FOREGROUND_SERVICE", "android.permission.FOREGROUND_SERVICE_DATA_SYNC"}, 5403);
        startForegroundService(new Intent(TranslationLauncherActivity.this, TranslationModelDownloadService.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}