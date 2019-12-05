package com.github.nikololoshka.pepegaschedule;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.github.nikololoshka.pepegaschedule.settings.ApplicationPreference;
import com.github.nikololoshka.pepegaschedule.utils.NotificationDispatcher;
import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getName();

    private DrawerLayout mDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.nav_home);
        setSupportActionBar(toolbar);

        // toggle navigation bar
        mDrawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        // navigation
        NavigationView navigationView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host);
        NavigationUI.setupWithNavController(toolbar, navController, mDrawer);
        NavigationUI.setupWithNavController(navigationView, navController);

        Button settingButton = mDrawer.findViewById(R.id.settings);
        settingButton.setOnClickListener(this);

        // настройка уведомлений приложения
        if (ApplicationPreference.firstRun(this)) {
                // android 8.0+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channelCommon = new NotificationChannel(
                        NotificationDispatcher.CHANNEL_COMMON,
                        getString(R.string.common_notification),
                        NotificationManager.IMPORTANCE_DEFAULT);

                channelCommon.setDescription(getString(R.string.common_notification_description));
                channelCommon.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                channelCommon.enableVibration(true);
                channelCommon.enableLights(true);

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                if (notificationManager == null) {
                    return;
                }

                notificationManager.createNotificationChannel(channelCommon);
            }

            ApplicationPreference.setFirstRun(this, false);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.settings) {
            NavController navController = Navigation.findNavController(this, R.id.nav_host);
            navController.navigate(R.id.toSettings);

            mDrawer.closeDrawers();
        }
    }
}
