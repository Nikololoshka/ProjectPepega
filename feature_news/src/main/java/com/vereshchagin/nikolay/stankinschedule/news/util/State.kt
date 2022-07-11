package com.vereshchagin.nikolay.stankinschedule.news.util

/**
 * Вспомогательный класс для отслеживания состояния загрузки ресурса.
 */
sealed class State<T> {
    /**
     * Ресурс загружается.
     */
    class Loading<T> : State<T>()

    /**
     * Ресурс успешно загружен.
     */
    data class Success<T>(val data: T) : State<T>()

    /**
     * При загрузке ресурса возникла ошибка.
     */
    data class Failed<T>(val error: Throwable) : State<T>()

    companion object {
        /**
         * Возвращает класс загрузки.
         */
        @JvmStatic
        fun <T> loading() = Loading<T>()

        /**
         * Возвращает класс успешной загрузки ресурса.
         */
        @JvmStatic
        fun <T> success(data: T) = Success(data)

        /**
         * Возвращает класс ошибки при загрузке ресурса.
         */
        @JvmStatic
        fun <T> failed(error: Throwable) = Failed<T>(error)
    }
}
