package com.vereshchagin.nikolay.stankinschedule.schedule.editor.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.BackButton
import com.vereshchagin.nikolay.stankinschedule.schedule.editor.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorToolbar(
    onApplyClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    onBackClicked: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    TopAppBar(
        title = {
            Text(text = stringResource(R.string.editor_title))
        },
        navigationIcon = {
            BackButton(onClick = onBackClicked)
        },
        actions = {
            IconButton(onClick = onDeleteClicked) {
                Icon(
                    painter = painterResource(R.drawable.ic_delete_pair),
                    contentDescription = null
                )
            }
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