package com.example.mytranslationapplication;

import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements TranslationObserver, AdapterView.OnItemSelectedListener {

    private AppBarConfiguration appBarConfiguration;
    private TextView textView;
    private EditText editText;
    private Button button;
    private Spinner spinner;
    private RelativeLayout progressBar;
    private LinearLayout translationLanguage;
    private String translateTo = "English";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progressBar);
        translationLanguage = findViewById(R.id.translation_language);
        textView = findViewById(R.id.textview_first);
        editText = findViewById(R.id.translate_text);
        button = findViewById(R.id.button_first);
        spinner = (Spinner) findViewById(R.id.planets_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.language_names,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TranslationHelper.getInstance().translateText(editText.getText().toString(), translateTo);
            }
        });
        TranslationHelper.getInstance(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        TranslationHelper.getInstance().closeTranslator();
    }
    @Override
    public void update(Object o) {
        textView.setText(o.toString());
    }
    @Override
    public void enableView() {
        progressBar.setVisibility(View.GONE);
        editText.setEnabled(true);
        button.setEnabled(true);
        spinner.setEnabled(true);
        translationLanguage.setAlpha(1);
    }
    @Override
    public void disableView(Object o) {
        editText.setEnabled(false);
        button.setEnabled(false);
        spinner.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        translationLanguage.setAlpha(0.3f);
    }
    @Override
    public void showMessage(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        translateTo = adapterView.getSelectedItem().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}
}