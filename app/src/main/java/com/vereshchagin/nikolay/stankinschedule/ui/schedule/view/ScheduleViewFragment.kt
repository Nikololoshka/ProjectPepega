package com.vereshchagin.nikolay.stankinschedule.ui.schedule.view

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.vereshchagin.nikolay.stankinschedule.MainActivity
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.FragmentScheduleViewBinding
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.PairItem
import com.vereshchagin.nikolay.stankinschedule.settings.ApplicationPreference
import com.vereshchagin.nikolay.stankinschedule.ui.BaseFragment
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.ScheduleEditorActivity
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.name.ScheduleNameEditorDialog
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.pair.PairEditorActivity
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.view.paging.ScheduleViewAdapter
import com.vereshchagin.nikolay.stankinschedule.utils.StatefulLayout2
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

    /**
     * Лаунчер для запросы разрешения на запись на устройство.
     */
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(), this::onSavePermissionResult
    )

    /**
     *Лаунчер для выбора места для сохранения расписания.
     */
    private val saveLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocumentTree(), this::onScheduleSaveResult
    )

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

        // callback для изменения названия расписания
        parentFragmentManager.setFragmentResultListener(
            ScheduleNameEditorDialog.REQUEST_SCHEDULE_NAME, this, this::onScheduleRenameResult
        )

        // адаптер
        adapter = ScheduleViewAdapter(this::onPairClicked)
        binding.schViewContainer.adapter = adapter

        viewModel.scheduleDays.observe(viewLifecycleOwner) {
            adapter.submitData(lifecycle, it)
        }

        viewModel.scheduleState.observe(viewLifecycleOwner) {
            val state = it ?: return@observe
            onScheduleStateChanged(state)
        }

        viewModel.actionState.observe(viewLifecycleOwner) {
            val state = it ?: return@observe
            onActionStateChanged(state)
        }

        trackScreen(TAG, MainActivity.TAG)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _statefulLayout = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_schedule_view, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // редактирование расписания
            R.id.edit_schedule -> {
                val intent = ScheduleEditorActivity.createIntent(requireContext(), scheduleName)
                startActivity(intent)
                return true
            }
            // переименование расписания
            R.id.rename_schedule -> {
                val dialog = ScheduleNameEditorDialog.newInstance(scheduleName)
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
                    onSavePermissionResult(true)
                } else {
                    permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
                        removeSchedule()
                    }.show()

                return true
            }
            // добавить пару
            R.id.add_pair -> {
                val intent = PairEditorActivity.newPairIntent(requireContext(), scheduleName)
                startActivity(intent)

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Обрабатывает запрос на изменение имени.
     */
    private fun onScheduleRenameResult(key: String, result: Bundle) {
        if (key == ScheduleNameEditorDialog.REQUEST_SCHEDULE_NAME) {
            val newScheduleName = result.getString(ScheduleNameEditorDialog.SCHEDULE_NAME)
            if (newScheduleName != null) {
                viewModel.renameSchedule(newScheduleName)
                scheduleName = newScheduleName
                updateActionBar()
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
     * Устанавливает название расписания в action bar.
     */
    private fun updateActionBar() {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = scheduleName
    }

    /**
     * Вызывается, когда была нажата пара.
     */
    private fun onPairClicked(pair: PairItem) {
        val intent = PairEditorActivity.editPairIntent(requireContext(), scheduleName, pair.id)
        startActivity(intent)
    }

    private fun onScheduleStateChanged(state: ScheduleViewViewModel.ScheduleState) {
        when (state) {
            ScheduleViewViewModel.ScheduleState.LOADING -> {
                statefulLayout.setState(StatefulLayout2.LOADING)
            }
            ScheduleViewViewModel.ScheduleState.SUCCESSFULLY_LOADED -> {
                statefulLayout.setState(StatefulLayout2.CONTENT)
            }
            ScheduleViewViewModel.ScheduleState.SUCCESSFULLY_LOADED_EMPTY -> {
                statefulLayout.setState(StatefulLayout2.EMPTY)
            }
            ScheduleViewViewModel.ScheduleState.NOT_EXIST -> {
                requireActivity().also {
                    Toast.makeText(it, "Schedule not exist: $scheduleName", Toast.LENGTH_SHORT)
                        .show()
                    it.onBackPressed()
                }
            }

        }
    }

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

            }
            ScheduleViewViewModel.ScheduleActionState.NONE -> {
                return
            }
        }
        viewModel.actionComplete()
    }

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
        val date = viewModel.schedule?.startDate()
        if (date != null) {
            scrollScheduleTo(date)
        }
    }

    /**
     * Перемещает текущую позицию просмотра расписания к концу расписания.
     */
    private fun scrollToScheduleEnd() {
        val date = viewModel.schedule?.endDate()
        if (date != null) {
            scrollScheduleTo(date)
        }
    }

    /**
     * Удаляет текущие расписание.
     */
    private fun removeSchedule() {
        viewModel.removeSchedule()
    }

    companion object {

        private const val TAG = "ScheduleViewFragment"

        private const val SCHEDULE_NAME = "schedule_name"
        private const val SCHEDULE_START_DATE = "schedule_start_date"

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