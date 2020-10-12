package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.paging

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.ItemRepositoryCategoryBinding
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.RepositoryCategoryItem

/**
 * Holder категории в репозитории.
 */
class RepositoryCategoryHolder(
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    val binding = ItemRepositoryCategoryBinding.bind(itemView)

    fun bind(item: RepositoryCategoryItem?) {
        Log.d("MyLog", "bind: $item")
    }

    companion object {
        fun create(parent: ViewGroup): RepositoryCategoryHolder {
            return RepositoryCategoryHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_repository_category, parent, false)
            )
        }
    }
}