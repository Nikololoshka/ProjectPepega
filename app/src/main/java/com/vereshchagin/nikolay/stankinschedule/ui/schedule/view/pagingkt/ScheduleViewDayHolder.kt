package com.vereshchagin.nikolay.stankinschedule.ui.schedule.view.pagingkt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vereshchagin.nikolay.stankinschedule.databinding.ItemScheduleDayCommonBinding
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Pair
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.view.PairCardView
import java.util.*
import kotlin.collections.ArrayList

/**
 * Holder для в просмотре расписания.
 */
class ScheduleViewDayHolder(
    private val binding: ItemScheduleDayCommonBinding,
    private val callback: (pair: Pair) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private val pairCards = ArrayList<PairCardView>()

    /**
     * Обновляет данные в holder.
     */
    fun bind(item: ScheduleViewDay?) {
        val data = item ?: return

        val title = data.day.toString("EEEE, dd MMMM").capitalize(Locale.ROOT)
        binding.dayTitle.scheduleDayTitle.text = title

        binding.dayPairs.scheduleDayPairs.removeAllViews()

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
            binding.dayPairs.scheduleDayPairs.addView(cardView)
        }

        // если нет пар
        if (data.pairs.isEmpty()) {
            binding.dayPairs.noPairs.noPairs.visibility = View.VISIBLE
            binding.dayPairs.scheduleDayPairs.visibility = View.GONE
        } else {
            binding.dayPairs.noPairs.noPairs.visibility = View.GONE
            binding.dayPairs.scheduleDayPairs.visibility = View.VISIBLE
        }
    }

    companion object {
        /**
         * Создает holder.
         */
        fun create(parent: ViewGroup, callback: (pair: Pair) -> Unit): ScheduleViewDayHolder {
            return ScheduleViewDayHolder(
                ItemScheduleDayCommonBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                callback
            )
        }
    }
}