package com.github.nikololoshka.pepegaschedule.schedule.myschedules;

import androidx.recyclerview.widget.RecyclerView;

public interface OnStartDragListener {
    /**
     * Вызывается когда требуется начать перетаскивание.
     * @param viewHolder - передвигаемая view.
     */
    void onStartDrag(RecyclerView.ViewHolder viewHolder);
}
