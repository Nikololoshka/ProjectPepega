package com.vereshchagin.nikolay.stankinschedule.ui.modulejournal.view.paging

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.vereshchagin.nikolay.stankinschedule.model.modulejournal.SemesterMarks
import com.vereshchagin.nikolay.stankinschedule.ui.modulejournal.view.paging.holder.SemesterMarksViewHolder

/**
 * Адаптер для отображения списка семестров.
 */
class SemesterMarksAdapter :
    PagingDataAdapter<SemesterMarks, SemesterMarksViewHolder>(SEMESTERS_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SemesterMarksViewHolder {
        return SemesterMarksViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: SemesterMarksViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        /**
         * Компаратор для сравнения семестров.
         */
        @JvmStatic
        val SEMESTERS_COMPARATOR = object : DiffUtil.ItemCallback<SemesterMarks>() {

            override fun areItemsTheSame(oldItem: SemesterMarks, newItem: SemesterMarks) =
                oldItem.rating == newItem.rating

            override fun areContentsTheSame(oldItem: SemesterMarks, newItem: SemesterMarks) =
                oldItem == newItem
        }
    }
}