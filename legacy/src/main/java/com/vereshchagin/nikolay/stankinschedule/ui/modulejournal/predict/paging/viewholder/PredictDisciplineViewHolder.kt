package com.vereshchagin.nikolay.stankinschedule.ui.modulejournal.predict.paging.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.ItemPredictDisciplineBinding
import com.vereshchagin.nikolay.stankinschedule.model.modulejournal.PredictDiscipline

/**
 * Элемент для отображения предмета и его оценки,
 * которая будет использоваться в расчете рейтинга.
 */
class PredictDisciplineViewHolder(
    private val markChanged: (item: PredictDiscipline, value: Int, position: Int) -> Unit,
    private val binding: ItemPredictDisciplineBinding,
) : RecyclerView.ViewHolder(binding.root) {

    /**
     * Текущая отображаемая дисциплина.
     */
    private var currentItem: PredictDiscipline? = null

    init {
        binding.markInput.doOnTextChanged { text, _, _, _ ->
            onMarkChanged(text.toString())
        }
    }

    /**
     * Связывает данные с элементом.
     */
    fun bind(item: PredictDiscipline) {
        currentItem = item

        binding.disciplineTitle.text = item.title
        binding.markInputLayout.hint = item.type.tag
        binding.markInput.setText(if (item.mark == 0) "" else item.mark.toString())

        bindingAdapterPosition
    }

    /**
     * Вызывается при изменении оценки в поле.
     */
    private fun onMarkChanged(text: String) {
        try {
            val value = text.toInt()

            binding.markInput.error = when {
                value < 25 -> getString(R.string.mj_mark_below_25)
                value > 100 -> getString(R.string.mj_mark_above_100)
                else -> {
                    currentItem?.let { markChanged(it, value, bindingAdapterPosition) }
                    null
                }
            }

        } catch (e: NumberFormatException) {

        }
    }

    /**
     * Возвращает строку из ресурсов.
     */
    private fun getString(@StringRes id: Int): String {
        return itemView.context.getString(id)
    }

    companion object {
        /**
         * Возвращает holder для отображения предмета и его оценки.
         */
        @JvmStatic
        fun create(
            parent: ViewGroup,
            markChanged: (item: PredictDiscipline, value: Int, position: Int) -> Unit,
        ): PredictDisciplineViewHolder {
            return PredictDisciplineViewHolder(
                markChanged,
                ItemPredictDisciplineBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }
    }
}