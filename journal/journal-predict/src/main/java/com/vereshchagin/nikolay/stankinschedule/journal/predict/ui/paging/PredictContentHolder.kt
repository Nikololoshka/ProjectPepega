package com.vereshchagin.nikolay.stankinschedule.journal.predict.ui.paging

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.AppTheme
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.journal.predict.domain.model.PredictMark

class PredictContentHolder(
    private val onMarkChange: (mark: PredictMark, value: Int) -> Unit,
    composeView: ComposeView,
) : ComposeRecyclerHolder(composeView) {

    @OptIn(ExperimentalMaterial3Api::class)
    fun bind(data: PredictAdapter.ContentItem) {
        composeView.setContent {
            AppTheme {

                var currentValue by remember { mutableStateOf(data.mark.value) }

                OutlinedTextField(
                    value = if (currentValue == 0) "" else currentValue.toString(),
                    onValueChange = {
                        val number = when {
                            it.isEmpty() -> 0
                            else -> it.toIntOrNull()
                        }
                        if (number != null) {
                            currentValue = number
                            onMarkChange(data.mark, number)
                        }
                    },
                    isError = currentValue == 0,
                    label = { Text(text = data.mark.type.tag) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp, horizontal = Dimen.ContentPadding)
                )
            }
        }
    }
}