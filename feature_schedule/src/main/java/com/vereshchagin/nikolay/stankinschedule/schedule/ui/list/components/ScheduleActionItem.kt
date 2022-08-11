package com.vereshchagin.nikolay.stankinschedule.schedule.ui.list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.schedule.R
import com.vereshchagin.nikolay.stankinschedule.schedule.domain.model.ScheduleInfo
import org.burnoutcrew.reorderable.ReorderableLazyListState
import org.burnoutcrew.reorderable.detectReorder

@Composable
fun ScheduleActionItem(
    schedule: ScheduleInfo,
    isSelected: Boolean,
    onClicked: () -> Unit,
    reorderedState: ReorderableLazyListState,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimen.ContentPadding),
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClicked
            )
            .padding(start = Dimen.ContentPadding)
            .let {
                if (isSelected) {
                    it.background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(16.dp, 0.dp, 0.dp, 16.dp)
                    )
                } else {
                    it
                }
            }
            .padding(
                start = Dimen.ContentPadding,
                end = Dimen.ContentPadding * 0.5f,
                top = 6.dp,
                bottom = 6.dp
            )

    ) {
        Text(
            text = schedule.scheduleName,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            onClick = {},
            enabled = true,
            modifier = Modifier.detectReorder(reorderedState)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_drag_handle),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}