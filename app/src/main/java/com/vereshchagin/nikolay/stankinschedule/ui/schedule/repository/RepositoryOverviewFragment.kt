package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.appbar.AppBarLayout
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.FragmentRepositoryOverviewBinding
import com.vereshchagin.nikolay.stankinschedule.databinding.ViewErrorWithButtonBinding
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.v1.CategoryEntry
import com.vereshchagin.nikolay.stankinschedule.ui.BaseFragment
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.paging.RepositoryItemAdapter
import com.vereshchagin.nikolay.stankinschedule.utils.ExceptionUtils
import com.vereshchagin.nikolay.stankinschedule.utils.State
import com.vereshchagin.nikolay.stankinschedule.utils.StatefulLayout2
import com.vereshchagin.nikolay.stankinschedule.utils.delegates.FragmentDelegate
import com.vereshchagin.nikolay.stankinschedule.utils.extensions.createBinding

/**
 * Фрагмент стартовой страницы удаленного репозитория.
 */
class RepositoryOverviewFragment : BaseFragment<FragmentRepositoryOverviewBinding>() {

    private lateinit var viewModel: RepositoryOverviewViewModel
    private var stateful: StatefulLayout2 by FragmentDelegate()

    override fun onInflateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): FragmentRepositoryOverviewBinding {
        return FragmentRepositoryOverviewBinding.inflate(inflater, container, false)
    }

    override fun onPostCreateView(savedInstanceState: Bundle?) {
        stateful = StatefulLayout2.Builder(binding.repositoryLayout)
            .init(StatefulLayout2.LOADING, binding.repositoryLoading.root)
            .addView(StatefulLayout2.CONTENT, binding.repositoryContainer)
            .addView(StatefulLayout2.ERROR, binding.repositoryError)
            .create()

        viewModel = ViewModelProvider(
            this, RepositoryOverviewViewModel.Factory(requireActivity().application)
        ).get(RepositoryOverviewViewModel::class.java)
        setActionBarTitle(getString(R.string.repository))

        binding.appBarRepository.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
                binding.repositoryRefresh.isEnabled = verticalOffset == 0
            }
        )
        binding.repositoryRefresh.setOnRefreshListener(this::onUpdateClicked)

        // описание репозитория
        viewModel.description.observe(viewLifecycleOwner) { state ->
            when (state) {
                is State.Loading -> {
                    stateful.setState(StatefulLayout2.LOADING)
                }
                is State.Success -> {
                    binding.lastUpdate = state.data.lastUpdateString()
                    stateful.setState(StatefulLayout2.CONTENT)
                }
                is State.Failed -> {
                    val description = ExceptionUtils.errorDescription(state.error, requireContext())
                    binding.repositoryError.createBinding<ViewErrorWithButtonBinding>()?.let {
                        it.errorTitle.text = description
                        it.errorAction.setOnClickListener { onUpdateClicked() }
                    }
                    stateful.setState(StatefulLayout2.ERROR)
                }
            }
        }

        val adapter = RepositoryItemAdapter(this::onCategoryClicked)
        binding.repositoryCategories.adapter = adapter

        // корневые категории
        viewModel.categories.observe(viewLifecycleOwner) { data ->
            adapter.submitData(lifecycle, data)
        }
    }

    /**
     * Вызывается, когда необходимо обновить данные репозитория.
     */
    private fun onUpdateClicked() {
        viewModel.updateRepository(false)
        binding.repositoryRefresh.isRefreshing = false
    }

    /**
     * Вызывается при нажатии на категории в списке.
     */
    private fun onCategoryClicked(category: CategoryEntry) {
        navigateTo(
            R.id.to_repository_category,
            RepositoryCategoryFragment.createBundle(category.id, category.name)
        )
    }
}