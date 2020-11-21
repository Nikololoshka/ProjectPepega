package com.vereshchagin.nikolay.stankinschedule

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.vereshchagin.nikolay.stankinschedule.databinding.ActivityMainBinding
import com.vereshchagin.nikolay.stankinschedule.ui.settings.ApplicationPreference
import com.vereshchagin.nikolay.stankinschedule.ui.settings.ApplicationPreferenceKt
import com.vereshchagin.nikolay.stankinschedule.utils.NotificationUtils
import org.joda.time.DateTime
import org.joda.time.Hours

/**
 * Главная активность приложения.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var appUpdateManager: AppUpdateManager
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // установка основной темы приложения, вместо splash темы
        setTheme(R.style.AppTheme_NoActionBar)

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // настройка toolbar'а
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        toolbar.setTitle(R.string.nav_home)
        setSupportActionBar(toolbar)

        // синхронизация toolbar и drawer
        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            toolbar,
            R.string.nav_drawer_open,
            R.string.nav_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // конфигурирования навигации
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        val navController = navHostFragment.navController

        val configuration = AppBarConfiguration.Builder(
            R.id.nav_home_fragment,
            R.id.nav_schedule_fragment,
            R.id.nav_module_journal_fragment,
            R.id.nav_news_fragment,
            R.id.nav_module_journal_login_fragment
        ).setOpenableLayout(binding.drawerLayout)
            .build()

        NavigationUI.setupWithNavController(toolbar, navController, configuration)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)

        // Bottom Navigation
        bottomNavigationView.setupWithNavController(navController)
        // Drawer
        binding.navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            // скрываем / показываем нижнюю навигацию
            bottomNavigationView.visibility =
                if (destination.parent?.id == R.id.settings_nav_graph ||
                    destination.id == R.id.nav_about_fragment
                ) {
                    View.GONE
                } else {
                    View.VISIBLE
                }

            if (destination.id == R.id.nav_schedule_view_fragment) {
                bottomNavigationView.menu.findItem(R.id.nav_schedule_fragment)?.isChecked = true
            }
        }

        // переключатель темы приложения
        val isDark = ApplicationPreference.currentManualMode(this)
        binding.darkModeButton.isChecked = isDark
        binding.darkModeButton.setOnClickListener(this::onDarkModeButtonClicked)
        updateDarkModeButton()

        // настройка уведомлений приложения
        // android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // общего назначения
            val channelCommon = NotificationChannel(
                NotificationUtils.CHANNEL_COMMON,
                getString(R.string.notification_common),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channelCommon.description = getString(R.string.notification_common_description)
            channelCommon.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            channelCommon.enableVibration(true)
            channelCommon.enableLights(true)

            // модульного журнала
            val channelModuleJournal = NotificationChannel(
                NotificationUtils.CHANNEL_MODULE_JOURNAL,
                getString(R.string.notification_mj),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channelModuleJournal.description = getString(R.string.notification_mj_description)
            channelModuleJournal.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            channelModuleJournal.enableVibration(true)
            channelModuleJournal.enableLights(true)

            getSystemService(NotificationManager::class.java)?.let { manager ->
                manager.createNotificationChannel(channelCommon)
                manager.createNotificationChannel(channelModuleJournal)
            }
        }

        appUpdateManager = AppUpdateManagerFactory.create(this)
        checkAppUpdate()

        val isAnalytics = ApplicationPreferenceKt.firebaseAnalytics(this)
        Firebase.analytics.setAnalyticsCollectionEnabled(isAnalytics)

        // throw RuntimeException("Stack deobfuscation example exception");
    }

    override fun onDestroy() {
        super.onDestroy()
        appUpdateManager.unregisterListener(this::onUpdateState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // отмена обновления
        if (requestCode == UPDATE_REQUEST && resultCode == RESULT_CANCELED) {
            Snackbar.make(
                binding.appBarMain.contentMain.containerMain,
                R.string.update_cancelled,
                Snackbar.LENGTH_LONG
            ).show()
            ApplicationPreferenceKt.setUpdateAppTime(this, DateTime.now())
        }
    }

    /**
     * Вызывается при нажатию на переключатель по смене темы приложения.
     */
    @Suppress("UNUSED_PARAMETER")
    private fun onDarkModeButtonClicked(ignored: View) {
        // если кнопка все равно осталась
        val darkMode = ApplicationPreference.currentDarkMode(this)
        if (darkMode != ApplicationPreference.DARK_MODE_MANUAL) {
            binding.darkModeButton.visibility = View.GONE
            return
        }

        var isDark = ApplicationPreference.currentManualMode(this)
        isDark = !isDark
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
        ApplicationPreference.setManualMode(this, isDark)

        /*
            Для анимации как в TG.
            UPD: не работает, т.к. сейчас смена темы сделана через
                 AppCompatDelegate.setDefaultNightMode

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
    }

    /**
     * Обновляет отображение кнопки ручного переключение
     * темной темы исходя из текущих настроек.
     */
    fun updateDarkModeButton() {
        val darkMode = ApplicationPreference.currentDarkMode(this)
        binding.darkModeButton.visibility =
            if (darkMode == ApplicationPreference.DARK_MODE_MANUAL) {
                View.VISIBLE
            } else {
                View.GONE
            }
    }

    /**
     * Проверка обновлений приложения.
     */
    private fun checkAppUpdate() {
        val lastUpdate = ApplicationPreferenceKt.updateAppTime(this)
        if (lastUpdate != null && Hours.hoursBetween(lastUpdate, DateTime.now()).hours < 24) {
            return
        }

        appUpdateManager.registerListener(this::onUpdateState)
        appUpdateManager.appUpdateInfo
            .addOnSuccessListener { updateInfo ->
                val stalenessDays = updateInfo.clientVersionStalenessDays()
                if (updateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && updateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
                    && stalenessDays != null
                    && stalenessDays >= DAYS_FOR_FLEXIBLE_UPDATE
                ) {
                    onShowUpdate(updateInfo)
                } else {
                    ApplicationPreferenceKt.setUpdateAppTime(this, DateTime.now())
                }
            }.addOnFailureListener {
                ApplicationPreferenceKt.setUpdateAppTime(this, DateTime.now())
            }
    }

    /**
     * Показывает диалог, что доступно обновление приложения.
     */
    private fun onShowUpdate(updateInfo: AppUpdateInfo) {
        Snackbar.make(
            binding.appBarMain.contentMain.containerMain,
            R.string.update_available,
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction(R.string.update_start_update) {
                appUpdateManager.startUpdateFlowForResult(
                    updateInfo,
                    AppUpdateType.FLEXIBLE,
                    this@MainActivity,
                    UPDATE_REQUEST
                )
            }
            show()
        }
    }

    /**
     * Вызывается, если статус обновления изменен.
     */
    private fun onUpdateState(state: InstallState) {
        // обновление загружено
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            Snackbar.make(
                binding.appBarMain.contentMain.containerMain,
                R.string.update_downloaded,
                Snackbar.LENGTH_INDEFINITE
            ).apply {
                setAction(R.string.update_restart) {
                    appUpdateManager.completeUpdate()
                }
                show()
            }
            ApplicationPreferenceKt.setUpdateAppTime(this, DateTime.now())
        }
    }

    companion object {
        const val UPDATE_REQUEST = 1
        const val DAYS_FOR_FLEXIBLE_UPDATE = 3

        const val TAG = "MainActivity"
    }
}