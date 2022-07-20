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
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepository
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.view.ScheduleViewFragment
import com.vereshchagin.nikolay.stankinschedule.utils.extensions.FLAG_MUTABLE_COMPAT
import com.vereshchagin.nikolay.stankinschedule.widget.paging.ScheduleWidgetListService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.joda.time.LocalDate

/**
 * Виджет с расписанием.
 */
class ScheduleWidget : AppWidgetProvider() {

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        val action = intent.action
        if (action != null && action.equals(ACTION_SCHEDULE_DAY_CLICKED, ignoreCase = true)) {

            Log.d(TAG, "onReceive: $intent")
            Log.d(TAG, "onReceive: ${intent.extras}")

            val date = intent.getSerializableExtra(SCHEDULE_DAY_TIME) as LocalDate? ?: return
            val scheduleId = intent.getLongExtra(SCHEDULE_ID, -1)

            // создание intent'а на открытие расписание на определенном дне
            val scheduleDayBundle = ScheduleViewFragment.createBundle(scheduleId, date)

            val scheduleDayPendingIntent = NavDeepLinkBuilder(context)
                .setComponentName(MainActivity::class.java)
                .setGraph(R.navigation.activity_main_nav_graph)
                .setDestination(R.id.nav_schedule_view_fragment)
                .setArguments(scheduleDayBundle)
                .createTaskStackBuilder()
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or FLAG_MUTABLE_COMPAT)

            try {
                scheduleDayPendingIntent?.send()

            } catch (ignored: CanceledException) {

            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        try {
            // Обновить все виджеты
            for (appWidgetId in appWidgetIds) {
                updateScheduleWidget(context, appWidgetManager, appWidgetId)
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
        private const val SCHEDULE_ID = "widget_schedule_id"
        private const val ACTION_SCHEDULE_DAY_CLICKED = "action_schedule_day_clicked"

        private const val TAG = "ScheduleWidgetLog"

        /**
         * Обновляет данные на виджете с расписанием.
         * @param context контекст.
         * @param appWidgetManager менеджер виджетов.
         * @param appWidgetId ID виджета.
         */
        fun updateScheduleWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
        ) {
            val widgetData = ScheduleWidgetConfigureActivity.loadPref(context, appWidgetId)
            updateScheduleWidgetData(context, appWidgetManager, appWidgetId, widgetData)
        }

        /**
         * Обновляет данные на виджете с помощью репозитория расписаний.
         * @param context контекст.
         * @param appWidgetManager менеджер виджетов.
         * @param appWidgetId ID виджета.
         * @param scheduleId ID расписания.
         * @param repository репозиторий с расписаниями.
         */
        suspend fun updateScheduleWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            scheduleId: Long,
            repository: ScheduleRepository,
        ) {
            // получение новых данных
            val scheduleItem = withContext(Dispatchers.IO) {
                repository.scheduleItem(scheduleId).first()
            } ?: return

            // обновление настроек
            var widgetData = ScheduleWidgetConfigureActivity.loadPref(context, appWidgetId)
            widgetData = ScheduleWidgetData(
                scheduleItem.scheduleName,
                widgetData.scheduleId,
                widgetData.subgroup,
                widgetData.display
            )
            ScheduleWidgetConfigureActivity.savePref(context, appWidgetId, widgetData)

            updateScheduleWidgetData(context, appWidgetManager, appWidgetId, widgetData)
        }

        /**
         * Обновляет только список в виджете с расписанием
         * @param appWidgetManager менеджер виджетов.
         * @param appWidgetId ID виджета.
         */
        fun updateScheduleWidgetList(appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_schedule_list)
        }

        /**
         * Обновляет данные в виджете с расписанием.
         * @param context контекст.
         * @param appWidgetManager менеджер виджетов.
         * @param appWidgetId ID виджета.
         * @param widgetData новые данные.
         */
        private fun updateScheduleWidgetData(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            widgetData: ScheduleWidgetData,
        ) {
            // для открытия приложения на расписании
            val schedulePendingIntent = NavDeepLinkBuilder(context)
                .setComponentName(MainActivity::class.java)
                .setGraph(R.navigation.activity_main_nav_graph)
                .setDestination(R.id.nav_schedule_view_fragment)
                .setArguments(
                    ScheduleViewFragment.createBundle(widgetData.scheduleId)
                )
                .createTaskStackBuilder()
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or FLAG_MUTABLE_COMPAT)

            // установка имени
            val scheduleName = widgetData.displayName(context)
            val views = RemoteViews(context.packageName, R.layout.widget_schedule)
            views.setTextViewText(R.id.widget_schedule_name, scheduleName)
            views.setOnClickPendingIntent(R.id.widget_schedule_name, schedulePendingIntent)

            // для открытия приложения на расписании на определенном дне
            val scheduleDayPendingIntent = PendingIntent.getBroadcast(
                context,
                appWidgetId,
                Intent(context, ScheduleWidget::class.java).also {
                    it.action = ACTION_SCHEDULE_DAY_CLICKED
                },
                PendingIntent.FLAG_UPDATE_CURRENT or FLAG_MUTABLE_COMPAT
            )
            views.setPendingIntentTemplate(R.id.widget_schedule_list, scheduleDayPendingIntent)

            // установка адаптера
            val dataIntent = Intent(context, ScheduleWidgetListService::class.java)
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
        fun createDayIntent(scheduleId: Long, date: LocalDate) = Intent().apply {
            putExtra(SCHEDULE_ID, scheduleId)
            putExtra(SCHEDULE_DAY_TIME, date)
        }
    }
}