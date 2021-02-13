package com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.vereshchagin.nikolay.stankinschedule.databinding.FragmentScheduleEditorBinding
import com.vereshchagin.nikolay.stankinschedule.ui.BaseFragment
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.view.paging.ScheduleEditorAdapter

/**
 * Фрагмент с списком пар для редактирования.
 */
class ScheduleEditorFragment : BaseFragment<FragmentScheduleEditorBinding>() {

    /**
     * ViewModel фрагмента.
     */
    private lateinit var viewModel: ScheduleEditorViewModel

    override fun onInflateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): FragmentScheduleEditorBinding {
        return FragmentScheduleEditorBinding.inflate(inflater, container, false)
    }

    override fun onPostCreateView(savedInstanceState: Bundle?) {
        val scheduleName = arguments?.getString(SCHEDULE_NAME)!!
        Log.d(TAG, "onPostCreateView: $scheduleName")

        viewModel = ViewModelProvider(
            this,
            ScheduleEditorViewModel.Factory(activity?.application!!, scheduleName)
        ).get(ScheduleEditorViewModel::class.java)


        val adapter = ScheduleEditorAdapter()
        binding.disciplineRecycler.adapter = adapter
        binding.disciplineRecycler.addItemDecoration(
            DividerItemDecoration(requireContext(), LinearLayout.VERTICAL)
        )

        viewModel.disciplines.observe(viewLifecycleOwner) {
            val data = it ?: return@observe
            adapter.submitData(lifecycle, data)
        }
    }

    companion object {

        private const val TAG = "ScheduleEditorLog"
        private const val SCHEDULE_NAME = "schedule_name"
    }
}