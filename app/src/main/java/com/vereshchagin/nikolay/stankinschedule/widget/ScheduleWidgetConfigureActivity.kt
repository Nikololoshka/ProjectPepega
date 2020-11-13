package com.vereshchagin.nikolay.stankinschedule.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.WidgetScheduleConfigureBinding
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Subgroup
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepository
import com.vereshchagin.nikolay.stankinschedule.utils.DropDownAdapter
import com.vereshchagin.nikolay.stankinschedule.utils.currentPosition
import com.vereshchagin.nikolay.stankinschedule.utils.setCurrentPosition
import com.vereshchagin.nikolay.stankinschedule.widget.ScheduleWidget.Companion.updateAppWidget
import java.util.*

/**
 * Конфигурационное активити для виджета с расписанием.
 */
class ScheduleWidgetConfigureActivity : AppCompatActivity(), View.OnClickListener {
    /**
     * ID виджета с расписанием.
     */
    private var scheduleAppWidgetId: Int = AppWidgetManager.INVALID_APPWIDGET_ID

    private lateinit var binding: WidgetScheduleConfigureBinding

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // устанавливаем RESULT_CANCELED, т.к. пользователь может
        // нажать кнопку назад и нужно отменить создание.
        setResult(RESULT_CANCELED)

        /// извлекаем ID конфигурируемого виджета
        intent.extras?.let {
            scheduleAppWidgetId = it.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }

        // если не корректное ID виджета
        if (scheduleAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        // настройка layout'а
        binding = WidgetScheduleConfigureBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val schedules = ScheduleRepository().schedules(this)
        initAutoComplete(binding.widgetScheduleSelector, schedules)
        initAutoComplete(
            binding.widgetSubgroupSelector, resources.getStringArray(R.array.subgroup_list).asList()
        )

        binding.widgetScheduleAdd.setOnClickListener(this)

        val text = intent.getStringExtra(CONFIGURE_BUTTON_TEXT_EXTRA)
        if (text != null) {
            binding.widgetScheduleAdd.text = text
        }

        // если нет расписаний, то отключить UI
        if (schedules.isEmpty()) {
            binding.widgetScheduleAdd.isEnabled = false
            binding.widgetScheduleSelector.isEnabled = false
            binding.widgetSubgroupSelector.isEnabled = false
        }

        // загрузка данных
        val data = loadPref(this, scheduleAppWidgetId)
        val position = schedules.indexOf(data.scheduleName)
        if (position != -1) {
            binding.widgetScheduleSelector.setCurrentPosition(position)
        }
        setSubgroupSpinner(data.subgroup)
        binding.widgetSubgroupDisplay.isChecked = data.display
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onClick(v: View) {
        // сохраняем текст
        val schedule = binding.widgetScheduleSelector.text.toString()
        savePref(
            this, scheduleAppWidgetId,
            WidgetData(schedule, currentSubgroup(), binding.widgetSubgroupDisplay.isChecked)
        )

        // обновляем виджет
        val appWidgetManager = AppWidgetManager.getInstance(this)
        updateAppWidget(this, appWidgetManager, scheduleAppWidgetId)

        // завершаем конфигурирование виджета
        setResult(RESULT_OK, Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, scheduleAppWidgetId)
        })
        finish()
    }

    /**
     * Инициализация полей с DropDown меню.
     */
    private fun initAutoComplete(
        autoComplete: MaterialAutoCompleteTextView,
        objects: List<String>
    ) {
        val adapter = DropDownAdapter(this, objects)
        autoComplete.setAdapter(adapter)

        if (autoComplete.text.isNullOrEmpty()) {
            autoComplete.setText(adapter.getItem(0), false)
        }
    }

    /**
     * @return выбранная подгруппа.
     */
    private fun currentSubgroup(): Subgroup {
        return listOf(
            Subgroup.COMMON, Subgroup.A, Subgroup.B
        )[binding.widgetSubgroupSelector.currentPosition()]
    }

    /**
     * Устанавливает поле с подгруппой пары.
     */
    private fun setSubgroupSpinner(subgroup: Subgroup) {
        val pos = listOf(
            Subgroup.COMMON, Subgroup.A, Subgroup.B
        ).indexOf(subgroup)

        binding.widgetSubgroupSelector.setCurrentPosition(pos)
    }

    /**
     * Информация с расписанием для виджета.
     */
    class WidgetData(
        val scheduleName: String?,
        val subgroup: Subgroup,
        val display: Boolean
    )

    companion object {
        const val CONFIGURE_BUTTON_TEXT_EXTRA = "configure_button_text"

        private const val SCHEDULE_WIDGET_PREFERENCE = "schedule_widget_preference"
        private const val SCHEDULE_WIDGET = "schedule_app_widget_"
        private const val NAME_SUFFIX = "_name"
        private const val SUBGROUP_SUFFIX = "_subgroup"
        private const val DISPLAY_SUFFIX = "_display"

        /**
         * Сохраняет данные виджета в SharedPreferences.
         * @param context контекст.
         * @param appWidgetId ID виджета.
         * @param data данные виджета расписание.
         */
        fun savePref(context: Context, appWidgetId: Int, data: WidgetData) {
            val preferences = context.getSharedPreferences(SCHEDULE_WIDGET_PREFERENCE, MODE_PRIVATE)
            preferences.edit()
                .putString(SCHEDULE_WIDGET + appWidgetId + NAME_SUFFIX, data.scheduleName)
                .putString(SCHEDULE_WIDGET + appWidgetId + SUBGROUP_SUFFIX, data.subgroup.tag)
                .putBoolean(SCHEDULE_WIDGET + appWidgetId + DISPLAY_SUFFIX, data.display)
                .apply()
        }

        /**
         * Загружает данные виджета из SharedPreferences.
         * @param context контекст.
         * @param appWidgetId ID виджета.
         * @return данные виджета расписания, отображаемого на виджете.
         */
        @JvmStatic
        fun loadPref(context: Context, appWidgetId: Int): WidgetData {
            val preferences = context.getSharedPreferences(SCHEDULE_WIDGET_PREFERENCE, MODE_PRIVATE)

            val scheduleName = preferences.getString(
                SCHEDULE_WIDGET + appWidgetId + NAME_SUFFIX, null
            )
            val subgroup = Subgroup.of(
                preferences.getString(
                    SCHEDULE_WIDGET + appWidgetId + SUBGROUP_SUFFIX, Subgroup.COMMON.tag
                )!!.capitalize(Locale.ROOT)
            )
            val display = preferences.getBoolean(
                SCHEDULE_WIDGET + appWidgetId + DISPLAY_SUFFIX, true
            )

            return WidgetData(scheduleName, subgroup, display)
        }

        /**
         * Удаляет данные, связанные с виджетом, из SharedPreferences.
         * @param context контекст.
         * @param appWidgetId ID виджета.
         */
        fun deletePref(context: Context, appWidgetId: Int) {
            val preferences = context.getSharedPreferences(SCHEDULE_WIDGET_PREFERENCE, MODE_PRIVATE)
            preferences.edit()
                .remove(SCHEDULE_WIDGET + appWidgetId + NAME_SUFFIX)
                .remove(SCHEDULE_WIDGET + appWidgetId + SUBGROUP_SUFFIX)
                .remove(SCHEDULE_WIDGET + appWidgetId + DISPLAY_SUFFIX)
                .apply()
        }
    }
}