package com.vereshchagin.nikolay.stankinschedule.utils

import android.content.Context
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.vereshchagin.nikolay.stankinschedule.R
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.HttpsURLConnection

/**
 * Вспомогательный класс для обработки исключений.
 */
object ExceptionUtils {

    /**
     * Возвращает описание ошибки для пользователя.
     */
    fun errorDescription(throwable: Throwable, context: Context): String {
        when (throwable) {
            // время ожидания сокета истекло
            is SocketTimeoutException -> {
                return context.getString(R.string.ex_socket_timeout)
            }
            // не удалось подключиться к хосту
            is UnknownHostException -> {
                return context.getString(R.string.ex_unknown_host)
            }
            // HTTP ошибка (retrofit2)
            is HttpException -> {
                return errorNetworkDescription(throwable, context)
            }
        }

        // запись неизвестной ошибки
        Firebase.crashlytics.recordException(throwable)
        return throwable.localizedMessage ?: throwable.toString()
    }

    /**
     * Возвращает описание HTTP ошибки сети.
     */
    private fun errorNetworkDescription(exception: HttpException, context: Context): String {
        return when (exception.code()) {
            // ошибка авторизации
            HttpsURLConnection.HTTP_UNAUTHORIZED -> {
                context.getString(R.string.ex_failed_unauthorized)
            }
            // не удалось получить ответ от сервера (время истекло)
            HttpsURLConnection.HTTP_GATEWAY_TIMEOUT -> {
                context.getString(R.string.ex_socket_timeout)
            }
            else -> {
                Firebase.crashlytics.recordException(exception)
                "[${exception.code()}] $exception"
            }
        }
    }
}