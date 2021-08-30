package com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.date

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.DatePicker
import androidx.annotation.ArrayRes
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.ActivityDateEditorBinding
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.*
import com.vereshchagin.nikolay.stankinschedule.utils.DateTimeUtils
import com.vereshchagin.nikolay.stankinschedule.utils.ScheduleUtils
import com.vereshchagin.nikolay.stankinschedule.utils.StatefulLayout2
import com.vereshchagin.nikolay.stankinschedule.utils.extensions.currentPosition
import com.vereshchagin.nikolay.stankinschedule.utils.extensions.setCurrentPosition
import com.vereshchagin.nikolay.stankinschedule.utils.extensions.setOkButton
import com.vereshchagin.nikolay.stankinschedule.view.DropDownAdapter
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat

/**
 * Активность редактора дат.
 */
class DateEditorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDateEditorBinding
    private lateinit var statefulMode: StatefulLayout2

    /**
     * Все даты пары.
     */
    private lateinit var date: Date

    /**
     * Текущий запрос в редактор (создание / редактирование).
     */
    private lateinit var request: Request

    /**
     * Редактируемая дата.
     */
    private var dateItem: DateItem? = null

    /**
     * Режим редактирования (одиночный / диапазон).
     */
    private var mode = Mode.SINGLE


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDateEditorBinding.inflate(layoutInflater)
        statefulMode = StatefulLayout2.Builder(binding.container)
            .init(Mode.SINGLE.number, binding.singleDateLayout)
            .addView(Mode.RANGE.number, binding.rangeDateLayout)
            .create()

        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // получение данных
        date = intent.getParcelableExtra(EXTRA_DATE)!!
        dateItem = intent.getParcelableExtra(EXTRA_DATE_ITEM)
        request = intent.getSerializableExtra(EXTRA_REQUEST) as Request

        initFields()

        // было сохранено состояние
        if (savedInstanceState != null) {
            mode = (savedInstanceState.getSerializable(DATE_MODE) as Mode)
            binding.dateMode.setCurrentPosition(mode.number)

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
        when (item.itemId) {
            // сохранение даты
            R.id.apply_date -> {
                onApplyDateClicked()
                return true
            }
            // удаление даты
            R.id.remove_date -> {
                omRemoveDateClicked()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * Инициализация полей.
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

        binding.singleDate.doOnTextChanged(this::onSingleDateChanged)
        binding.singleDateLayout.setEndIconOnClickListener {
            onCalendarClicked(R.id.single_date_layout)
        }

        binding.dateStart.doOnTextChanged(this::onStartDateRangeChanged)
        binding.dateStartLayout.setEndIconOnClickListener {
            onCalendarClicked(R.id.date_start_layout)
        }

        binding.dateEnd.doOnTextChanged(this::onEndDateRangeChanged)
        binding.dateEndLayout.setEndIconOnClickListener {
            onCalendarClicked(R.id.date_end_layout)
        }
    }

    /**
     * Вызывается для сохранения текущих результатов редактирования даты.
     */
    private fun onApplyDateClicked() {
        try {
            // получает финальную дату
            val newDate: DateItem = when (mode) {
                Mode.SINGLE -> {
                    DateSingle(
                        binding.singleDate.text.toString(),
                        DateTimeUtils.PRETTY_DATE_PATTERN
                    )
                }
                Mode.RANGE -> {
                    DateRange(
                        binding.dateStart.text.toString(),
                        binding.dateEnd.text.toString(),
                        currentFrequency(),
                        DateTimeUtils.PRETTY_DATE_PATTERN
                    )
                }
            }

            // можно ли иметь (изменить) эту дату
            date.possibleChange(dateItem, newDate)

            // если все хорошо, то возвращает результат
            val intent = Intent()
            intent.putExtra(EXTRA_DATE_OLD, dateItem)
            intent.putExtra(EXTRA_DATE_NEW, newDate)
            if (dateItem == null) {
                setResult(RESULT_DATE_NEW, intent)
            } else {
                setResult(RESULT_DATE_EDIT, intent)
            }
            onBackPressed()

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
                            "${
                                ScheduleUtils.dateToString(e.first, this)
                            } <-> ${
                                ScheduleUtils.dateToString(e.second, this)
                            }"
                }
            }

            MaterialAlertDialogBuilder(this, R.style.AppAlertDialog)
                .setTitle(R.string.error)
                .setMessage(message)
                .setOkButton()
                .show()

        } catch (e: Exception) {
            Log.e(TAG, "onOptionsItemSelected: Unknown error. $e")
            throw RuntimeException("Unknown error", e)
        }
    }

    /**
     * Вызывается для удаления текущей даты.
     */
    private fun omRemoveDateClicked() {
        // даты и так нет
        if (dateItem == null) {
            onBackPressed()
        }

        val intent = Intent()
        intent.putExtra(EXTRA_DATE_OLD, dateItem)
        setResult(RESULT_DATE_REMOVE, intent)
        onBackPressed()
    }

    /**
     * Валидация поля с вводом одной даты.
     */
    @Suppress("UNUSED_PARAMETER")
    private fun onSingleDateChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
        val isValid = commonDateCheck(binding.singleDate, text, before)
        binding.singleDateLayout.error = if (isValid) {
            null
        } else {
            getString(R.string.date_editor_enter_valid_date)
        }
    }

    /**
     * Валидация ввода диапазона начала даты.
     */
    @Suppress("UNUSED_PARAMETER")
    private fun onStartDateRangeChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
        val isValid = commonDateCheck(binding.dateStart, text, before)
        binding.dateStartLayout.error = if (isValid) {
            null
        } else {
            getString(R.string.date_editor_enter_valid_date)
        }
    }

    /**
     * Валидация ввода конца диапазона даты.
     */
    @Suppress("UNUSED_PARAMETER")
    private fun onEndDateRangeChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
        val isValid = commonDateCheck(binding.dateEnd, text, before)
        binding.dateEndLayout.error = if (isValid) {
            null
        } else {
            getString(R.string.date_editor_enter_valid_date)
        }
    }

    /**
     * Общая проверка даты на правильность.
     */
    private fun commonDateCheck(
        dateView: TextInputEditText,
        text: CharSequence?,
        before: Int,
    ): Boolean {

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
                val formatter = DateTimeFormat.forPattern(DateTimeUtils.PRETTY_DATE_PATTERN)
                formatter.parseLocalDate(textDate)

            } catch (e: UnsupportedOperationException) {
                isValid = false
            } catch (e: IllegalArgumentException) {
                isValid = false
            }
        }
        return isValid
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
     * Возвращает соответствующий список строк.
     * @param id ID ресурса.
     */
    private fun resourcesArray(@ArrayRes id: Int): List<String> {
        return resources.getStringArray(id).asList()
    }

    /**
     * Отображает текущую редактируемую дату в View.
     */
    private fun bind(item: DateItem) {
        // одиночная дата
        if (item is DateSingle) {
            binding.singleDate.setText(item.date.toString(DateTimeUtils.PRETTY_DATE_PATTERN))
            switchMode(Mode.SINGLE)
        }
        // диапазон дат
        if (item is DateRange) {
            binding.dateStart.setText(item.start.toString(DateTimeUtils.PRETTY_DATE_PATTERN))
            binding.dateEnd.setText(item.end.toString(DateTimeUtils.PRETTY_DATE_PATTERN))
            setCurrentFrequency(item.frequency())
            switchMode(Mode.RANGE)
        }
        binding.dateMode.setCurrentPosition(mode.number)
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
    private fun showDatePicker(
        text: String,
        listener: (view: DatePicker, year: Int, month: Int, dayOfMonth: Int) -> Unit,
    ) {
        var showDate = LocalDate.now()
        try {
            val formatter = DateTimeFormat.forPattern(DateTimeUtils.PRETTY_DATE_PATTERN)
            showDate = formatter.parseLocalDate(text)

        } catch (ignored: IllegalArgumentException) {

        } catch (ignored: UnsupportedOperationException) {

        }

        DatePickerDialog(
            this, listener, showDate.year, showDate.monthOfYear - 1, showDate.dayOfMonth
        ).show()
    }

    /**
     * Создает Date Picker для установления даты в необходимое поле.
     * @param editText поле, в которое устанавливается дата.
     */
    private fun createDatePicker(editText: TextInputEditText) {
        showDatePicker(
            editText.text.toString()
        ) { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            val singleDate = LocalDate(year, month + 1, dayOfMonth)
            editText.setText(singleDate.toString(DateTimeUtils.PRETTY_DATE_PATTERN))
        }
    }

    /**
     * Вызывается, если был нажата кнопка календаря
     */
    private fun onCalendarClicked(@IdRes id: Int) {
        when (id) {
            // единственная дата
            R.id.single_date_layout -> {
                createDatePicker(binding.singleDate)
            }
            // дата начала
            R.id.date_start_layout -> {
                createDatePicker(binding.dateStart)
            }
            // дата конца
            R.id.date_end_layout -> {
                createDatePicker(binding.dateEnd)
            }
        }
    }

    /**
     * Устанавливает периодичность.
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
    private fun currentFrequency(): Frequency {
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

    companion object {

        private val TAG = DateEditorActivity::getComponentName.name

        const val EXTRA_DATE_NEW = "extra_new_date"
        const val EXTRA_DATE_OLD = "extra_old_date"
        const val EXTRA_DATE = "extra_date"

        const val RESULT_DATE_REMOVE = 1
        const val RESULT_DATE_NEW = 2
        const val RESULT_DATE_EDIT = 3

        private const val EXTRA_DATE_ITEM = "extra_date_item"
        private const val EXTRA_REQUEST = "extra_request"

        private const val DATE_MODE = "date_mode"

        /**
         * Intent на создание новой даты.
         */
        fun newDateIntent(context: Context, date: Date): Intent {
            val intent = Intent(context, DateEditorActivity::class.java)
            intent.putExtra(EXTRA_DATE, date)
            intent.putExtra(EXTRA_REQUEST, Request.NEW_DATE)
            return intent
        }

        /**
         * Intent на редактирование даты.
         */
        fun editDateIntent(context: Context, date: Date, dateItem: DateItem): Intent {
            val intent = Intent(context, DateEditorActivity::class.java)
            intent.putExtra(EXTRA_DATE, date)
            intent.putExtra(EXTRA_DATE_ITEM, dateItem)
            intent.putExtra(EXTRA_REQUEST, Request.EDIT_DATE)
            return intent
        }
    }
}