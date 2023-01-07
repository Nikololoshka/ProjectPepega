package com.vereshchagin.nikolay.stankinschedule.core.ui.utils

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.vereshchagin.nikolay.stankinschedule.core.ui.R
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.HttpsURLConnection

fun Context.exceptionDescription(t: Throwable): String? {
    val descriptionRes = exceptionDescriptionRes(t) ?: return null
    return getString(descriptionRes)
}

@Composable
fun exceptionDescription(t: Throwable): String? {
    val descriptionRes = exceptionDescriptionRes(t) ?: return null
    return stringResource(descriptionRes)
}


@StringRes
private fun exceptionDescriptionRes(t: Throwable): Int? {
    when (t) {
        // время ожидания сокета истекло
        is SocketTimeoutException -> {
            return R.string.ex_socket_timeout
        }
        // не удалось подключиться к хосту
        is UnknownHostException -> {
            return R.string.ex_unknown_host
        }
        // HTTP ошибка (retrofit2)
        is HttpException -> {
            when (t.code()) {
                // ошибка авторизации
                HttpsURLConnection.HTTP_UNAUTHORIZED -> {
                    return R.string.ex_failed_unauthorized
                }
                // не удалось получить ответ от сервера (время истекло)
                HttpsURLConnection.HTTP_GATEWAY_TIMEOUT -> {
                    return R.string.ex_socket_timeout
                }
            }
        }
    }

    return null
}