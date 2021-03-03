package com.vereshchagin.nikolay.stankinschedule.ui.schedule.myschedules.paging

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.ItemScheduleBinding
import com.vereshchagin.nikolay.stankinschedule.utils.extensions.setVisibility

/**
 * Holder расписания в списке.
 */
class ScheduleItemHolder(
    private val binding: ItemScheduleBinding,
    private var itemListener: SchedulesAdapter.OnScheduleItemListener,
    private val dragListener: DragToMoveCallback.OnStartDragListener,
    private val animationController: (animate: Boolean) -> Unit,
) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    private val defaultBackground = binding.root.background

    init {
        binding.scheduleItem.setOnClickListener(this)
        binding.scheduleItem.setOnLongClickListener {
            itemListener.onScheduleItemLongClicked(
                binding.scheduleInfo.text.toString(), bindingAdapterPosition
            )
            true
        }

        binding.favoriteSchedule.setOnClickListener(this)
        setOnTouchListener(this)
    }

    /**
     * Устанавливает данные в holder.
     */
    fun bind(
        name: String,
        isFavorite: Boolean,
        isAnimateFavoriteButton: Boolean,
        isActive: Boolean,
        isEditable: Boolean
    ) {
        binding.scheduleInfo.text = name
        setActiveState(isActive, isEditable)

        val animate = isFavorite && isAnimateFavoriteButton
        binding.favoriteSchedule.setToggle(isFavorite, animate)
        if (animate) {
            animationController.invoke(false)
        }
    }

    private fun setActiveState(isActive: Boolean, isEditable: Boolean) {
        binding.root.background = if(isActive && isEditable) {
            ContextCompat.getDrawable(binding.root.context, R.drawable.selector_schedule_item)
        } else {
            defaultBackground
        }
        binding.root.isActivated = isActive
        binding.movingHandle.setVisibility(isEditable)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnTouchListener(holder: ScheduleItemHolder) {
        binding.movingHandle.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                dragListener.onStartDrag(holder)
            }
            false
        }
    }

    override fun onClick(v: View?) {
        val name = binding.scheduleInfo.text.toString()
        when (v?.id) {
            R.id.schedule_item -> {
                itemListener.onScheduleItemClicked(name, bindingAdapterPosition)
            }
            R.id.favorite_schedule -> {
                itemListener.onScheduleFavoriteSelected(name)
                animationController.invoke(true)
            }
        }
    }

    companion object {
        fun create(
            parent: ViewGroup,
            itemListener: SchedulesAdapter.OnScheduleItemListener,
            dragListener: DragToMoveCallback.OnStartDragListener,
            animationController: (animate: Boolean) -> Unit,
        ): ScheduleItemHolder {
            return ScheduleItemHolder(
                ItemScheduleBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ),
                itemListener, dragListener, animationController
            )
        }
    }

}