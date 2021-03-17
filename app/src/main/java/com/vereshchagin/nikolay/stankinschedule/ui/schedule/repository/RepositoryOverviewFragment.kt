package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.appbar.AppBarLayout
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.FragmentRepositoryOverviewBinding
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.v1.CategoryEntry
import com.vereshchagin.nikolay.stankinschedule.ui.BaseFragment
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.paging.RepositoryItemAdapter
import com.vereshchagin.nikolay.stankinschedule.utils.StatefulLayout2
import com.vereshchagin.nikolay.stankinschedule.utils.delegates.FragmentDelegate

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

        val adapter = RepositoryItemAdapter(this::onCategoryClicked)
        binding.repositoryCategories.adapter = adapter

        // корневые категории
        viewModel.categories.observe(viewLifecycleOwner) {
            adapter.submitData(lifecycle, it)
            stateful.setState(StatefulLayout2.CONTENT)
        }
    }

    /**
     *
     */
    private fun onCategoryClicked(category: CategoryEntry) {
        navigateTo(
            R.id.to_repository_category,
            RepositoryCategoryFragment.createBundle(category.id, category.name)
        )
    }
}