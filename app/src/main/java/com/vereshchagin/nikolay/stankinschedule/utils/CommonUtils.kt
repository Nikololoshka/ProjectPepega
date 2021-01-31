package com.vereshchagin.nikolay.stankinschedule.utils

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.util.TypedValue
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.settings.ApplicationPreference


/**
 * Вспомогательный класс с общими функциями.
 */
object CommonUtils {

    /**
     * Возвращает размер в пикселях, переводя из dp.
     * @param dp значение в DP.
     * @param resources ресурсы для взятия размеров.
     */
    @JvmStatic
    fun dpToPx(dp: Float, resources: Resources): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics
        )
    }

    /**
     * Возвращает размер в пикселях, переводя из sp.
     * @param sp значение в SP.
     * @param resources ресурсы для взятия размеров.
     */
    @JvmStatic
    fun spToPx(sp: Float, resources: Resources): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, sp, resources.displayMetrics
        )
    }

    /**
     * Открывает внешнюю ссылку в браузере.
     * @param context контекст приложения.
     * @param url URL адрес.
     */
    @JvmStatic
    fun openBrowser(context: Context, url: String) {
        if (ApplicationPreference.useAppBrowser(context)) {
            val customTabsIntent = CustomTabsIntent.Builder()
                .setShowTitle(true)
                .setShareState(CustomTabsIntent.SHARE_STATE_ON)
                .setDefaultColorSchemeParams(
                    CustomTabColorSchemeParams.Builder()
                        .setToolbarColor(
                            ContextCompat.getColor(context, R.color.colorPrimary)
                        )
                        .setSecondaryToolbarColor(
                            ContextCompat.getColor(context, R.color.colorAccent)
                        )
                        .build()
                )
                .build()

            customTabsIntent.launchUrl(context, Uri.parse(url))

        } else {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            ContextCompat.startActivity(context, intent, null)
        }
    }
}