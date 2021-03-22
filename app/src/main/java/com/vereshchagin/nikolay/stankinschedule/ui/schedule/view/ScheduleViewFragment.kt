package com.vereshchagin.nikolay.stankinschedule.ui.schedule.view

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.FragmentScheduleViewBinding
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Pair
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepository
import com.vereshchagin.nikolay.stankinschedule.settings.ApplicationPreference
import com.vereshchagin.nikolay.stankinschedule.ui.BaseFragment
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.name.ScheduleNameEditorDialog
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.pair.PairEditorActivity
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.view.paging.ScheduleViewAdapter
import com.vereshchagin.nikolay.stankinschedule.utils.State
import com.vereshchagin.nikolay.stankinschedule.utils.StatefulLayout2
import kotlinx.coroutines.flow.collectLatest
import org.joda.time.LocalDate

/**
 * Фрагмент просмотра расписания.
 */
class ScheduleViewFragment : BaseFragment<FragmentScheduleViewBinding>() {

    /**
     * ViewModel фрагмента.
     */
    private lateinit var viewModel: ScheduleViewViewModel

    /**
     * Менеджер состояний.
     */
    private var _statefulLayout: StatefulLayout2? = null
    private val statefulLayout get() = _statefulLayout!!

    /**
     * Название расписания.
     */
    private lateinit var scheduleName: String

    /**
     * Адаптер расписания.
     */
    private lateinit var adapter: ScheduleViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onInflateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): FragmentScheduleViewBinding {
        return FragmentScheduleViewBinding.inflate(inflater, container, false)
    }

    override fun onPostCreateView(savedInstanceState: Bundle?) {
        _statefulLayout = StatefulLayout2.Builder(binding.statefulLayout)
            .init(StatefulLayout2.LOADING, binding.schViewLoading.root)
            .addView(StatefulLayout2.CONTENT, binding.schViewContainer)
            .addView(StatefulLayout2.EMPTY, binding.schViewEmpty)
            .addView(StatefulLayout2.ERROR, binding.schViewError)
            .create()

        // получение отображаемых данных
        scheduleName = arguments?.getString(SCHEDULE_NAME)!!
        val startDate = arguments?.getSerializable(SCHEDULE_START_DATE) as LocalDate?
        updateActionBar()

        viewModel = ViewModelProvider(
            this,
            ScheduleViewViewModel.Factory(
                scheduleName,
                startDate,
                activity?.application!!
            )
        ).get(ScheduleViewViewModel::class.java)

        // отображение расписания (горизонтально / вертикально)
        val method = ApplicationPreference.scheduleViewMethod(requireContext())
        if (method == ApplicationPreference.SCHEDULE_VIEW_HORIZONTAL) {
            (binding.schViewContainer.layoutManager as LinearLayoutManager).let {
                it.orientation = RecyclerView.HORIZONTAL
                val snapHelper = LinearSnapHelper()
                snapHelper.attachToRecyclerView(binding.schViewContainer)
            }
        }

        // адаптер
        adapter = ScheduleViewAdapter(this::onPairClicked)
        binding.schViewContainer.adapter = adapter

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                Log.d(TAG, "onItemRangeInserted: $positionStart $itemCount")
            }
        })

        viewModel.scheduleDays.observe(viewLifecycleOwner) {
            adapter.submitData(lifecycle, it)
        }

        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest { loadStates ->
                val isLoading = loadStates.refresh is LoadState.Loading
                if (isLoading) {
                    statefulLayout.setState(StatefulLayout2.LOADING)
                } else {
                    val state = viewModel.state.value
                    if (state is State.Success) {
                        updateContentView(state.data)
                    }
                }
            }
        }

        viewModel.state.observe(viewLifecycleOwner) {
            val state = it ?: return@observe
            if (state is State.Loading) {
                statefulLayout.setState(StatefulLayout2.LOADING)
            } else if (state is State.Failed) {
                statefulLayout.setState(StatefulLayout2.ERROR)
                binding.errorTitle.text = state.error.toString()
            }
        }

        trackScreen(TAG)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_schedule_view, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // переименование расписания
            R.id.rename_schedule -> {
                val dialog = ScheduleNameEditorDialog.newInstance(scheduleName)
                dialog.setTargetFragment(this, REQUEST_SCHEDULE_NAME)
                dialog.show(parentFragmentManager, dialog.tag)

                return true
            }
            // перейти к дню
            R.id.go_to_day -> {
                val currentDay = currentDay()

                // picker даты
                DatePickerDialog(
                    requireContext(),
                    { _, year, month, dayOfMonth ->
                        scrollScheduleTo(LocalDate(year, month + 1, dayOfMonth))
                    },
                    currentDay.year,
                    currentDay.monthOfYear - 1,
                    currentDay.dayOfMonth
                ).apply {
                    setButton(
                        DialogInterface.BUTTON_NEUTRAL, getString(R.string.sch_view_today)
                    ) { _, _ ->
                        scrollScheduleTo(LocalDate.now())
                    }
                    show()
                }

                return true
            }
            // перейти к началу расписания
            R.id.to_start_schedule -> {
                scrollToScheduleStart()
                return true
            }
            // перейти к концу расписания
            R.id.to_end_schedule -> {
                scrollToScheduleEnd()
                return true
            }
            // сохранить расписание на устройство
            R.id.save_schedule -> {
                // проверка возможности записи
                val permissionStatus = ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                    selectSchedulePathForSaveToDevice()
                } else {
                    requestPermissions(
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        REQUEST_PERMISSION_WRITE_STORAGE
                    )
                }

                return true
            }
            // удалить расписания
            R.id.remove_schedule -> {
                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.warning)
                    .setMessage(getString(R.string.sch_view_will_be_deleted))
                    .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                        dialog.cancel()
                    }
                    .setPositiveButton(getString(R.string.yes_continue)) { _, _ ->
                        removeScheduleAndExit()
                    }.show()

                return true
            }
            // добавить пару
            R.id.add_pair -> {
                if (viewModel.state.value is State.Success) {
                    val intent = PairEditorActivity.newPairIntent(requireContext(), scheduleName)
                    startActivityForResult(intent, REQUEST_PAIR)
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _statefulLayout = null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        // удалось ли получить запрос на запись
        if (requestCode == REQUEST_PERMISSION_WRITE_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // если да, сохраняем расписание
                selectSchedulePathForSaveToDevice()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_CANCELED || data == null) {
            return
        }

        when (requestCode) {
            // запрос связанный с парой
            REQUEST_PAIR -> {
                refreshSchedule()
            }
            // запрос на изменение имени
            REQUEST_SCHEDULE_NAME -> {
                val newScheduleName = data.getStringExtra(ScheduleNameEditorDialog.SCHEDULE_NAME)
                if (newScheduleName != null) {
                    ScheduleRepository()
                        .renameSchedule(requireContext(), scheduleName, newScheduleName)

                    scheduleName = newScheduleName
                    updateActionBar()
                    refreshSchedule()
                }
                showSnack(R.string.sch_view_renamed)
            }
            // запрос на сохранения расписания
            REQUEST_SAVE_SCHEDULE -> {
                saveScheduleToDevice(data)
            }
        }
    }

    /**
     * Устанавливает название расписания в action bar.
     */
    private fun updateActionBar() {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = scheduleName
    }

    /**
     * Перезагружает расписание.
     */
    private fun refreshSchedule() {
        viewModel.refreshPagerView(scheduleName, currentDay())
    }

    /**
     * Вызывается, когда была нажата пара.
     */
    private fun onPairClicked(pair: Pair) {
        val intent = PairEditorActivity.editPairIntent(requireContext(), scheduleName, pair)
        startActivityForResult(intent, REQUEST_PAIR)
    }

    /*
    private fun onAdapterStateChanged(states: CombinedLoadStates) {
        if (states.append.endOfPaginationReached && states.prepend.endOfPaginationReached) {
            if (adapter.itemCount == 0) {
                updateContentView(true)
            }
        }
        if (adapter.itemCount != 0) {
            updateContentView(false)
        }
    }

    private fun updateContentView(empty: Boolean) {
        if (viewModel.state.value is State.Success) {
            if (empty) {
                statefulLayout.setState(StatefulLayout2.EMPTY)
            } else {
                statefulLayout.setState(StatefulLayout2.CONTENT)
            }
        }
    }*/

    /**
     * Возвращает текущею отображаемую позицию.
     * Если не удалось получить, то возвращает RecyclerView.NO_POSITION.
     */
    private fun currentPosition(): Int {
        return (binding.schViewContainer.layoutManager as LinearLayoutManager?)
            ?.findFirstCompletelyVisibleItemPosition() ?: RecyclerView.NO_POSITION
    }

    /**
     * Возвращает текущий отображаемый день.
     */
    private fun currentDay(): LocalDate {
        val position = currentPosition()
        return if (position != RecyclerView.NO_POSITION) {
            adapter.item(position)?.day ?: LocalDate.now()
        } else {
            LocalDate.now()
        }
    }

    /**
     * Перемещает текущую позицию просмотра расписания к нужной дате.
     */
    private fun scrollScheduleTo(scrollDate: LocalDate) {
        var scrollIndex = RecyclerView.NO_POSITION
        for ((index, item) in adapter.snapshot().items.withIndex()) {
            if (item.day == scrollDate) {
                scrollIndex = index
                break
            }
        }

        if (scrollIndex != RecyclerView.NO_POSITION) {
            binding.schViewContainer.scrollToPosition(scrollIndex)
        } else {
            viewModel.updatePagerView(scrollDate)
        }
    }

    /**
     * Перемещает текущую позицию просмотра расписания к начале расписания.
     */
    private fun scrollToScheduleStart() {
        val date = viewModel.currentSchedule()?.startDate()
        if (date != null) {
            scrollScheduleTo(date)
        }
    }

    /**
     * Перемещает текущую позицию просмотра расписания к концу расписания.
     */
    private fun scrollToScheduleEnd() {
        val date = viewModel.currentSchedule()?.endDate()
        if (date != null) {
            scrollScheduleTo(date)
        }
    }

    /**
     * Обновляет UI контента расписания.
     * Если расписание пустое, то отображает соответствующие сообщение.
     */
    private fun updateContentView(empty: Boolean) {
        statefulLayout.setState(
            if (empty) {
                StatefulLayout2.EMPTY
            } else {
                StatefulLayout2.CONTENT
            }
        )
    }

    /**
     * Выбор пути для сохранения расписания на устройство.
     */
    private fun selectSchedulePathForSaveToDevice() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        startActivityForResult(
            Intent.createChooser(intent, getString(R.string.choose_folder)),
            REQUEST_SAVE_SCHEDULE
        )
    }

    /**
     * Сохраняет расписание на устройство.
     */
    private fun saveScheduleToDevice(data: Intent) {
        try {
            val uriFolder = data.data ?: return
            viewModel.saveScheduleToDevice(uriFolder)
            showSnack(R.string.sch_view_saved)

        } catch (ignored: Exception) {
            showSnack(R.string.sch_view_saved_error)
        }
    }

    /**
     * Удаляет текущие расписание.
     */
    private fun removeScheduleAndExit() {
        ScheduleRepository()
            .removeSchedule(requireContext(), scheduleName)
        showSnack(R.string.sch_removed)
        requireActivity().onBackPressed()
    }

    companion object {

        private const val TAG = "ScheduleViewFragment"

        private const val SCHEDULE_NAME = "schedule_name"
        private const val SCHEDULE_START_DATE = "schedule_start_date"

        private const val REQUEST_SCHEDULE_NAME = 0
        private const val REQUEST_PAIR = 1
        private const val REQUEST_SAVE_SCHEDULE = 2

        private const val REQUEST_PERMISSION_WRITE_STORAGE = 3

        /**
         * Создает bundle с данными, требуемыми для фрагмента.
         */
        @JvmStatic
        fun createBundle(scheduleName: String, startDate: LocalDate = LocalDate.now()): Bundle {
            return Bundle().apply {
                putString(SCHEDULE_NAME, scheduleName)
                putSerializable(SCHEDULE_START_DATE, startDate)
            }
        }
    }
}