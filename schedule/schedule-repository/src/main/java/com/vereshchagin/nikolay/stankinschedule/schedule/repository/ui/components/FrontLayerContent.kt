package com.vereshchagin.nikolay.stankinschedule.schedule.repository.ui.components

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.BackdropScaffoldDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.R
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.domain.model.RepositoryItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FrontLayerContent(
    repositoryItems: List<RepositoryItem>,
    onItemClicked: (item: RepositoryItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentSchedules by animateIntAsState(targetValue = repositoryItems.size)

    Column(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimen.ContentPadding)
                .height(BackdropScaffoldDefaults.HeaderHeight)
        ) {
            Text(
                text = stringResource(R.string.repository_schedules_filter, currentSchedules),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
            )
        }

        Divider(
            modifier = Modifier
                .padding(horizontal = Dimen.ContentPadding)
                .fillMaxWidth()
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(count = 2),
            contentPadding = PaddingValues(Dimen.ContentPadding),
            verticalArrangement = Arrangement.spacedBy(Dimen.ContentPadding),
            horizontalArrangement = Arrangement.spacedBy(Dimen.ContentPadding),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (repositoryItems.isEmpty()) {
                item(span = { GridItemSpan(currentLineSpan = 2) }) {
                    Text(
                        text = stringResource(R.string.repository_no_schedules_found),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            items(repositoryItems) { item ->
                RepositorySchedule(
                    item = item,
                    onItemClicked = onItemClicked,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}