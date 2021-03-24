package com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.v1

/**
 * Интерфейс объекта в удаленном расписании.
 */
interface RepositoryItem {
    /**
     * Возвращает данные, для отображения их в UI.
     */
    fun data(): String
}