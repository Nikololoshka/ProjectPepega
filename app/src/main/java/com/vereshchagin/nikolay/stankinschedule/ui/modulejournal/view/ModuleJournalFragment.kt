package com.vereshchagin.nikolay.stankinschedule.ui.modulejournal.view

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.FragmentModuleJournalBinding
import com.vereshchagin.nikolay.stankinschedule.databinding.ViewErrorWithButtonBinding
import com.vereshchagin.nikolay.stankinschedule.settings.ModuleJournalPreference
import com.vereshchagin.nikolay.stankinschedule.ui.BaseFragment
import com.vereshchagin.nikolay.stankinschedule.ui.modulejournal.view.paging.SemesterMarksAdapter
import com.vereshchagin.nikolay.stankinschedule.utils.DrawableUtils
import com.vereshchagin.nikolay.stankinschedule.utils.ExceptionUtils
import com.vereshchagin.nikolay.stankinschedule.utils.State
import com.vereshchagin.nikolay.stankinschedule.utils.StatefulLayout2

/**
 * Фрагмент модульного журнала с оценками.
 */
class ModuleJournalFragment : BaseFragment<FragmentModuleJournalBinding>() {

    /**
     * ViewModel фрагмента.
     */
    private val viewModel by viewModels<ModuleJournalViewModel> {
        ModuleJournalViewModel.Factory(activity?.application!!)
    }

    /**
     * Менеджер состояний.
     */
    private var _statefulLayout: StatefulLayout2? = null
    private val statefulLayout get() = _statefulLayout!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onInflateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentModuleJournalBinding {
        return FragmentModuleJournalBinding.inflate(inflater, container, false)
    }

    override fun onPostCreateView(savedInstanceState: Bundle?) {
        // не выполнен вход
        val signIn = ModuleJournalPreference.isSignIn(requireContext())
        if (!signIn) {
            navigateTo(R.id.toModuleJournalLoginFragment)
            return
        }
        ModuleJournalWorker.startWorker(requireContext())

        binding.mjLoadingStudent.setShimmer(DrawableUtils.createShimmer())
        _statefulLayout = StatefulLayout2.Builder(binding.mjRoot)
            .init(StatefulLayout2.LOADING, binding.mjLoading)
            .addView(StatefulLayout2.CONTENT, binding.mjRefresh)
            .addView(StatefulLayout2.ERROR, binding.mjContentError)
            .create()

        binding.appBarMj.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
                binding.mjRefresh.isEnabled = verticalOffset == 0
            }
        )
        binding.mjRefresh.setOnRefreshListener(this::refreshAll)

        // информация о студенте
        viewModel.studentData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is State.Success -> {
                    binding.mjStudentName.text = state.data.student
                    binding.mjStudentGroup.text = state.data.group

                    statefulLayout.setState(StatefulLayout2.CONTENT)
                }
                is State.Loading -> {
                    statefulLayout.setState(StatefulLayout2.LOADING)
                }
                is State.Failed -> {
                    statefulLayout.setState(StatefulLayout2.ERROR)
                    val errorBinding = DataBindingUtil.bind<ViewErrorWithButtonBinding>(
                        binding.mjContentError.root
                    )
                    val errorText = ExceptionUtils.errorDescription(state.error, requireContext())
                    errorBinding?.let {
                        it.errorTitle.text = errorText
                        it.errorAction.setOnClickListener {
                            refreshAll(true)
                        }
                    }
                }
            }
            binding.mjRefresh.isRefreshing = state is State.Loading
        }

        viewModel.predictedRating.observe(viewLifecycleOwner) { rating ->
            binding.mjStudentPredictRating.text = rating
        }

        viewModel.currentRating.observe(viewLifecycleOwner) { rating ->
            binding.mjStudentRating.text = rating
        }

        // направление pager'а и tab layout
        val currentDirection = requireContext().resources.configuration.layoutDirection
        val requireDirection = if (currentDirection == View.LAYOUT_DIRECTION_LTR) {
            View.LAYOUT_DIRECTION_RTL
        } else {
            View.LAYOUT_DIRECTION_LTR
        }
        binding.mjPagerSemesters.layoutDirection = requireDirection
        binding.mjTabSemesters.layoutDirection = requireDirection

        // настройка списка семестров
        val adapter = SemesterMarksAdapter()
        binding.mjPagerSemesters.adapter = adapter

        TabLayoutMediator(
            binding.mjTabSemesters, binding.mjPagerSemesters, true
        ) { tab, position ->
            tab.text = viewModel.tabTitle(position)
        }.attach()

        // список семестров с оценками
        viewModel.semesters.observe(viewLifecycleOwner) {
            val data = it ?: return@observe
            adapter.submitData(lifecycle, data)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _statefulLayout = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_module_journal, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // выход из модульного журнала
            R.id.mj_sign_out -> {
                signOut()
                return true
            }
            // обновить данные журнала
            R.id.mj_update_marks -> {
                refreshAll()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * Перезагружает все оценки в модульном журнале.
     */
    private fun refreshAll(afterError: Boolean = false) {
        binding.mjRefresh.isRefreshing = true
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
        private const val TAG = "ModuleJournalLog"
    }
}