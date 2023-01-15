package com.vereshchagin.nikolay.stankinschedule.core.ui.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_REQUIRE_NON_BROWSER
import android.net.Uri
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import com.vereshchagin.nikolay.stankinschedule.core.ui.R


object BrowserUtils {

    fun openLink(context: Context, url: String, includeApp: Boolean = false) {
        openLink(context, Uri.parse(url), includeApp)
    }

    fun openLink(context: Context, uri: Uri, includeApp: Boolean = false) {
        if (includeApp) {
            try {
                val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                    addCategory(Intent.CATEGORY_BROWSABLE)
                    flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_REQUIRE_NON_BROWSER
                }
                context.startActivity(intent)
                return

            } catch (ignored: ActivityNotFoundException) {

            }
        }

        startCustomTabs(context, uri)
    }

    private fun startCustomTabs(context: Context, uri: Uri) {
        val customTabsIntent = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .setShareState(CustomTabsIntent.SHARE_STATE_ON)
            .setDefaultColorSchemeParams(
                CustomTabColorSchemeParams.Builder()
                    .setToolbarColor(context.getColor(R.color.onSecondary))
                    .build()
            )
            .build()

        customTabsIntent.launchUrl(context, uri)
    }
}