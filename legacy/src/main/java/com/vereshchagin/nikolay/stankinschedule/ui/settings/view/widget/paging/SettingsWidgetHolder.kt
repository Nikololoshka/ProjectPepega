package com.vereshchagin.nikolay.stankinschedule.ui.settings.view.widget.paging

import android.appwidget.AppWidgetManager
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vereshchagin.nikolay.stankinschedule.databinding.ItemWidgetSettingsBinding

/**
 * Holder элемента RecyclerView с виджетами.
 */
class SettingsWidgetHolder(
    private val binding: ItemWidgetSettingsBinding,
    private val clickListener: (widgetId: Int) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    /**
     * Текущий ID виджета.
     */
    private var widgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    init {
        binding.widgetItem.setOnClickListener {
            clickListener(widgetId)
        }
    }

    /**
     * Обновляет данные в holder.
     * @param item данные виджета с расписанием.
     */
    fun bind(item: SettingsWidgetItem) {
        binding.widgetScheduleName.text = item.scheduleName
        this.widgetId = item.widgetId
    }

    companion object {
        /**
         * Создает SettingsWidgetHolder виджета расписания для отображения в списке.
         */
        fun create(
            parent: ViewGroup,
            clickListener: (widgetId: Int) -> Unit,
        ): SettingsWidgetHolder {
            return SettingsWidgetHolder(
                ItemWidgetSettingsBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ),
                clickListener
            )
        }
    }
}