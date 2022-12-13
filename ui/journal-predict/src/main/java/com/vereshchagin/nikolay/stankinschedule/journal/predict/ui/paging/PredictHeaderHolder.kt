package com.vereshchagin.nikolay.stankinschedule.journal.predict.ui.paging

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.ComposeView
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.AppTheme
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen

class PredictHeaderHolder(
    composeView: ComposeView,
) : ComposeRecyclerHolder(composeView) {

    fun bind(data: PredictAdapter.HeaderItem) {
        composeView.setContent {
            AppTheme {
                Card(
                    shape = RectangleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.background
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = data.discipline,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(Dimen.ContentPadding)
                    )
                }
            }
        }
    }
}