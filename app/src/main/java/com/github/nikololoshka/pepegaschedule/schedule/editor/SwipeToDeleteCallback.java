package com.github.nikololoshka.pepegaschedule.schedule.editor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nikololoshka.pepegaschedule.R;


/**
 * Callback для удаления с помощью swipe из RecyclerView.
 *
 * https://medium.com/@zackcosborn/step-by-step-recyclerview-swipe-to-delete-and-undo-7bbae1fce27e
 */
public abstract class SwipeToDeleteCallback extends ItemTouchHelper.Callback {

    private ColorDrawable mBackground;
    private Drawable deleteDrawable;

    SwipeToDeleteCallback(@NonNull Context context) {
        int backgroundColor = Color.parseColor("#b80f0a");
        mBackground = new ColorDrawable(backgroundColor);

        deleteDrawable = ContextCompat.getDrawable(context, R.drawable.ic_swipe_delete_button);
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        return false;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 20; //so background is behind the rounded corners of itemView

        int iconMargin = (itemView.getHeight() - deleteDrawable.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - deleteDrawable.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + deleteDrawable.getIntrinsicHeight();

        if (dX > 0) {
            // Swiping to the right
            int iconLeft = itemView.getLeft() + iconMargin;
            int iconRight = itemView.getLeft() + iconMargin + deleteDrawable.getIntrinsicWidth();

            deleteDrawable.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            mBackground.setBounds(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + ((int) dX) + backgroundCornerOffset,
                    itemView.getBottom());

        } else if (dX < 0) {
            // Swiping to the left
            int iconLeft = itemView.getRight() - iconMargin - deleteDrawable.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;

            deleteDrawable.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            mBackground.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                    itemView.getTop(), itemView.getRight(), itemView.getBottom());
        } else { // view is unSwiped
            mBackground.setBounds(0, 0, 0, 0);
        }

        mBackground.draw(c);
        deleteDrawable.draw(c);
    }

    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return 0.7f;
    }
}


