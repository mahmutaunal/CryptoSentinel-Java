package com.example.aesalgorithm;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button sifrelemeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sifrelemeButton = findViewById(R.id.encryption_button);

        //switch to SifrelemeActivity
        sifrelemeButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SifrelemeActivity.class);
            startActivity(intent);
        });
    }
}