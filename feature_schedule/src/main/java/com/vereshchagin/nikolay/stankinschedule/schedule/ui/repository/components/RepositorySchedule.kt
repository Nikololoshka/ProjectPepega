package com.vereshchagin.nikolay.stankinschedule.schedule.ui.repository.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.schedule.domain.model.RepositoryItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepositorySchedule(
    item: RepositoryItem,
    onItemClicked: (item: RepositoryItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
    ) {
        Text(
            text = item.name,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onItemClicked(item) }
                .padding(
                    horizontal = Dimen.ContentPadding,
                    vertical = Dimen.ContentPadding * 2
                )
        )
    }
}