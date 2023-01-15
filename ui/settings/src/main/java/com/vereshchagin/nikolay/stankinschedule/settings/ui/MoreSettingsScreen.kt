package com.vereshchagin.nikolay.stankinschedule.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.vereshchagin.nikolay.stankinschedule.settings.ui.components.SettingsScaffold
import com.vereshchagin.nikolay.stankinschedule.settings.ui.components.SwitchPreference

@Composable
fun MoreSettingsScreen(
    viewModel: SettingsViewModel,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    SettingsScaffold(
        title = stringResource(R.string.settings_more_title),
        onBackPressed = onBackPressed,
        modifier = modifier
    ) {
        val isAnalyticsEnabled by viewModel.isAnalyticsEnabled.collectAsState()

        SwitchPreference(
            title = stringResource(R.string.pref_send_analytics_data),
            subtitle = stringResource(R.string.pref_send_analytics_data_summary),
            checked = isAnalyticsEnabled,
            onCheckedChange = viewModel::setAnalyticsEnabled
        )
    }
}