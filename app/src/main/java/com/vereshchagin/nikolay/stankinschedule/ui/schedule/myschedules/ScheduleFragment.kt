package com.vereshchagin.nikolay.stankinschedule.ui.schedule.myschedules

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.FragmentScheduleBinding
import com.vereshchagin.nikolay.stankinschedule.ui.BaseFragment
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.name.ScheduleNameEditorActivity
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.myschedules.paging.DragToMoveCallback
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.myschedules.paging.SchedulesAdapter
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.view.ScheduleViewFragment
import com.vereshchagin.nikolay.stankinschedule.ui.settings.SchedulePreference
import com.vereshchagin.nikolay.stankinschedule.utils.PermissionsUtils
import com.vereshchagin.nikolay.stankinschedule.utils.StatefulLayout2
import org.apache.commons.io.IOUtils
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
     * Callback для ActionMode.
     */
    private val actionCallback = object : ActionMode.Callback {
        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            when (item?.itemId) {
                R.id.remove_schedule -> {

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
            viewModel.clearSelection()
            binding.addSchedule.show()
            adapter.notifyDataSetChanged()

            actionMode = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition

                adapter.moveItem(fromPosition, toPosition)

                return true
            }
        }

        itemTouchHelper = ItemTouchHelper(dragToMoveCallback)
        itemTouchHelper.attachToRecyclerView(binding.schedules)

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
                startActionMode()
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
                        val intent = Intent(activity, ScheduleNameEditorActivity::class.java)
                        startActivityForResult(intent, REQUEST_NEW_SCHEDULE)
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
                TODO("Unhandled result")
            }
            // загрузка расписания с устройста
            REQUEST_LOAD_SCHEDULE -> {
                try {
                    val uri = data.data ?: throw RuntimeException("Invalid uri")
                    val resolver = requireContext().contentResolver
                    val json = resolver.openInputStream(uri)?.use { stream ->
                        IOUtils.toString(stream, StandardCharsets.UTF_8)
                    } ?: throw RuntimeException("Cannot load json")

                    viewModel.loadScheduleFromJson(json)
                    showSnack("LOADED!!!")

                } catch (e: Exception) {
                    e.printStackTrace()
                    showSnack("Error :(")
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

        navigateTo(R.id.fromScheduleFragmentToScheduleViewFragment,
            ScheduleViewFragment.createBundle(schedule,
                SchedulePreference.createPath(requireContext(), schedule)
            )
        )
    }

    override fun onScheduleItemLongClicked(schedule: String, position: Int) {
        startActionMode(position)
    }

    override fun onScheduleFavoriteSelected(favorite: String) {
        viewModel.setFavorite(favorite)
    }

    override fun onScheduleItemMove(fromPosition: Int, toPosition: Int) {
        viewModel.moveSchedule(fromPosition, toPosition)
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder?) {
        itemTouchHelper.startDrag(viewHolder!!)
    }

    /**
     * Запускает режим ActionMode.
     */
    private fun startActionMode(position: Int = -1) {
        if (actionMode == null) {
            actionMode = (activity as AppCompatActivity?)?.startSupportActionMode(actionCallback)
            binding.addSchedule.hide()
            updateActionModeTitle()

            if (position >= 0) {
                selectItem(position)
            }
        }
    }

    /**
     * "Выбирает" объект в режиме ActionMode.
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
     * Обновляет число выбранных элементов в ActionMode.
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

    companion object {
        private val TAG = ScheduleFragment::class.java.simpleName + "Log"

        private const val ACTION_MODE = "action_mode"

        private const val REQUEST_ADD_SCHEDULE = 1
        private const val REQUEST_NEW_SCHEDULE = 2
        private const val REQUEST_PERMISSION_READ_STORAGE = 3
        private const val REQUEST_LOAD_SCHEDULE = 4
    }
}