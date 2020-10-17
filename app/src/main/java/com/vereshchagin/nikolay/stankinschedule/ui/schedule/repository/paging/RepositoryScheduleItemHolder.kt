package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vereshchagin.nikolay.stankinschedule.databinding.ItemRepositoryScheduleBinding
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.worker.ScheduleDownloadWorker

/**
 * Holder расписания
 */
class RepositoryScheduleItemHolder(
    private val binding: ItemRepositoryScheduleBinding
) : RecyclerView.ViewHolder(binding.root) {

    private var category: String? = null

    init {
        binding.repositorySchedule.setOnClickListener { view ->
            category?.let { categoryName ->
                ScheduleDownloadWorker.startWorker(
                    view.context,
                    categoryName,
                    binding.scheduleName!!
                )
            }
        }
    }

    /**
     * Устанавливает данные в holder.
     */
    fun bind(scheduleName: String, category: String) {
        binding.scheduleName = scheduleName
        this.category = category
    }

    companion object {
        /**
         * Создает Holder для адаптера.
         */
        @JvmStatic
        fun create(parent: ViewGroup): RepositoryScheduleItemHolder {
            return RepositoryScheduleItemHolder(
                ItemRepositoryScheduleBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }
    }
}