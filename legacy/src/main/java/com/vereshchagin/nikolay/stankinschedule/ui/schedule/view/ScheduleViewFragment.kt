package com.vereshchagin.nikolay.stankinschedule.ui.schedule.view

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.vereshchagin.nikolay.stankinschedule.MainActivity
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.FragmentScheduleViewBinding
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.PairItem
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.ScheduleItem
import com.vereshchagin.nikolay.stankinschedule.settings.ApplicationPreferenceKt
import com.vereshchagin.nikolay.stankinschedule.ui.BaseFragment
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.name.ScheduleNameEditorDialog
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.pair.PairEditorActivity
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.view.ScheduleEditorActivity
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.view.paging.ScheduleViewAdapter
import com.vereshchagin.nikolay.stankinschedule.utils.StatefulLayout2
import com.vereshchagin.nikolay.stankinschedule.utils.delegates.FragmentDelegate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import org.joda.time.Days
import org.joda.time.LocalDate
import javax.inject.Inject

/**
 * Фрагмент просмотра расписания.
 */
@AndroidEntryPoint
class ScheduleViewFragment :
    BaseFragment<FragmentScheduleViewBinding>(FragmentScheduleViewBinding::inflate) {

    @Inject
    lateinit var viewModelFactory: ScheduleViewViewModel.ScheduleViewFactory

    @Inject
    lateinit var preference: ApplicationPreferenceKt

    /**
     * ViewModel фрагмента.
     */
    private val viewModel: ScheduleViewViewModel by viewModels {
        ScheduleViewViewModel.provideFactory(viewModelFactory, scheduleId, startScheduleDate, this)
    }

    /**
     * Менеджер состояний.
     */
    private var statefulLayout: StatefulLayout2 by FragmentDelegate()

    /**
     * ID расписания.
     */
    private var scheduleId: Long = -1

    /**
     * Начальная дата для отображение расписания.
     */
    private var startScheduleDate: LocalDate = LocalDate.now()

    /**
     * Адаптер расписания.
     */
    private var adapter: ScheduleViewAdapter by FragmentDelegate()

    /**
     * Лаунчер для запросы разрешения на запись на устройство.
     */
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(), this::onSavePermissionResult
    )

    /**
     * Лаунчер для выбора места для сохранения расписания.
     */
    private val saveLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocumentTree(), this::onScheduleSaveResult
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


    override fun onPostCreateView(savedInstanceState: Bundle?) {
        statefulLayout = StatefulLayout2.Builder(binding.statefulLayout)
            .init(StatefulLayout2.LOADING, binding.schViewLoading.root)
            .addView(StatefulLayout2.CONTENT, binding.schViewContainer)
            .addView(StatefulLayout2.EMPTY, binding.schViewEmpty)
            .create()

        // получение отображаемых данных
        val currentArguments = requireArguments()
        scheduleId = currentArguments.getLong(SCHEDULE_ID, -1)
        startScheduleDate = currentArguments.getSerializable(SCHEDULE_START_DATE) as LocalDate?
            ?: LocalDate.now()

        // отображение расписания (горизонтально / вертикально)
        val method = preference.scheduleViewMethod
        if (method == ApplicationPreferenceKt.SCHEDULE_VIEW_HORIZONTAL) {
            (binding.schViewContainer.layoutManager as LinearLayoutManager).let {
                it.orientation = RecyclerView.HORIZONTAL
                val snapHelper = LinearSnapHelper()
                snapHelper.attachToRecyclerView(binding.schViewContainer)
            }
        }

        // callback для изменения названия расписания
        parentFragmentManager.setFragmentResultListener(
            ScheduleNameEditorDialog.REQUEST_SCHEDULE_NAME, this, this::onScheduleRenameResult
        )

        // адаптер
        adapter = ScheduleViewAdapter(this::onPairClicked)
        binding.schViewContainer.adapter = adapter

        // определение текущей позиции для сохранения ее в последующих обновлениях
        binding.schViewContainer.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    //Dragging
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    viewModel.updatePagingDate(currentPagerDay())
                }
            }
        })

        // информация о расписании
        lifecycleScope.launchWhenStarted {
            viewModel.scheduleItem
                .filterNotNull()
                .collect(::onScheduleItemChanged)
        }

        // состояние загрузки расписания
        lifecycleScope.launchWhenStarted {
            viewModel.scheduleState.collect(::onScheduleStateChanged)
        }

        // отображение списка с занятиями в расписании
        lifecycleScope.launchWhenStarted {
            viewModel.scheduleDays.collect { data ->
                adapter.submitData(data)
            }
        }

        // состояние действий над расписаниями
        lifecycleScope.launchWhenCreated {
            viewModel.actionState.collect(::onActionStateChanged)
        }
    }

    override fun onStart() {
        super.onStart()
        trackScreen(TAG, MainActivity.TAG)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_schedule_view, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        // расписание синхронизировано
        val isSync = isScheduleSynced()
        menu.findItem(R.id.edit_schedule).isEnabled = !isSync
        menu.findItem(R.id.rename_schedule).isEnabled = !isSync
        menu.findItem(R.id.add_pair).isEnabled = !isSync

        // расписание пустое
        val isEmpty = isScheduleEmpty()
        menu.findItem(R.id.go_to).isEnabled = !isEmpty
        menu.findItem(R.id.go_to_day).isEnabled = !isEmpty
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // редактирование расписания
            R.id.edit_schedule -> {
                onScheduleEditClicked()
                return true
            }
            // переименование расписания
            R.id.rename_schedule -> {
                onScheduleRenameClicked()
                return true
            }
            // перейти к дню
            R.id.go_to_day -> {
                onScheduleGoToDayClicked()
                return true
            }
            // перейти к началу расписания
            R.id.to_start_schedule -> {
                onScrollToStartClicked()
                return true
            }
            // перейти к концу расписания
            R.id.to_end_schedule -> {
                onScrollToEndClicked()
                return true
            }
            // сохранить расписание на устройство
            R.id.save_schedule -> {
                onScheduleSaveClicked()
                return true
            }
            // удалить расписания
            R.id.remove_schedule -> {
                onScheduleRemoveClicked()
                return true
            }
            // добавить пару
            R.id.add_pair -> {
                onAddPairClicked()
                return true
            }
            // информация о расписании
            R.id.schedule_info -> {
                showScheduleInfo()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Вызывает активность для редактирования расписания.
     */
    private fun onScheduleEditClicked() {
        if (canScheduleEdit()) {
            val intent = ScheduleEditorActivity.createIntent(requireContext(), scheduleId)
            startActivity(intent)
        } else {
            showScheduleNotEditDialog()
        }
    }

    /**
     * Вызывает редактор названия расписания.
     */
    private fun onScheduleRenameClicked() {
        if (canScheduleEdit()) {
            val scheduleName = viewModel.scheduleItem.value?.scheduleName
            if (scheduleName != null) {
                val dialog = ScheduleNameEditorDialog.newInstance(scheduleName)
                dialog.show(parentFragmentManager, dialog.tag)
            }
        } else {
            showScheduleNotEditDialog()
        }
    }

    /**
     * Вызывает перемещение в определенной дате при просмотре расписания.
     */
    private fun onScheduleGoToDayClicked() {
        val currentDay = currentPagerDay() ?: LocalDate.now()

        // picker даты
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                tryScrollToDate(LocalDate(year, month + 1, dayOfMonth))
            },
            currentDay.year,
            currentDay.monthOfYear - 1,
            currentDay.dayOfMonth
        ).apply {
            setButton(
                DialogInterface.BUTTON_NEUTRAL, getString(R.string.sch_view_today)
            ) { _, _ ->
                tryScrollToDate(LocalDate.now())
            }
            show()
        }
    }

    /**
     * Вызывает окно для добавление новой пары в расписание.
     */
    private fun onAddPairClicked() {
        if (canScheduleEdit()) {
            val intent = PairEditorActivity.newPairIntent(requireContext(), scheduleId)
            startActivity(intent)
        } else {
            showScheduleNotEditDialog()
        }
    }


    /**
     * Перемещает текущую позицию просмотра расписания к начале расписания.
     */
    private fun onScrollToStartClicked() {
        val date = viewModel.scheduleStartDate
        if (date != null) {
            tryScrollToDate(date)
        }
    }

    /**
     * Перемещает текущую позицию просмотра расписания к концу расписания.
     */
    private fun onScrollToEndClicked() {
        val date = viewModel.scheduleEndDate
        if (date != null) {
            tryScrollToDate(date)
        }
    }

    /**
     * Обрабатывает сохранение расписания на устройство.
     */
    private fun onScheduleSaveClicked() {
        // проверка возможности записи на устройство
        val permissionStatus = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            onSavePermissionResult(true)
        } else {
            permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    /**
     * Обрабатывает запрос на предоставление прав на сохранения расписания.
     *
     * Если hasPermission - true, то запускает активность с выбором пути
     * для сохранения расписания на устройство.
     */
    private fun onSavePermissionResult(hasPermission: Boolean) {
        if (hasPermission) {
            saveLauncher.launch(Uri.EMPTY)
        }
    }

    /**
     * Удаляет текущие расписание.
     */
    private fun onScheduleRemoveClicked() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.warning)
            .setMessage(getString(R.string.sch_view_will_be_deleted))
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.cancel()
            }
            .setPositiveButton(getString(R.string.yes_continue)) { _, _ ->
                viewModel.removeSchedule()
            }.show()
    }

    /**
     * Проверяет, является ли расписание редактируемым.
     */
    private fun canScheduleEdit(): Boolean {
        return !isScheduleSynced() && !isScheduleLoading()
    }

    /**
     * В процессе загрузки ли текущие расписание.
     */
    private fun isScheduleLoading(): Boolean {
        return viewModel.scheduleState.value == ScheduleViewViewModel.ScheduleState.LOADING
    }

    /**
     * Является ли текущие расписание синхронизированным.
     */
    private fun isScheduleSynced(): Boolean {
        return viewModel.scheduleItem.value?.synced ?: false
    }

    /**
     * Проверяет, является ли текущие расписание пустым.
     */
    private fun isScheduleEmpty(): Boolean {
        return viewModel.scheduleState.value ==
                ScheduleViewViewModel.ScheduleState.SUCCESSFULLY_LOADED_EMPTY
    }

    /**
     * Показывает диалог о том, что текущие расписание нельзя редактировать.
     */
    private fun showScheduleNotEditDialog() {
        Snackbar.make(binding.root, R.string.sch_view_not_edit, Snackbar.LENGTH_SHORT)
            .setAction(R.string.details) {
                showScheduleInfo()
            }
            .show()
    }

    /**
     * Показывает диалог с информацией о расписании
     */
    private fun showScheduleInfo() {
        val info = viewModel.scheduleItem.value ?: return

        val builder = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.sch_info)
            .setMessage(
                getString(
                    R.string.sch_info_description,
                    info.scheduleName,
                    getString(if (info.synced) R.string.sch_synced else R.string.sch_not_synced)
                )
            )
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }

        // отключение синхронизации
        if (info.synced) {
            builder.setNeutralButton(R.string.sch_off_sync) { dialog, _ ->
                showScheduleSyncDisableDialog()
                dialog.dismiss()
            }
        }

        builder.show()
    }

    /**
     * Показывает диалог с возможностью отключить синхронизацию расписания.
     */
    private fun showScheduleSyncDisableDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.warning)
            .setMessage(R.string.sch_off_sync_description)
            .setPositiveButton(R.string.yes_continue) { dialog, _ ->
                viewModel.disableScheduleSynced()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Обрабатывает запрос на изменение имени.
     */
    private fun onScheduleRenameResult(key: String, result: Bundle) {
        if (key == ScheduleNameEditorDialog.REQUEST_SCHEDULE_NAME) {
            val newScheduleName = result.getString(ScheduleNameEditorDialog.SCHEDULE_NAME)
            if (newScheduleName != null) {
                viewModel.renameSchedule(newScheduleName)
            }
        }
    }

    /**
     * Обрабатывает запрос на сохранения расписания.
     */
    private fun onScheduleSaveResult(uri: Uri?) {
        if (uri != null) {
            viewModel.saveScheduleToDevice(uri)
        }
    }

    /**
     * Вызывается при обновлении информации о расписании.
     */
    private fun onScheduleItemChanged(item: ScheduleItem) {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = item.scheduleName
    }

    /**
     * Вызывается, когда была нажата пара.
     */
    private fun onPairClicked(pair: PairItem) {
        if (canScheduleEdit()) {
            val intent = PairEditorActivity.editPairIntent(requireContext(), scheduleId, pair.id)
            startActivity(intent)
        }
    }

    /**
     * Обрабатывает состояние отображаемого расписания.
     */
    private fun onScheduleStateChanged(state: ScheduleViewViewModel.ScheduleState) {
        Log.d(TAG, "onScheduleStateChanged: $state")
        when (state) {
            ScheduleViewViewModel.ScheduleState.LOADING -> {
                statefulLayout.setState(StatefulLayout2.LOADING)
            }
            ScheduleViewViewModel.ScheduleState.SUCCESSFULLY_LOADED -> {
                statefulLayout.setState(StatefulLayout2.CONTENT)
                adapter.refresh()
            }
            ScheduleViewViewModel.ScheduleState.SUCCESSFULLY_LOADED_EMPTY -> {
                statefulLayout.setState(StatefulLayout2.EMPTY)
            }
            ScheduleViewViewModel.ScheduleState.NOT_EXIST -> {
                requireActivity().also {
                    Toast.makeText(it, "Schedule not exist: $scheduleId", Toast.LENGTH_SHORT)
                        .show()
                    it.onBackPressed()
                }
            }
        }
    }

    /**
     * Отображает состояние, связанное с действиями над расписанием.
     */
    private fun onActionStateChanged(state: ScheduleViewViewModel.ScheduleActionState) {
        when (state) {
            ScheduleViewViewModel.ScheduleActionState.RENAMED -> {
                showSnack(R.string.sch_view_renamed)
            }
            ScheduleViewViewModel.ScheduleActionState.REMOVED -> {
                showSnack(R.string.sch_removed)
                requireActivity().onBackPressed()
            }
            ScheduleViewViewModel.ScheduleActionState.EXPORTED -> {
                showSnack(R.string.sch_view_saved)
            }
        }
    }

    private fun tryScrollToDate(date: LocalDate) {
        val position = currentPosition()
        if (position == RecyclerView.NO_POSITION) {
            viewModel.showPagerForDate(date)
        } else {
            val currentDay = adapter.item(position)?.day
            if (currentDay == null) {
                viewModel.showPagerForDate(date)
            } else {
                val delta = Days.daysBetween(date, currentDay).days
                val newPosition = position - delta
                val totalCount = adapter.itemCount
                if (newPosition in 0 until totalCount) {
                    val prefetchDistance = ScheduleViewViewModel.PAGE_SIZE / 2
                    if (newPosition > prefetchDistance && newPosition < (totalCount - prefetchDistance - 1)) {
                        binding.schViewContainer.smoothScrollToPosition(newPosition)
                    } else {
                        binding.schViewContainer.scrollToPosition(newPosition)
                    }
                } else {
                    viewModel.showPagerForDate(date)
                }
            }
        }
    }

    /**
     * Возвращает текущею отображаемую позицию.
     * Если не удалось получить, то возвращает RecyclerView.NO_POSITION.
     */
    private fun currentPosition(): Int {
        return (binding.schViewContainer.layoutManager as LinearLayoutManager)
            .findFirstCompletelyVisibleItemPosition()
    }

    /**
     * Возвращает текущий отображаемый день.
     * Если не удалось определить день, то возвращается null.
     */
    private fun currentPagerDay(): LocalDate? {
        return adapter.item(currentPosition())?.day
    }

    companion object {

        private const val TAG = "ScheduleViewFragment"

        private const val SCHEDULE_ID = "schedule_id"
        private const val SCHEDULE_START_DATE = "schedule_start_date"

        /**
         * Создает bundle с данными, требуемыми для фрагмента.
         */
        @JvmStatic
        fun createBundle(scheduleId: Long, startDate: LocalDate = LocalDate.now()): Bundle {
            return Bundle().apply {
                putLong(SCHEDULE_ID, scheduleId)
                putSerializable(SCHEDULE_START_DATE, startDate)
            }
        }
    }
}