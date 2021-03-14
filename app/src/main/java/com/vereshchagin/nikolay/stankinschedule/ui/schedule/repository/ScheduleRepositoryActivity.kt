package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.ActivityScheduleRepositoryBinding
import com.vereshchagin.nikolay.stankinschedule.databinding.ViewErrorWithButtonBinding
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRemoteRepository
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepository
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.paging.RepositoryCategoryAdapter
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.worker.ScheduleDownloadWorker
import com.vereshchagin.nikolay.stankinschedule.utils.ExceptionUtils
import com.vereshchagin.nikolay.stankinschedule.utils.State
import com.vereshchagin.nikolay.stankinschedule.utils.StatefulLayout2
import com.vereshchagin.nikolay.stankinschedule.utils.extensions.createBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Удаленный репозиторий с расписаниями.
 */
class ScheduleRepositoryActivity : AppCompatActivity() {

    private lateinit var statefulLayout: StatefulLayout2
    private lateinit var binding: ActivityScheduleRepositoryBinding

    private val viewModel by viewModels<ScheduleRepositoryViewModel> {
        ScheduleRepositoryViewModel.Factory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScheduleRepositoryBinding.inflate(layoutInflater)

        statefulLayout = StatefulLayout2.Builder(binding.repositoryLayout)
            .init(StatefulLayout2.LOADING, binding.repositoryLoading.root)
            .addView(StatefulLayout2.CONTENT, binding.repositoryContainer)
            .addView(StatefulLayout2.ERROR, binding.repositoryError)
            .create()

        lifecycleScope.launch(Dispatchers.IO) {
            val repo = ScheduleRemoteRepository(this@ScheduleRepositoryActivity)
            repo.loadRepositoryEntry()
        }

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.repositoryRefresh.setOnRefreshListener {
            viewModel.update(false)
        }

        binding.appBarRepository.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
                binding.repositoryRefresh.isEnabled = verticalOffset == 0
            }
        )

        // описание репозитория
        viewModel.description.observe(this) { state ->
            when (state) {
                is State.Success -> {
                    binding.repositoryLastUpdate.text = getString(
                        R.string.repository_last_update, state.data.lastUpdate
                    )
                    binding.repositoryRefresh.isRefreshing = false
                    binding
                    statefulLayout.setState(StatefulLayout2.CONTENT)
                }
                is State.Loading -> {
                    statefulLayout.setState(StatefulLayout2.LOADING)
                }
                is State.Failed -> {

                    val description = ExceptionUtils.errorDescription(state.error, this)
                    binding.repositoryError.createBinding<ViewErrorWithButtonBinding>()?.let {
                        it.errorTitle.text = description
                        it.errorAction.setOnClickListener {
                            viewModel.update()
                        }
                        statefulLayout.setState(StatefulLayout2.ERROR)
                    }
                }
            }
        }

        val adapter = RepositoryCategoryAdapter(this::onScheduleItemClicked)
        binding.repositoryCategories.adapter = adapter
        binding.repositoryCategories.offscreenPageLimit = 1

        TabLayoutMediator(
            binding.tabCategories, binding.repositoryCategories, true
        ) { tab, position ->
            tab.text = viewModel.tabTitle(position)
        }.attach()

        // добавление категорий в ViewPager2
        viewModel.categories.observe(this) {
            val data = it ?: return@observe
            adapter.submitData(lifecycle, data)
        }
    }

    private fun onScheduleItemClicked(scheduleName: String, categoryName: String, id: Int) {
        lifecycleScope.launch {
            val repository = ScheduleRepository(this@ScheduleRepositoryActivity)
            if (repository.isScheduleExist(scheduleName)) {
                Snackbar.make(binding.root, R.string.schedule_editor_exists, Snackbar.LENGTH_SHORT)
                    .show()
                return@launch
            }

            ScheduleDownloadWorker.startWorker(
                binding.root.context,
                categoryName,
                scheduleName,
                id
            )

            Snackbar.make(
                binding.root,
                binding.root.context.getString(R.string.repository_start_loading, scheduleName),
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_schedule_repository, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.repository_update) {
            viewModel.update(false)
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val TAG = "ScheduleRepositoryLog"
    }
}