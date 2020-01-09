package com.github.nikololoshka.pepegaschedule.schedule.myschedules;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nikololoshka.pepegaschedule.R;

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
        super();
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

    public static class RecyclerViewBackground extends Drawable {

        private RecyclerView mRecyclerView;
        private Paint mPaint;

        RecyclerViewBackground(@NonNull Context context) {
            super();
            mPaint = new Paint();
            mPaint.setColor(ContextCompat.getColor(context, R.color.colorCardViewBackground));
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            if (mRecyclerView.getChildCount() == 0) {
                return;
            }

            int bottom = mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1).getBottom();
            if (bottom >= mRecyclerView.getHeight()) {
                bottom = mRecyclerView.getHeight();
            }
            canvas.drawRect(0, 0, getBounds().width(), bottom, mPaint);
        }

        @Override
        public void setAlpha(int alpha) {
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
        }

        @Override
        public int getOpacity() {
            return PixelFormat.OPAQUE;
        }

        public void attachRecyclerView(RecyclerView recyclerView) {
            mRecyclerView = recyclerView;
        }
    }
}
