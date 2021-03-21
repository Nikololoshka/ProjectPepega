package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.paging.holder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vereshchagin.nikolay.stankinschedule.databinding.ItemRepositoryItemBinding
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.v1.RepositoryItem

/**
 *
 */
class RepositoryItemViewHolder<T : RepositoryItem>(
    private val binding: ItemRepositoryItemBinding,
    private val clickListener: (item: T) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    private var currentItem: T? = null

    init {
        binding.repositoryItem.setOnClickListener {
            currentItem?.let { item ->
                clickListener(item)
            }
        }
    }

    /**
     *
     */
    fun bind(item: T?) {
        currentItem = item
        binding.itemText = item?.data()
    }

    companion object {
        /**
         *
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