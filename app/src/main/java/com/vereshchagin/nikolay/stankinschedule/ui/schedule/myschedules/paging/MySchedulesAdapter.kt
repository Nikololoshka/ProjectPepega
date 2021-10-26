package com.vereshchagin.nikolay.stankinschedule.ui.schedule.myschedules.paging

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.ScheduleItem
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.myschedules.paging.viewholder.MyScheduleItemHolder

/**
 * Адаптер списка с всеми расписаниями.
 */
class MySchedulesAdapter(
    private val itemListener: OnScheduleItemListener,
    private val dragListener: DragToMoveCallback.OnStartDragListener,
) : ListAdapter<MyScheduleItem, MyScheduleItemHolder>(MY_SCHEDULE_ITEM_COMPARATOR) {

    /**
     * Избранное расписание
     */
    var favorite: ScheduleItem? = null
        set(value) {
            val currentValue = field
            field = value
            updateItem(currentValue)
            updateItem(value)
        }

    /**
     * Редактируется ли сейчас список расписаний.
     */
    var isEditable = false
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    /**
     * Нужно ли анимировать ли кнопку "избранное".
     */
    private var isAnimateFavoriteButton = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyScheduleItemHolder {
        return MyScheduleItemHolder.create(
            parent, itemListener, dragListener, this::animationController
        )
    }


    override fun onBindViewHolder(holder: MyScheduleItemHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, item == favorite, isAnimateFavoriteButton, isEditable)
    }

    /**
     * Контролирует рекурсию анимации конпки избранное.
     */
    private fun animationController(animate: Boolean) {
        isAnimateFavoriteButton = animate
    }


    private fun updateItem(item: ScheduleItem?) {
        if (item != null) {
            if (isEditable) {
                val itemPosition = currentList.indexOfFirst { it.id == item.id }
                if (itemPosition != -1) {
                    notifyItemChanged(itemPosition)
                }
            } else {
                notifyItemChanged(item.position)
            }
        }
    }

    /**
     * Перемещает элементы в списке (Drag & Drop).
     */
    fun moveItem(fromPosition: Int, toPosition: Int) {
        itemListener.onScheduleItemMove(fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    /**
     * Устанавливает новый список с расписанием.
     */
    fun updateSchedule(schedules: MutableList<MyScheduleItem>) {
        submitList(schedules)
    }

    /**
     * Callback интерфейс для взаимодействия со списком.
     */
    interface OnScheduleItemListener {
        /**
         * Расписание нажато.
         */
        fun onScheduleItemClicked(scheduleId: Long, position: Int)

        /**
         * Долгое нажатие по расписанию.
         */
        fun onScheduleItemLongClicked(scheduleId: Long, position: Int)

        /**
         * Выбрано избранное расписание.
         */
        fun onScheduleFavoriteSelected(favoriteId: Long)

        /**
         * Перемещено расписание.
         */
        fun onScheduleItemMove(fromPosition: Int, toPosition: Int)
    }

    companion object {
        /**
         * Компаратор для сравнения MyScheduleItem.
         */
        private val MY_SCHEDULE_ITEM_COMPARATOR = object : DiffUtil.ItemCallback<MyScheduleItem>() {
            override fun areItemsTheSame(
                oldItem: MyScheduleItem,
                newItem: MyScheduleItem,
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: MyScheduleItem,
                newItem: MyScheduleItem,
            ): Boolean = oldItem == newItem
        }
    }
}