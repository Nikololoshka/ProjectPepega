package com.vereshchagin.nikolay.stankinschedule.ui.settings.view.widget.paging

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

/**
 * Адаптер для RecyclerView для отображения виджетов.
 */
class SettingsWidgetAdapter(
    private val clickListener: (widgetId: Int) -> Unit,
) : ListAdapter<SettingsWidgetItem, SettingsWidgetHolder>(WIDGET_ITEM_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsWidgetHolder {
        return SettingsWidgetHolder.create(parent, clickListener)
    }

    override fun onBindViewHolder(holder: SettingsWidgetHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        /**
         * Компаратор для сравнения объектов для отображения списка виджетов с расписанием.
         */
        private val WIDGET_ITEM_COMPARATOR = object : DiffUtil.ItemCallback<SettingsWidgetItem>() {
            override fun areItemsTheSame(
                oldItem: SettingsWidgetItem, newItem: SettingsWidgetItem,
            ): Boolean = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: SettingsWidgetItem, newItem: SettingsWidgetItem,
            ): Boolean = oldItem == newItem
        }
    }
}