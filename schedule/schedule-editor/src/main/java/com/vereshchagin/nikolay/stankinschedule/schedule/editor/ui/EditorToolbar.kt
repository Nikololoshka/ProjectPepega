package com.vereshchagin.nikolay.stankinschedule.schedule.editor.ui

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.BackButton
import com.vereshchagin.nikolay.stankinschedule.schedule.editor.R

@Composable
fun EditorToolbar(
    onApplyClicked: () -> Unit,
    onBackClicked: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    SmallTopAppBar(
        title = {
            Text(text = stringResource(R.string.editor_title))
        },
        navigationIcon = {
            BackButton(onClick = onBackClicked)
        },
        actions = {
            IconButton(onClick = onApplyClicked) {
                Icon(
                    painter = painterResource(R.drawable.ic_done),
                    contentDescription = null
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}