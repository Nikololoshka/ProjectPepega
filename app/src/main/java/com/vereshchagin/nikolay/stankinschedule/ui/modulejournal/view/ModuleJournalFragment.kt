package com.vereshchagin.nikolay.stankinschedule.ui.modulejournal.view

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.FragmentModuleJournalBinding
import com.vereshchagin.nikolay.stankinschedule.databinding.ViewErrorWithButtonBinding
import com.vereshchagin.nikolay.stankinschedule.settings.ModuleJournalPreference
import com.vereshchagin.nikolay.stankinschedule.ui.BaseFragment
import com.vereshchagin.nikolay.stankinschedule.ui.modulejournal.predict.ModuleJournalPredictActivity
import com.vereshchagin.nikolay.stankinschedule.ui.modulejournal.view.paging.SemesterMarksAdapter
import com.vereshchagin.nikolay.stankinschedule.utils.DrawableUtils
import com.vereshchagin.nikolay.stankinschedule.utils.ExceptionUtils
import com.vereshchagin.nikolay.stankinschedule.utils.State
import com.vereshchagin.nikolay.stankinschedule.utils.StatefulLayout2
import com.vereshchagin.nikolay.stankinschedule.utils.delegates.FragmentDelegate
import com.vereshchagin.nikolay.stankinschedule.utils.extensions.createBinding
import kotlinx.coroutines.flow.collectLatest

/**
 * Фрагмент модульного журнала с оценками.
 */
class ModuleJournalFragment :
    BaseFragment<FragmentModuleJournalBinding>(FragmentModuleJournalBinding::inflate) {

    /**
     * ViewModel фрагмента.
     */
    private val viewModel by viewModels<ModuleJournalViewModel> {
        ModuleJournalViewModel.Factory(activity?.application!!)
    }

    /**
     * Менеджер состояний.
     */
    private var statefulStudentLayout: StatefulLayout2 by FragmentDelegate()
    private var statefulSemestersLayout: StatefulLayout2 by FragmentDelegate()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onPostCreateView(savedInstanceState: Bundle?) {
        // не выполнен вход
        val signIn = ModuleJournalPreference.isSignIn(requireContext())
        if (!signIn) {
            navigateTo(R.id.toModuleJournalLoginFragment)
            return
        }
        ModuleJournalWorker.startWorker(requireContext())

        binding.studentLoadingShimmer.setShimmer(DrawableUtils.createShimmer())
        statefulStudentLayout = StatefulLayout2.Builder(binding.mjContainer)
            .init(StatefulLayout2.LOADING, binding.studentLoading)
            .addView(StatefulLayout2.CONTENT, binding.refresh)
            .addView(StatefulLayout2.ERROR, binding.studentError)
            .create()

        statefulSemestersLayout = StatefulLayout2.Builder(binding.semestersContainer)
            .init(StatefulLayout2.LOADING, binding.semestersLoading.root)
            .addView(StatefulLayout2.CONTENT, binding.semestersPager)
            .create()

        binding.appBar.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
                binding.refresh.isEnabled = verticalOffset == 0
            }
        )
        binding.refresh.setOnRefreshListener(this::refreshAll)

        // информация о студенте
        viewModel.studentData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is State.Success -> {
                    binding.studentName.text = state.data.student
                    binding.studentGroup.text = state.data.group

                    statefulStudentLayout.setState(StatefulLayout2.CONTENT)
                }
                is State.Loading -> {
                    statefulStudentLayout.setState(StatefulLayout2.LOADING)
                }
                is State.Failed -> {
                    statefulStudentLayout.setState(StatefulLayout2.ERROR)
                    val errorText = ExceptionUtils.errorDescription(state.error, requireContext())
                    binding.studentError.createBinding<ViewErrorWithButtonBinding>()?.let {
                        it.errorTitle.text = errorText
                        it.errorAction.setOnClickListener {
                            refreshAll(true)
                        }
                    }
                }
            }
            binding.refresh.isRefreshing = state is State.Loading
        }

        viewModel.predictedRating.observe(viewLifecycleOwner) { rating ->
            binding.studentPredictRating.text = rating
        }

        viewModel.currentRating.observe(viewLifecycleOwner) { rating ->
            binding.studentRating.text = rating
        }

        // направление pager'а и tab layout
        val currentDirection = requireContext().resources.configuration.layoutDirection
        val requireDirection = if (currentDirection == View.LAYOUT_DIRECTION_LTR) {
            View.LAYOUT_DIRECTION_RTL
        } else {
            View.LAYOUT_DIRECTION_LTR
        }
        binding.semestersPager.layoutDirection = requireDirection
        binding.semestersTabs.layoutDirection = requireDirection

        // настройка списка семестров
        val adapter = SemesterMarksAdapter()
        binding.semestersPager.adapter = adapter

        lifecycleScope.launchWhenStarted {
            adapter.loadStateFlow.collectLatest {
                onSemesterMarksStateChanged(it)
            }
        }

        TabLayoutMediator(
            binding.semestersTabs, binding.semestersPager, true
        ) { tab, position ->
            tab.text = viewModel.tabTitle(position)
        }.attach()

        // список семестров с оценками
        viewModel.semesters.observe(viewLifecycleOwner) {
            val data = it ?: return@observe
            adapter.submitData(lifecycle, data)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_module_journal, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // выход из модульного журнала
            R.id.sign_out -> {
                signOut()
                return true
            }
            // обновить данные журнала
            R.id.update_marks -> {
                refreshAll()
                return true
            }
            // к расчету рейтинга
            R.id.predict_rating -> {
                val intent = Intent(requireContext(), ModuleJournalPredictActivity::class.java)
                startActivity(intent)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * Вызывается при изменении состояния загрузки семестров.
     */
    private fun onSemesterMarksStateChanged(loadStates: CombinedLoadStates) {
        when (val state = loadStates.refresh) {
            is LoadState.Loading -> {
                statefulSemestersLayout.setState(StatefulLayout2.LOADING)
            }
            is LoadState.Error -> {
                val description = ExceptionUtils.errorDescription(state.error, requireContext())
                Snackbar.make(binding.coordinatorLayout, description, Snackbar.LENGTH_LONG)
                    .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                    .setAction(R.string.retry) {
                        refreshAll()
                    }
                    .show()
            }
            else -> {
                statefulSemestersLayout.setState(StatefulLayout2.CONTENT)
            }
        }
    }

    /**
     * Перезагружает все оценки в модульном журнале.
     */
    private fun refreshAll(afterError: Boolean = false) {
        binding.refresh.isRefreshing = true
        viewModel.refreshModuleJournal(false, afterError)
    }

    /**
     * Выполнят выход из модульного журнала.
     */
    private fun signOut() {
        try {
            viewModel.signOut()
            ModuleJournalWorker.cancelWorker(requireContext())
            navigateTo(R.id.toModuleJournalLoginFragment)

        } catch (e: Exception) {
            val description = ExceptionUtils.errorDescription(e, requireContext())

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.error)
                .setMessage(description)
                .setNeutralButton(R.string.ok) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    companion object {
        @Suppress("unused")
        private const val TAG = "ModuleJournalLog"
    }
}