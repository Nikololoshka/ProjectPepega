package com.vereshchagin.nikolay.stankinschedule.ui.modulejournal.predict.paging

import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.vereshchagin.nikolay.stankinschedule.model.modulejournal.PredictDiscipline
import com.vereshchagin.nikolay.stankinschedule.model.modulejournal.SemesterMarks
import com.vereshchagin.nikolay.stankinschedule.ui.modulejournal.predict.paging.viewholder.PredictDisciplineViewHolder
import kotlinx.coroutines.launch

/**
 * Адаптер для отображения предметов для вычисления рейтинга.
 */
class PredictDisciplineAdapter(
    private val markChanged: (item: PredictDiscipline, value: Int) -> Unit,
    private val onItemCountChanged: (count: Int) -> Unit,
) : RecyclerView.Adapter<PredictDisciplineViewHolder>() {

    private var semesterMarks: SemesterMarks? = null
    private var data = emptyList<PredictDiscipline>()
    private var showAllDisciplines = false

    fun submitData(lifecycle: Lifecycle, semesterMarks: SemesterMarks) {
        lifecycle.coroutineScope.launch {
            submitData(semesterMarks)
        }
    }

    private fun submitData(semesterMarks: SemesterMarks) {
        this.semesterMarks = semesterMarks
        updateData(false)
    }

    private fun updateData(animation: Boolean) {
        val semester = semesterMarks ?: return

        val newData = arrayListOf<PredictDiscipline>()
        for (discipline in semester.disciplines) {
            for ((type, mark) in discipline.marks) {
                if (showAllDisciplines || (mark == 0)) {
                    newData.add(PredictDiscipline(discipline.title, type, mark))
                }
            }
        }

        onItemCountChanged(newData.size)

        if (animation) {
            val oldData = data
            diffUpdate(oldData, newData)
            data = newData
        } else {
            data = newData
            notifyDataSetChanged()
        }
    }

    private fun diffUpdate(oldData: List<PredictDiscipline>, newData: List<PredictDiscipline>) {
        val callback = object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return oldData.size
            }

            override fun getNewListSize(): Int {
                return newData.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldData[oldItemPosition].title == newData[newItemPosition].title
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldData[oldItemPosition] == newData[newItemPosition]
            }
        }

        val diff = DiffUtil.calculateDiff(callback)
        diff.dispatchUpdatesTo(this)
    }

    fun showDisciplines(show: Boolean) {
        showAllDisciplines = show
        updateData(true)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PredictDisciplineViewHolder {
        return PredictDisciplineViewHolder.create(parent, this::test)
    }

    override fun onBindViewHolder(holder: PredictDisciplineViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun test(item: PredictDiscipline, value: Int, position: Int) {
        data[position].mark = value
        markChanged(item, value)
    }
}