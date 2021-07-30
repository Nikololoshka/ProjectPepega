package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.FragmentRepositoryScheduleBinding
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.v1.ScheduleVersionEntry
import com.vereshchagin.nikolay.stankinschedule.ui.BaseFragment
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.name.ScheduleNameEditorDialog
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.paging.RepositoryItemAdapter
import com.vereshchagin.nikolay.stankinschedule.utils.ExceptionUtils
import com.vereshchagin.nikolay.stankinschedule.utils.State
import com.vereshchagin.nikolay.stankinschedule.utils.StatefulLayout2
import com.vereshchagin.nikolay.stankinschedule.utils.delegates.FragmentDelegate

/**
 * Фрагмент расписания с версиями в удаленном репозитории.
 */
class RepositoryScheduleFragment :
    BaseFragment<FragmentRepositoryScheduleBinding>(FragmentRepositoryScheduleBinding::inflate) {

    private enum class ActionMode {
        SYNC_SCHEDULE,
        DOWNLOAD_SCHEDULE,
        NOTING
    }

    private lateinit var viewModel: RepositoryScheduleViewModel
    private var statefulSchedule: StatefulLayout2 by FragmentDelegate()
    private var statefulVersions: StatefulLayout2 by FragmentDelegate()

    private var adapter: RepositoryItemAdapter<ScheduleVersionEntry> by FragmentDelegate()

    /**
     * Текущие название расписания.
     */
    private lateinit var scheduleName: String

    /**
     * Текущие выполняемое действие.
     */
    private var actionMode = ActionMode.NOTING

    /**
     * Текущая позиция версии расписания для загрузки.
     */
    private var currentVersionPosition = RecyclerView.NO_POSITION

    override fun onPostCreateView(savedInstanceState: Bundle?) {
        statefulSchedule = StatefulLayout2.Builder(binding.repositoryContainer)
            .init(StatefulLayout2.LOADING, binding.versionsLoading.root)
            .addView(StatefulLayout2.CONTENT, binding.schedulesContainer)
            .create()

        statefulVersions = StatefulLayout2.Builder(binding.versionsContainer)
            .init(StatefulLayout2.CONTENT, binding.scheduleVersions)
            .addView(StatefulLayout2.EMPTY, binding.scheduleSynced)
            .create()

        val arguments = requireArguments()
        val scheduleId = arguments.getInt(EXTRA_SCHEDULE_ID)
        scheduleName = arguments.getString(EXTRA_SCHEDULE_NAME)!!
        setActionBarTitle(scheduleName)

        val savedMode = savedInstanceState?.getSerializable(ACTION_MODE) as ActionMode?
        if (savedMode != null) {
            actionMode = savedMode
        }
        val savedPos = savedInstanceState?.getInt(CURRENT_VERSION_POS, RecyclerView.NO_POSITION)
        if (savedPos != null) {
            currentVersionPosition = savedPos
        }

        viewModel = ViewModelProvider(
            this,
            RepositoryScheduleViewModel.Factory(
                requireActivity().application,
                scheduleName,
                scheduleId
            )
        ).get(RepositoryScheduleViewModel::class.java)

        adapter = RepositoryItemAdapter(this::onScheduleVersionClicked)
        binding.scheduleVersions.adapter = adapter

        // расписание репозитория
        viewModel.scheduleEntry.observe(this) { state ->
            when (state) {
                is State.Success -> {
                    val data = state.data

                    binding.scheduleName = data.name
                    adapter.submitData(lifecycle, data.versionEntries())
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

        // статус загрузки расписания
        viewModel.downloadState.observe(this) { state ->
            // расписание уже существует
            if (state == RepositoryScheduleViewModel.DownloadState.EXIST) {
                actionMode = ActionMode.DOWNLOAD_SCHEDULE
                selectScheduleName(true)
            }
            // начата загрузка
            else if (state == RepositoryScheduleViewModel.DownloadState.START) {
                showSnack(R.string.repository_start_loading, args = arrayOf(scheduleName))
            }

            if (state != RepositoryScheduleViewModel.DownloadState.IDLE) {
                viewModel.downloadStateComplete()
            }
        }

        // статус синхронизации расписания
        viewModel.syncState.observe(this) {
            val state = it ?: return@observe

            when (state) {
                RepositoryScheduleViewModel.SyncState.NOT_SYNCED -> {
                    binding.scheduleSync.setImageResource(R.drawable.ic_schedule_sync_enable)
                    statefulVersions.setState(StatefulLayout2.CONTENT)
                }
                RepositoryScheduleViewModel.SyncState.SYNCED -> {
                    binding.scheduleSync.setImageResource(R.drawable.ic_schedule_sync_disable)
                    statefulVersions.setState(StatefulLayout2.EMPTY)
                }
                RepositoryScheduleViewModel.SyncState.SYNCING -> {
                    binding.scheduleSync.setImageResource(R.drawable.avd_schedule_sync)
                    val drawable = binding.scheduleSync.drawable
                    if (drawable is AnimatedVectorDrawable) {
                        drawable.start()
                    }
                }
                RepositoryScheduleViewModel.SyncState.EXIST -> {
                    actionMode = ActionMode.SYNC_SCHEDULE
                    binding.scheduleSync.setImageResource(R.drawable.ic_schedule_sync_problem)
                    selectScheduleName(false)
                }
            }
        }

        parentFragmentManager.setFragmentResultListener(
            ScheduleNameEditorDialog.REQUEST_SCHEDULE_NAME, this, this::onScheduleNameSelected
        )

        binding.scheduleSync.setOnClickListener {
            viewModel.toggleSyncSchedule(false)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(ACTION_MODE, actionMode)
        outState.putInt(CURRENT_VERSION_POS, currentVersionPosition)
    }

    /**
     * Вызывает диалог, для выбора названия расписания.
     */
    private fun selectScheduleName(customName: Boolean) {
        val builder = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.repository_schedule_exist)
            .setMessage(getString(R.string.repository_schedule_exist_description, scheduleName))
            // заменить существующее
            .setPositiveButton(R.string.repository_schedule_replace) { dialog, _ ->
                if (actionMode == ActionMode.DOWNLOAD_SCHEDULE) {
                    viewModel.downloadScheduleVersion(scheduleName, currentVersionPosition, true)
                } else if (actionMode == ActionMode.SYNC_SCHEDULE) {
                    viewModel.toggleSyncSchedule(true)
                }
                dialog.dismiss()
            }
            // отмена
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                actionMode = ActionMode.NOTING
                dialog.dismiss()
            }

        // выбор нового имени
        if (customName) {
            builder.setNeutralButton(R.string.repository_schedule_change) { dialog, _ ->
                val nameDialog = ScheduleNameEditorDialog.newInstance(scheduleName)
                nameDialog.show(parentFragmentManager, nameDialog.tag)
                dialog.dismiss()
            }
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
                viewModel.downloadScheduleVersion(newScheduleName, currentVersionPosition)
            }
        }
    }

    /**
     * Вызывается при нажатии на версию расписания.
     */
    private fun onScheduleVersionClicked(entry: ScheduleVersionEntry) {
        currentVersionPosition = adapter.snapshot().indexOf(entry)
        viewModel.downloadScheduleVersion(scheduleName, entry.toVersion())
    }

    companion object {

        private const val ACTION_MODE = "action_mode"
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