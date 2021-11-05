package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.overview

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.AppBarLayout
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.FragmentRepositoryOverviewBinding
import com.vereshchagin.nikolay.stankinschedule.databinding.ViewErrorWithButtonBinding
import com.vereshchagin.nikolay.stankinschedule.model.schedule.remote.ScheduleCategoryEntry
import com.vereshchagin.nikolay.stankinschedule.ui.BaseFragment
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.category.RepositoryCategoryFragment
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.paging.RepositoryItemAdapter
import com.vereshchagin.nikolay.stankinschedule.utils.ExceptionUtils
import com.vereshchagin.nikolay.stankinschedule.utils.State
import com.vereshchagin.nikolay.stankinschedule.utils.StatefulLayout2
import com.vereshchagin.nikolay.stankinschedule.utils.delegates.FragmentDelegate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

/**
 * Фрагмент стартовой страницы удаленного репозитория.
 */
@AndroidEntryPoint
class RepositoryOverviewFragment :
    BaseFragment<FragmentRepositoryOverviewBinding>(FragmentRepositoryOverviewBinding::inflate) {

    /**
     * ViewModel фрагмента.
     */
    private val viewModel: RepositoryOverviewViewModel by viewModels()

    private var stateful: StatefulLayout2 by FragmentDelegate()


    override fun onPostCreateView(savedInstanceState: Bundle?) {
        stateful = StatefulLayout2.Builder(binding.repositoryLayout)
            .init(StatefulLayout2.LOADING, binding.repositoryLoading.root)
            .addView(StatefulLayout2.CONTENT, binding.repositoryContainer)
            .addView(StatefulLayout2.ERROR, binding.repositoryError)
            .create()
        setActionBarTitle(getString(R.string.repository))

        binding.appBarRepository.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
                binding.repositoryRefresh.isEnabled = verticalOffset == 0
            }
        )
        binding.repositoryRefresh.setOnRefreshListener(this::onUpdateClicked)

        // описание репозитория
        lifecycleScope.launchWhenStarted {
            viewModel.description.collectLatest { state ->
                when (state) {
                    is State.Loading -> {
                        stateful.setState(StatefulLayout2.LOADING)
                    }
                    is State.Success -> {
                        binding.lastUpdate = state.data.lastUpdate
                        stateful.setState(StatefulLayout2.CONTENT)
                    }
                    is State.Failed -> {
                        val description =
                            ExceptionUtils.errorDescription(state.error, requireContext())

                        binding.repositoryError.createBinding<ViewErrorWithButtonBinding>()?.let {
                            it.errorTitle.text = description
                            it.errorAction.setOnClickListener { onUpdateClicked() }
                        }

                        stateful.setState(StatefulLayout2.ERROR)
                    }
                }
                binding.repositoryRefresh.isRefreshing = false
            }
        }

        val adapter = RepositoryItemAdapter(this::onCategoryClicked)
        binding.repositoryCategories.adapter = adapter

        // корневые категории
        lifecycleScope.launchWhenCreated {
            viewModel.categories.collectLatest { data ->
                adapter.submitData(data)
            }
        }
    }

    /**
     * Вызывается, когда необходимо обновить данные репозитория.
     */
    private fun onUpdateClicked() {
        viewModel.updateRepository(false)
    }

    /**
     * Вызывается при нажатии на категории в списке.
     */
    private fun onCategoryClicked(category: ScheduleCategoryEntry) {
        navigateTo(
            R.id.to_repository_category,
            RepositoryCategoryFragment.createBundle(
                category.id,
                category.name
            )
        )
    }
}