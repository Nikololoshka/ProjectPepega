package com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.name

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.DialogScheduleNameEditorBinding
import com.vereshchagin.nikolay.stankinschedule.ui.settings.SchedulePreference
import com.vereshchagin.nikolay.stankinschedule.utils.focusAndShowKeyboard

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
    ): View? {
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

            for (word in SchedulePreference.banCharacters()) {
                if (text.contains(word)) {
                    binding.scheduleNameLayout.error = getString(R.string.schedule_editor_not_allowed_character)
                    return@doAfterTextChanged
                }
            }
            binding.scheduleNameLayout.error = null
        }

        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        binding.okButton.setOnClickListener {
            val name = binding.scheduleName.text.toString()
            if (name.isEmpty()) {
                binding.scheduleNameLayout.error = getString(R.string.schedule_editor_empty_name)
                return@setOnClickListener
            }

            val isExist = SchedulePreference.contains(requireContext(), name)
            if (isExist) {
                binding.scheduleNameLayout.error = getString(R.string.schedule_editor_exists)
                return@setOnClickListener
            }

            for (word in SchedulePreference.banCharacters()) {
                if (name.contains(word)) {
                    binding.scheduleNameLayout.error = getString(R.string.schedule_editor_not_allowed_character)
                    return@setOnClickListener
                }
            }

            val intent = Intent()
            intent.putExtra(SCHEDULE_NAME, name)
            setResult(intent)
            dismiss()
        }

        return binding.root
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

    private fun setResult(intent: Intent) {
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
    }

    companion object {

        const val SCHEDULE_NAME = "schedule_name"

        @JvmStatic
        fun newInstance(scheduleName: String?) = ScheduleNameEditorDialog().apply {
            val args = Bundle()
            args.putString(SCHEDULE_NAME, scheduleName)
            arguments = args
        }
    }
}