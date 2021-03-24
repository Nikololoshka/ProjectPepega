package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.paging.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vereshchagin.nikolay.stankinschedule.databinding.ItemRepositoryItemBinding
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.v1.RepositoryItem

/**
 * Элемент для отображения объекта репозитория.
 */
class RepositoryItemViewHolder<T : RepositoryItem>(
    private val binding: ItemRepositoryItemBinding,
    private val clickListener: (item: T) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    /**
     * Текущий элемент.
     */
    private var currentItem: T? = null

    init {
        binding.repositoryItem.setOnClickListener {
            currentItem?.let { item ->
                clickListener(item)
            }
        }
    }

    /**
     * Обновляет данные в элементе.
     */
    fun bind(item: T?) {
        currentItem = item
        binding.itemText = item?.data()
    }

    companion object {
        /**
         * Создает holder элемент.
         */
        fun <T : RepositoryItem> create(
            parent: ViewGroup,
            clickListener: (item: T) -> Unit,
        ): RepositoryItemViewHolder<T> {
            return RepositoryItemViewHolder(
                ItemRepositoryItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ),
                clickListener
            )
        }
    }
}