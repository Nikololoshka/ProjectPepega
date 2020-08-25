package com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.date

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.DatePicker
import androidx.annotation.ArrayRes
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.ActivityDateEditorBinding
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.*
import com.vereshchagin.nikolay.stankinschedule.utils.*
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat

/**
 * Автивность редактора дат.
 */
class DateEditorActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityDateEditorBinding
    private lateinit var statefulMode: StatefulLayout2

    private lateinit var date: Date
    private lateinit var request: Request
    private var dateItem: DateItem? = null
    
    private var mode = Mode.SINGLE

    /**
     * Watcher для единственной даты.
     */
    private val SINGLE_WATCHER = object : DateWatcher() {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val isValid = commonCheck(binding.singleDate, s, before)
            binding.singleDateLayout.error = if (isValid) {
                null
            } else {
                getString(R.string.date_editor_enter_valid_date)
            }
        }
    }

    /**
     * Watcher для начала диапозода дат.
     */
    private val START_RANGE_WATCHER = object : DateWatcher() {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val isValid = commonCheck(binding.dateStart, s, before)
            binding.dateStartLayout.error = if (isValid) {
                null
            } else {
                getString(R.string.date_editor_enter_valid_date)
            }
        }
    }

    /**
     * Watcher для конца диапозона дат.
     */
    private val END_RANGE_WATCHER = object : DateWatcher() {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val isValid = commonCheck(binding.dateEnd, s, before)
            binding.dateEndLayout.error = if (isValid) {
                null
            } else {
                getString(R.string.date_editor_enter_valid_date)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDateEditorBinding.inflate(layoutInflater)
        statefulMode = StatefulLayout2.Builder(binding.container)
            .init(Mode.SINGLE.number, binding.singleDateLayout)
            .addView(Mode.RANGE.number, binding.rangeDateLayout)
            .create()

        setContentView(binding.root)

        date = intent.getParcelableExtra(EXTRA_DATE)!!
        dateItem = intent.getParcelableExtra(EXTRA_DATE_ITEM)
        request = intent.getSerializableExtra(EXTRA_REQUEST) as Request

        initFields()

        if (savedInstanceState != null) {
            switchMode(savedInstanceState.getSerializable(DATE_MODE) as Mode)
        } else {
            dateItem?.let {
                bind(it)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_date_editor, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(DATE_MODE, mode)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            // сохранение
            R.id.apply_date -> {
                try {
                    val newDate: DateItem = when (mode) {
                        Mode.SINGLE -> {
                            DateSingle(
                                binding.singleDate.text.toString(),
                                DATE_PATTERN
                            )
                        }
                        Mode.RANGE -> {
                            DateRange(
                                binding.dateStart.text.toString(),
                                binding.dateEnd.text.toString(),
                                currentFrequency(),
                                DATE_PATTERN
                            )
                        }
                    }
                    date.possibleChange(dateItem, newDate)

                    val intent = Intent()
                    intent.putExtra(EXTRA_DATE_OLD, dateItem)
                    intent.putExtra(EXTRA_DATE_NEW, newDate)
                    if (dateItem == null) {
                        setResult(RESULT_DATE_NEW, intent)
                    } else {
                        setResult(RESULT_DATE_EDIT, intent)
                    }
                    onBackPressed()

                    return true

                } catch (e: DateException) {
                    val message = when (e) {
                        is DateFrequencyException -> {
                            getString(R.string.date_editor_invalid_frequency)
                        }
                        is DateDayOfWeekException -> {
                            getString(R.string.date_editor_invalid_day_of_week)
                        }
                        is DateParseException -> {
                            getString(R.string.date_editor_invalid_date, e.parseDate)
                        }
                        is DateIntersectException -> {
                            getString(R.string.date_editor_impossible_added_date) +
                                "\n${e.first.toString(this)} <-> ${e.second.toString(this)}"
                        }
                        else -> {
                            "Unknown type: $e"
                        }
                    }

                    MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.error)
                        .setMessage(message)
                        .setOkButton()
                        .show()

                    return true

                } catch (e: Exception) {
                    Log.e(TAG, "onOptionsItemSelected: Unknown error. $e")
                    throw RuntimeException("Unknown error", e)
                }
            }
            // удаление
            R.id.remove_date -> {
                // даты и так нет
                if (dateItem == null) {
                    onBackPressed()
                    return true
                }

                val intent = Intent()
                intent.putExtra(EXTRA_DATE_OLD, dateItem)
                setResult(RESULT_DATE_REMOVE, intent)
                onBackPressed()

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * Инициализцая полей.
     */
    private fun initFields() {
        initAutoComplete(binding.dateMode, resourcesArray(R.array.date_type_list))
        initAutoComplete(binding.dateFrequency, resourcesArray(R.array.frequency_list))

        binding.dateMode.addTextChangedListener {
            switchMode(when (binding.dateMode.currentPosition()) {
                0 -> Mode.SINGLE
                1 -> Mode.RANGE
                else -> throw RuntimeException("Unknown date mode: ${it.toString()}")
            })
        }

        binding.singleDate.addTextChangedListener(SINGLE_WATCHER)
        binding.singleDateLayout.setEndIconOnClickListener {
            onCalendarClicked(R.id.single_date_layout)
        }
        // TODO("Добавить проверку периодичности в дате")
        binding.dateStart.addTextChangedListener(START_RANGE_WATCHER)
        binding.dateStartLayout.setEndIconOnClickListener {
            onCalendarClicked(R.id.date_start_layout)
        }

        binding.dateEnd.addTextChangedListener(END_RANGE_WATCHER)
        binding.dateEndLayout.setEndIconOnClickListener {
            onCalendarClicked(R.id.date_end_layout)
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
     * Возвращает соответсвующий список строк.
     * @param id ID ресурса.
     */
    private fun resourcesArray(@ArrayRes id: Int): List<String> {
        return resources.getStringArray(id).asList()
    }

    /**
     * Присоединяет данные к View.
     */
    private fun bind(item: DateItem) {
        if (item is DateSingle) {
            binding.singleDate.setText(item.date.toString(DATE_PATTERN))
            switchMode(Mode.SINGLE)
        }
        if (item is DateRange) {
            binding.dateStart.setText(item.start.toString(DATE_PATTERN))
            binding.dateEnd.setText(item.end.toString(DATE_PATTERN))
            setCurrentFrequency(item.frequency())
            switchMode(Mode.RANGE)
        }
    }

    /**
     * Смена режима (типа) даты.
     */
    private fun switchMode(newMode: Mode) {
        mode = newMode
        statefulMode.setState(mode.number)
    }

    /**
     * Показывает Picker для выбора даты.
     * @param text текущая дата.
     * @param listener callback для результата.
     */
    private fun showDataPicker(
        text: String,
        listener: (view: DatePicker, year: Int, month: Int, dayOfMonth: Int) -> Unit
    ) {
        var showDate = LocalDate.now()
        try {
            val formatter = DateTimeFormat.forPattern(DATE_PATTERN)
            showDate = formatter.parseLocalDate(text)

        } catch (ignored: IllegalArgumentException) {
        } catch (ignored: UnsupportedOperationException) {
        }

        DatePickerDialog(
            this, listener, showDate.year, showDate.monthOfYear - 1, showDate.dayOfMonth
        ).show()
    }

    /**
     * Вызывается, если был нажата кнопка календаря
     */
    private fun onCalendarClicked(@IdRes id: Int) {
        when (id) {
            // одинственная дата
            R.id.single_date_layout -> {
                showDataPicker(
                    binding.singleDate.text.toString()
                ) { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                    val singleDate = LocalDate(year, month + 1, dayOfMonth)
                    binding.singleDate.setText(singleDate.toString(DATE_PATTERN))
                }
            }
            // дата начала
            R.id.date_start_layout -> {
                showDataPicker(
                    binding.dateStart.text.toString()
                ) { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                    val startDate = LocalDate(year, month + 1, dayOfMonth)
                    binding.dateStart.setText(startDate.toString(DATE_PATTERN))
                }
            }
            // дата концв
            R.id.date_end_layout -> {
                showDataPicker(
                    binding.dateEnd.text.toString()
                ) { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                    val startDate = LocalDate(year, month + 1, dayOfMonth)
                    binding.dateEnd.setText(startDate.toString(DATE_PATTERN))
                }
            }
        }
    }

    /**
     * Устаавливает периодичность.
     */
    private fun setCurrentFrequency(frequency: Frequency) {
        val pos = listOf(
            Frequency.EVERY, Frequency.THROUGHOUT
        ).indexOf(frequency)
        binding.dateFrequency.setCurrentPosition(pos)
    }

    /**
     * Возвращает текущую периодичность.
     */
    private fun currentFrequency() : Frequency {
        return listOf(
            Frequency.EVERY, Frequency.THROUGHOUT
        )[binding.dateFrequency.currentPosition()]
    }

    /**
     * Перечисление с запросами.
     */
    private enum class Request {
        NEW_DATE,
        EDIT_DATE
    }

    /**
     * Перечисление с режимами.
     */
    private enum class Mode(val number: Int) {
        SINGLE(0),
        RANGE(1)
    }

    /**
     * Watcher для ввода даты.
     */
    private abstract class DateWatcher : TextWatcher {

        override fun afterTextChanged(s: Editable?) {}

        /**
         * Общая проверка даты на правильность.
         */
        fun commonCheck(dateView: TextInputEditText, text: CharSequence?, before: Int): Boolean {
            var textDate: String = if (text.isNullOrEmpty()) {
                return true
            } else {
                text.toString()
            }

            var isValid = true
            try {
                when {
                    textDate.length == 2 && before == 0 -> {
                        if (textDate.toInt() !in 1..31) {
                            isValid = false
                        } else {
                            textDate += "."
                            dateView.setText(textDate)
                            dateView.setSelection(textDate.length)
                        }
                    }
                    textDate.length == 5 && before == 0 -> {
                        val month = textDate.substring(3)
                        if (month.toInt() !in 1..12) {
                            isValid = false
                        } else {
                            val year = DateTime.now().year
                            textDate += ".$year"

                            dateView.setText(textDate)
                            dateView.clearFocus()
                        }
                    }
                    textDate.length != 10 -> {
                        isValid = false
                    }
                }
            } catch (e: NumberFormatException) {
                isValid = false
            }

            if (isValid && textDate.length == 10) {
                try {
                    val formatter = DateTimeFormat.forPattern(DATE_PATTERN)
                    formatter.parseLocalDate(textDate)
                } catch (e: UnsupportedOperationException) {
                    isValid = false
                }
            }
            return isValid
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    }

    companion object {

        private val TAG = DateEditorActivity2::getComponentName.name

        const val EXTRA_DATE_NEW = "extra_new_date"
        const val EXTRA_DATE_OLD = "extra_old_date"
        const val EXTRA_DATE = "extra_date"

        const val RESULT_DATE_REMOVE = 1
        const val RESULT_DATE_NEW = 2
        const val RESULT_DATE_EDIT = 3

        private const val EXTRA_DATE_ITEM = "extra_date_item"
        private const val EXTRA_REQUEST = "extra_request"

        private const val DATE_PATTERN = "dd.MM.yyyy"
        private const val DATE_MODE = "date_mode"

        /**
         * Intent на создание новой даты.
         */
        fun newDateIntent(context: Context, date: Date): Intent {
            val intent = Intent(context, DateEditorActivity2::class.java)
            intent.putExtra(EXTRA_DATE, date)
            intent.putExtra(EXTRA_REQUEST, Request.NEW_DATE)
            return intent
        }

        /**
         * Intent на редактиование даты.
         */
        fun editDateIntent(context: Context, date: Date, dateItem: DateItem): Intent {
            val intent = Intent(context, DateEditorActivity2::class.java)
            intent.putExtra(EXTRA_DATE, date)
            intent.putExtra(EXTRA_DATE_ITEM, dateItem)
            intent.putExtra(EXTRA_REQUEST, Request.EDIT_DATE)
            return intent
        }
    }
}