package com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.view.paging

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.view.paging.viewholder.ScheduleDisciplineViewHolder

class ScheduleEditorAdapter
    : PagingDataAdapter<String, ScheduleDisciplineViewHolder>(DISCIPLINE_COMPARATOR) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ScheduleDisciplineViewHolder {
        return ScheduleDisciplineViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ScheduleDisciplineViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DISCIPLINE_COMPARATOR = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }
}