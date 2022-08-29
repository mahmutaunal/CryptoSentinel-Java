package com.mahmutalperenunal.aesalgorithmapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button sifrelemeButton;
    Button sifreCozmeButton;

    public static final String OLUSAN_SIFRE = "Şifreli Metin";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sifrelemeButton = findViewById(R.id.encryption_button);
        sifreCozmeButton = findViewById(R.id.sifreCozme_button);

        //switch to SifrelemeActivity
        sifrelemeButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SifrelemeActivity.class);
            startActivity(intent);
        });


        //switch to SifreCozmeActivity
        sifreCozmeButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SifreCozmeActivity.class);
            intent.putExtra(OLUSAN_SIFRE, "");
            startActivity(intent);
        });

    }
}