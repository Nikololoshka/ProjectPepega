package com.vereshchagin.nikolay.stankinschedule.journal.viewer.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.vereshchagin.nikolay.stankinschedule.journal.viewer.R
import com.vereshchagin.nikolay.stankinschedule.core.R as R_core

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalToolBar(
    onPredictAction: () -> Unit,
    onSignOutAction: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.journal_title),
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    var showMenu by remember { mutableStateOf(false) }

    TopAppBar(
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

            IconButton(onClick = { showMenu = !showMenu }) {
                Icon(
                    painter = painterResource(R_core.drawable.ic_action_more),
                    contentDescription = null
                )
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = {
                        Text(text = stringResource(R.string.sign_out))
                    },
                    onClick = {
                        onSignOutAction()
                        showMenu = false
                    }
                )
            }

        },
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}