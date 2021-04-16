package com.vereshchagin.nikolay.stankinschedule.ui.schedule.myschedules.paging

import android.util.SparseBooleanArray
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.ScheduleItem
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.myschedules.paging.viewholder.ScheduleItemHolder
import java.util.*

/**
 * Адаптер списка с всеми расписаниями.
 */
class SchedulesAdapter(
    private val itemListener: OnScheduleItemListener,
    private val dragListener: DragToMoveCallback.OnStartDragListener,
) : RecyclerView.Adapter<ScheduleItemHolder>() {

    /**
     * Выбранные расписания (в режиме редактирования).
     */
    private var selectedItems = SparseBooleanArray()

    /**
     * Список с расписаниями.
     */
    var schedules: MutableList<ScheduleItem> = mutableListOf()
        private set

    /**
     * Избранное расписание
     */
    private var favorite: String? = null

    /**
     * Нужно ли анимировать ли кнопку "избранное".
     */
    private var isAnimateFavoriteButton = false

    /**
     * Редактируется ли сейчас список расписаний.
     */
    private var isEditable = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleItemHolder {
        return ScheduleItemHolder.create(
            parent, itemListener, dragListener, this::animationController
        )
    }

    override fun getItemCount(): Int = schedules.size

    override fun onBindViewHolder(holder: ScheduleItemHolder, position: Int) {
        val item = schedules[position]
        holder.bind(
            item, item.scheduleName == favorite, isAnimateFavoriteButton,
            selectedItems.get(position, false), isEditable
        )
    }

    /**
     * Контролирует рекурсию анимации конпки избранное.
     */
    private fun animationController(animate: Boolean) {
        isAnimateFavoriteButton = animate
    }

    /**
     * Перемещает элементы в списке (Drag & Drop).
     */
    fun moveItem(fromPosition: Int, toPosition: Int) {
        Collections.swap(schedules, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)

        itemListener.onScheduleItemMove(fromPosition, toPosition)
    }

    /**
     * Устанавливает новый список с расписанием.
     */
    fun submitList(schedules: List<ScheduleItem>) {
        this.schedules = schedules.toMutableList()
        notifyDataSetChanged()
    }

    /**
     * Устанавливает избранное расписание.
     */
    fun submitFavorite(favorite: String?) {
        this.favorite = favorite
        notifyDataSetChanged()
    }

    /**
     * Устанавливает список с расписаниями, которые "выбраны".
     */
    fun setSelectedItems(selectedItems: SparseBooleanArray) {
        this.selectedItems = selectedItems
        notifyDataSetChanged()
    }

    /**
     * Устанавливает список в режим редактирования.
     */
    fun setEditable(editable: Boolean) {
        isEditable = editable
        notifyDataSetChanged()
    }

    /**
     * Callback интерфейс для взамодействия со списком.
     */
    interface OnScheduleItemListener {
        /**
         * Расписание нажато.
         */
        fun onScheduleItemClicked(schedule: String, position: Int)

        /**
         * Долгое нажатие по расписанию.
         */
        fun onScheduleItemLongClicked(schedule: String, position: Int)

        /**
         * Выбрано избранное расписание.
         */
        fun onScheduleFavoriteSelected(favorite: String)

        /**
         * Перемещено расписание.
         */
        fun onScheduleItemMove(fromPosition: Int, toPosition: Int)
    }
}