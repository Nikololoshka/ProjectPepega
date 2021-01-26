package com.vereshchagin.nikolay.stankinschedule.ui.modulejournal.predict.paging.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
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

    private var currentItem: PredictDiscipline? = null

    init {
        binding.markInput.doOnTextChanged { text, _, _, _ ->
            val string = text.toString()
            try {
                val value = string.toInt()

                binding.markInputLayout.error = when {
                    value < 25 -> "< 25"
                    value > 100 -> "> 100"
                    else -> {
                        binding.markInputLayout.isErrorEnabled = false
                        currentItem?.let { markChanged(it, value, bindingAdapterPosition) }
                        null
                    }
                }

            } catch (e: NumberFormatException) {

            }
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