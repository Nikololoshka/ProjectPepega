package com.vereshchagin.nikolay.stankinschedule.schedule.widget.ui

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.widget.RemoteViews
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import com.vereshchagin.nikolay.stankinschedule.core.ui.toTitleCase
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.Subgroup
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.Type
import com.vereshchagin.nikolay.stankinschedule.schedule.core.ui.PairColors
import com.vereshchagin.nikolay.stankinschedule.schedule.core.ui.data.toColor
import com.vereshchagin.nikolay.stankinschedule.schedule.widget.R
import com.vereshchagin.nikolay.stankinschedule.schedule.widget.domain.model.ScheduleWidgetDay
import com.vereshchagin.nikolay.stankinschedule.schedule.widget.domain.model.ScheduleWidgetPair
import com.vereshchagin.nikolay.stankinschedule.schedule.widget.domain.model.ScheduleWidgetPairType
import com.vereshchagin.nikolay.stankinschedule.schedule.widget.domain.usecase.ScheduleWidgetUseCase
import com.vereshchagin.nikolay.stankinschedule.schedule.widget.ui.base.CoroutinesRemoteFactory
import com.vereshchagin.nikolay.stankinschedule.schedule.widget.utils.ScheduleDeepLink
import org.joda.time.LocalDate
import com.vereshchagin.nikolay.stankinschedule.core.R as R_core

class ScheduleWidgetRemoteFactory(
    context: Context,
    intent: Intent,
    private val useCase: ScheduleWidgetUseCase
) : CoroutinesRemoteFactory(context, intent) {

    private var isError = false

    private var scheduleId: Long = -1

    private var days: List<ScheduleWidgetDay> = emptyList()

    private var colors: PairColors = PairColors.defaults()

    private val lightColor: Int =
        context.resources.getColor(R_core.color.md_theme_light_onSurface, context.theme)

    private val darkColor: Int =
        context.resources.getColor(R_core.color.md_theme_dark_onSurface, context.theme)

    override suspend fun onDataChanged() {
        val data = useCase.loadWidgetData(appWidgetId)

        if (data == null) {
            isError = true
            return
        }

        scheduleId = data.scheduleId

        val today = LocalDate.now()
        val daysWithPairs = useCase.scheduleDays(
            data.scheduleId, data.subgroup, today, today.plusDays(7)
        )

        days = daysWithPairs.mapIndexed { index, pairs ->
            val now = today.plusDays(index)

            ScheduleWidgetDay(
                day = now.toString("EE, dd MMMM").toTitleCase(),
                date = now,
                pairs = pairs.map { pair ->
                    ScheduleWidgetPair(
                        title = pair.title,
                        classroom = pair.classroom,
                        time = pair.time.toString(),
                        type = when (pair.type) {
                            Type.LECTURE -> ScheduleWidgetPairType.Lecture
                            Type.SEMINAR -> ScheduleWidgetPairType.Seminar
                            Type.LABORATORY -> {
                                when (pair.subgroup) {
                                    Subgroup.A -> ScheduleWidgetPairType.SubgroupA
                                    Subgroup.B -> ScheduleWidgetPairType.SubgroupB
                                    else -> ScheduleWidgetPairType.Laboratory
                                }
                            }
                        }
                    )
                }
            )
        }

        colors = useCase.pairColors().toColor()
    }

    override fun getCount(): Int = if (isError) 1 else days.size

    override fun getViewAt(position: Int): RemoteViews {
        return if (isError) getErrorView() else getDayView(days[position])
    }

    private fun getErrorView(): RemoteViews {
        return RemoteViews(packageName, R.layout.widget_schedule_error)
    }

    private fun getDayView(day: ScheduleWidgetDay): RemoteViews {
        val dayView = RemoteViews(packageName, R.layout.widget_schedule_item)
        dayView.removeAllViews(R.id.day_layout)

        // заголовок дня
        dayView.setTextViewText(R.id.day_title, day.day)

        if (day.pairs.isEmpty()) {
            val emptyDay = RemoteViews(packageName, R.layout.widget_schedule_item_empty)
            dayView.addView(R.id.day_layout, emptyDay)
        } else {
            addPairs(day.pairs) { pairsView, color ->
                setPairColor(pairsView, color)
                dayView.addView(R.id.day_layout, pairsView)
            }
        }

        setDayClickIntent(dayView, day.date)

        return dayView
    }

    private fun setDayClickIntent(view: RemoteViews, date: LocalDate) {
        view.setOnClickFillInIntent(
            R.id.widget_day, ScheduleDeepLink.viewerIntent(scheduleId, date)
        )
    }

    private fun setPairColor(view: RemoteViews, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            view.setColorStateList(
                R.id.widget_pair,
                "setBackgroundTintList",
                ColorStateList.valueOf(color)
            )
        } else {
            view.setInt(
                R.id.widget_pair_type,
                "setColorFilter",
                color
            )
        }
    }

    private fun addPairs(
        pairs: List<ScheduleWidgetPair>,
        addView: (view: RemoteViews, color: Int) -> Unit
    ) {
        for (pair in pairs) {
            val pairsView = RemoteViews(packageName, R.layout.widget_schedule_item_pair)
            val (color, isDark) = colorForPair(pair.type)

            pairsView.setTextViewText(R.id.widget_pair_title, pair.title)
            val timeWithClassroom = if (pair.classroom.isNotEmpty()) {
                pair.time + ", " + pair.classroom
            } else {
                pair.time
            }

            pairsView.setTextViewText(R.id.widget_pair_time, timeWithClassroom)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                pairsView.setTextColor(
                    R.id.widget_pair_title,
                    if (isDark) darkColor else lightColor
                )
                pairsView.setTextColor(R.id.widget_pair_time, if (isDark) darkColor else lightColor)
            }

            addView(pairsView, color)
        }
    }

    private fun colorForPair(type: ScheduleWidgetPairType): Pair<Int, Boolean> {
        val color = when (type) {
            ScheduleWidgetPairType.Lecture -> colors.lectureColor
            ScheduleWidgetPairType.Seminar -> colors.seminarColor
            ScheduleWidgetPairType.Laboratory -> colors.laboratoryColor
            ScheduleWidgetPairType.SubgroupA -> colors.subgroupAColor
            ScheduleWidgetPairType.SubgroupB -> colors.subgroupBColor
        }.toArgb()

        return color to (ColorUtils.calculateLuminance(color) < 0.5)
    }

    override fun getLoadingView(): RemoteViews {
        return RemoteViews(packageName, R.layout.widget_schedule_item_loading)
    }
}