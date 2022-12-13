package com.vereshchagin.nikolay.stankinschedule.schedule.repository.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> FilterRow(
    selected: T?,
    items: List<T>,
    title: @Composable (item: T) -> String,
    onItemSelected: (item: T) -> Unit,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    itemSpacing: Dp = 2.dp,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = Dimen.ContentPadding),
        horizontalArrangement = Arrangement.spacedBy(itemSpacing),
        modifier = modifier
    ) {
        items(items) { item ->
            FilterChip(
                selected = selected == item,
                onClick = { onItemSelected(item) },
                leadingIcon = if (selected == item) {
                    {
                        Icon(
                            painter = painterResource(R.drawable.ic_chip_selected),
                            contentDescription = null
                        )
                    }
                } else {
                    null
                },
                label = {
                    Text(
                        text = title(item),
                        color = contentColor,
                    )
                },
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = MaterialTheme.colorScheme.outline,
                    selectedBorderColor = MaterialTheme.colorScheme.outline,
                    borderWidth = 1.dp,
                    selectedBorderWidth = 1.dp
                ),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = containerColor,
                ),
                modifier = Modifier.animateContentSize()
            )
        }
    }
}