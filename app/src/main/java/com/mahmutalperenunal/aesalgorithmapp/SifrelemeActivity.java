package com.mahmutalperenunal.aesalgorithmapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class SifrelemeActivity extends AppCompatActivity {

    EditText sifrelenecekMetin, sifre;
    TextView sifreliMetin;
    Button sifreleBtn, sifreCozBtn;

    String sifreliMetinString;

    String AES = "AES";

    AdView adView;

    public static final String OLUSAN_SIFRE = "Şifreli Metin";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sifreleme);

        Toolbar toolbar = (Toolbar) findViewById(R.id.sifrelemeToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //set admob banner
        MobileAds.initialize(this); {}
        AdRequest adRequest = new AdRequest.Builder().build();
        adView = findViewById(R.id.sifreleme_adView);
        adView.loadAd(adRequest);

        sifrelenecekMetin = findViewById(R.id.sifrelenecekMetin_editText);
        sifre = findViewById(R.id.sifre_editText);
        sifreliMetin = findViewById(R.id.sifreliMetin_text);
        sifreleBtn = findViewById(R.id.sifrele_button);
        sifreCozBtn = findViewById(R.id.sifreCozme_button);


        //encrypt text
        sifreleBtn.setOnClickListener(v -> {
            try {
                sifreliMetinString = sifrele(sifrelenecekMetin.getText().toString(), sifre.getText().toString());
                sifreliMetin.setText(sifreliMetinString);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        //send encrypted text to SifreCozmeActivity
        sifreCozBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SifreCozmeActivity.class);
            intent.putExtra(OLUSAN_SIFRE, sifreliMetinString);
            startActivity(intent);
        });

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    //decrypt with the generated key
    private String sifrele(String Veri, String sifre) throws Exception {
        SecretKeySpec anahtar = anahtarUret(sifre);
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.ENCRYPT_MODE, anahtar);
        byte[] sifreliDeger = cipher.doFinal(Veri.getBytes());
        return Base64.encodeToString(sifreliDeger, Base64.DEFAULT);
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