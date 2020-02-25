package com.vereshchagin.nikolay.stankinschedule;

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

        Intent mainIntent = new Intent(this, MainActivity.class);

        final Intent intent = getIntent();
        final String action = intent.getAction();
        final String data = intent.getDataString();

        if (Intent.ACTION_VIEW.equals(action) && data != null) {
            mainIntent.setAction(Intent.ACTION_VIEW);
            mainIntent.putExtra(MainActivity.MODULE_JOURNAL_EXTRA, data);
        }

        startActivity(mainIntent);
        finish();
    }
}
