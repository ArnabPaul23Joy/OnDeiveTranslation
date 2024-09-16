package com.example.mytranslationapplication;


import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.languageid.LanguageIdentification;
import com.google.mlkit.nl.languageid.LanguageIdentifier;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.List;

public class TranslationHelper {
    private static TranslationHelper translateHelper;
    private static final String TAG = "TranslateHelper";
    private Translator translator;
    private String langCode;
    private DownloadConditions conditions;
    private List<TranslationObserver> observers = new ArrayList<>();
    public TranslationHelper(TranslationObserver observer){
        initializeTranslator(observer);
    }
    public static TranslationHelper getInstance(TranslationObserver observer){
        if(translateHelper == null)
            translateHelper = new TranslationHelper(observer);
        return translateHelper;
    }
    public static TranslationHelper getInstance(){
        return translateHelper;
    }

    private void initializeTranslator(TranslationObserver observer){
        addObserver(observer);
    }
    public void translateText(String text, String translateTo){
        try{
            closeTranslator();
            detectAndTranslateLanguage(text, translateTo);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
            for(TranslationObserver observer: observers)
                observer.showMessage(e.getMessage());
        }
    }
    public void closeTranslator(){
        if(translator != null)
            translator.close();
    }
    private void downloadTargetLanguage(String text, String translateTo){
        for(TranslationObserver observer: observers)
            observer.showMessage("Translating language");
        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(langCode)
                        .setTargetLanguage(getLanguageCode(translateTo))
                        .build();
        translator =
                Translation.getClient(options);
        conditions = new DownloadConditions.Builder()
                .build();
        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        Log.d(TAG, "downloadModelIfNeeded");
                        for(TranslationObserver observer: observers)
                            observer.showMessage("Translation model downloaded");
                        translateSourceLanguage(text);
                    }
                })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                for(TranslationObserver observer: observers){
                                    observer.showMessage(e.getMessage());
                                    observer.enableView();
                                }
                            }
                        });
    }
    private void translateSourceLanguage(String text){
        translator.translate(text)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        Log.d(TAG, "translation: " + o.toString());
                        for(TranslationObserver observer: observers)
                            observer.showMessage("Translated !!!!!!!");
                        notifyObservers(o);
                    }
                })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                for(TranslationObserver observer: observers){
                                    observer.showMessage(e.getMessage());
                                    observer.enableView();
                                }
                            }
                        });
    }
    private void addObserver(TranslationObserver observer){
        if(!observers.contains(observer))
            observers.add(observer);
    }

    private void removeObserver(TranslationObserver observer){
        if(observers.contains(observer))
            observers.remove(observer);
    }
    private void notifyObservers(Object o){
        for(TranslationObserver observer: observers){
            observer.update(o);
            observer.enableView();
        }
    }
    private String getLanguageCode(String translateTo){
        return TranslationConstants.LANGUAGE_CODES.get(TranslationConstants.LANGUAGE_NAMES.indexOf(translateTo));
    }
    private String detectAndTranslateLanguage(String text, String translateTo){
        for(TranslationObserver observer: observers)
            observer.showMessage("Detecting the language");
        LanguageIdentifier languageIdentifier =
                LanguageIdentification.getClient();
        languageIdentifier.identifyLanguage(text)
                .addOnSuccessListener(
                        new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(@Nullable String languageCode) {
                                langCode = languageCode;
                                downloadTargetLanguage(text, translateTo);
                                Log.d(TAG, "Language identified");
                                for(TranslationObserver observer: observers)
                                    observer.showMessage("Language identified");
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                for(TranslationObserver observer: observers){
                                    observer.showMessage(e.getMessage());
                                    observer.enableView();
                                }
                            }
                        });
        return langCode;
    }
}
class TranslationNotiThread extends Thread{
    private static TranslationNotiThread translationNotiThread;
    private Handler handler;
    private TranslationNotiThread(){}
    public static TranslationNotiThread getInstance(){
        if(translationNotiThread == null){
            translationNotiThread = new TranslationNotiThread();
            translationNotiThread.setPriority(Thread.MAX_PRIORITY);
            translationNotiThread.start();
        }
        return translationNotiThread;
    }
    public void passMessage(Runnable runnable){
        while(handler == null){}
        handler.post(runnable);
    }
    public void stopThread(){
        handler.getLooper().quit();
    }
    public void clearMessages(){
        while(handler == null){}
        handler.removeCallbacksAndMessages(this);
    }
    @Override
    public void run(){
        Looper.prepare();
        handler = new Handler(Looper.myLooper());
        Looper.loop();
    }
}

