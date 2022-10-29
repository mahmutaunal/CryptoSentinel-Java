package com.mahmutalperenunal.aesalgorithmapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button sifrelemeButton;
    Button sifreCozmeButton;

    public static final String OLUSAN_SIFRE = "Şifreli Metin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);

        sifrelemeButton = findViewById(R.id.encryption_button);
        sifreCozmeButton = findViewById(R.id.sifreCozme_button);

        //switch to SifrelemeActivity
        sifrelemeButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SifrelemeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        });


        //switch to SifreCozmeActivity
        sifreCozmeButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SifreCozmeActivity.class);
            intent.putExtra(OLUSAN_SIFRE, "");
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        });

    }


    //create toolbar menus
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    //change app theme
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.tema) {
            new AlertDialog.Builder(this, R.style.CustomAlertDialog)
                    .setTitle("Uygulama Tema")
                    .setMessage("Uygulama temasını seçiniz.")
                    .setPositiveButton("Açık", (dialogInterface, i) -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO))
                    .setNegativeButton("Koyu", (dialogInterface, i) -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES))
                    .setNeutralButton("Sistem Teması", (dialogInterface, i) -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM))
                    .show();
        }
        return true;
    }


    //exit app on back button pressed
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}