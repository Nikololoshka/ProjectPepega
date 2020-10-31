package com.vereshchagin.nikolay.stankinschedule.ui.schedule.myschedules

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.FragmentScheduleBinding
import com.vereshchagin.nikolay.stankinschedule.ui.BaseFragment
import com.vereshchagin.nikolay.stankinschedule.ui.home.ChangeSubgroupBottomSheet
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.name.ScheduleNameEditorDialog
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.myschedules.paging.DragToMoveCallback
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.myschedules.paging.SchedulesAdapter
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.worker.ScheduleDownloadWorker.Companion.SCHEDULE_DOWNLOADED_EVENT
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.view.ScheduleViewFragment
import com.vereshchagin.nikolay.stankinschedule.ui.settings.ApplicationPreference
import com.vereshchagin.nikolay.stankinschedule.utils.PermissionsUtils
import com.vereshchagin.nikolay.stankinschedule.utils.StatefulLayout2
import com.vereshchagin.nikolay.stankinschedule.utils.extractFilename
import org.apache.commons.io.IOUtils
import java.io.FileNotFoundException
import java.nio.charset.StandardCharsets


/**
 * Фрагмент с расписаниями.
 */
class ScheduleFragment : BaseFragment<FragmentScheduleBinding>(),
    SchedulesAdapter.OnScheduleItemListener, DragToMoveCallback.OnStartDragListener {

    private val viewModel by viewModels<ScheduleViewModel> {
        ScheduleViewModel.Factory(activity?.application!!)
    }

    private lateinit var stateful: StatefulLayout2
    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var adapter: SchedulesAdapter

    private var actionMode: ActionMode? = null

    /**
     * Ресивер для просмотра появления нового расписания (при скачивании).
     */
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            viewModel.update()
        }
    }

    /**
     * Callback для ActionMode.
     */
    private val actionCallback = object : ActionMode.Callback {
        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            when (item?.itemId) {
                R.id.remove_schedule -> {
                    removeSchedules()
                }
            }
            return true
        }

        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            mode?.let {
                it.menuInflater?.inflate(R.menu.menu_schedule_action_mode, menu)
                actionMode = it
                return true
            }
            return false
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return true
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            binding.addSchedule.show()
            adapter.setEditable(false)

            actionMode = null
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        LocalBroadcastManager.getInstance(context)
            .registerReceiver(
                receiver,
                IntentFilter(SCHEDULE_DOWNLOADED_EVENT)
            )
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(receiver)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        viewModel.update()
    }

    override fun onStop() {
        super.onStop()
        actionMode?.finish()
    }

    override fun onInflateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentScheduleBinding {
        return FragmentScheduleBinding.inflate(inflater, container, false)
    }

    override fun onPostCreateView(savedInstanceState: Bundle?) {
        stateful = StatefulLayout2.Builder(binding.schedulesContainer)
            .init(StatefulLayout2.CONTENT, binding.schedules)
            .addView(StatefulLayout2.EMPTY, binding.emptySchedules)
            .create()

        binding.addSchedule.setOnClickListener {
            val dialog = AddScheduleBottomSheet()
            dialog.setTargetFragment(this, REQUEST_ADD_SCHEDULE)
            dialog.show(parentFragmentManager, dialog.tag)
        }

        adapter = SchedulesAdapter(this, this)
        binding.schedules.adapter = adapter

        // drag для расписаний
        val dragToMoveCallback = object : DragToMoveCallback() {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition = viewHolder.bindingAdapterPosition
                val toPosition = target.bindingAdapterPosition

                adapter.moveItem(fromPosition, toPosition)

                return true
            }
        }

        itemTouchHelper = ItemTouchHelper(dragToMoveCallback)
        itemTouchHelper.attachToRecyclerView(binding.schedules)

        binding.schedules.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )

        // скрытие кнопки при прокрутке
        binding.schedules.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    binding.addSchedule.show()
                }
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if ((dy > 0 || dy < 0) && binding.addSchedule.isShown) {
                    binding.addSchedule.hide()
                }
            }
        })

        // расписания
        viewModel.adapterData.observe(viewLifecycleOwner, Observer {
            val (schedules, favorite) = it ?: return@Observer

            if (schedules.isEmpty()) {
                stateful.setState(StatefulLayout2.EMPTY)
            } else {
                adapter.submitList(schedules, favorite)
                stateful.setState(StatefulLayout2.CONTENT)
            }
        })

        viewModel.selectedItems.observe(viewLifecycleOwner, Observer {
            val selectedItems = it ?: return@Observer
            adapter.setSelectedItems(selectedItems)
        })

        // был активирован action mode
        savedInstanceState?.getBoolean(ACTION_MODE)?.let {
            if (it) {
                startActionMode(isRestore = true)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(ACTION_MODE, actionMode != null)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_schedule, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.edit_schedules) {
            startActionMode()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK || data == null) {
            return
        }

        when (requestCode) {
            // добавление расписания
            REQUEST_ADD_SCHEDULE -> {
                when (data.getIntExtra(AddScheduleBottomSheet.SCHEDULE_ACTION, -1)) {
                    R.id.create_schedule -> {
                        val dialog = ScheduleNameEditorDialog.newInstance("")
                        dialog.setTargetFragment(this, REQUEST_NEW_SCHEDULE)
                        dialog.show(parentFragmentManager, dialog.tag)
                    }
                    R.id.from_repository -> {
                        navigateTo(R.id.toScheduleRepositoryFragment)
                    }
                    R.id.load_schedule -> {
                        loadScheduleFromDevice()
                    }
                }
            }
            // создано новое расписание
            REQUEST_NEW_SCHEDULE -> {
                val scheduleName = data.getStringExtra(ScheduleNameEditorDialog.SCHEDULE_NAME)
                if (!scheduleName.isNullOrEmpty()) {
                    Log.d(TAG, "onActivityResult: ")
                    viewModel.createSchedule(scheduleName)
                }
            }
            // загрузка расписания с устройста
            REQUEST_LOAD_SCHEDULE -> {
                var scheduleName = ""
                try {
                    val uri = data.data ?: throw RuntimeException("Invalid uri")
                    val resolver = requireContext().contentResolver
                    val json = resolver.openInputStream(uri)?.use { stream ->
                        IOUtils.toString(stream, StandardCharsets.UTF_8)
                    } ?: throw RuntimeException("Cannot load json")

                    scheduleName =
                        uri.extractFilename(requireContext()) ?: throw FileNotFoundException()

                    viewModel.loadScheduleFromJson(json, scheduleName)
                    showSnack(R.string.sch_successfully_added, args = arrayOf(scheduleName))

                } catch (e: Exception) {
                    e.printStackTrace()
                    showSnack(R.string.sch_failed_add, args = arrayOf(scheduleName))
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PERMISSION_READ_STORAGE) {
            if (PermissionsUtils.isGrand(grantResults)) {
                loadScheduleFromDevice()
            }
        }
    }

    override fun onScheduleItemClicked(schedule: String, position: Int) {
        if (actionMode != null) {
            selectItem(position)
            return
        }

        navigateTo(
            R.id.to_schedule_view_fragment, ScheduleViewFragment.createBundle(schedule)
        )
    }

    override fun onScheduleItemLongClicked(schedule: String, position: Int) {
        startActionMode(position)
    }

    override fun onScheduleFavoriteSelected(favorite: String) {
        val isNew = viewModel.setFavorite(favorite)

        if (isNew) {
            // текущая подгруппа
            val subgroup = ApplicationPreference.subgroup(requireContext())
            var subgroupString = subgroup.toString(requireContext())
            if (subgroupString.isEmpty()) {
                subgroupString = getString(R.string.sch_without_subgroup)
            }

            // отображение выбранной подгруппы
            Snackbar.make(
                binding.schedulesLayout,
                getString(R.string.sch_home_display_subgroup, subgroupString),
                Snackbar.LENGTH_LONG
            )
                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                .setAction(R.string.sch_home_change_subgroup) {
                    val dialog = ChangeSubgroupBottomSheet()
                    dialog.show(parentFragmentManager, dialog.tag)
                }
                .show()
        }
    }

    override fun onScheduleItemMove(fromPosition: Int, toPosition: Int) {
        viewModel.moveSchedule(fromPosition, toPosition)
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder?) {
        itemTouchHelper.startDrag(viewHolder!!)
    }

    /**
     * Запускает режим редактирования.
     */
    private fun startActionMode(position: Int = -1, isRestore: Boolean = false) {
        if (actionMode == null) {
            actionMode = (activity as AppCompatActivity?)?.startSupportActionMode(actionCallback)

            adapter.setEditable(true)
            binding.addSchedule.hide()
            updateActionModeTitle()

            if (!isRestore) {
                viewModel.clearSelection()
            }

            if (position >= 0) {
                selectItem(position)
            }
        }
    }

    /**
     * "Выбирает" объект в режиме редактирования.
     */
    private fun selectItem(position: Int) {
        val count = viewModel.selectItem(position)
        adapter.notifyItemChanged(position)

        if (count == 0) {
            actionMode?.finish()
        } else {
            updateActionModeTitle()
        }
    }

    /**
     * Обновляет число выбранных элементов в режиме редактирования.
     */
    private fun updateActionModeTitle() {
        viewModel.selectedItems.value?.let {
            actionMode?.apply {
                title = it.size().toString()
                invalidate()
            }
        }
    }

    /**
     * Загружает расписание с устройства.
     */
    private fun loadScheduleFromDevice() {
        // проверка разрешения
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        if (PermissionsUtils.checkGrandPermission(requireContext(), permission)) {
            requestPermissions(arrayOf(permission), REQUEST_PERMISSION_READ_STORAGE)
            return
        }

        // есть разрешение на чтение
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"

        startActivityForResult(intent, REQUEST_LOAD_SCHEDULE)
    }

    /**
     * Удаляет выбранные расписании.
     */
    private fun removeSchedules() {
        viewModel.selectedItems.value?.let {
            val count = it.size()
            if (count <= 0) {
                return
            }

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.warning)
                .setMessage(getString(R.string.sch_remove_schedules, count.toString()))
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(R.string.yes_continue) { dialog, _ ->
                    viewModel.removeSelected()
                    actionMode?.finish()
                    dialog.dismiss()
                }
                .show()
        }
    }

    companion object {
        private val TAG = ScheduleFragment::class.java.simpleName + "Log"

        private const val ACTION_MODE = "action_mode"

        private const val REQUEST_ADD_SCHEDULE = 1
        private const val REQUEST_NEW_SCHEDULE = 2
        private const val REQUEST_PERMISSION_READ_STORAGE = 3
        private const val REQUEST_LOAD_SCHEDULE = 4
    }
}