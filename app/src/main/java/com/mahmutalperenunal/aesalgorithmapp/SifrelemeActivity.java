package com.mahmutalperenunal.aesalgorithmapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
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

public class SifrelemeActivity extends AppCompatActivity {

    EditText sifrelenecekMetin, sifre;
    TextView sifreliMetin, sifreText;
    Button sifreleBtn, shareButton;

    String sifreliMetinString;

    String AES = "AES";

    AdView adView;

    public static final String OLUSAN_SIFRE = "Şifreli Metin";


    @SuppressLint("MissingInflatedId")
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
        shareButton = findViewById(R.id.share_button);
        sifreText = findViewById(R.id.sifre_text);


        //encrypt text
        sifreleBtn.setOnClickListener(v -> {
            try {
                sifreliMetinString = sifrele(sifrelenecekMetin.getText().toString(), sifre.getText().toString());
                sifreliMetin.setText(sifreliMetinString);
                sifreText.setText(sifre.getText());
                Toast.makeText(this, "Metin Şifrelendi!", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                Toast.makeText(this, "İşlem Başarısız! Lütfen Tekrar Deneyin!", Toast.LENGTH_SHORT).show();
                sifre.setError("Hata!");
                sifrelenecekMetin.setError("Hata!");
                e.printStackTrace();
            }
        });


        //share text to another device
        shareButton.setOnClickListener(v -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Şifreli Metin: " + sifreliMetin.getText() + "\nŞifre: " + sifreText.getText());
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
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