package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.paging

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.RepositoryCategoryItem

/**
 * Адаптер для расписаний в категории.
 */
class RepositoryScheduleItemAdapter : RecyclerView.Adapter<RepositoryScheduleItemHolder>() {

    /**
     * Список с расписаниями.
     */
    private var schedules: List<String> = listOf()
    private var category: String = ""

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RepositoryScheduleItemHolder {
        return RepositoryScheduleItemHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RepositoryScheduleItemHolder, position: Int) {
        holder.bind(schedules[position], category)
    }

    override fun getItemCount() = schedules.size

    /**
     * Устанавливает данные в адаптер.
     */
    fun submitList(item: RepositoryCategoryItem)  {
        schedules = item.schedules
        category = item.categoryName
        notifyDataSetChanged()
    }
}