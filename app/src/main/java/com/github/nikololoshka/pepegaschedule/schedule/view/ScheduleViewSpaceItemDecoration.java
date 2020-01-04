package com.github.nikololoshka.pepegaschedule.schedule.view;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Добавляет отступ между каждым 2-ым элементом RecyclerView.
 */
public class ScheduleViewSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private final int mVerticalSpaceHeight;

    ScheduleViewSpaceItemDecoration(int verticalSpaceHeight) {
        mVerticalSpaceHeight = verticalSpaceHeight;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) % 2 == 1) {
            RecyclerView.Adapter adapter = parent.getAdapter();

            if (adapter != null && adapter.getItemCount() - 1 != parent.getChildAdapterPosition(view)) {
                outRect.bottom = mVerticalSpaceHeight;
            }
        }
    }
}
