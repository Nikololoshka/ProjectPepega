package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.FragmentRepositoryCategoryBinding
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.RepositoryItem
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.v1.CategoryEntry
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.v1.ScheduleEntry
import com.vereshchagin.nikolay.stankinschedule.ui.BaseFragment
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.paging.RepositoryItemAdapter
import com.vereshchagin.nikolay.stankinschedule.utils.StatefulLayout2
import com.vereshchagin.nikolay.stankinschedule.utils.delegates.FragmentDelegate

/**
 *
 */
class RepositoryCategoryFragment : BaseFragment<FragmentRepositoryCategoryBinding>() {

    private lateinit var viewModel: RepositoryCategoryViewModel
    private var stateful: StatefulLayout2 by FragmentDelegate()

    override fun onInflateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): FragmentRepositoryCategoryBinding {
        return FragmentRepositoryCategoryBinding.inflate(inflater, container, false)
    }

    override fun onPostCreateView(savedInstanceState: Bundle?) {

        val arguments = requireArguments()
        val parent = arguments.getInt(EXTRA_PARENT)
        val title = arguments.getString(EXTRA_TITLE)

        viewModel = ViewModelProvider(
            this, RepositoryCategoryViewModel.Factory(requireActivity().application, parent)
        ).get(RepositoryCategoryViewModel::class.java)
        setActionBarTitle(title)

        val adapter = RepositoryItemAdapter(this::onRepositoryItemClicked)
        binding.items.adapter = adapter

        viewModel.categories.observe(viewLifecycleOwner) {
            adapter.submitData(lifecycle, it)
        }
    }

    private fun onRepositoryItemClicked(item: RepositoryItem) {
        when (item) {
            is CategoryEntry -> {
                navigateTo(R.id.to_repository_category_self, createBundle(item.id, item.name))
            }
            is ScheduleEntry -> {
                Log.d("MyLog", "onRepositoryItemClicked: ${item.name}")
            }
        }
    }

    companion object {

        private const val EXTRA_PARENT = "extra_parent"
        private const val EXTRA_TITLE = "extra_title"

        /**
         *
         */
        fun createBundle(parent: Int, title: String): Bundle {
            return bundleOf(EXTRA_PARENT to parent, EXTRA_TITLE to title)
        }
    }
}