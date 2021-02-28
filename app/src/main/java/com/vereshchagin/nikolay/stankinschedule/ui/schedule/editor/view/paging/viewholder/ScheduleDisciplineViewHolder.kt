package com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.view.paging.viewholder

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vereshchagin.nikolay.stankinschedule.databinding.ItemEditedDisciplineBinding
import com.vereshchagin.nikolay.stankinschedule.databinding.ItemEditedDisciplinePairBinding
import com.vereshchagin.nikolay.stankinschedule.model.schedule.editor.ScheduleEditorDiscipline
import com.vereshchagin.nikolay.stankinschedule.settings.ApplicationPreference
import com.vereshchagin.nikolay.stankinschedule.utils.extensions.setupRectRoundBackground


class ScheduleDisciplineViewHolder(
    private val callback: OnPairListener,
    private val binding: ItemEditedDisciplineBinding,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.callback = callback
        val context = binding.root.context

        val (lectureColor, seminarColor, labColor) = ApplicationPreference.colors(
            context,
            ApplicationPreference.LECTURE_COLOR,
            ApplicationPreference.SEMINAR_COLOR,
            ApplicationPreference.LABORATORY_COLOR
        )

        val round = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 10f, context.resources.displayMetrics
        )

        binding.lecturersLabel.setupRectRoundBackground(lectureColor, round)
        binding.seminarsLabel.setupRectRoundBackground(seminarColor, round)
        binding.labsLabel.setupRectRoundBackground(labColor, round)
    }

    fun bind(data: ScheduleEditorDiscipline?) {
        if (data == null) {
            return
        }

        binding.discipline = data.discipline

        binding.lecturers.removeAllViews()
        for (pair in data.lecturers) {
            val item = addPairItem(binding.lecturers)
            item.pair = pair
        }

        binding.seminars.removeAllViews()
        for (pair in data.seminars) {
            val item = addPairItem(binding.seminars)
            item.pair = pair
        }

        binding.labs.removeAllViews()
        for (pair in data.labs) {
            val item = addPairItem(binding.labs)
            item.pair = pair
        }
    }

    private fun addPairItem(parent: ViewGroup): ItemEditedDisciplinePairBinding {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ItemEditedDisciplinePairBinding.inflate(
            layoutInflater, parent, true
        ).also {
            it.callback = callback
        }
    }

    companion object {

        fun create(callback: OnPairListener, parent: ViewGroup): ScheduleDisciplineViewHolder {
            return ScheduleDisciplineViewHolder(
                callback,
                ItemEditedDisciplineBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }
    }
}