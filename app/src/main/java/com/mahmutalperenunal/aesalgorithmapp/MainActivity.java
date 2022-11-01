package com.mahmutalperenunal.aesalgorithmapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;

public class MainActivity extends AppCompatActivity {

    Button sifrelemeButton;
    Button sifreCozmeButton;

    AdView adView;

    AppUpdateManager appUpdateManager;

    ReviewManager reviewManager;
    ReviewInfo reviewInfo = null;

    public static final String OLUSAN_SIFRE = "Şifreli Metin";

    public static final int UPDATE_CODE = 22;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);

        //set admob banner
        MobileAds.initialize(this); {}
        AdRequest adRequest = new AdRequest.Builder().build();
        adView = findViewById(R.id.main_adView);
        adView.loadAd(adRequest);

        //set update manager
        checkUpdate();

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
                    .setTitle("Uygulama Teması")
                    .setMessage("Uygulama temasını seçiniz.")
                    .setPositiveButton("Açık", (dialogInterface, i) -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO))
                    .setNegativeButton("Koyu", (dialogInterface, i) -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES))
                    .setNeutralButton("Sistem Teması", (dialogInterface, i) -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM))
                    .show();

        } else if (item.getItemId() == R.id.review) {

            //start review manager
            activateReviewInfo();

        }

        return true;
    }


    //update manager
    public void checkUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(this);
        Task<AppUpdateInfo> task = appUpdateManager.getAppUpdateInfo();
        task.addOnSuccessListener(appUpdateInfo -> {

            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {

                try {
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE,
                            MainActivity.this, UPDATE_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                    Log.e("Update Error", e.toString());
                }

            }

        });

        appUpdateManager.registerListener(listener);

    }

    InstallStateUpdatedListener listener = installState -> {

        if (installState.installStatus() == InstallStatus.DOWNLOADED) {
            popUp();
        }

    };

    private void popUp() {

        Snackbar snackbar = Snackbar.make(
                findViewById(android.R.id.content),
                "App Update Almost Done.",
                Snackbar.LENGTH_INDEFINITE
                );

        snackbar.setAction("Roload", view -> appUpdateManager.completeUpdate());

        snackbar.setTextColor(Color.parseColor("#FF000"));
        snackbar.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UPDATE_CODE) {

            if (resultCode != RESULT_OK) {

            }

        }
    }


    //app review
    private void activateReviewInfo() {
        reviewManager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> request = reviewManager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                reviewInfo = task.getResult();
                startReviewFlow();
            }
        });
    }

    private void startReviewFlow() {
        Task<Void> flow = reviewManager.launchReviewFlow(this, reviewInfo);
        flow.addOnCompleteListener(task -> {
            // The flow has finished. The API does not indicate whether the user
            // reviewed or not, or even whether the review dialog was shown. Thus, no
            // matter the result, we continue our app flow.
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