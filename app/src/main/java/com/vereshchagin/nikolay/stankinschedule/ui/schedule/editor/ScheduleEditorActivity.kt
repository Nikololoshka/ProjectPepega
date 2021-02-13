package com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.ActivityScheduleEditorBinding

/**
 * Активность для редактирования расписания.
 */
class ScheduleEditorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScheduleEditorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScheduleEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // конфигурирования навигации
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        navHostFragment.navController
            .setGraph(R.navigation.activity_schedule_editor_nav_graph, intent.extras)

    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        return navHostFragment.navController.navigateUp() || super.onSupportNavigateUp()
    }

    companion object {

        private const val TAG = "ScheduleEditorLog"
        private const val SCHEDULE_NAME = "schedule_name"

        /**
         *  Создает intent для вызова редактора расписания.
         */
        fun createIntent(context: Context, scheduleName: String): Intent {
            return Intent(context, ScheduleEditorActivity::class.java).apply {
                putExtra(SCHEDULE_NAME, scheduleName)
            }
        }
    }
}