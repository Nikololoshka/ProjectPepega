package com.vereshchagin.nikolay.stankinschedule.schedule.editor.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.datepicker.MaterialDatePicker
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.joda.time.LocalDate

@AndroidEntryPoint
class PairEditorActivity : AppCompatActivity() {

    private val viewModel: PairEditorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Получение ID пары
        var pairId: Long? = intent.getLongExtra(PAIR_ID, -1L)
        if (pairId == -1L) pairId = null

        setContent {
            AppTheme {
                PairEditorScreen(
                    pairId = pairId,
                    onBackClicked = {
                        onBackPressed()
                    },
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.pickerRequests.collectLatest {
                    showDatePicker(it)
                }
            }
        }
    }

    private fun showDatePicker(request: DateRequest) {
        val dialog = MaterialDatePicker.Builder.datePicker()
            .setTitleText(request.title)
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .setSelection(request.selectedDate.toDateTimeAtCurrentTime().millis)
            .build()

        dialog.addOnPositiveButtonClickListener {
            viewModel.onDateResult(DateResult(request.id, LocalDate(it)))
        }

        dialog.show(supportFragmentManager, DATE_PICKER_TAG)
    }

    companion object {
        private const val DATE_PICKER_TAG = "date_picker_tag"

        private const val PAIR_ID = "pair_id"

        fun createIntent(context: Context, pairId: Long?): Intent {
            return Intent(context, PairEditorActivity::class.java).apply {
                putExtra(PAIR_ID, pairId)
            }
        }
    }
}