package com.vereshchagin.nikolay.stankinschedule.ui.settings.view.widget.paging

/**
 * Объект данных для отображения виджетов с расписанием в списке.
 * @param scheduleName название расписания.
 * @param widgetId ID виджета с расписанием.
 */
data class SettingsWidgetItem(
    val scheduleName: String,
    val widgetId: Int,
)