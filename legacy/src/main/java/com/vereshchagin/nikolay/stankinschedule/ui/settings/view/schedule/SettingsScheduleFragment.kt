package com.vereshchagin.nikolay.stankinschedule.ui.settings.view.schedule

import android.appwidget.AppWidgetManager
import android.os.Bundle
import android.view.View
import androidx.preference.PreferenceFragmentCompat
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.settings.ApplicationPreferenceKt
import com.vereshchagin.nikolay.stankinschedule.utils.WidgetUtils
import com.vereshchagin.nikolay.stankinschedule.widget.ScheduleWidget
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Категория настроек расписания приложения.
 */
@AndroidEntryPoint
class SettingsScheduleFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var preference: ApplicationPreferenceKt

    override fun onCreatePreferences(savedInstanceState: Bundle, rootKey: String) {
        setPreferencesFromResource(R.xml.preferences_schedule, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preference.colorPreferenceChanged {
            val currentContext = requireContext()
            val appWidgetManager = AppWidgetManager.getInstance(currentContext)
            WidgetUtils.scheduleWidgets(currentContext).forEach { widgetId ->
                ScheduleWidget.updateScheduleWidgetList(appWidgetManager, widgetId)
            }
        }
    }
}