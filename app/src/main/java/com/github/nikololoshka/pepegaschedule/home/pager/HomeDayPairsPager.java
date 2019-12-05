package com.github.nikololoshka.pepegaschedule.home.pager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class HomeDayPairsPager extends ViewPager {

    private View mCurrentView;
    private boolean isDragging;

    public HomeDayPairsPager(@NonNull Context context) {
        super(context);
        addListener();
    }

    public HomeDayPairsPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        addListener();
    }

    public void setDragging(boolean dragging) {
        isDragging = dragging;
        requestLayout();
    }

    private void addListener() {
        addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state != SCROLL_STATE_SETTLING) {
                    isDragging = state != SCROLL_STATE_IDLE;
                    requestLayout();
                }
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mCurrentView == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        mCurrentView.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        int height = mCurrentView.getMeasuredHeight();

        if (isDragging) {
            for(int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                int h = child.getMeasuredHeight();
                if(h > height) {
                    height = h;
                }
            }
        }

        // Log.d("MyLog", "Now " + getCurrentItem() + "; h " + height + "; drag " + isDragging);

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

//        int delta = h - oldh;
//        Log.d("MyLog", String.valueOf(delta));
    }

    public void measureCurrentView(View currentView) {
        mCurrentView = currentView;
        isDragging = false;
        requestLayout();
    }

    public void update(int index) {
        setCurrentItem(index);
        measure(0, 0);
    }
}
