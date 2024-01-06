package com.easym.vegie.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.easym.vegie.R;
import com.easym.vegie.SessionData.SessionManager;
import com.easym.vegie.Utils.Utility;

/**
 * Created by Arti on 26th Sept 2020.
 */

public class SplashScreen extends BaseActivity {
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                if(isNetworkConnected()) {

                    // This method will be executed once the timer is over
                    if (SessionManager.getInstance(SplashScreen.this).isLoggedIn()) {

                        if (userPref.isUserPreferLanguageSet()) {
                            startActivity(new Intent(SplashScreen.this, HomePageActivity.class));
                            finish();
                        } else {
                            startActivity(new Intent(SplashScreen.this, LanguageActivity.class));
                            finish();
                        }

                    } else {
                        Intent i = new Intent(SplashScreen.this, LoginPageActivity.class);
                        startActivity(i);
                        finish();
                    }

                } else {

                    Utility.simpleAlert(SplashScreen.this, "", getString(R.string.check_network_connection));

                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    private boolean isNetworkConnected() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();

    }
}