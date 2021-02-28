package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.paging

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.RepositoryCategoryItem

/**
 * Адаптер для расписаний в категории.
 */
class RepositoryScheduleItemAdapter(
    private val clickListener: (scheduleName: String, position: Int) -> Unit,
) : RecyclerView.Adapter<RepositoryScheduleItemHolder>() {

    /**
     * Список с расписаниями.
     */
    private var schedules: List<String> = listOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RepositoryScheduleItemHolder {
        return RepositoryScheduleItemHolder.create(parent, clickListener)
    }

    override fun onBindViewHolder(holder: RepositoryScheduleItemHolder, position: Int) {
        holder.bind(schedules[position])
    }

    override fun getItemCount() = schedules.size

    /**
     * Устанавливает данные в адаптер.
     */
    fun submitList(item: RepositoryCategoryItem)  {
        schedules = item.schedules
        notifyDataSetChanged()
    }
}