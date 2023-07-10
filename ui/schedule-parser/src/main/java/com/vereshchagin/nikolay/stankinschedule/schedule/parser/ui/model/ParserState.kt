package com.vereshchagin.nikolay.stankinschedule.schedule.parser.ui.model

import android.graphics.Bitmap
import com.vereshchagin.nikolay.stankinschedule.core.ui.components.UIState
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.domain.model.ParserSettings

abstract class ParserState(val step: Int) {


    open val isSuccess: Boolean get() = true

    class SelectFile(
        val file: SelectedFile? = null,
        val preview: Bitmap? = null
    ) : ParserState(1) {
        override val isSuccess: Boolean get() = file != null
    }

    class Settings(val settings: ParserSettings) : ParserState(2)

    class ParserResult(
        val state: UIState<ParsedFile>
    ) : ParserState(3) {
        override val isSuccess: Boolean get() = state is UIState.Success
    }

    class SaveResult(
        val scheduleName: String
    ) : ParserState(4)

    class ImportFinish : ParserState(5)

    companion object {
        const val STEP_TOTAL = 5
    }
}