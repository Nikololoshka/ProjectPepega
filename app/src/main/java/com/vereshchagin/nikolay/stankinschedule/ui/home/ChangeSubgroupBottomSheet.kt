package com.vereshchagin.nikolay.stankinschedule.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RadioGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.DialogChangeSubgroupBinding
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Subgroup
import com.vereshchagin.nikolay.stankinschedule.ui.settings.ApplicationPreference


class ChangeSubgroupBottomSheet : BottomSheetDialogFragment(), View.OnClickListener {

    private var _binding: DialogChangeSubgroupBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogChangeSubgroupBinding.inflate(inflater, container, false)

        val subgroup = ApplicationPreference.subgroup(requireContext())
        binding.subgroupSelector.check(when (subgroup) {
            Subgroup.COMMON -> R.id.subgroup_common
            Subgroup.A -> R.id.subgroup_a
            Subgroup.B -> R.id.subgroup_b
        })
        binding.selectButton.setOnClickListener(this)

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
        val id = binding.subgroupSelector.checkedRadioButtonId
        if (id == RadioGroup.NO_ID) {
            Toast.makeText(context, "No selected subgroup", Toast.LENGTH_SHORT).show()
            return
        }

        val subgroup = when (id) {
            R.id.subgroup_a -> Subgroup.A
            R.id.subgroup_b -> Subgroup.B
            else -> Subgroup.COMMON
        }

        ApplicationPreference.setSubgroup(requireContext(), subgroup)
        setResult(Intent())

        dismiss()
    }

    private fun setResult(intent: Intent) {
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
    }
}