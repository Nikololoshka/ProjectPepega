package com.vereshchagin.nikolay.stankinschedule.schedule.widget.ui

import android.content.Context
import android.widget.RemoteViews
import com.vereshchagin.nikolay.stankinschedule.core.ui.toTitleCase
import com.vereshchagin.nikolay.stankinschedule.schedule.widget.R
import com.vereshchagin.nikolay.stankinschedule.schedule.widget.domain.model.ScheduleWidgetDay
import com.vereshchagin.nikolay.stankinschedule.schedule.widget.ui.base.CoroutinesRemoteFactory
import org.joda.time.LocalDate


class ScheduleWidgetRemoteFactory(context: Context) : CoroutinesRemoteFactory(context) {

    private var isError = false

    private var days: List<ScheduleWidgetDay> = emptyList()

    override suspend fun onDataChanged() {
        days = MutableList(7) {
            val date = LocalDate.now().plusDays(it)
            ScheduleWidgetDay(
                day = date.toString("EE, dd MMMM").toTitleCase(),
                date = date,
                pairs = emptyList()
            )
        }
    }

    override fun getCount(): Int = if (isError) 1 else days.size

    override fun getViewAt(position: Int): RemoteViews {
        return if (isError) getErrorView() else getDayView(days[position])
    }

    private fun getErrorView(): RemoteViews {
        return RemoteViews(packageName, R.layout.widget_schedule_item_loading)
    }

    private fun getDayView(day: ScheduleWidgetDay): RemoteViews {
        val dayView = RemoteViews(packageName, R.layout.widget_schedule_item)
        dayView.removeAllViews(R.id.day_layout)

        // заголовок дня
        dayView.setTextViewText(R.id.day_title, day.day)

        if (day.pairs.isEmpty()) {
            val emptyDay = RemoteViews(packageName, R.layout.widget_schedule_item_empty)
            dayView.addView(R.id.day_layout, emptyDay)
        }

        return dayView
    }

    override fun getLoadingView(): RemoteViews {
        return RemoteViews(packageName, R.layout.widget_schedule_item_loading)
    }
}