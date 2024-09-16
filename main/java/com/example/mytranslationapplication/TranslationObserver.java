package com.example.mytranslationapplication;

public interface TranslationObserver {
    void update(Object o);
    void disableView(Object o);
    void enableView();
    void showMessage(String errorMessage);
}
