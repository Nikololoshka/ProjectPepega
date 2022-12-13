package com.vereshchagin.nikolay.stankinschedule.core.ui.components

/**
 * Вспомогательный класс для отслеживания состояния загрузки ресурса.
 */
sealed class UIState<T> {
    /**
     * Ресурс загружается.
     */
    class Loading<T> : UIState<T>()

    /**
     * Ресурс успешно загружен.
     */
    data class Success<T>(val data: T) : UIState<T>()

    /**
     * При загрузке ресурса возникла ошибка.
     */
    data class Failed<T>(val error: Throwable) : UIState<T>()

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

fun <T> UIState<T>.getOrNull(): T? {
    if (this is UIState.Success) {
        return data
    }
    return null
}
