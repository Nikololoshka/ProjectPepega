package com.vereshchagin.nikolay.stankinschedule.ui.schedule.myschedules.paging.viewholder

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.ItemScheduleBinding
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.myschedules.paging.DragToMoveCallback
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.myschedules.paging.MyScheduleItem
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.myschedules.paging.MySchedulesAdapter
import com.vereshchagin.nikolay.stankinschedule.utils.extensions.setVisibility

/**
 * Holder расписания в списке.
 */
class MyScheduleItemHolder(
    private val binding: ItemScheduleBinding,
    private var itemListener: MySchedulesAdapter.OnScheduleItemListener,
    private val dragListener: DragToMoveCallback.OnStartDragListener,
    private val animationController: (animate: Boolean) -> Unit,
) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    private val defaultBackground = binding.root.background

    private var currentItem: MyScheduleItem? = null

    init {
        binding.scheduleItem.setOnClickListener(this)

        binding.scheduleItem.setOnLongClickListener {
            val scheduleId = currentItem?.id
            if (scheduleId != null) {
                itemListener.onScheduleItemLongClicked(scheduleId, bindingAdapterPosition)
            }
            true
        }

        binding.favoriteSchedule.setOnClickListener(this)
        setOnTouchListener(this)
    }

    /**
     * Устанавливает данные в holder.
     */
    fun bind(
        item: MyScheduleItem,
        isFavorite: Boolean,
        isAnimateFavoriteButton: Boolean,
        isEditable: Boolean,
    ) {
        currentItem = item
        binding.scheduleInfo.text = item.scheduleName
        setActiveState(item.isSelected, isEditable)

        binding.scheduleSync.setVisibility(item.synced)

        val animate = isFavorite && isAnimateFavoriteButton
        binding.favoriteSchedule.setToggle(isFavorite, animate)
        if (animate) {
            animationController.invoke(false)
        }
    }

    private fun setActiveState(isSelected: Boolean, isEditable: Boolean) {
        binding.root.background = if (isSelected && isEditable) {
            ContextCompat.getDrawable(binding.root.context, R.drawable.selector_schedule_item)
        } else {
            defaultBackground
        }
        binding.root.isActivated = isSelected
        binding.movingHandle.setVisibility(isEditable)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnTouchListener(holder: MyScheduleItemHolder) {
        binding.movingHandle.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                dragListener.onStartDrag(holder)
            }
            false
        }
    }

    override fun onClick(v: View?) {
        val scheduleId = currentItem?.id ?: return
        when (v?.id) {
            R.id.schedule_item -> {
                itemListener.onScheduleItemClicked(scheduleId, bindingAdapterPosition)
            }
            R.id.favorite_schedule -> {
                itemListener.onScheduleFavoriteSelected(scheduleId)
                animationController.invoke(true)
            }
        }
    }

    companion object {
        fun create(
            parent: ViewGroup,
            itemListener: MySchedulesAdapter.OnScheduleItemListener,
            dragListener: DragToMoveCallback.OnStartDragListener,
            animationController: (animate: Boolean) -> Unit,
        ): MyScheduleItemHolder {
            return MyScheduleItemHolder(
                ItemScheduleBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ),
                itemListener, dragListener, animationController
            )
        }
    }

}