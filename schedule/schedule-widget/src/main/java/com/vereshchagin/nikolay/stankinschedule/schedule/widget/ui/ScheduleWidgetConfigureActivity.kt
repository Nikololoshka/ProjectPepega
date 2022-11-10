package com.vereshchagin.nikolay.stankinschedule.schedule.widget.ui

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.AppTheme
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.Subgroup
import com.vereshchagin.nikolay.stankinschedule.schedule.widget.domain.model.ScheduleWidgetData
import com.vereshchagin.nikolay.stankinschedule.schedule.widget.domain.repository.ScheduleWidgetPreference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ScheduleWidgetConfigureActivity : AppCompatActivity() {

    @Inject
    lateinit var preference: ScheduleWidgetPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setResult(RESULT_CANCELED)

        val appWidgetId = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID
        )

        setContent {
            AppTheme {
                Button(
                    onClick = { onScheduleWidgetChanged(appWidgetId) }
                ) {
                    Text(text = "Add")
                }
            }
        }
    }

    private fun onScheduleWidgetChanged(appWidgetId: Int) {
        val data = ScheduleWidgetData(
            "My schedule",
            1,
            Subgroup.A,
            true
        )

        preference.saveData(appWidgetId, data)

        // обновляем виджет
        val appWidgetManager = AppWidgetManager.getInstance(this)
        ScheduleWidget.onUpdateWidget(this, appWidgetManager, appWidgetId, data)

        // завершаем конфигурирование виджета
        setResult(
            RESULT_OK,
            Intent().apply { putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId) }
        )
        finish()
    }
}