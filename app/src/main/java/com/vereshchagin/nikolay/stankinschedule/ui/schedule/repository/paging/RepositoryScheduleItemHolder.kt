package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vereshchagin.nikolay.stankinschedule.databinding.ItemRepositoryScheduleBinding

/**
 * Holder расписания
 */
class RepositoryScheduleItemHolder(
    private val binding: ItemRepositoryScheduleBinding,
    private val clickListener: (scheduleName: String, position: Int) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.repositorySchedule.setOnClickListener {
            binding.scheduleName?.let {
                clickListener.invoke(it, bindingAdapterPosition)
            }
        }
    }

    /**
     * Устанавливает данные в holder.
     */
    fun bind(scheduleName: String) {
        binding.scheduleName = scheduleName
    }

    companion object {
        /**
         * Создает Holder для адаптера.
         */
        @JvmStatic
        fun create(
            parent: ViewGroup,
            callback: (scheduleName: String, position: Int) -> Unit
        ): RepositoryScheduleItemHolder {
            return RepositoryScheduleItemHolder(
                ItemRepositoryScheduleBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ),
                callback
            )
        }
    }
}