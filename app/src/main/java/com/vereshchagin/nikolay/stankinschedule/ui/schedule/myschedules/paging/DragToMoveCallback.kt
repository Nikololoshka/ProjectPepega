package com.vereshchagin.nikolay.stankinschedule.ui.schedule.myschedules.paging

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * Callback для перемещения с помощью Drag & Drop в RecyclerView.
 */
abstract class DragToMoveCallback : ItemTouchHelper.Callback() {
    /**
     * Listener для перетаскивания с помощью Drag & Drop элемента.
     */
    interface OnStartDragListener {
        /**
         * Вызывается, когда требуется начать перетаскивание.
         *
         * @param viewHolder - передвигаемая view.
         */
        fun onStartDrag(viewHolder: RecyclerView.ViewHolder?)
    }

    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
    ): Int {
        return makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
}