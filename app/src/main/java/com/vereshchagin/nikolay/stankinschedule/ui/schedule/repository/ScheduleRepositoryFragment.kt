package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.FragmentScheduleRepositoryBinding
import com.vereshchagin.nikolay.stankinschedule.ui.BaseFragment
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.paging.RepositoryCategoryAdapter
import com.vereshchagin.nikolay.stankinschedule.utils.State
import com.vereshchagin.nikolay.stankinschedule.utils.StatefulLayout2

/**
 * Удаленный репозиторий с расписаниеями.
 */
class ScheduleRepositoryFragment : BaseFragment<FragmentScheduleRepositoryBinding>() {

    private var _statefulLayout: StatefulLayout2? = null
    private val statefulLayout get() = _statefulLayout!!

    private val viewModel by viewModels<ScheduleRepositoryViewModel> {
        ScheduleRepositoryViewModel.Factory(activity?.application!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onInflateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentScheduleRepositoryBinding {
        return FragmentScheduleRepositoryBinding.inflate(inflater, container, false)
    }

    override fun onPostCreateView(savedInstanceState: Bundle?) {
        _statefulLayout = StatefulLayout2.Builder(binding.repositoryLayout)
            .init(StatefulLayout2.LOADING,  binding.repositoryLoading.root)
            .addView(StatefulLayout2.CONTENT, binding.repositoryContainer)
            .addView(StatefulLayout2.ERROR, binding.repositoryErrorLayout)
            .create()

        binding.repositoryErrorRetry.setOnClickListener {
            viewModel.update()
        }

        binding.repositoryRefresh.setOnRefreshListener {
            viewModel.update(false)
        }

        binding.appBarRepository.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
                binding.repositoryRefresh.isEnabled = verticalOffset == 0
            }
        )

        // описание репозитория
        viewModel.description.observe(viewLifecycleOwner) { state ->
            when (state) {
                is State.Success -> {
                    binding.description = state.data
                    binding.repositoryRefresh.isRefreshing = false
                    statefulLayout.setState(StatefulLayout2.CONTENT)
                }
                is State.Loading -> {
                    statefulLayout.setState(StatefulLayout2.LOADING)
                }
                is State.Failed -> {
                    binding.repositoryError.text = state.error.toString()
                    statefulLayout.setState(StatefulLayout2.ERROR)
                }
            }
        }

        val adapter = RepositoryCategoryAdapter()
        binding.repositoryCategories.adapter = adapter
        binding.repositoryCategories.offscreenPageLimit = 1

        val mediator = TabLayoutMediator(
            binding.tabCategories, binding.repositoryCategories, true
        ) { tab, position ->
            tab.text = viewModel.tabTitle(position)
        }

        binding.repositoryCategories.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    if (!mediator.isAttached) {
                        mediator.attach()
                    }
                }
            }
        )

        // добавление категорий в ViewPager2
        viewModel.categories.observe(viewLifecycleOwner) {
            val data = it ?: return@observe
            adapter.submitData(lifecycle, data)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_schedule_repository, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.repository_update) {
            viewModel.update(false)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _statefulLayout = null
    }

    companion object {
        const val TAG = "ScheduleRepositoryLog"
    }
}