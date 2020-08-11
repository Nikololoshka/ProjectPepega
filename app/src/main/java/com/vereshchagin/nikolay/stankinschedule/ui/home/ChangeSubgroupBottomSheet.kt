package com.vereshchagin.nikolay.stankinschedule.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.DialogChangeSubgroupBinding

class ChangeSubgroupBottomSheet : BottomSheetDialogFragment() {

    private var _binding: DialogChangeSubgroupBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogChangeSubgroupBinding.inflate(inflater, container, false)

        binding.subgroupSelector.check(R.id.subgroup_common)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}