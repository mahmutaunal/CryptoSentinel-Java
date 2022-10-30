package com.mahmutalperenunal.aesalgorithmapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class SifreCozmeActivity extends AppCompatActivity {

    EditText sifreliMetin, sifre;
    TextView sifresiCozulmusMetin;
    Button sifreCozBtn;

    String sifresiCozulmusMetinString;

    String AES = "AES";

    AdView adView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sifre_cozme);

        Toolbar toolbar = (Toolbar) findViewById(R.id.sifreCozmeToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //set admob banner
        MobileAds.initialize(this); {}
        AdRequest adRequest = new AdRequest.Builder().build();
        adView = findViewById(R.id.sifreCozme_adView);
        adView.loadAd(adRequest);

        Intent intent = getIntent();

        sifreliMetin = findViewById(R.id.sifreliMetin_editText);
        sifre = findViewById(R.id.sifre_editText);
        sifresiCozulmusMetin = findViewById(R.id.sifresiCozulmusMetin_text);
        sifreCozBtn = findViewById(R.id.sifreCoz_button);

        String sifreliMetinString = intent.getStringExtra(SifrelemeActivity.OLUSAN_SIFRE);
        sifreliMetin.setText(sifreliMetinString);


        //decode encrypted text
        sifreCozBtn.setOnClickListener(v -> {
            try {
                sifresiCozulmusMetinString = sifreCozme(sifreliMetin.getText().toString(), sifre.getText().toString());
                sifresiCozulmusMetin.setText(sifresiCozulmusMetinString);
            } catch (Exception e) {
                Toast.makeText(this, "Hatalı Giriş! Lütfen Tekrar Deneyin!", Toast.LENGTH_SHORT).show();
                sifre.setError("Hata!");
                sifre.setText("");
                sifresiCozulmusMetin.setText("");
                e.printStackTrace();
            }
        });

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    //decrypt with the generated key
    private String sifreCozme(String sifreliMetin, String sifre) throws Exception{
        SecretKeySpec anahtar = anahtarUret(sifre);
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.DECRYPT_MODE, anahtar);
        byte[] sifresiCozulmusDeger = Base64.decode(sifreliMetin, Base64.DEFAULT);
        byte[] sifreliDeger = cipher.doFinal(sifresiCozulmusDeger);
        return new String(sifreliDeger);
    }


    //generating a key with the password entered by the user
    private SecretKeySpec anahtarUret(String sifre) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = sifre.getBytes(StandardCharsets.UTF_8);
        digest.update(bytes, 0, bytes.length);
        byte[] anahtar = digest.digest();
        return new SecretKeySpec(anahtar, "AES");
    }


    //back to mainActivity
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

}