package com.vereshchagin.nikolay.stankinschedule.widget

import android.app.PendingIntent
import android.app.PendingIntent.CanceledException
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews
import androidx.navigation.NavDeepLinkBuilder
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.vereshchagin.nikolay.stankinschedule.MainActivity
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Subgroup
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.view.ScheduleViewFragment
import org.joda.time.LocalDate

/**
 * Виджет с расписанием.
 */
class ScheduleWidget : AppWidgetProvider() {

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        val action = intent.action
        if (action != null && action.equals(ACTION_SCHEDULE_DAY_CLICKED, ignoreCase = true)) {

            val date = intent.getSerializableExtra(SCHEDULE_DAY_TIME) as LocalDate
            val scheduleName = intent.getStringExtra(SCHEDULE_NAME) ?: return

            Log.d("MyLog", "onReceive: $date")

            // создание intent'а на открытие расписание на определенном дне
            val scheduleDayBundle = ScheduleViewFragment.createBundle(
                scheduleName, date
            )

            val scheduleDayPendingIntent = NavDeepLinkBuilder(context)
                .setComponentName(MainActivity::class.java)
                .setGraph(R.navigation.activity_main_nav_graph)
                .setDestination(R.id.nav_schedule_view_fragment)
                .setArguments(scheduleDayBundle)
                .createPendingIntent()

            try {
                scheduleDayPendingIntent.send()
            } catch (ignored: CanceledException) {

            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        try {
            // Обновить все виджеты
            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        } catch (t: Throwable) {
            FirebaseCrashlytics.getInstance().recordException(t)
            throw t
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // Удалить настройки , связанные с этими виджетами
        for (appWidgetId in appWidgetIds) {
            ScheduleWidgetConfigureActivity.deletePref(context, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Вызывается когда установлен первый виджет
    }

    override fun onDisabled(context: Context) {
        // Вызывается когда удален последний виджет
        // ApplicationPreference.clearScheduleWidgetIDs(context);
    }

    companion object {
        private const val SCHEDULE_DAY_TIME = "widget_schedule_day_time"
        private const val SCHEDULE_NAME = "widget_schedule_name"
        private const val ACTION_SCHEDULE_DAY_CLICKED = "action_schedule_day_clicked"

        /**
         * Обновляет данные на виджете с расписанием.
         * @param context контекст.
         * @param appWidgetManager менеджер виджетов.
         * @param appWidgetId ID виджета.
         */
        @JvmStatic
        fun updateAppWidget(
            context: Context, appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            // загрузка данных о виджете
            val widgetData = ScheduleWidgetConfigureActivity.loadPref(context, appWidgetId)
            var scheduleName = widgetData.scheduleName
            if (scheduleName == null) {
                scheduleName = context.getString(R.string.widget_schedule_name)
            }

            // для открытия приложения на распиании
            val scheduleBundle = ScheduleViewFragment.createBundle(
                scheduleName
            )
            val schedulePendingIntent = NavDeepLinkBuilder(context)
                .setComponentName(MainActivity::class.java)
                .setGraph(R.navigation.activity_main_nav_graph)
                .setDestination(R.id.nav_schedule_view_fragment)
                .setArguments(scheduleBundle)
                .createPendingIntent()

            // подгруппа виджета
            if (widgetData.display && widgetData.subgroup != Subgroup.COMMON) {
                scheduleName += " ${widgetData.subgroup.toString(context)}"
            }

            // установка имени
            val views = RemoteViews(context.packageName, R.layout.widget_schedule)
            views.setTextViewText(R.id.widget_schedule_name, scheduleName)
            views.setOnClickPendingIntent(R.id.widget_schedule_name, schedulePendingIntent)

            // для открытия приложения на расписании на определенном дне
            val scheduleDayPendingIntent = PendingIntent.getBroadcast(
                context, appWidgetId, Intent(context, ScheduleWidget::class.java).also {
                    it.action = ACTION_SCHEDULE_DAY_CLICKED
                }, 0
            )
            views.setPendingIntentTemplate(R.id.widget_schedule_list, scheduleDayPendingIntent)

            // установка адаптера
            val dataIntent = Intent(context, ScheduleWidgetRemoteFactory.Service::class.java)
            dataIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

            // уникальный адаптер для каждого виджета
            val data = Uri.parse(dataIntent.toUri(Intent.URI_INTENT_SCHEME))
            dataIntent.data = data
            views.setRemoteAdapter(R.id.widget_schedule_list, dataIntent)

            // Обновление виджета
            appWidgetManager.updateAppWidget(appWidgetId, views)
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_schedule_list)
        }

        /**
         * Создает intent для обратного вызова приложения с расписанием на определенном дне.
         */
        @JvmStatic
        fun createDayIntent(scheduleName: String, date: LocalDate) = Intent().apply {
            putExtra(SCHEDULE_NAME, scheduleName)
            putExtra(SCHEDULE_DAY_TIME, date)
        }
    }
}