package com.example.aesalgorithm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sifre_cozme);

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
                e.printStackTrace();
            }
        });
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
}