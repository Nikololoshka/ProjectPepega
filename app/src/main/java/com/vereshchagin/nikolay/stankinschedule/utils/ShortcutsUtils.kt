package com.vereshchagin.nikolay.stankinschedule.utils

import android.os.Build
import androidx.annotation.RequiresApi

/**
 * Вспомогательный класс для работы с ярлыками (shortcuts).
 */
@RequiresApi(Build.VERSION_CODES.N_MR1)
object ShortcutsUtils {

    /**
     * Shortcut у избранному расписанию.
     */
    const val FAVORITE_SHORTCUT =
        "com.vereshchagin.nikolay.stankinschedule.FAVORITE_SHORTCUT_ACTION"

    /**
     * Shortcut к модульному журналу.
     */
    const val MODULE_JOURNAL_SHORTCUT =
        "com.vereshchagin.nikolay.stankinschedule.MODULE_JOURNAL_SHORTCUT_ACTION"
}