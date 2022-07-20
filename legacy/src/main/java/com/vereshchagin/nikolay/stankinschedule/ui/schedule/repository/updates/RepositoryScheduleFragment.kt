package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.updates

import android.os.Bundle
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.FragmentRepositoryScheduleBinding
import com.vereshchagin.nikolay.stankinschedule.model.schedule.remote.ScheduleUpdateEntry
import com.vereshchagin.nikolay.stankinschedule.ui.BaseFragment
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.name.ScheduleNameEditorDialog
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.paging.RepositoryItemAdapter
import com.vereshchagin.nikolay.stankinschedule.utils.ExceptionUtils
import com.vereshchagin.nikolay.stankinschedule.utils.State
import com.vereshchagin.nikolay.stankinschedule.utils.StatefulLayout2
import com.vereshchagin.nikolay.stankinschedule.utils.delegates.FragmentDelegate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

/**
 * Фрагмент расписания с версиями в удаленном репозитории.
 */
@AndroidEntryPoint
class RepositoryScheduleFragment :
    BaseFragment<FragmentRepositoryScheduleBinding>(FragmentRepositoryScheduleBinding::inflate) {

    @Inject
    lateinit var viewModelFactory: RepositoryScheduleViewModel.RepositoryScheduleFactory

    /**
     * ViewModel фрагмента.
     */
    private val viewModel: RepositoryScheduleViewModel by viewModels {
        RepositoryScheduleViewModel.provideFactory(viewModelFactory, scheduleId)
    }

    private var statefulSchedule: StatefulLayout2 by FragmentDelegate()

    private var adapter: RepositoryItemAdapter<ScheduleUpdateEntry> by FragmentDelegate()

    /**
     * Текущие название расписания.
     */
    private var scheduleId: Int = -1

    /**
     * Текущая позиция версии расписания для загрузки.
     */
    private var currentVersionPosition = RecyclerView.NO_POSITION


    override fun onPostCreateView(savedInstanceState: Bundle?) {
        statefulSchedule = StatefulLayout2.Builder(binding.repositoryContainer)
            .init(StatefulLayout2.LOADING, binding.versionsLoading.root)
            .addView(StatefulLayout2.CONTENT, binding.schedulesContainer)
            .create()

        val arguments = requireArguments()
        scheduleId = arguments.getInt(EXTRA_SCHEDULE_ID)
        val scheduleName = arguments.getString(EXTRA_SCHEDULE_NAME)
        setActionBarTitle(scheduleName)

        val savedPos = savedInstanceState?.getInt(CURRENT_VERSION_POS, RecyclerView.NO_POSITION)
        if (savedPos != null) {
            currentVersionPosition = savedPos
        }

        adapter = RepositoryItemAdapter(this::onScheduleVersionClicked)
        binding.scheduleVersions.adapter = adapter

        // расписание репозитория
        lifecycleScope.launchWhenStarted {
            viewModel.updatesState.collectLatest { state ->
                when (state) {
                    is State.Success -> {
                        val (entry, data) = state.data
                        adapter.submitData(PagingData.from(data))
                        binding.scheduleName = entry.name
                        setActionBarTitle(entry.name)

                        statefulSchedule.setState(StatefulLayout2.CONTENT)
                    }
                    is State.Loading -> {
                        statefulSchedule.setState(StatefulLayout2.LOADING)
                    }
                    is State.Failed -> {
                        val context = requireContext()
                        val description = ExceptionUtils.errorDescription(state.error, context)
                        Toast.makeText(context, description, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // статус загрузки расписания
        lifecycleScope.launchWhenStarted {
            viewModel.downloadState.collectLatest { data ->
                val (state, currentScheduleName) = data
                when (state) {
                    // расписание уже существует
                    RepositoryScheduleViewModel.DownloadState.EXIST -> {
                        selectScheduleName(currentScheduleName)
                    }
                    // начата загрузка
                    RepositoryScheduleViewModel.DownloadState.START -> {
                        showSnack(
                            R.string.repository_start_loading, args = arrayOf(currentScheduleName)
                        )
                    }
                }
            }
        }

        parentFragmentManager.setFragmentResultListener(
            ScheduleNameEditorDialog.REQUEST_SCHEDULE_NAME, this, this::onScheduleNameSelected
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(CURRENT_VERSION_POS, currentVersionPosition)
    }

    /**
     * Вызывает диалог, для выбора названия расписания.
     */
    private fun selectScheduleName(scheduleName: String) {
        val builder = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.repository_schedule_exist)
            .setMessage(getString(R.string.repository_schedule_exist_description, scheduleName))
            // заменить существующее
            .setPositiveButton(R.string.repository_schedule_replace) { dialog, _ ->
                val entry = adapter.snapshot().getOrNull(currentVersionPosition)
                if (entry != null) {
                    viewModel.downloadScheduleUpdate(scheduleName, entry, true)
                }
                dialog.dismiss()
            }
            // выбор нового имени
            .setNeutralButton(R.string.repository_schedule_change) { dialog, _ ->
                val nameDialog = ScheduleNameEditorDialog.newInstance(scheduleName)
                nameDialog.show(parentFragmentManager, nameDialog.tag)
                dialog.dismiss()
            }
            // отмена
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }

        builder.show()
    }

    /**
     * Вызывается, когда было выбрано новое для сохранения расписания.
     */
    private fun onScheduleNameSelected(key: String, bundle: Bundle) {
        if (key == ScheduleNameEditorDialog.REQUEST_SCHEDULE_NAME) {
            val newScheduleName = bundle.getString(ScheduleNameEditorDialog.SCHEDULE_NAME)
            if (!newScheduleName.isNullOrEmpty() && currentVersionPosition != RecyclerView.NO_POSITION) {
                val entry = adapter.snapshot().getOrNull(currentVersionPosition)
                if (entry != null) {
                    viewModel.downloadScheduleUpdate(newScheduleName, entry, false)
                }
            }
        }
    }

    /**
     * Вызывается при нажатии на версию расписания.
     */
    private fun onScheduleVersionClicked(entry: ScheduleUpdateEntry) {
        val currentScheduleName = viewModel.currentScheduleName()
        if (currentScheduleName != null) {
            currentVersionPosition = adapter.snapshot().indexOf(entry)
            viewModel.downloadScheduleUpdate(currentScheduleName, entry, false)
        }

    }

    companion object {

        private const val CURRENT_VERSION_POS = "current_version_position"

        private const val EXTRA_SCHEDULE_ID = "extra_id"
        private const val EXTRA_SCHEDULE_NAME = "extra_name"

        /**
         * Создает bundle с параметрами для перехода к фрагменту.
         */
        fun createBundle(id: Int, scheduleName: String): Bundle {
            return bundleOf(EXTRA_SCHEDULE_ID to id, EXTRA_SCHEDULE_NAME to scheduleName)
        }
    }
}