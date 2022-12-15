package com.vereshchagin.nikolay.stankinschedule.schedule.widget.ui.configure

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.AppTheme
import com.vereshchagin.nikolay.stankinschedule.schedule.widget.domain.model.ScheduleWidgetData
import com.vereshchagin.nikolay.stankinschedule.schedule.widget.ui.ScheduleWidget
import com.vereshchagin.nikolay.stankinschedule.schedule.widget.ui.components.ScheduleWidgetConfigureScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScheduleWidgetConfigureActivity : AppCompatActivity() {

    private val viewModel: ScheduleWidgetConfigureViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setResult(RESULT_CANCELED)

        val appWidgetId = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID
        )

        setContent {
            AppTheme {
                ScheduleWidgetConfigureScreen(
                    appWidgetId = appWidgetId,
                    viewModel = viewModel,
                    onBackPressed = { onBackPressedDispatcher.onBackPressed() },
                    onScheduleWidgetChanged = ::onScheduleWidgetChanged,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    private fun onScheduleWidgetChanged(appWidgetId: Int, data: ScheduleWidgetData) {
        // обновляем виджет
        val appWidgetManager = AppWidgetManager.getInstance(this)
        ScheduleWidget.onUpdateWidget(this, appWidgetManager, appWidgetId, data, true)

        // завершаем конфигурирование виджета
        setResult(
            RESULT_OK,
            Intent().apply { putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId) }
        )
        finish()
    }
}