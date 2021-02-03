package com.vereshchagin.nikolay.stankinschedule.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import android.widget.RemoteViewsService.RemoteViewsFactory
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Pair
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Subgroup
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Type
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepository
import com.vereshchagin.nikolay.stankinschedule.settings.ApplicationPreference
import com.vereshchagin.nikolay.stankinschedule.settings.ApplicationPreference.LABORATORY_COLOR
import com.vereshchagin.nikolay.stankinschedule.settings.ApplicationPreference.LECTURE_COLOR
import com.vereshchagin.nikolay.stankinschedule.settings.ApplicationPreference.SEMINAR_COLOR
import org.joda.time.LocalDate
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.ArrayList

/**
 * Адаптер для виджета с расписанием.
 */
class ScheduleWidgetRemoteFactory(
    context: Context, intent: Intent
) : RemoteViewsFactory {

    /**
     * Пакет.
     */
    private val packageName = context.packageName

    /**
     * ID виджета.
     */
    private val scheduleAppWidgetId = intent.getIntExtra(
        AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID
    )

    /**
     * Контекст.
     */
    private val context = WeakReference(context)

    /**
     * Список дней.
     */
    private val days = ArrayList<ScheduleWidgetDayItem>()

    /**
     * Название расписание.
     */
    private var scheduleName = ""

    /**
     * Подгруппа в расписании.
     */
    private var subgroup = Subgroup.COMMON

    /**
     * Ошибка при загрузке.
      */
    private var loadingError = false
    private val errorMessage = context.getString(R.string.widget_schedule_error)

    /**
     * Цвета в расписании.
     */
    private var lectureColor = 0
    private var seminarColor = 0
    private var laboratoryColor = 0

    override fun onCreate() {

    }

    override fun onDataSetChanged() {
        try {
            days.clear()
            val currentContext = context.get()
            if (currentContext == null) {
                loadingError = true
                return
            }

            // загрузка цветов
            val (lecture, seminar, laboratory) = ApplicationPreference.colors(
                currentContext, LECTURE_COLOR, SEMINAR_COLOR, LABORATORY_COLOR
            )
            lectureColor = lecture; seminarColor = seminar; laboratoryColor = laboratory

            // загрузка данных
            val widgetData =
                ScheduleWidgetConfigureActivity.loadPref(currentContext, scheduleAppWidgetId)
            if (widgetData.scheduleName == null) {
                loadingError = true
                return
            }
            scheduleName = widgetData.scheduleName
            subgroup = widgetData.subgroup

            try {
                val schedule = ScheduleRepository().load(scheduleName, currentContext)

                var data = LocalDate.now()
                for (i in 0 until 7) {
                    days.add(
                        ScheduleWidgetDayItem(
                            data.toString("EE, dd MMMM").capitalize(Locale.ROOT),
                            schedule.pairsByDate(data),
                            data
                        )
                    )
                    data = data.plusDays(1)
                }
                loadingError = false

            } catch (e: Exception) {
                loadingError = true
            }
        } catch (t: Throwable) {
            FirebaseCrashlytics.getInstance().recordException(t)
            throw t
        }
    }

    override fun onDestroy() {

    }

    override fun getCount(): Int {
        return if (loadingError) 1 else days.size
    }

    override fun getViewAt(position: Int): RemoteViews {
        try {
            // отображаем ошибку
            if (loadingError) {
                val errorView = RemoteViews(packageName, R.layout.widget_schedule_error)
                errorView.setTextViewText(R.id.error_title, errorMessage)
                return errorView
            }

            // создаем view с днем
            val dayView = RemoteViews(packageName, R.layout.widget_item_schedule)
            dayView.removeAllViews(R.id.schedule_day_pairs)
            val item = days[position]

            // заголовок дня
            dayView.setTextViewText(R.id.schedule_day_title, item.dayTitle)

            // добавляем пары
            var isAdded = false
            for (pair in item.pairs) {

                // если не подходит по подгруппе
                if (!pair.isCurrently(subgroup)) {
                    continue
                }

                // установка данных в пару
                val pairView = RemoteViews(packageName, R.layout.widget_item_schedule_pair)
                pairView.setTextViewText(R.id.widget_schedule_title, pair.title)
                pairView.setTextViewText(R.id.widget_schedule_time, pair.time.toString())
                pairView.setTextViewText(R.id.widget_schedule_classroom, pair.classroom)

                // цвет типа
                val color = when (pair.type) {
                    Type.LECTURE -> lectureColor
                    Type.SEMINAR -> seminarColor
                    Type.LABORATORY -> laboratoryColor
                }

                pairView.setInt(R.id.widget_schedule_type, "setColorFilter", color)
                dayView.addView(R.id.schedule_day_pairs, pairView)
                isAdded = true
            }

            // если нет пар
            if (!isAdded) {
                val pairView = RemoteViews(packageName, R.layout.widget_item_schedule_no_pairs)
                dayView.addView(R.id.schedule_day_pairs, pairView)
            }

            // для обратного вызова приложения с расписанием на определенном дне
            dayView.setOnClickFillInIntent(
                R.id.widget_item_schedule_app,
                ScheduleWidget.createDayIntent(scheduleName, item.dayTime)
            )

            return dayView

        } catch (t: Throwable) {
            FirebaseCrashlytics.getInstance().recordException(t)
            throw t
        }
    }

    override fun getLoadingView(): RemoteViews {
        return RemoteViews(packageName, R.layout.widget_item_schedule_shimmer)
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    /**
     * Сервис, который создает адаптер по обновлению данных виджета.
     */
    class Service : RemoteViewsService() {
        override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
            return ScheduleWidgetRemoteFactory(applicationContext, intent)
        }
    }

    /**
     * Информация о дне виджета с расписанием.
     * @param dayTitle заголовок дня.
     * @param pairs пары дня.
     * @param dayTime дата дня.
     */
    private class ScheduleWidgetDayItem(
        var dayTitle: String,
        var pairs: List<Pair>,
        var dayTime: LocalDate
    )

    companion object {
        private const val TAG = "ScheduleAppWgtFactoryLog"
    }
}