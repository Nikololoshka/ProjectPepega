package com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.pair

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.*
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.date.DateEditorActivity
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.pair.PairEditorViewModel.State.*
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.pair.list.PairDatesAdaptor
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.pair.list.SwipeToDeleteCallback
import com.vereshchagin.nikolay.stankinschedule.utils.*
import com.vereshchagin.nikolay.stankinschedule.utils.extensions.currentPosition
import com.vereshchagin.nikolay.stankinschedule.utils.extensions.setCurrentPosition
import com.vereshchagin.nikolay.stankinschedule.utils.extensions.setOkButton
import com.vereshchagin.nikolay.stankinschedule.view.DropDownAdapter

/**
 * Активность редактирования пары.
 */
class PairEditorActivity : AppCompatActivity(), PairDatesAdaptor.OnDateItemClickListener {

    private lateinit var binding: ActivityPairEditorBinding
    private val viewModel by viewModels<PairEditorViewModel> {
        PairEditorViewModel.Factory(application, scheduleName)
    }

    private lateinit var statefulEditor: StatefulLayout2
    private lateinit var statefulDates: StatefulLayout2
    private lateinit var adapter: PairDatesAdaptor

    private lateinit var request: Request
    private lateinit var date: Date
    private lateinit var scheduleName: String
    private var editablePair: Pair? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        initFields()

        request = intent.getSerializableExtra(EXTRA_REQUEST) as Request
        scheduleName = intent.getStringExtra(EXTRA_SCHEDULE_NAME)!!
        editablePair = intent.getParcelableExtra(EXTRA_PAIR)

        // Log.d(TAG, "onCreate: $scheduleName")

        if (savedInstanceState != null) {
            date = savedInstanceState.getParcelable(DATE_PAIR)!!

        } else {
            if (request == Request.EDIT_PAIR) {
                editablePair!!.let {
                    date = it.date.clone()
                    bind(it)
                }
            } else {
                date = Date()
            }
        }

        // настройка дат
        val (every, through) = resources.getStringArray(R.array.frequency_simple_list)
        adapter = PairDatesAdaptor(this, every, through)
        binding.recyclerDates.adapter = adapter

        adapter.submitList(date)
        updateDatesCountView()

        binding.addDate.setOnClickListener {
            val intent = DateEditorActivity.newDateIntent(this, date)
            startActivityForResult(intent, REQUEST_DATE_EDITOR)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
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
        val title = binding.editTextTitle.text.toString()
        when (request) {
            Request.NEW_PAIR -> {
                if (title.isEmpty() || date.isEmpty()) {
                    super.onBackPressed()
                    return
                }
            }
            Request.EDIT_PAIR -> {
                val equal = editablePair!!.elementEqua1s(
                    title,
                    binding.editTextLecturer.text.toString(),
                    binding.editTextClassroom.text.toString(),
                    currentType(),
                    currentSubgroup(),
                    Time(currentStartTime(), currentEndTime()),
                    date
                )

                if (equal) {
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
                savePair()
                dialog.dismiss()
            }
            .show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            // завершить редактирование пары
            R.id.apply_pair -> {
                savePair()
                return true
            }
            // удалить текущую пару
            R.id.remove_pair -> {
                removePair()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data == null  || requestCode != REQUEST_DATE_EDITOR) {
            return
        }

        val oldDate = data.getParcelableExtra<DateItem>(DateEditorActivity.EXTRA_DATE_OLD)
        val newDate = data.getParcelableExtra<DateItem>(DateEditorActivity.EXTRA_DATE_NEW)

        Log.d(TAG, "onActivityResult: $oldDate and $newDate")

        when (resultCode) {
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
        startActivityForResult(intent, REQUEST_DATE_EDITOR)
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
        initAutoComplete(binding.spinnerTimeStart2, Time.STARTS)
        initAutoComplete(binding.spinnerTimeEnd2, Time.ENDS)

        // Listener для ограничения списка окончания пар, при смене начала пары.
        binding.spinnerTimeStart2.setOnItemClickListener { _, _, position, _ ->
            var newPos = binding.spinnerTimeEnd2.currentPosition()

            val times = Time.ENDS.subList(position, Time.ENDS.size)
            val adapter = DropDownAdapter(baseContext, times)
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
    private fun initAutoComplete(autoComplete: MaterialAutoCompleteTextView, objects: List<String>) {
        val adapter = DropDownAdapter(this, objects)
        autoComplete.setAdapter(adapter)

        if (autoComplete.text.isNullOrEmpty()) {
            autoComplete.setText(adapter.getItem(0), false)
        }
    }

    /**
     * Присоединяет данные к View.
     */
    private fun bind(pair: Pair) {
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
        val position = Time.ENDS.indexOf(binding.spinnerTimeEnd2.text.toString())
        val times = Time.ENDS.subList(position, Time.ENDS.size)
        val adapter = DropDownAdapter(baseContext, times)
        binding.spinnerTimeEnd2.setAdapter(adapter)
    }

    private fun resourcesArray(id: Int): List<String> {
        return resources.getStringArray(id).asList()
    }

    /**
     * Сохраняет пару в расписание.
     * @return true если удалось сохранить, иначе false.
     */
    private fun savePair() : Boolean {
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

            val newPair = Pair(title, lecturer, classroom, type, subgroup, time, date)
            viewModel.schedule?.let {
                it.changePair(editablePair, newPair)
                viewModel.saveSchedule()
            }

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
    private fun removePair() {
        viewModel.schedule?.let {
            it.remove(editablePair)
            viewModel.saveSchedule()
        }
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

        private const val REQUEST_DATE_EDITOR = 0

        private const val EXTRA_SCHEDULE_NAME = "extra_schedule"
        private const val EXTRA_PAIR = "extra_pair"
        private const val EXTRA_REQUEST = "extra_request"

        private const val DATE_PAIR = "date_pair"

        /**
         * Intent на создание новой пары.
         */
        fun newPairIntent(context: Context, scheduleName: String): Intent {
            val intent = Intent(context, PairEditorActivity::class.java)
            intent.putExtra(EXTRA_SCHEDULE_NAME, scheduleName)
            intent.putExtra(EXTRA_REQUEST, Request.NEW_PAIR)
            return intent
        }

        /**
         * Intent на редактирование пары.
         */
        fun editPairIntent(context: Context, scheduleName: String, pair: Pair) : Intent {
            val intent = Intent(context, PairEditorActivity::class.java)
            intent.putExtra(EXTRA_SCHEDULE_NAME, scheduleName)
            intent.putExtra(EXTRA_PAIR, pair)
            intent.putExtra(EXTRA_REQUEST, Request.EDIT_PAIR)
            return intent
        }
    }
}