package com.vereshchagin.nikolay.stankinschedule;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.vereshchagin.nikolay.stankinschedule.settings.ApplicationPreference;
import com.vereshchagin.nikolay.stankinschedule.utils.NotificationUtils;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivityLog";

    private DrawerLayout mDrawer;
    private ImageButton mDarkModeButton;

    // private ImageView mImageView;

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
                this, mDrawer, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        // navigation
        NavigationView navigationView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host);
        NavigationUI.setupWithNavController(toolbar, navController, mDrawer);
        NavigationUI.setupWithNavController(navigationView, navController);

        Button settingButton = mDrawer.findViewById(R.id.settings);
        settingButton.setOnClickListener(this);

        mDarkModeButton = mDrawer.findViewById(R.id.dark_mode_button);
        mDarkModeButton.setOnClickListener(this);
        updateDarkModeButton();

        if (ApplicationPreference.firstRun(this)) {
            ApplicationPreference.setFirstRun(this, false);
        }

        // настройка уведомлений приложения
        // android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // общего назначения
            NotificationChannel channelCommon = new NotificationChannel(
                    NotificationUtils.CHANNEL_COMMON,
                    getString(R.string.notification_common),
                    NotificationManager.IMPORTANCE_DEFAULT);

            channelCommon.setDescription(getString(R.string.notification_common_description));
            channelCommon.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channelCommon.enableVibration(true);
            channelCommon.enableLights(true);

            // модульного журнала
            NotificationChannel channelModuleJournal = new NotificationChannel(
                    NotificationUtils.CHANNEL_MODULE_JOURNAL,
                    getString(R.string.notification_mj),
                    NotificationManager.IMPORTANCE_DEFAULT);

            channelModuleJournal.setDescription(getString(R.string.notification_mj_description));
            channelModuleJournal.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channelModuleJournal.enableVibration(true);
            channelModuleJournal.enableLights(true);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager == null) {
                return;
            }

            notificationManager.createNotificationChannel(channelCommon);
            notificationManager.createNotificationChannel(channelModuleJournal);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.settings: {
                NavController navController = Navigation.findNavController(this, R.id.nav_host);
                navController.navigate(R.id.toSettings);
                mDrawer.closeDrawers();
                break;
            }
            case R.id.dark_mode_button: {
                // если кнопка все равно осталась
                String darkMode = ApplicationPreference.currentDarkMode(this);
                if (!darkMode.equals(ApplicationPreference.DARK_MODE_MANUAL)) {
                    mDarkModeButton.setVisibility(View.GONE);
                    return;
                }

                boolean isDark = ApplicationPreference.currentManualMode(this);
                isDark = !isDark;

                AppCompatDelegate.setDefaultNightMode(isDark ?
                        AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
                ApplicationPreference.setManualMode(this, isDark);

                /*
                getWindow().setWindowAnimations(R.style.WindowAnimationTransition);
                recreate();

                int w = mDrawer.getMeasuredWidth();
                int h = mDrawer.getMeasuredHeight();

                Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                mDrawer.draw(canvas);

                mImageView.setImageBitmap(bitmap);
                mImageView.setVisibility(View.VISIBLE);

                int startRadius = 0;
                int endRadius = (int) Math.hypot(w, h);

                Animator animator = ViewAnimationUtils.createCircularReveal(mDrawer, w / 2, h / 2, startRadius, endRadius);
                animator.setDuration(2000);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mImageView.setImageDrawable(null);
                        mImageView.setVisibility(View.GONE);
                    }
                });
                animator.start();
                */

                break;
            }
        }
    }

    /**
     * Обновляет отображение кнопки ручного переключение
     * темной темы исходя из текущих настроек.
     */
    public void updateDarkModeButton() {
        String darkMode = ApplicationPreference.currentDarkMode(this);
        mDarkModeButton.setVisibility(darkMode.equals(ApplicationPreference.DARK_MODE_MANUAL) ?
                View.VISIBLE : View.GONE);
    }
}
