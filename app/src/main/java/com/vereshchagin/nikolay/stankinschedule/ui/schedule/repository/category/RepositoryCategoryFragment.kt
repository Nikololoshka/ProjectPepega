package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.category

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.FragmentRepositoryCategoryBinding
import com.vereshchagin.nikolay.stankinschedule.model.schedule.remote.ScheduleCategoryEntry
import com.vereshchagin.nikolay.stankinschedule.model.schedule.remote.ScheduleItemEntry
import com.vereshchagin.nikolay.stankinschedule.model.schedule.remote.ScheduleRepositoryItem
import com.vereshchagin.nikolay.stankinschedule.ui.BaseFragment
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.paging.RepositoryItemAdapter
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.updates.RepositoryScheduleFragment
import com.vereshchagin.nikolay.stankinschedule.utils.State
import com.vereshchagin.nikolay.stankinschedule.utils.StatefulLayout2
import com.vereshchagin.nikolay.stankinschedule.utils.delegates.FragmentDelegate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

/**
 * Фрагмент категории в удаленном репозитории.
 */
@AndroidEntryPoint
class RepositoryCategoryFragment :
    BaseFragment<FragmentRepositoryCategoryBinding>(FragmentRepositoryCategoryBinding::inflate) {

    @Inject
    lateinit var viewModelFactory: RepositoryCategoryViewModel.RepositoryCategoryFactory

    /**
     * ViewModel фрагмента.
     */
    private val viewModel: RepositoryCategoryViewModel by viewModels {
        RepositoryCategoryViewModel.provideFactory(viewModelFactory, parentCategory)
    }

    /**
     * ID родительской категории.
     */
    private var parentCategory: Int = -1

    private var stateful: StatefulLayout2 by FragmentDelegate()


    override fun onPostCreateView(savedInstanceState: Bundle?) {
        stateful = StatefulLayout2.Builder(binding.categoryContainer)
            .init(StatefulLayout2.LOADING, binding.categoryLoading.root)
            .addView(StatefulLayout2.CONTENT, binding.categoryItems)
            .create()

        // получение аргументов
        val arguments = requireArguments()
        parentCategory = arguments.getInt(EXTRA_PARENT)
        val title = arguments.getString(EXTRA_TITLE)

        setActionBarTitle(title)

        val adapter = RepositoryItemAdapter(this::onRepositoryItemClicked)
        binding.categoryItems.adapter = adapter

        // список с элементами
        lifecycleScope.launchWhenStarted {
            viewModel.entries.collectLatest { data ->
                adapter.submitData(lifecycle, data)
                stateful.setState(StatefulLayout2.CONTENT)
            }
        }

        // категория
        lifecycleScope.launchWhenStarted {
            viewModel.categoryState.collectLatest { state ->
                when (state) {
                    is State.Success -> {
                        setActionBarTitle(state.data.name)
                        stateful.setState(StatefulLayout2.CONTENT)
                    }
                    is State.Loading -> {
                        stateful.setState(StatefulLayout2.LOADING)
                    }
                }
            }
        }
    }

    /**
     * Вызывается при нажатие на элемент в списке.
     */
    private fun onRepositoryItemClicked(item: ScheduleRepositoryItem) {
        when (item) {
            // нажата категория
            is ScheduleCategoryEntry -> {
                navigateTo(R.id.to_repository_category_self, createBundle(item.id, item.name))
            }
            // нажато расписание
            is ScheduleItemEntry -> {
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