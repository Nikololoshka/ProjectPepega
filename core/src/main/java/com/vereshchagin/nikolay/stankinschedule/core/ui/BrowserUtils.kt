package com.vereshchagin.nikolay.stankinschedule.core.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat

object BrowserUtils {

    fun openLink(context: Context, url: String) {
        if (false) {
            ContextCompat.startActivity(
                context,
                Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(url)
                },
                null
            )
        }

        val customTabsIntent = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .setShareState(CustomTabsIntent.SHARE_STATE_ON)
            .setDefaultColorSchemeParams(
                CustomTabColorSchemeParams.Builder()
                    .setToolbarColor(Color.Blue.toArgb())
                    .build()
            )
            .build()

        customTabsIntent.launchUrl(context, Uri.parse(url))
    }

    fun openLink(context: Context, uri: Uri) = openLink(context, uri.toString())
}