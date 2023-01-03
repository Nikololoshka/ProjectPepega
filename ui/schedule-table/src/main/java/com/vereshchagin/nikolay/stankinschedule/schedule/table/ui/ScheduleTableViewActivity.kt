package com.vereshchagin.nikolay.stankinschedule.schedule.table.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScheduleTableViewActivity : AppCompatActivity() {

    private val viewModel: ScheduleTableViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val scheduleId = intent.getLongExtra(SCHEDULE_ID, -1)

        setContent {
            AppTheme {
                ScheduleTableScreen(
                    scheduleId = scheduleId,
                    viewModel = viewModel,
                    onBackClicked = { onBackPressedDispatcher.onBackPressed() },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    companion object {

        private const val SCHEDULE_ID = "schedule_id"

        fun createIntent(context: Context, scheduleId: Long): Intent {
            return Intent(context, ScheduleTableViewActivity::class.java).apply {
                putExtra(SCHEDULE_ID, scheduleId)
            }
        }
    }
}

