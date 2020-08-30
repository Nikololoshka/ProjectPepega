package com.vereshchagin.nikolay.stankinschedule.ui.schedule.myschedules

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.DialogAddScheduleBinding

class AddScheduleBottomSheet : BottomSheetDialogFragment(), View.OnClickListener {

    private var _binding: DialogAddScheduleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogAddScheduleBinding.inflate(inflater, container, false)

        binding.createSchedule.setOnClickListener(this)
        binding.fromRepository.setOnClickListener(this)
        binding.loadSchedule.setOnClickListener(this)

        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            val bottomSheet = dialog?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {
        v?.id?.let {
            if (it in listOf(R.id.create_schedule, R.id.from_repository, R.id.load_schedule)) {
                val intent = Intent()
                intent.putExtra(SCHEDULE_ACTION, it)
                setResult(intent)
                dismiss()
            }
        }
    }

    private fun setResult(intent: Intent) {
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
    }

    companion object {
        const val SCHEDULE_ACTION = "schedule_action"
    }
}