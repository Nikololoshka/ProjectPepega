package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import com.vereshchagin.nikolay.stankinschedule.databinding.FragmentRepositoryScheduleBinding
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.v1.ScheduleVersionEntry
import com.vereshchagin.nikolay.stankinschedule.ui.BaseFragment
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.paging.RepositoryItemAdapter
import com.vereshchagin.nikolay.stankinschedule.utils.ExceptionUtils
import com.vereshchagin.nikolay.stankinschedule.utils.State
import com.vereshchagin.nikolay.stankinschedule.utils.StatefulLayout2
import com.vereshchagin.nikolay.stankinschedule.utils.delegates.FragmentDelegate

/**
 *
 */
class RepositoryScheduleFragment : BaseFragment<FragmentRepositoryScheduleBinding>() {

    private lateinit var viewModel: RepositoryScheduleViewModel
    private var stateful: StatefulLayout2 by FragmentDelegate()

    override fun onInflateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): FragmentRepositoryScheduleBinding {
        return FragmentRepositoryScheduleBinding.inflate(inflater, container, false)
    }

    override fun onPostCreateView(savedInstanceState: Bundle?) {
        stateful = StatefulLayout2.Builder(binding.repositoryContainer)
            .init(StatefulLayout2.LOADING, binding.versionsLoading.root)
            .addView(StatefulLayout2.CONTENT, binding.versionsContainer)
            .create()

        val arguments = requireArguments()
        val scheduleId = arguments.getInt(EXTRA_SCHEDULE_ID)
        val scheduleName = arguments.getString(EXTRA_SCHEDULE_NAME)
        setActionBarTitle(scheduleName)

        viewModel = ViewModelProvider(
            this, RepositoryScheduleViewModel.Factory(requireActivity().application, scheduleId)
        ).get(RepositoryScheduleViewModel::class.java)

        val adapter = RepositoryItemAdapter(this::onScheduleVersionClicked)
        binding.scheduleVersions.adapter = adapter

        // расписание репозитория
        viewModel.scheduleEntry.observe(viewLifecycleOwner) { state ->
            when (state) {
                is State.Success -> {
                    val data = state.data

                    binding.scheduleName = data.name
                    adapter.submitData(lifecycle, data.versionEntries())
                    stateful.setState(StatefulLayout2.CONTENT)
                }
                is State.Loading -> {
                    stateful.setState(StatefulLayout2.LOADING)
                }
                is State.Failed -> {
                    val context = requireContext()
                    val description = ExceptionUtils.errorDescription(state.error, context)
                    Toast.makeText(context, description, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun onScheduleVersionClicked(version: ScheduleVersionEntry) {
        viewModel.downloadScheduleVersion(version)
    }

    companion object {

        private const val EXTRA_SCHEDULE_ID = "extra_id"
        private const val EXTRA_SCHEDULE_NAME = "extra_name"

        /**
         *
         */
        fun createBundle(id: Int, scheduleName: String): Bundle {
            return bundleOf(EXTRA_SCHEDULE_ID to id, EXTRA_SCHEDULE_NAME to scheduleName)
        }
    }
}