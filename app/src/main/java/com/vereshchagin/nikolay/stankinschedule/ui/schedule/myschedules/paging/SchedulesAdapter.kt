package com.vereshchagin.nikolay.stankinschedule.ui.schedule.myschedules.paging

import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vereshchagin.nikolay.stankinschedule.R
import java.util.*


class SchedulesAdapter(
    private val itemListener: OnScheduleItemListener,
    private val dragListener: DragToMoveCallback.OnStartDragListener
) : RecyclerView.Adapter<ScheduleItemHolder>() {

    private var selectedItems = SparseBooleanArray()
    private var schedules: List<String> = listOf()
    private var favorite: String = ""
    private var isAnimateFavoriteButton = false
    private var isEditable = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleItemHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_schedule, parent, false)
        return ScheduleItemHolder(view, itemListener, dragListener, this::animationController)
    }

    override fun getItemCount(): Int = schedules.size

    override fun onBindViewHolder(holder: ScheduleItemHolder, position: Int) {
        val name = schedules[position]
        holder.bind(name, name == favorite, isAnimateFavoriteButton,
            selectedItems.get(position, false), isEditable)
    }

    private fun animationController(animate: Boolean) {
        isAnimateFavoriteButton = animate
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        Collections.swap(schedules, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)

        itemListener.onScheduleItemMove(fromPosition, toPosition)
    }

    fun submitList(schedules: List<String>, favorite: String) {
        this.schedules = schedules
        this.favorite = favorite

        notifyDataSetChanged()
    }

    fun setSelectedItems(selectedItems: SparseBooleanArray) {
        this.selectedItems = selectedItems
        notifyDataSetChanged()
    }

    fun setEditable(editable: Boolean) {
        isEditable = editable
        notifyDataSetChanged()
    }

    interface OnScheduleItemListener {
        fun onScheduleItemClicked(schedule: String, position: Int)
        fun onScheduleItemLongClicked(schedule: String, position: Int)
        fun onScheduleFavoriteSelected(favorite: String)
        fun onScheduleItemMove(fromPosition: Int, toPosition: Int)
    }
}