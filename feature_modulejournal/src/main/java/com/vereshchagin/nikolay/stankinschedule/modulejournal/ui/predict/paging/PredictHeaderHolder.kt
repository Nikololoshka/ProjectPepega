package com.vereshchagin.nikolay.stankinschedule.modulejournal.ui.predict.paging

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.ComposeView
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.AppTheme
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen

class PredictHeaderHolder(
    composeView: ComposeView,
) : ComposeRecyclerHolder(composeView) {

    @OptIn(ExperimentalMaterial3Api::class)
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