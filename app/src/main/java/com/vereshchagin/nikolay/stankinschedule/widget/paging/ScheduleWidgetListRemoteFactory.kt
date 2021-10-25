package com.vereshchagin.nikolay.stankinschedule.widget.paging

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService.RemoteViewsFactory
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Subgroup
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Type
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepository
import com.vereshchagin.nikolay.stankinschedule.settings.ApplicationPreference
import com.vereshchagin.nikolay.stankinschedule.settings.ApplicationPreference.LABORATORY_COLOR
import com.vereshchagin.nikolay.stankinschedule.settings.ApplicationPreference.LECTURE_COLOR
import com.vereshchagin.nikolay.stankinschedule.settings.ApplicationPreference.SEMINAR_COLOR
import com.vereshchagin.nikolay.stankinschedule.utils.extensions.toTitleString
import com.vereshchagin.nikolay.stankinschedule.widget.ScheduleWidget
import com.vereshchagin.nikolay.stankinschedule.widget.ScheduleWidgetConfigureActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.joda.time.LocalDate
import java.lang.ref.WeakReference

/**
 * Адаптер для виджета с расписанием.
 */
class ScheduleWidgetListRemoteFactory(
    context: Context,
    intent: Intent,
    private val repository: ScheduleRepository,
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
    private val days = ArrayList<ScheduleWidgetListItem>()

    /**
     * Название расписание.
     */
    private var scheduleName = ""

    /**
     * ID расписания.
     */
    private var scheduleId = -1L

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
            val widgetData = ScheduleWidgetConfigureActivity.loadPref(
                currentContext, scheduleAppWidgetId
            )

            try {
                val schedule = runBlocking {
                    repository.schedule(widgetData.scheduleId).first()
                }

                if (schedule == null) {
                    loadingError = true
                    return
                }

                scheduleName = schedule.info.scheduleName
                scheduleId = schedule.info.id
                subgroup = widgetData.subgroup

                var data = LocalDate.now()
                for (i in 0 until 7) {
                    days.add(
                        ScheduleWidgetListItem(
                            data.toString("EE, dd MMMM").toTitleString(),
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
        /**
         * Нет обновления ScheduleName!!!
         *
         * RemoteViews#setEmptyView
         */

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
                ScheduleWidget.createDayIntent(scheduleId, item.dayTime)
            )

            return dayView

        } catch (t: Throwable) {
            Firebase.crashlytics.recordException(t)
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

    companion object {
        private const val TAG = "ScheduleWidgetLog"
    }
}