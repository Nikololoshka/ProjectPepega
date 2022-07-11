package com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.pair

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.ActivityPairEditorBinding
import com.vereshchagin.nikolay.stankinschedule.model.schedule.PairIntersectException
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.PairItem
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.*
import com.vereshchagin.nikolay.stankinschedule.ui.BaseActivity
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.date.DateEditorActivity
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.pair.PairEditorViewModel.State.*
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.pair.list.PairDatesAdaptor
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.pair.list.SwipeToDeleteCallback
import com.vereshchagin.nikolay.stankinschedule.utils.StatefulLayout2
import com.vereshchagin.nikolay.stankinschedule.utils.extensions.currentPosition
import com.vereshchagin.nikolay.stankinschedule.utils.extensions.setCurrentPosition
import com.vereshchagin.nikolay.stankinschedule.utils.extensions.setOkButton
import com.vereshchagin.nikolay.stankinschedule.view.DropDownAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Активность редактирования пары.
 */
@AndroidEntryPoint
class PairEditorActivity :
    BaseActivity<ActivityPairEditorBinding>(ActivityPairEditorBinding::inflate),
    PairDatesAdaptor.OnDateItemClickListener {

    @Inject
    lateinit var viewModelFactory: PairEditorViewModel.PairEditorFactory

    /**
     * ViewModel активности.
     */
    private val viewModel: PairEditorViewModel by viewModels {
        PairEditorViewModel.provideFactory(viewModelFactory, scheduleId, editablePairId)
    }

    private lateinit var statefulEditor: StatefulLayout2
    private lateinit var statefulDates: StatefulLayout2
    private lateinit var adapter: PairDatesAdaptor

    /**
     * Тип запроса для редактора (создание / редактирование).
     */
    private lateinit var request: Request

    /**
     * Дата редактируемой пары.
     */
    private lateinit var date: Date

    /**
     * ID расписания в котором редактируется пара.
     */
    private var scheduleId: Long = -1

    /**
     * ID редактируемой пары.
     */
    private var editablePairId: Long = -1

    /**
     * Лаунчер для обработки результатов от редактирования дат.
     */
    private val dateEditorLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(), this::onDateEditorResult
    )

    override fun onPostCreateView(savedInstanceState: Bundle?) {

        binding = ActivityPairEditorBinding.inflate(layoutInflater)

        statefulEditor = StatefulLayout2.Builder(binding.statefulLayout)
            .init(StatefulLayout2.LOADING, binding.editorLoading.root)
            .addView(StatefulLayout2.CONTENT, binding.editorContent)
            .create()

        statefulDates = StatefulLayout2.Builder(binding.datesLayout)
            .init(StatefulLayout2.EMPTY, binding.emptyDates)
            .addView(StatefulLayout2.CONTENT, binding.recyclerDates)
            .create()

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // получение данных
        request = intent.getSerializableExtra(EXTRA_REQUEST) as Request
        scheduleId = intent.getLongExtra(EXTRA_SCHEDULE_ID, -1)
        editablePairId = intent.getLongExtra(EXTRA_PAIR_ID, -1)

        initFields()

        viewModel.editablePair.observe(this) { pair ->
            if (savedInstanceState != null) {
                date = savedInstanceState.getParcelable(DATE_PAIR)!!
            } else {
                if (request == Request.EDIT_PAIR && pair != null) {
                    date = pair.date.clone()
                    bind(pair)

                } else {
                    val title = intent.getStringExtra(EXTRA_DISCIPLINE_NAME)
                    if (!title.isNullOrEmpty()) {
                        binding.editTextTitle.setText(title)
                    }
                    val type = intent.getStringExtra(EXTRA_PAIR_TYPE)
                    if (!type.isNullOrEmpty()) {
                        setCurrentType(Type.of(type))
                    }
                    date = Date()
                }
            }
            initDatesAdapter()
        }

        viewModel.scheduleState.observe(this, Observer {
            val state = it ?: return@Observer

            when (state) {
                SUCCESSFULLY_SAVED -> {
                    val intent = Intent()
                    setResult(Activity.RESULT_OK, intent)
                    super.onBackPressed()
                }
                SUCCESSFULLY_LOADED -> {
                    statefulEditor.setState(StatefulLayout2.CONTENT)
                }
                LOADING -> {
                    statefulEditor.setState(StatefulLayout2.LOADING)
                }
                ERROR -> {
                    Toast.makeText(
                        applicationContext,
                        R.string.pair_editor_loading_schedule_error, Toast.LENGTH_LONG
                    ).show()
                    super.onBackPressed()
                }
            }
        })
    }

    private fun initDatesAdapter() {
        val (every, through) = resources.getStringArray(R.array.frequency_simple_list)
        adapter = PairDatesAdaptor(this, every, through)
        binding.recyclerDates.adapter = adapter

        adapter.submitList(date)
        updateDatesCountView()

        binding.addDate.setOnClickListener {
            val intent = DateEditorActivity.newDateIntent(this, date)
            dateEditorLauncher.launch(intent)
        }

        val swipeToDeleteCallback = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val item = date.remove(position)
                Log.d(TAG, "onSwiped: $date")

                adapter.submitList(date)

                Snackbar.make(
                    binding.root,
                    R.string.pair_editor_date_removed,
                    Snackbar.LENGTH_SHORT
                )
                    .setAction(R.string.undo) {
                        date.add(item)
                        adapter.submitList(date)
                        updateDatesCountView()
                    }
                    .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            super.onDismissed(transientBottomBar, event)
                            Log.d(TAG, "onDismissed: $date")
                        }
                    })
                    .show()

                updateDatesCountView()
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerDates)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_pair_editor, menu)
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(DATE_PAIR, date)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        restoreBind()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        when (request) {
            Request.NEW_PAIR -> {
                val title = binding.editTextTitle.text.toString()
                if (title.isEmpty() || date.isEmpty()) {
                    super.onBackPressed()
                    return
                }
            }
            Request.EDIT_PAIR -> {

                val currentPair = viewModel.editablePair.value

                val isEqual = if (currentPair != null) {
                    val title = binding.editTextTitle.text.toString()
                    val lecturer = binding.editTextLecturer.text.toString()
                    val classroom = binding.editTextClassroom.text.toString()
                    val type = currentType()
                    val subgroup = currentSubgroup()
                    val time = Time(currentStartTime(), currentEndTime())

                    currentPair.equalData(title, lecturer, classroom, type, subgroup, time, date)

                } else {
                    true
                }

                // нет изменений, то выходим
                if (isEqual) {
                    super.onBackPressed()
                    return
                }
            }
        }

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.pair_editor_pair_changed_title)
            .setMessage(R.string.pair_editor_pair_changed_message)
            .setNegativeButton(R.string.exit) { dialog, _ ->
                super.onBackPressed()
                dialog.dismiss()
            }
            .setPositiveButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .setNeutralButton(R.string.pair_editor_save) { dialog, _ ->
                onSavePairClicked()
                dialog.dismiss()
            }
            .show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // завершить редактирование пары
            R.id.apply_pair -> {
                onSavePairClicked()
                return true
            }
            // удалить текущую пару
            R.id.remove_pair -> {
                onRemovePairClicked()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * Вызывается при завершении редактирования даты пары.
     */
    private fun onDateEditorResult(result: ActivityResult) {
        val data = result.data ?: return

        val oldDate = data.getParcelableExtra<DateItem>(DateEditorActivity.EXTRA_DATE_OLD)
        val newDate = data.getParcelableExtra<DateItem>(DateEditorActivity.EXTRA_DATE_NEW)

        when (result.resultCode) {
            DateEditorActivity.RESULT_DATE_REMOVE -> {
                date.remove(oldDate)
            }
            DateEditorActivity.RESULT_DATE_NEW -> {
                date.add(newDate!!)
            }
            DateEditorActivity.RESULT_DATE_EDIT -> {
                date.remove(oldDate)
                date.add(newDate!!)
            }
        }

        adapter.submitList(date)
        updateDatesCountView()
    }

    override fun onDateItemClicked(position: Int) {
        val intent = DateEditorActivity.editDateIntent(this, date, date.get(position))
        dateEditorLauncher.launch(intent)
    }

    /**
     * Инициализация полей.
     */
    private fun initFields() {
        binding.editTextTitle.doOnTextChanged { _, _, _, _ ->
            isCorrectTitleField()
        }

        initAutoComplete(binding.spinnerType2, resourcesArray(R.array.type_list))
        initAutoComplete(binding.spinnerSubgroup2, resourcesArray(R.array.subgroup_list))
        initAutoComplete(binding.spinnerTimeStart2, resourcesArray(R.array.time_start_list))
        initAutoComplete(binding.spinnerTimeEnd2, resourcesArray(R.array.time_end_list))

        // Listener для ограничения списка окончания пар, при смене начала пары.
        val endTimes = resourcesArray(R.array.time_end_list)
        binding.spinnerTimeStart2.setOnItemClickListener { _, _, position, _ ->
            var newPos = binding.spinnerTimeEnd2.currentPosition()

            val times = endTimes.subList(position, endTimes.size)
            val adapter = DropDownAdapter(this, times)
            binding.spinnerTimeEnd2.setAdapter(adapter)

            if (newPos > 7 - position) {
                newPos = 0
            }

            binding.spinnerTimeEnd2.setText(times[newPos], false)
        }
    }

    /**
     * Инициализация полей с DropDown меню.
     */
    private fun initAutoComplete(
        autoComplete: MaterialAutoCompleteTextView,
        objects: List<String>,
    ) {
        val adapter = DropDownAdapter(this, objects)
        autoComplete.setAdapter(adapter)

        if (autoComplete.text.isNullOrEmpty()) {
            autoComplete.setText(adapter.getItem(0), false)
        }
    }

    /**
     * Присоединяет данные к View.
     */
    private fun bind(pair: PairItem) {
        binding.editTextTitle.setText(pair.title)
        binding.editTextLecturer.setText(pair.lecturer)
        binding.editTextClassroom.setText(pair.classroom)
        setCurrentType(pair.type)
        setCurrentSubgroup(pair.subgroup)
        setCurrentTime(pair.time)
    }

    /**
     * Восстанавливает состояние View.
     */
    private fun restoreBind() {
        val endTimes = resourcesArray(R.array.time_end_list)
        val position = endTimes.indexOf(binding.spinnerTimeEnd2.text.toString())
        val times = endTimes.subList(position, endTimes.size)
        val adapter = DropDownAdapter(this, times)
        binding.spinnerTimeEnd2.setAdapter(adapter)
    }

    private fun resourcesArray(id: Int): List<String> {
        return resources.getStringArray(id).asList()
    }

    /**
     * Сохраняет пару в расписание.
     * @return true если удалось сохранить, иначе false.
     */
    private fun onSavePairClicked(): Boolean {
        if (!isCorrectTitleField() || !isCorrectDateField()) {
            return false
        }

        try {
            val title = binding.editTextTitle.text.toString()
            val lecturer = binding.editTextLecturer.text.toString()
            val classroom = binding.editTextClassroom.text.toString()
            val type = currentType()
            val subgroup = currentSubgroup()
            val time = Time(currentStartTime(), currentEndTime())

            val newPair = PairItem(title, lecturer, classroom, type, subgroup, time, date)
            viewModel.changePair(newPair)

        } catch (e: PairIntersectException) {
            val message = getString(R.string.pair_editor_conflict_pair, e.first)
            MaterialAlertDialogBuilder(this, R.style.AppAlertDialog)
                .setTitle(R.string.error)
                .setMessage(message)
                .setOkButton()
                .show()

            return false
        }

        return true
    }

    /**
     * Удаляет пару из расписания.
     */
    private fun onRemovePairClicked() {
        viewModel.removePair()
    }

    /**
     * Устанавливает тип пары.
     */
    private fun setCurrentType(type: Type) {
        val pos = listOf(
            Type.LECTURE, Type.SEMINAR, Type.LABORATORY
        ).indexOf(type)

        binding.spinnerType2.setCurrentPosition(pos)
    }

    /**
     * Текущий тип пары.
     */
    private fun currentType(): Type {
        return listOf(
            Type.LECTURE, Type.SEMINAR, Type.LABORATORY
        )[binding.spinnerType2.currentPosition()]
    }

    /**
     * Устанавливает подгруппу.
     */
    private fun setCurrentSubgroup(subgroup: Subgroup) {
        val pos = listOf(
            Subgroup.COMMON, Subgroup.A, Subgroup.B
        ).indexOf(subgroup)

        binding.spinnerSubgroup2.setCurrentPosition(pos)
    }

    /**
     * Устанавливает время.
     */
    private fun setCurrentTime(time: Time) {
        binding.spinnerTimeStart2.setCurrentPosition(time.number())
        binding.spinnerTimeEnd2.setCurrentPosition(time.number() + time.duration - 1)
        restoreBind()
    }

    /**
     * Текущая подгруппа.
     */
    private fun currentSubgroup(): Subgroup {
        return listOf(
            Subgroup.COMMON, Subgroup.A, Subgroup.B
        )[binding.spinnerSubgroup2.currentPosition()]
    }

    /**
     * Начало пары.
     */
    private fun currentStartTime(): String {
        return binding.spinnerTimeStart2.text.toString()
    }

    /**
     * Конец пары.
     */
    private fun currentEndTime(): String {
        return binding.spinnerTimeEnd2.text.toString()
    }

    /**
     * Проверяет, корректно ли поле с названием пары.
     */
    private fun isCorrectTitleField(): Boolean {
        val text = binding.editTextTitle.text.toString()
        if (text.isEmpty()) {
            binding.titleLayout.error = getString(R.string.pair_editor_empty_title)
            return false
        }
        binding.titleLayout.error = null
        return true
    }

    /**
     * Проверяет, корректна ли дата пары.
     */
    private fun isCorrectDateField(): Boolean {
        if (date.isEmpty()) {
            MaterialAlertDialogBuilder(this, R.style.AppAlertDialog)
                .setTitle(R.string.error)
                .setMessage(R.string.pair_editor_empty_dates_list)
                .setOkButton()
                .show()

            return false
        }
        return true
    }

    /**
     * Обновляет View с списком дат.
     */
    private fun updateDatesCountView() {
        statefulDates.setState(
            if (date.isEmpty()) StatefulLayout2.EMPTY else StatefulLayout2.CONTENT
        )
    }

    /**
     * Перечисление с запросами.
     */
    private enum class Request {
        NEW_PAIR,
        EDIT_PAIR
    }

    companion object {

        private const val TAG = "PairEditorActivity2Log"

        private const val EXTRA_SCHEDULE_ID = "extra_schedule"
        private const val EXTRA_DISCIPLINE_NAME = "extra_discipline_name"
        private const val EXTRA_PAIR_TYPE = "extra_pair_type"
        private const val EXTRA_PAIR_ID = "extra_pair"
        private const val EXTRA_REQUEST = "extra_request"

        private const val DATE_PAIR = "date_pair"

        /**
         * Intent на создание новой пары.
         */
        fun newPairIntent(
            context: Context,
            scheduleId: Long,
            disciplineName: String? = null,
            type: Type? = null,
        ): Intent {
            val intent = Intent(context, PairEditorActivity::class.java)
            intent.putExtra(EXTRA_SCHEDULE_ID, scheduleId)
            intent.putExtra(EXTRA_REQUEST, Request.NEW_PAIR)
            intent.putExtra(EXTRA_DISCIPLINE_NAME, disciplineName)
            intent.putExtra(EXTRA_PAIR_TYPE, type?.tag)
            return intent
        }

        /**
         * Intent на редактирование пары.
         */
        fun editPairIntent(context: Context, scheduleId: Long, pairId: Long): Intent {
            val intent = Intent(context, PairEditorActivity::class.java)
            intent.putExtra(EXTRA_SCHEDULE_ID, scheduleId)
            intent.putExtra(EXTRA_PAIR_ID, pairId)
            intent.putExtra(EXTRA_REQUEST, Request.EDIT_PAIR)
            return intent
        }
    }
}