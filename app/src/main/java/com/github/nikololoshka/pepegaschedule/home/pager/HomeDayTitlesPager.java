package com.github.nikololoshka.pepegaschedule.home.pager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class HomeDayTitlesPager extends ViewPager {
    public HomeDayTitlesPager(@NonNull Context context) {
        super(context);
    }

    public HomeDayTitlesPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int height = 0;

            for(int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

                int h = child.getMeasuredHeight();
                if(h > height) {
                    height = h;
                }
            }

            if (height != 0) {
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            }

            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        public void update(int index) {
            setCurrentItem(index);
            measure(0, 0);
        }
}
