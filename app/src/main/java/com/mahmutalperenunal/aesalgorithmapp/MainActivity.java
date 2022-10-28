package com.mahmutalperenunal.aesalgorithmapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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