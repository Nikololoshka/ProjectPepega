package com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.view.paging

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.vereshchagin.nikolay.stankinschedule.model.schedule.editor.ScheduleEditorDiscipline
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.view.paging.viewholder.OnPairListener
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.view.paging.viewholder.ScheduleDisciplineViewHolder

class ScheduleEditorAdapter(
    private val callback: OnPairListener,
) : PagingDataAdapter<ScheduleEditorDiscipline, ScheduleDisciplineViewHolder>(DISCIPLINE_COMPARATOR) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ScheduleDisciplineViewHolder {
        return ScheduleDisciplineViewHolder.create(callback, parent)
    }

    override fun onBindViewHolder(holder: ScheduleDisciplineViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DISCIPLINE_COMPARATOR =
            object : DiffUtil.ItemCallback<ScheduleEditorDiscipline>() {
                override fun areItemsTheSame(
                    oldItem: ScheduleEditorDiscipline,
                    newItem: ScheduleEditorDiscipline,
                ): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(
                    oldItem: ScheduleEditorDiscipline,
                    newItem: ScheduleEditorDiscipline,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}