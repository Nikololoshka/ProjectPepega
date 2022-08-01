package com.vereshchagin.nikolay.stankinschedule.news.ui.viewer.components

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.BackButton
import com.vereshchagin.nikolay.stankinschedule.news.R


@Composable
fun NewsViewerToolBar(
    title: String?,
    onBackPressed: () -> Unit,
    onOpenInBrowser: () -> Unit,
    onShareNews: () -> Unit,
    onUpdateNews: () -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }

    LargeTopAppBar(
        title = {
            Text(
                text = title ?: stringResource(R.string.news_viewer_title),
                style = MaterialTheme.typography.titleLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {
                        Toast.makeText(context, title, Toast.LENGTH_SHORT).show()
                    }
            )
        },
        actions = {

            IconButton(onClick = { showMenu = !showMenu }) {
                Icon(Icons.Default.MoreVert, contentDescription = null)
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = {
                        Text(text = stringResource(R.string.news_open_in_browser))
                    },
                    onClick = {
                        onOpenInBrowser()
                        showMenu = false
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(text = stringResource(R.string.news_share))
                    },
                    onClick = {
                        onShareNews()
                        showMenu = false
                    },
                )
                DropdownMenuItem(
                    text = {
                        Text(text = stringResource(R.string.news_update))
                    },
                    onClick = {
                        onUpdateNews()
                        showMenu = false
                    },
                )
            }
        },
        navigationIcon = {
            BackButton(onClick = onBackPressed)
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}