package com.mahmutalperenunal.aesalgorithmapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class DecodingActivity extends AppCompatActivity {

    EditText encryptedText, password;
    TextView DecryptedText;
    Button decryptPasswordBtn, shareButton;

    String DecryptedTextString;

    String AES = "AES";

    AdView adView;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decoding);

        //set toolbar
        Toolbar toolbar = findViewById(R.id.decodingToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //set admob banner
        MobileAds.initialize(this);
        {
        }
        AdRequest adRequest = new AdRequest.Builder().build();
        adView = findViewById(R.id.decoding_adView);
        adView.loadAd(adRequest);

        //initialize views
        encryptedText = findViewById(R.id.encryptedText_editText);
        password = findViewById(R.id.password_editText);
        DecryptedText = findViewById(R.id.decryptedText_text);
        decryptPasswordBtn = findViewById(R.id.decryptPassword_button);
        shareButton = findViewById(R.id.share_button);


        if (savedInstanceState != null) {
            String savedDecryptedText = savedInstanceState.getString("decryptedText");

            DecryptedText.setText(savedDecryptedText);
        }

        //decode encrypted text
        decryptPasswordBtn.setOnClickListener(v -> {
            try {
                DecryptedTextString = decoding(encryptedText.getText().toString(), password.getText().toString());
                DecryptedText.setText(DecryptedTextString);
                Toast.makeText(this, R.string.decrypted_text, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, R.string.try_again_text, Toast.LENGTH_SHORT).show();
                password.setError(getString(R.string.error_text));
                password.setText("");
                DecryptedText.setText("");
                e.printStackTrace();
            }
        });


        //share text to another device
        shareButton.setOnClickListener(v -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.decryptedText_text) + DecryptedText.getText());
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        });

    }


    //back to mainActivity
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    //save decrypted text when screen rotates
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("decryptedText", String.valueOf(DecryptedText.getText()));
        super.onSaveInstanceState(outState);
    }


    //decrypt with the generated key
    private String decoding(String encryptedText, String password) throws Exception {
        SecretKeySpec key = keyProduct(password);
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedValue = Base64.decode(encryptedText, Base64.DEFAULT);
        byte[] encryptedValue = cipher.doFinal(decryptedValue);
        return new String(encryptedValue);
    }


    //generating a key with the password entered by the user
    private SecretKeySpec keyProduct(String password) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes(StandardCharsets.UTF_8);
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        return new SecretKeySpec(key, "AES");
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