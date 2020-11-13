package com.vereshchagin.nikolay.stankinschedule.ui.schedule.view.paging

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

/**
 * Добавляет отступ между каждым 2-ым элементом RecyclerView.
 */
class ScheduleViewSpaceItemDecoration(
    private val verticalSpaceHeight: Int
) : ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State
    ) {
        if (parent.getChildAdapterPosition(view) % 2 == 1) {
            val adapter = parent.adapter
            if (adapter != null && adapter.itemCount - 1 != parent.getChildAdapterPosition(view)) {
                outRect.bottom = verticalSpaceHeight
            }
        }
    }
}