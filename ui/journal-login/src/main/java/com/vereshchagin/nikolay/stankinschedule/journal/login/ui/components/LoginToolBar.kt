package com.vereshchagin.nikolay.stankinschedule.journal.login.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.vereshchagin.nikolay.stankinschedule.journal.login.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginToolBar(
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.journal_login_title),
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}