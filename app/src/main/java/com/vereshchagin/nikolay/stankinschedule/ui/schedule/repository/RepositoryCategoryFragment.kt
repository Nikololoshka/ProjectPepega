package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.FragmentRepositoryCategoryBinding
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.v1.CategoryEntry
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.v1.RepositoryItem
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.v1.ScheduleEntry
import com.vereshchagin.nikolay.stankinschedule.ui.BaseFragment
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.paging.RepositoryItemAdapter
import com.vereshchagin.nikolay.stankinschedule.utils.StatefulLayout2
import com.vereshchagin.nikolay.stankinschedule.utils.delegates.FragmentDelegate

/**
 * Фрагмент категории в удаленном репозитории.
 */
class RepositoryCategoryFragment :
    BaseFragment<FragmentRepositoryCategoryBinding>(FragmentRepositoryCategoryBinding::inflate) {

    private lateinit var viewModel: RepositoryCategoryViewModel
    private var stateful: StatefulLayout2 by FragmentDelegate()


    override fun onPostCreateView(savedInstanceState: Bundle?) {

        stateful = StatefulLayout2.Builder(binding.categoryContainer)
            .init(StatefulLayout2.LOADING, binding.categoryLoading.root)
            .addView(StatefulLayout2.CONTENT, binding.categoryItems)
            .create()

        val arguments = requireArguments()
        val parent = arguments.getInt(EXTRA_PARENT)
        val title = arguments.getString(EXTRA_TITLE)

        viewModel = ViewModelProvider(
            this, RepositoryCategoryViewModel.Factory(requireActivity().application, parent)
        ).get(RepositoryCategoryViewModel::class.java)
        setActionBarTitle(title)

        val adapter = RepositoryItemAdapter(this::onRepositoryItemClicked)
        binding.categoryItems.adapter = adapter

        viewModel.categories.observe(this) {
            val data = it ?: return@observe
            adapter.submitData(lifecycle, data)
            stateful.setState(StatefulLayout2.CONTENT)
        }
    }

    /**
     * Вызывается при нажатие на элемент в списке.
     */
    private fun onRepositoryItemClicked(item: RepositoryItem) {
        when (item) {
            // нажата категория
            is CategoryEntry -> {
                navigateTo(R.id.to_repository_category_self, createBundle(item.id, item.name))
            }
            // нажато расписание
            is ScheduleEntry -> {
                navigateTo(
                    R.id.to_repository_schedule,
                    RepositoryScheduleFragment.createBundle(item.id, item.name)
                )
            }
        }
    }

    companion object {

        private const val EXTRA_PARENT = "extra_parent"
        private const val EXTRA_TITLE = "extra_title"

        /**
         * Создает bundle с параметрами, необходимых для перехода к фрагменту.
         */
        fun createBundle(parent: Int, title: String): Bundle {
            return bundleOf(EXTRA_PARENT to parent, EXTRA_TITLE to title)
        }
    }
}