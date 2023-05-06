package com.mahmutalperenunal.aesalgorithmapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
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

    Button encryptionButton;
    Button decodingButton;

    AdView adView;

    AppUpdateManager appUpdateManager;

    ReviewManager reviewManager;
    ReviewInfo reviewInfo = null;

    public static final int UPDATE_CODE = 22;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set toolbar
        Toolbar toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);

        //set admob banner
        MobileAds.initialize(this); {}
        AdRequest adRequest = new AdRequest.Builder().build();
        adView = findViewById(R.id.main_adView);
        adView.loadAd(adRequest);

        //set update manager
        checkUpdate();

        //set theme
        checkLastSelectedTheme();

        encryptionButton = findViewById(R.id.encryption_button);
        decodingButton = findViewById(R.id.decoding_button);

        //switch to EncryptionActivity
        encryptionButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), EncryptionActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        });


        //switch to DecodingActivity
        decodingButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), DecodingActivity.class);
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
        if (item.getItemId() == R.id.theme) {

            new AlertDialog.Builder(this, R.style.CustomAlertDialog)
                    .setTitle(R.string.theme_text)
                    .setMessage(R.string.choose_theme_text)
                    .setPositiveButton(R.string.light_text , (dialogInterface, i) -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                        //save theme
                        SharedPreferences sharedPreferences = getSharedPreferences("theme", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("theme", "light");
                        editor.apply();
                    })
                    .setNegativeButton(R.string.dark_text, (dialogInterface, i) -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                        //save theme
                        SharedPreferences sharedPreferences = getSharedPreferences("theme", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("theme", "dark");
                        editor.apply();
                    })
                    .setNeutralButton(R.string.system_theme_text, (dialogInterface, i) -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

                        //save theme
                        SharedPreferences sharedPreferences = getSharedPreferences("theme", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("theme", "system_default");
                        editor.apply();
                    })
                    .show();

        } else if (item.getItemId() == R.id.review) {

            //start review manager
            activateReviewInfo();

        } else if (item.getItemId() == R.id.developer) {

            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=Mahmut+Alperen+%C3%9Cnal")));
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=Mahmut+Alperen+%C3%9Cnal")));
            }

        }

        return true;
    }


    //check last theme and set
    public void checkLastSelectedTheme() {
        SharedPreferences sharedPreferences = getSharedPreferences("theme", MODE_PRIVATE);
        String theme = sharedPreferences.getString("theme", "system_default");

        switch (theme) {
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case "system_default":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
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
                R.string.updated_text,
                Snackbar.LENGTH_INDEFINITE
                );

        snackbar.setAction("Reload", view -> appUpdateManager.completeUpdate());

        snackbar.setTextColor(Color.parseColor("#FF000"));
        snackbar.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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