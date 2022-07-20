package com.vereshchagin.nikolay.stankinschedule.model.schedule.remote

/**
 * Интерфейс объекта в удаленном расписании.
 */
interface ScheduleRepositoryItem {
    /**
     * Возвращает данные, для отображения их в UI.
     */
    fun data(): String
}