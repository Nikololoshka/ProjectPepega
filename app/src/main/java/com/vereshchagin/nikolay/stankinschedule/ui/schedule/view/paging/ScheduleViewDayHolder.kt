package com.vereshchagin.nikolay.stankinschedule.ui.schedule.view.paging

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vereshchagin.nikolay.stankinschedule.databinding.ItemScheduleDayBinding
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.PairItem
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.view.PairCardView
import java.util.*
import kotlin.collections.ArrayList

/**
 * Holder для в просмотре расписания.
 */
class ScheduleViewDayHolder(
    private val binding: ItemScheduleDayBinding,
    private val callback: (pair: PairItem) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    private val pairCards = ArrayList<PairCardView>()

    /**
     * Обновляет данные в holder.
     */
    fun bind(item: ScheduleViewDay?) {
        val data = item ?: return

        val title = data.day.toString("EEEE, dd MMMM").capitalize(Locale.ROOT)
        binding.scheduleDayTitle.text = title

        binding.scheduleDayPairs.removeAllViews()

        for ((i, pair) in data.pairs.withIndex()) {
            var cardView: PairCardView

            // до создаем view пары, если не хватает
            if (i < pairCards.size) {
                cardView = pairCards[i]
                cardView.updatePair(pair)

            } else {
                cardView = PairCardView(itemView.context, pair)
                cardView.setOnClickListener { v ->
                    callback.invoke((v as PairCardView).pair()!!)
                }
                pairCards.add(cardView)
            }
            binding.scheduleDayPairs.addView(cardView)
        }

        // если нет пар
        if (data.pairs.isEmpty()) {
            binding.noPairs.noPairs.visibility = View.VISIBLE
            binding.scheduleDayPairs.visibility = View.GONE
        } else {
            binding.noPairs.noPairs.visibility = View.GONE
            binding.scheduleDayPairs.visibility = View.VISIBLE
        }
    }

    companion object {
        /**
         * Создает holder.
         */
        fun create(parent: ViewGroup, callback: (pair: PairItem) -> Unit): ScheduleViewDayHolder {
            return ScheduleViewDayHolder(
                ItemScheduleDayBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                callback
            )
        }
    }
}