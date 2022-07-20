package com.vereshchagin.nikolay.stankinschedule.ui.schedule.myschedules

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.vereshchagin.nikolay.stankinschedule.MainActivity
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.FragmentMyScheduleBinding
import com.vereshchagin.nikolay.stankinschedule.settings.ApplicationPreferenceKt
import com.vereshchagin.nikolay.stankinschedule.ui.BaseFragment
import com.vereshchagin.nikolay.stankinschedule.ui.home.ChangeSubgroupBottomSheet
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.name.ScheduleNameEditorDialog
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.myschedules.paging.DragToMoveCallback
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.myschedules.paging.MySchedulesAdapter
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.ScheduleRepositoryActivity
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.view.ScheduleViewFragment
import com.vereshchagin.nikolay.stankinschedule.utils.PermissionsUtils
import com.vereshchagin.nikolay.stankinschedule.utils.ScheduleUtils
import com.vereshchagin.nikolay.stankinschedule.utils.StatefulLayout2
import com.vereshchagin.nikolay.stankinschedule.utils.delegates.FragmentDelegate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject


/**
 * Фрагмент с расписаниями.
 */
@AndroidEntryPoint
class MySchedulesFragment :
    BaseFragment<FragmentMyScheduleBinding>(FragmentMyScheduleBinding::inflate),
    MySchedulesAdapter.OnScheduleItemListener,
    DragToMoveCallback.OnStartDragListener {

    @Inject
    lateinit var preference: ApplicationPreferenceKt

    private val viewModel: MySchedulesViewModel by viewModels()

    private var stateful by FragmentDelegate<StatefulLayout2>()

    private var itemTouchHelper by FragmentDelegate<ItemTouchHelper>()
    private var adapter by FragmentDelegate<MySchedulesAdapter>()

    private var actionMode: ActionMode? = null

    /**
     * Callback для ActionMode.
     */
    private val actionCallback = object : ActionMode.Callback {
        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            when (item?.itemId) {
                R.id.remove_schedule -> {
                    removeSelectedSchedules()
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
            if (lifecycle.currentState != Lifecycle.State.CREATED) {
                adapter.isEditable = false
                binding.addSchedule.show()
            }
            viewModel.completeActionMode()
            actionMode = null
        }
    }

    /**
     * Лаунчер для получения разрешения на чтение.
     */
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(), this::onReadPermissionForLoad
    )

    /**
     * Лаунчер для выбора расписания с устройства.
     */
    private val pickFileLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument(), this::onScheduleLoadFromDevice
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onDestroyView() {
        actionMode?.finish()
        super.onDestroyView()
    }

    override fun onPostCreateView(savedInstanceState: Bundle?) {
        stateful = StatefulLayout2.Builder(binding.schedulesContainer)
            .init(StatefulLayout2.LOADING, binding.schedulesLoading)
            .addView(StatefulLayout2.CONTENT, binding.schedules)
            .addView(StatefulLayout2.EMPTY, binding.emptySchedules)
            .create()

        binding.addSchedule.setOnClickListener {
            val dialog = AddScheduleBottomSheet()
            dialog.show(parentFragmentManager, dialog.tag)
        }

        // callback для добавления нового расписания
        parentFragmentManager.setFragmentResultListener(
            AddScheduleBottomSheet.REQUEST_ADD_SCHEDULE, this, this::onScheduleAddClicked
        )

        // callback для создания нового расписания
        parentFragmentManager.setFragmentResultListener(
            ScheduleNameEditorDialog.REQUEST_SCHEDULE_NAME, this, this::onScheduleCreateClicked
        )

        adapter = MySchedulesAdapter(this, this)
        binding.schedules.adapter = adapter

        // drag для расписаний
        val dragToMoveCallback = object : DragToMoveCallback() {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
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
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )

        // скрытие кнопки при прокрутке
        binding.schedules.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE && actionMode == null) {
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

        // избранное расписание
        lifecycleScope.launchWhenCreated {
            viewModel.favorite.collect { favorite ->
                adapter.favorite = favorite
            }
        }

        // список с расписаниями
        lifecycleScope.launchWhenStarted {
            viewModel.schedules.collectLatest { schedules ->
                Log.d(TAG, "onPostCreateView: $schedules")
                if (schedules.isEmpty()) {
                    stateful.setState(StatefulLayout2.EMPTY)

                } else {
                    adapter.updateSchedule(schedules)
                    stateful.setState(StatefulLayout2.CONTENT)
                }
            }
        }

        // действия над списком расписаний
        lifecycleScope.launchWhenCreated {
            viewModel.actionState.collect(::onActionStateChanged)
        }

        // был активирован action mode
        savedInstanceState?.getBoolean(ACTION_MODE)?.let { hasMode ->
            if (hasMode) {
                startActionMode(isRestore = true)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        trackScreen(TAG, MainActivity.TAG)
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

    /**
     * Вызывается, когда выбран способ добавления расписания.
     */
    private fun onScheduleAddClicked(key: String, bundle: Bundle) {
        if (key == AddScheduleBottomSheet.REQUEST_ADD_SCHEDULE) {
            when (bundle.getInt(AddScheduleBottomSheet.SCHEDULE_ACTION, -1)) {
                R.id.create_schedule -> {
                    val dialog = ScheduleNameEditorDialog.newInstance("")
                    dialog.show(parentFragmentManager, dialog.tag)
                }
                R.id.from_repository -> {
                    startActivity(
                        Intent(requireContext(), ScheduleRepositoryActivity::class.java)
                    )
                }
                R.id.load_schedule -> {
                    loadScheduleFromDevice()
                }
            }
        }
    }

    /**
     * Вызывается, когда необходимо создать новое расписание.
     */
    private fun onScheduleCreateClicked(key: String, bundle: Bundle) {
        if (key == ScheduleNameEditorDialog.REQUEST_SCHEDULE_NAME) {
            val scheduleName = bundle.getString(ScheduleNameEditorDialog.SCHEDULE_NAME)
            if (!scheduleName.isNullOrEmpty()) {
                viewModel.createSchedule(scheduleName)
            }
        }
    }

    /**
     * Загрузка расписания с устройства.
     */
    private fun onScheduleLoadFromDevice(uri: Uri?) {
        if (uri != null) {
            viewModel.loadScheduleFromDevice(uri)
        }
    }

    override fun onScheduleItemClicked(scheduleId: Long, position: Int) {
        if (actionMode != null) {
            selectItem(position)
            return
        }

        navigateTo(
            R.id.to_schedule_view_fragment, ScheduleViewFragment.createBundle(scheduleId)
        )
    }

    override fun onScheduleItemLongClicked(scheduleId: Long, position: Int) {
        startActionMode(position)
    }

    override fun onScheduleFavoriteSelected(favoriteId: Long) {
        val isNew = viewModel.setFavorite(favoriteId)

        if (isNew) {
            // текущая подгруппа
            val subgroup = preference.scheduleSubgroup
            var subgroupString = ScheduleUtils.subgroupToString(subgroup, requireContext())
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

    /**
     * Вызывается, когда получили разрешения на чтение расписания из вне.
     */
    private fun onReadPermissionForLoad(hasPermission: Boolean) {
        if (hasPermission) {
            pickFileLauncher.launch(arrayOf("application/json"))
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
     * @param position позиция, с которой начался режим.
     * @param isRestore восстанавливаем ли предыдущий режим редактирования.
     */
    private fun startActionMode(position: Int = -1, isRestore: Boolean = false) {
        if (actionMode == null) {
            actionMode = (requireActivity() as AppCompatActivity)
                .startSupportActionMode(actionCallback)

            adapter.isEditable = true
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
        viewModel.selectItem(position)
        adapter.notifyItemChanged(position)

        if (viewModel.selectedItems() == 0) {
            actionMode?.finish()
        } else {
            updateActionModeTitle()
        }
    }

    /**
     * Обновляет число выбранных элементов в режиме редактирования.
     */
    private fun updateActionModeTitle() {
        val count = viewModel.selectedItems()
        actionMode?.apply {
            title = count.toString()
            invalidate()
        }
    }

    /**
     * Запускает загрузку расписание с устройства, если есть разрешение.
     * Иначе запрашивает его.
     */
    private fun loadScheduleFromDevice() {
        // проверка разрешения
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        if (PermissionsUtils.checkGrandPermission(requireContext(), permission)) {
            permissionLauncher.launch(permission)
            return
        } else {
            onReadPermissionForLoad(true)
        }
    }

    /**
     * Удаляет выбранные расписании.
     */
    private fun removeSelectedSchedules() {
        val count = viewModel.selectedItems()
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

    /**
     * Отображает состояние, связанное с действиями над списком расписаний.
     */
    private fun onActionStateChanged(action: MySchedulesViewModel.ScheduleAction) {
        when (action) {
            MySchedulesViewModel.ScheduleAction.IMPORTED -> {
                showSnack(R.string.sch_successfully_added)
            }
            MySchedulesViewModel.ScheduleAction.IMPORTED_FAILED -> {
                showSnack(R.string.sch_failed_add)
            }
            MySchedulesViewModel.ScheduleAction.REMOVED_SELECTED -> {
                showSnack(R.string.sch_remove_selected)
            }
        }
    }

    companion object {
        private const val TAG = "MySchedulesFragmentLog"
        private const val ACTION_MODE = "action_mode"
    }
}