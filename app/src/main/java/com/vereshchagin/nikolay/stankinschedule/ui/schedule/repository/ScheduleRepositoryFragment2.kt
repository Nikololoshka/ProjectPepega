package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.vereshchagin.nikolay.stankinschedule.databinding.FragmentScheduleRepository2Binding
import com.vereshchagin.nikolay.stankinschedule.ui.BaseFragment
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.paging.RepositoryCategoryAdapter
import com.vereshchagin.nikolay.stankinschedule.utils.State

/**
 * Удаленный репозиторий с расписаниеями.
 */
class ScheduleRepositoryFragment2 : BaseFragment<FragmentScheduleRepository2Binding>() {

    private val viewModel by viewModels<ScheduleRepositoryViewModel> {
        ScheduleRepositoryViewModel.Factory(activity?.application!!)
    }

    override fun onInflateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentScheduleRepository2Binding {
        return FragmentScheduleRepository2Binding.inflate(inflater, container, false)
    }

    override fun onPostCreateView(savedInstanceState: Bundle?) {
        // описание репозитория
        viewModel.description.observe(viewLifecycleOwner) { state ->
            when (state) {
                is State.Success -> {
                    binding.description = state.data
                }
                is State.Loading -> {
                    Log.d(TAG, "onPostCreateView: loading...")
                }
                is State.Failed -> {
                    Log.d(TAG, "onPostCreateView: ${state.error}")
                }
            }
        }

        val adapter = RepositoryCategoryAdapter()
        binding.repositoryCategories.adapter = adapter

        // добавление категорий в ViewPager2
        viewModel.categories.observe(viewLifecycleOwner) {
            val data = it ?: return@observe
            adapter.submitData(lifecycle, data)
        }
    }

    companion object {
        const val TAG = "ScheduleRepositoryLog"
    }
}