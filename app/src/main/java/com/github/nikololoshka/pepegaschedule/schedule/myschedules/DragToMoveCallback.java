package com.github.nikololoshka.pepegaschedule.schedule.myschedules;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Callback для перемещения с помошью Drag & Drop в RecyclerView.
 */
public abstract class DragToMoveCallback extends ItemTouchHelper.Callback {

    /**
     * Listener для перетаскивания с помощью Drag & Drop элемента.
     */
    public interface OnStartDragListener {
        /**
         * Вызывается когда требуется начать перетаскивание.
         * @param viewHolder - передвигаемая view.
         */
        void onStartDrag(RecyclerView.ViewHolder viewHolder);
    }

    DragToMoveCallback() {
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
    }
}
