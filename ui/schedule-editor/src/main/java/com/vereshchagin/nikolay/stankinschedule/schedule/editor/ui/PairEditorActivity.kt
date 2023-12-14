package com.vereshchagin.nikolay.stankinschedule.schedule.editor.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.datepicker.MaterialDatePicker
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.AppTheme
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.Type
import com.vereshchagin.nikolay.stankinschedule.schedule.editor.ui.components.DateRequest
import com.vereshchagin.nikolay.stankinschedule.schedule.editor.ui.components.DateResult
import com.vereshchagin.nikolay.stankinschedule.schedule.editor.ui.components.EditorMode
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.joda.time.LocalDate

@AndroidEntryPoint
class PairEditorActivity : AppCompatActivity() {

    private val viewModel: PairEditorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Получение ID пары и расписания
        val scheduleId: Long = intent.getLongExtra(SCHEDULE_ID, -1L)
        var pairId: Long? = intent.getLongExtra(PAIR_ID, -1L)
        if (pairId == -1L) pairId = null

        // Preset
        val titlePreset = intent.getStringExtra(TITLE_PRESET)
        val typePresetTag = intent.getStringExtra(TYPE_PRESET)
        val typePreset = if (typePresetTag != null) Type.of(typePresetTag) else null
        val formPreset = if (titlePreset != null && typePreset != null) {
            FormPreset(titlePreset, typePreset)
        } else {
            null
        }

        setContent {
            AppTheme {
                PairEditorScreen(
                    mode = if (pairId == null) EditorMode.Create else EditorMode.Edit,
                    scheduleId = scheduleId,
                    pairId = pairId,
                    formPreset = formPreset,
                    onBackClicked = {
                        onBackPressedDispatcher.onBackPressed()
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
        private const val SCHEDULE_ID = "schedule_id"
        private const val TITLE_PRESET = "title_preset"
        private const val TYPE_PRESET = "type_preset"

        fun createIntent(
            context: Context,
            scheduleId: Long,
            pairId: Long?,
            preset: FormPreset? = null
        ): Intent {
            return Intent(context, PairEditorActivity::class.java).apply {
                putExtra(PAIR_ID, pairId)
                putExtra(SCHEDULE_ID, scheduleId)
                putExtra(TITLE_PRESET, preset?.title)
                putExtra(TYPE_PRESET, preset?.type?.tag)
            }
        }
    }
}