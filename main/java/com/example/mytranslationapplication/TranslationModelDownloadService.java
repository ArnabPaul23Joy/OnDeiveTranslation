package com.example.mytranslationapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

public class TranslationModelDownloadService extends Service {
    private static final String TAG = "TranslationModelDownloadService";
    public TranslationModelDownloadService() {}
    private int totalCount = 0;
    private Translator translator;
    private NotificationManagerCompat notificationManager;
    private NotificationCompat.Builder builder;
    private Notification notification;
    private static boolean isAlive = false;
    @Override
    public void onDestroy() {
        isAlive = false;
        if(translator != null)
            translator.close();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!isAlive)
            downloadModels();
        isAlive = true;
        return START_STICKY;
    }
    private void downloadModels(){
        showNotification();
        for (String srcCode : TranslationConstants.LANGUAGE_CODES){
            for (String targetCode : TranslationConstants.LANGUAGE_CODES){
                if(!srcCode.equals(targetCode))
                    downloadLanguageModel(srcCode, targetCode);
            }
        }
    }
    private void showNotification(){
        notificationManager = NotificationManagerCompat.from(this);

        NotificationChannel chan = new NotificationChannel(
                getApplicationContext().getPackageName(),
                "Translation Foreground Service",
                NotificationManager.IMPORTANCE_LOW);
        notificationManager.createNotificationChannel(chan);

        builder = new NotificationCompat.Builder(this, getApplicationContext().getPackageName());
        builder.setContentTitle("Language Model Download")
                .setContentText("Download in progress")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .setProgress(100, 0, false)
                .setOnlyAlertOnce(true);
        notification = builder.build();
        notificationManager.notify(123126543, notification);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            startForeground(123126543, notification);
        } else {
            startForeground(123126543, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
        }
    }
    private void downloadLanguageModel(String langCodeSource, String langCodeTarget){
        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(langCodeSource)
                        .setTargetLanguage(langCodeTarget)
                        .build();
        translator =
                Translation.getClient(options);
        DownloadConditions conditions = new DownloadConditions.Builder()
                .build();
        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        totalCount++;
                        int tempCount = (totalCount * 100)/((TranslationConstants.LANGUAGE_CODES.size() * TranslationConstants.LANGUAGE_CODES.size()) - TranslationConstants.LANGUAGE_CODES.size());
                        updateNotification(tempCount);
                        Log.d(TAG, "onSuccess  " + totalCount);
                        if(langCodeSource.equals(TranslationConstants.LANGUAGE_CODES.get(TranslationConstants.LANGUAGE_CODES.size()-1)) && langCodeTarget.equals(TranslationConstants.LANGUAGE_CODES.get(TranslationConstants.LANGUAGE_CODES.size()-2))){
                            updateNotification(100);
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()); // getActivity() for Fragment
                            Boolean statusLocked = prefs.edit().putBoolean("modelDownload", true).commit();
                            Intent intent = new Intent(TranslationModelDownloadService.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            getApplicationContext().startActivity(intent);
                            stopSelf();
                        }
                        translator.close();
                    }
                })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure");
                                if(langCodeSource.equals(TranslationConstants.LANGUAGE_CODES.get(TranslationConstants.LANGUAGE_CODES.size()-1)) && langCodeTarget.equals(TranslationConstants.LANGUAGE_CODES.get(TranslationConstants.LANGUAGE_CODES.size()-2))){
                                    updateNotification(100);
                                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()); // getActivity() for Fragment
                                    Boolean statusLocked = prefs.edit().putBoolean("modelDownload", true).commit();
                                    Intent intent = new Intent(TranslationModelDownloadService.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    getApplicationContext().startActivity(intent);
                                    stopSelf();
                                }
                                translator.close();
                            }
                        });
    }
    private void updateNotification(int progress){
        TranslationNotiThread.getInstance().clearMessages();
        if(progress == 100) {
            builder.setContentText("Download Completed")
                    .setOngoing(false)
                    .setProgress(0, 0, false);
            notificationManager.notify(123126543, builder.build());
            TranslationNotiThread.getInstance().stopThread();
            return;
        }
        TranslationNotiThread.getInstance().passMessage(new Runnable() {
            @Override
            public void run() {
                builder.setProgress(100 ,progress ,false);
                notificationManager.notify(123126543, builder.build());
            }
        });
    }
}