package com.vereshchagin.nikolay.stankinschedule;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.view.ScheduleViewFragment;
import com.vereshchagin.nikolay.stankinschedule.ui.settings.ApplicationPreference;
import com.vereshchagin.nikolay.stankinschedule.ui.settings.SchedulePreference;
import com.vereshchagin.nikolay.stankinschedule.utils.NotificationUtils;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String MODULE_JOURNAL_VIEW = "module_journal_view";

    public static final String SCHEDULE_VIEW = "schedule_view";
    public static final String EXTRA_SCHEDULE_NAME = "extra_schedule_name";

    public static final String VIEW_ACTION = "view_action";

    private static final String TAG = "MainActivityLog";

    private DrawerLayout mDrawer;
    private ImageButton mDarkModeButton;

    // private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
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
        NavigationUI.setupWithNavController(navigationView, navController);

        AppBarConfiguration configuration = new AppBarConfiguration.Builder(
            R.id.nav_home_fragment, R.id.nav_schedule_fragment,
            R.id.nav_module_journal_fragment, R.id.nav_news_fragment, R.id.nav_module_journal_login_fragment
        ).setOpenableLayout(mDrawer).build();

        NavigationUI.setupWithNavController(toolbar, navController, configuration);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);


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

        newActionView(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        newActionView(intent);
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

    /**
     * Исходя из intent осуществляет переход в нужное место в приложении.
     * @param intent intent запуска приложения.
     */
    private void newActionView(@Nullable Intent intent) {
        if (intent != null) {
            @Nullable String action = intent.getStringExtra(VIEW_ACTION);


            if (action != null) {
                NavController navController = Navigation.findNavController(this, R.id.nav_host);

                switch (action) {
                    // к модульному журналу
                    case MODULE_JOURNAL_VIEW: {
                        navController.navigate(R.id.toModuleJournalFragment);
                        return;
                    }
                    // к расписанию
                    case SCHEDULE_VIEW: {
                        String scheduleName = intent.getStringExtra(EXTRA_SCHEDULE_NAME);
                        Log.d(TAG, "newActionView: " + scheduleName);
                        if (scheduleName != null) {
                            String schedulePath = SchedulePreference.createPath(this, scheduleName);

                            Bundle args = ScheduleViewFragment.createBundle(scheduleName, schedulePath);
                            navController.navigate(R.id.fromHomeFragmentToScheduleViewFragment, args);
                            return;
                        }
                        break;
                    }
                }
            }

            // если deep link
            if (Intent.ACTION_VIEW.equals(intent.getAction()) && (intent.getData() != null)) {
                NavController navController = Navigation.findNavController(this, R.id.nav_host);
                navController.navigate(R.id.toModuleJournalFragment);
            }
        }
    }
}
