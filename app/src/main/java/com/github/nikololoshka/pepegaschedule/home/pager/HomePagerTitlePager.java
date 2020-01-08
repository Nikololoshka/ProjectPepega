package com.github.nikololoshka.pepegaschedule.home.pager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

/**
 * Верхний pager для отображение заголовка дня (дату).
 */
public class HomePagerTitlePager extends ViewPager {

    public HomePagerTitlePager(@NonNull Context context) {
        super(context);
    }

    public HomePagerTitlePager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Переизмеряет pager.
     */
    public void remeasure() {
        measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Среди всех child элементов берем самы максимальный по высоте.
        // Это и будет высота нашего pager'а.

        int height = 0;

        for(int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            // UNSPECIFIED - размер, который хочет иметь child
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

            int h = child.getMeasuredHeight();
            if(h > height) {
                height = h;
            }
        }

        if (height != 0) {
            // EXACTLY - точный размер
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
