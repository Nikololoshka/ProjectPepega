package com.vereshchagin.nikolay.stankinschedule.ui.schedule.view.paging

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.PairItem

/**
 * Адаптер для просмотра расписания.
 */
class ScheduleViewAdapter(
    private val pairCallback: (pair: PairItem) -> Unit,
) : PagingDataAdapter<ScheduleViewDay, ScheduleViewDayHolder>(SCHEDULE_DAY_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewDayHolder {
        return ScheduleViewDayHolder.create(parent, pairCallback)
    }

    override fun onBindViewHolder(holder: ScheduleViewDayHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * Возвращает элемент в адаптере по позиции.
     * Если позиция не верная, то null.
     */
    fun item(position: Int): ScheduleViewDay? {
        return if (itemCount <= position) null else getItem(position)
    }

    companion object {
        /**
         * Компаратор для сравнения дней в просмотре расписания.
         */
        private val SCHEDULE_DAY_COMPARATOR = object : DiffUtil.ItemCallback<ScheduleViewDay>() {

            override fun areItemsTheSame(oldItem: ScheduleViewDay, newItem: ScheduleViewDay) =
                oldItem.day == newItem.day

            override fun areContentsTheSame(oldItem: ScheduleViewDay, newItem: ScheduleViewDay) =
                oldItem == newItem
        }
    }
}