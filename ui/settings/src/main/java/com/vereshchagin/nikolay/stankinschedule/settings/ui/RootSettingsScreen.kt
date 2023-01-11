package com.vereshchagin.nikolay.stankinschedule.settings.ui

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vereshchagin.nikolay.stankinschedule.core.domain.settings.DarkMode
import com.vereshchagin.nikolay.stankinschedule.core.ui.utils.BrowserUtils
import com.vereshchagin.nikolay.stankinschedule.settings.ui.components.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun RootSettingsScreen(
    viewModel: SettingsViewModel,
    onBackPressed: () -> Unit,
    navigateToSchedule: () -> Unit,
    navigateToWidgets: () -> Unit,
    modifier: Modifier = Modifier
) {

    SettingsScaffold(
        title = stringResource(R.string.settings_title),
        onBackPressed = onBackPressed,
        modifier = modifier
    ) {
        val context = LocalContext.current

        val nightMode by viewModel.nightMode.collectAsState()

        LaunchedEffect(nightMode) {
            val mode = when (nightMode) {
                DarkMode.Default -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                DarkMode.Dark -> AppCompatDelegate.MODE_NIGHT_YES
                DarkMode.Light -> AppCompatDelegate.MODE_NIGHT_NO
            }

            if (mode != AppCompatDelegate.getDefaultNightMode()) {
                AppCompatDelegate.setDefaultNightMode(mode)
            }
        }

        DialogPreference(
            title = stringResource(R.string.pref_dark_mode),
            items = DarkMode.values().asList(),
            selected = nightMode,
            label = {
                @StringRes val id = when (it) {
                    DarkMode.Default -> R.string.dark_mode_default
                    DarkMode.Dark -> R.string.dark_mode_dark
                    DarkMode.Light -> R.string.dark_mode_light
                }
                stringResource(id)
            },
            onItemChanged = { viewModel.setNightMode(it) },
            icon = R.drawable.ic_pref_dark_mode
        )

        PreferenceDivider()

        RegularPreference(
            title = stringResource(R.string.pref_schedule),
            subtitle = stringResource(R.string.pref_schedule_summary),
            onClick = navigateToSchedule,
            icon = R.drawable.ic_pref_schedule
        )

        /*
        RegularPreference(
            title = stringResource(R.string.pref_widget),
            subtitle = stringResource(R.string.pref_widget_summary),
            onClick = { },
            icon = R.drawable.ic_pref_widgets
        )
         */

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            RegularPreference(
                title = stringResource(R.string.pref_notification),
                subtitle = stringResource(R.string.pref_notification_summary),
                onClick = {
                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    context.startActivity(intent)
                },
                icon = R.drawable.ic_pref_notifications
            )
        }

        PreferenceDivider()

        RegularPreference(
            title = stringResource(R.string.terms_and_conditions),
            subtitle = stringResource(R.string.terms_and_conditions_summary),
            onClick = {
                BrowserUtils.openLink(
                    context = context,
                    url = "https://nikololoshka.github.io/#/stankin-schedule/terms"
                )
            },
            icon = R.drawable.ic_terms
        )

        RegularPreference(
            title = stringResource(R.string.privacy_policy),
            subtitle = stringResource(R.string.privacy_policy_summary),
            onClick = {
                BrowserUtils.openLink(
                    context = context,
                    url = "https://nikololoshka.github.io/#/stankin-schedule/policy"
                )
            },
            icon = R.drawable.ic_privacy_policy
        )

        PreferenceSpacer()

        Image(
            painter = painterResource(R.drawable.logo_about),
            contentDescription = null,
            modifier = Modifier
                .sizeIn(maxHeight = 200.dp, minWidth = 200.dp)
        )

        Text(
            text = stringResource(R.string.version) + BuildConfig.APP_VERSION,
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = stringResource(R.string.about_description_developer),
            style = MaterialTheme.typography.titleSmall
        )

        PreferenceSpacer()

        Text(
            text = stringResource(R.string.about_description_text),
            modifier = Modifier.fillMaxWidth(),
        )

        PreferenceSpacer()
    }
}
