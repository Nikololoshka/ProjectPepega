package com.vereshchagin.nikolay.stankinschedule.settings.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.vereshchagin.nikolay.stankinschedule.core.ui.ext.toHEX
import com.vereshchagin.nikolay.stankinschedule.schedule.core.ui.toColor
import com.vereshchagin.nikolay.stankinschedule.schedule.settings.domain.model.PairColorGroup
import com.vereshchagin.nikolay.stankinschedule.schedule.settings.domain.model.PairColorType
import com.vereshchagin.nikolay.stankinschedule.schedule.widget.ui.ScheduleWidget
import com.vereshchagin.nikolay.stankinschedule.settings.ui.components.PreferenceCategory
import com.vereshchagin.nikolay.stankinschedule.settings.ui.components.SettingsScaffold
import com.vereshchagin.nikolay.stankinschedule.settings.ui.components.SwitchPreference
import com.vereshchagin.nikolay.stankinschedule.settings.ui.components.color.ColorPreference

@Composable
fun ScheduleSettingsScreen(
    viewModel: SettingsViewModel,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    SettingsScaffold(
        title = stringResource(R.string.setting_schedule_title),
        onBackPressed = onBackPressed,
        modifier = modifier
    ) {
        val isVertical by viewModel.isVerticalViewer.collectAsState(false)

        SwitchPreference(
            title = stringResource(R.string.schedule_view_ver),
            subtitle = stringResource(R.string.schedule_view_ver_summary),
            checked = isVertical,
            onCheckedChange = viewModel::setVerticalViewer
        )

        PreferenceCategory(
            title = stringResource(R.string.schedule_pair_settings),
            modifier = Modifier.fillMaxWidth()
        )

        val pairColorGroup by viewModel.pairColorGroup.collectAsState(PairColorGroup.default())
        val pairColors by remember { derivedStateOf { pairColorGroup.toColor() } }
        val pairColorsDefault = PairColorGroup.default().toColor()

        val context = LocalContext.current
        LaunchedEffect(Unit) {
            viewModel.colorChanged.collect {
                ScheduleWidget.updateAllWidgets(context, true)
            }
        }

        ColorPreference(
            title = stringResource(R.string.lecture_color),
            subtitle = stringResource(R.string.color_summary),
            color = pairColors.lectureColor,
            defaultColor = pairColorsDefault.lectureColor,
            onColorChanged = { viewModel.setPairColor(it.toHEX(), PairColorType.Lecture) }
        )

        ColorPreference(
            title = stringResource(R.string.seminar_color),
            subtitle = stringResource(R.string.color_summary),
            color = pairColors.seminarColor,
            defaultColor = pairColorsDefault.seminarColor,
            onColorChanged = { viewModel.setPairColor(it.toHEX(), PairColorType.Seminar) }
        )

        ColorPreference(
            title = stringResource(R.string.laboratory_color),
            subtitle = stringResource(R.string.color_summary),
            color = pairColors.laboratoryColor,
            defaultColor = pairColorsDefault.laboratoryColor,
            onColorChanged = { viewModel.setPairColor(it.toHEX(), PairColorType.Laboratory) }
        )

        ColorPreference(
            title = stringResource(R.string.subgroup_a_color),
            subtitle = stringResource(R.string.color_summary),
            color = pairColors.subgroupAColor,
            defaultColor = pairColorsDefault.subgroupAColor,
            onColorChanged = { viewModel.setPairColor(it.toHEX(), PairColorType.SubgroupA) }
        )

        ColorPreference(
            title = stringResource(R.string.subgroup_b_color),
            subtitle = stringResource(R.string.color_summary),
            color = pairColors.subgroupBColor,
            defaultColor = pairColorsDefault.subgroupBColor,
            onColorChanged = { viewModel.setPairColor(it.toHEX(), PairColorType.SubgroupB) }
        )
    }
}
