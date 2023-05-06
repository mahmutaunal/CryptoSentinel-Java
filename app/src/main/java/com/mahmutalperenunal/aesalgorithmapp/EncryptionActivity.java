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
import androidx.appcompat.app.AlertDialog;
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

public class EncryptionActivity extends AppCompatActivity {

    EditText textToBeEncrypted, password;
    TextView encryptedText, passwordText;
    Button encryptBtn, shareButton;

    String encryptedTextString;

    String AES = "AES";

    AdView adView;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encryption);

        //set toolbar
        Toolbar toolbar = findViewById(R.id.encryptionToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //set admob banner
        MobileAds.initialize(this);
        {
        }
        AdRequest adRequest = new AdRequest.Builder().build();
        adView = findViewById(R.id.encryption_adView);
        adView.loadAd(adRequest);

        //initialize views
        textToBeEncrypted = findViewById(R.id.textToBeEncrypted_editText);
        password = findViewById(R.id.password_editText);
        encryptedText = findViewById(R.id.encryptedText_text);
        encryptBtn = findViewById(R.id.encrypt_button);
        shareButton = findViewById(R.id.share_button);
        passwordText = findViewById(R.id.password_text);

        if (savedInstanceState != null) {
            String savedEncryptedText = savedInstanceState.getString("encryptedText");
            String savedPassword = savedInstanceState.getString("password");

            encryptedText.setText(savedEncryptedText);
            passwordText.setText(savedPassword);
        }

        //encrypt button
        encryptBtn.setOnClickListener(v -> {
            if (password == null) {
                password.setError(getString(R.string.compulsory_text));
                Toast.makeText(this, R.string.enter_password_text, Toast.LENGTH_SHORT).show();
            } else {
                try {
                    encryptedTextString = encrypt(textToBeEncrypted.getText().toString(), password.getText().toString());
                    encryptedText.setText(encryptedTextString);
                    passwordText.setText(password.getText());
                    Toast.makeText(this, R.string.text_encrypted_text, Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    Toast.makeText(this, R.string.try_again_text, Toast.LENGTH_SHORT).show();
                    password.setError(getString(R.string.error_text));
                    textToBeEncrypted.setError(getString(R.string.error_text));
                    e.printStackTrace();
                }
            }
        });

        //share button
        shareButton.setOnClickListener(v -> new AlertDialog.Builder(this, R.style.CustomAlertDialog)
                .setTitle(R.string.share_text)
                .setMessage(R.string.share_message_text)
                .setPositiveButton(R.string.only_text, (dialogInterface, i) -> {

                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.ciphertext) + encryptedText.getText());
                    sendIntent.setType("text/plain");

                    Intent shareIntent = Intent.createChooser(sendIntent, null);
                    startActivity(shareIntent);

                })
                .setNegativeButton(R.string.only_password_text, (dialogInterface, i) -> {

                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.password_text) + passwordText.getText());
                    sendIntent.setType("text/plain");

                    Intent shareIntent = Intent.createChooser(sendIntent, null);
                    startActivity(shareIntent);

                })
                .setNeutralButton(R.string.text_and_password_text, (dialogInterface, i) -> {

                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.ciphertext) + encryptedText.getText() + "\n" + getString(R.string.password_text) + passwordText.getText());
                    sendIntent.setType("text/plain");

                    Intent shareIntent = Intent.createChooser(sendIntent, null);
                    startActivity(shareIntent);

                })
                .show());

    }


    //back button
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    //save encrypted text and password when screen rotates
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("encryptedText", String.valueOf(encryptedText.getText()));
        outState.putString("password", String.valueOf(passwordText.getText()));
        super.onSaveInstanceState(outState);
    }

    //decrypt with the generated key
    private String encrypt(String data, String password) throws Exception {
        SecretKeySpec key = keyProduct(password);
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedValue = cipher.doFinal(data.getBytes());
        return Base64.encodeToString(encryptedValue, Base64.DEFAULT);
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