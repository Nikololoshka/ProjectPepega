package com.vereshchagin.nikolay.stankinschedule.journal.predict.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vereshchagin.nikolay.stankinschedule.core.ui.theme.Dimen
import com.vereshchagin.nikolay.stankinschedule.journal.predict.ui.components.SemesterSelectorBottomSheet

class SemesterSelectorDialog : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val semesters: List<String>? = requireArguments().getStringArrayList(SEMESTERS)
        requireNotNull(semesters)

        val currentSemester: String? = requireArguments().getString(CURRENT_SEMESTER)
        requireNotNull(currentSemester)

        return ComposeView(inflater.context).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                SemesterSelectorBottomSheet(
                    currentSemester = currentSemester,
                    semesters = semesters,
                    onSemesterSelected = ::semesterSelected,
                    modifier = Modifier.padding(vertical = Dimen.ContentPadding * 2)
                )
            }
        }
    }

    private fun semesterSelected(semester: String) {
        setFragmentResult(REQUEST_SEMESTER_SELECTOR, bundleOf(RESULT_SEMESTER to semester))
        dismiss()
    }

    companion object {

        private const val SEMESTERS = "semesters"
        private const val CURRENT_SEMESTER = "current_semester"

        const val REQUEST_SEMESTER_SELECTOR = "request_semester_selector"
        const val RESULT_SEMESTER = "result_semester"

        fun newInstance(semesters: List<String>, currentSemester: String): SemesterSelectorDialog {
            return SemesterSelectorDialog().apply {
                arguments = bundleOf(
                    SEMESTERS to semesters,
                    CURRENT_SEMESTER to currentSemester
                )
            }
        }
    }
}