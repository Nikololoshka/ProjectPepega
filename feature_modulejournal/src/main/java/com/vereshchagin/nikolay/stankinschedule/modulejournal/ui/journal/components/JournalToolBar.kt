package com.vereshchagin.nikolay.stankinschedule.modulejournal.ui.journal.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.vereshchagin.nikolay.stankinschedule.modulejournal.R

@Composable
fun JournalToolBar(
    onPredictAction: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.journal_title),
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    SmallTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        actions = {
            IconButton(onClick = onPredictAction) {
                Icon(
                    painter = painterResource(R.drawable.ic_predict),
                    contentDescription = null
                )
            }
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}