package com.vereshchagin.nikolay.stankinschedule.utils

import android.content.Context
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.HttpsURLConnection

object ExceptionUtils {

    /**
     * Возвращает описание ошибки для пользователя.
     */
    fun errorDescription(throwable: Throwable, context: Context): String {
        when (throwable) {
            // HTTP ошибка (retrofit2)
            is HttpException -> errorNetworkDescription(throwable, context)
            // время ожидания сокета истекло
            is SocketTimeoutException -> {

            }
            // не удалось подключиться к хосту
            is UnknownHostException -> {

            }
        }

        // запись неизвестной ошибки
        Firebase.crashlytics.recordException(throwable)
        return throwable.localizedMessage ?: throwable.toString()
    }

    fun errorNetworkDescription(exception: HttpException, context: Context) {
        when (exception.code()) {
            // ошибка авторизации
            HttpsURLConnection.HTTP_UNAUTHORIZED -> {

            }
            // не удалось получить ответ от сервера (время истекло)
            HttpsURLConnection.HTTP_GATEWAY_TIMEOUT -> {

            }
        }
    }
}