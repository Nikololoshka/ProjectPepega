package com.vereshchagin.nikolay.stankinschedule.ui.modulejournal.view.paging.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vereshchagin.nikolay.stankinschedule.databinding.ItemModuleJournalSemesterBinding
import com.vereshchagin.nikolay.stankinschedule.model.modulejournal.SemesterMarks
import com.vereshchagin.nikolay.stankinschedule.utils.extensions.setVisibility

/**
 * Элемент для отображения оценок в семестре.
 */
class SemesterMarksViewHolder(
    private val binding: ItemModuleJournalSemesterBinding,
) : RecyclerView.ViewHolder(binding.root) {

    /**
     * Связывает данные с элементом.
     */
    fun bind(marks: SemesterMarks?) {
        if (marks != null) {
            binding.marksTable.setSemesterMarks(marks)
        }
        binding.marksTable.setVisibility(marks != null)
        binding.semesterLoading.setVisibility(marks == null)
    }

    companion object {
        /**
         * Возвращает holder семестра с оценками.
         */
        @JvmStatic
        fun create(parent: ViewGroup): SemesterMarksViewHolder {
            return SemesterMarksViewHolder(
                ItemModuleJournalSemesterBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }
    }
}