package com.github.nikololoshka.pepegaschedule;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        if ((getResources().getConfiguration().uiMode &
//                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
//            setTheme(R.style.AppTheme_Splash_Dark);
//        } else {
//            setTheme(R.style.AppTheme_Splash);
//        }
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
