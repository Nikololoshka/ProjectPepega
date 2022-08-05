package com.vereshchagin.nikolay.stankinschedule.schedule.ui.repository.components

import androidx.compose.material.BackdropScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.TopAppBar
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.painterResource
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.BackButton
import com.vereshchagin.nikolay.stankinschedule.schedule.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RepositoryToolBar(
    scaffoldState: BackdropScaffoldState,
    onBackPressed: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    CompositionLocalProvider(
        LocalContentColor provides MaterialTheme.colorScheme.onSecondaryContainer
    ) {

        TopAppBar(
            title = {
                Text(
                    text = "Repository",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            navigationIcon = {
                if (scaffoldState.isRevealed) {
                    IconButton(
                        onClick = { scope.launch { scaffoldState.conceal() } }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_backdrop_close),
                            contentDescription = null
                        )
                    }
                } else {
                    BackButton(
                        onClick = onBackPressed,
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = {
                        if (scaffoldState.isRevealed) {
                            scope.launch { scaffoldState.conceal() }
                        } else {
                            scope.launch { scaffoldState.reveal() }
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_backdrop_tune),
                        contentDescription = null,
                    )
                }
            },
            backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        )
    }
}