package com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.view.paging.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vereshchagin.nikolay.stankinschedule.databinding.ItemEditedDisciplineBinding


class ScheduleDisciplineViewHolder(
    private val binding: ItemEditedDisciplineBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(data: String?) {
        binding.disciplineTitle.text = data
    }

    companion object {

        fun create(parent: ViewGroup): ScheduleDisciplineViewHolder {
            return ScheduleDisciplineViewHolder(
                ItemEditedDisciplineBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }
    }
}