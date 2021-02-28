package com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.name

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.DialogScheduleNameEditorBinding
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepositoryKt
import com.vereshchagin.nikolay.stankinschedule.utils.extensions.focusAndShowKeyboard
import kotlinx.coroutines.launch

class ScheduleNameEditorDialog : DialogFragment() {

    private var _binding: DialogScheduleNameEditorBinding? = null
    private val binding get() = _binding!!

    private var scheduleName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.AppDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogScheduleNameEditorBinding.inflate(layoutInflater)

        scheduleName = arguments?.getString(SCHEDULE_NAME)

        if (binding.scheduleName.text.isNullOrEmpty()) {
            binding.scheduleName.setText(scheduleName)
        }

        binding.scheduleName.doAfterTextChanged { text ->
            if (text.isNullOrEmpty()) {
                binding.scheduleNameLayout.error = getString(R.string.schedule_editor_empty_name)
                return@doAfterTextChanged
            } else {
                binding.scheduleNameLayout.error = null
            }
            binding.scheduleNameLayout.error = null
        }

        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        binding.okButton.setOnClickListener {
            val name = binding.scheduleName.text.toString()
            onScheduleNameChanged(name)
        }

        return binding.root
    }

    private fun onScheduleNameChanged(scheduleName: String) {
        if (scheduleName.isEmpty()) {
            binding.scheduleNameLayout.error = getString(R.string.schedule_editor_empty_name)
        }

        lifecycleScope.launch {
            val repository = ScheduleRepositoryKt(requireContext())
            val exist = repository.isScheduleExist(scheduleName)
            if (exist) {
                binding.scheduleNameLayout.error = getString(R.string.schedule_editor_exists)
            } else {
                setResult(bundleOf(SCHEDULE_NAME to scheduleName))
                dismiss()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(MATCH_PARENT, WRAP_CONTENT)
        }
        binding.scheduleName.focusAndShowKeyboard()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setResult(bundle: Bundle) {
        setFragmentResult(REQUEST_SCHEDULE_NAME, bundle)
    }

    companion object {

        const val REQUEST_SCHEDULE_NAME = "request_schedule_name"
        const val SCHEDULE_NAME = "schedule_name"

        @JvmStatic
        fun newInstance(scheduleName: String?) = ScheduleNameEditorDialog().apply {
            arguments = bundleOf(SCHEDULE_NAME to scheduleName)
        }
    }
}