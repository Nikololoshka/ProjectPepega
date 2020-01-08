package com.github.nikololoshka.pepegaschedule.home.pager;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class HomeDayPairsPager extends ViewPager {

    private View mCurrentView;
    private boolean isDragging;

    private int mHc;
    private int mHt;

    private boolean init = false;

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

        int hc = getMeasuredHeight();
        int ht = height;

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

        if (init) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(ht, MeasureSpec.EXACTLY);
            init = false;
        }

        animate(hc, height);

//        Log.d("MyLog", mHc + " now " + mHt);
//        Log.d("MyLog", hc + " to " + height);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void animate(int current, int target) {
        Log.d("MyLog", "------------------");
        Log.d("MyLog", mHc + " now " + mHt);
        Log.d("MyLog", current + " to " + target);

        if (mHc == 0 || mHt != target) {
            mHc = current;
            mHt = target;
        } else {
            return;
        }

        Log.d("MyLogAnimate", current + " to " + target);

        ValueAnimator valueAnimator = ValueAnimator.ofInt(current, target);
        valueAnimator.setDuration(400);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                HomeDayPairsPager.this.getLayoutParams().height = value;
                HomeDayPairsPager.this.requestLayout();
            }
        });
        valueAnimator.start();
    }

    private void reanimate() {
        if (mCurrentView == null) {
            return;
        }

        int hc = getMeasuredHeight();

        mCurrentView.measure(0, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        int ht = mCurrentView.getMeasuredHeight();

        animate(hc, ht);
    }

    public void measureCurrentView(View currentView) {
        mCurrentView = currentView;
        isDragging = false;

        reanimate();
    }

    public void update(int index) {
        setCurrentItem(index);

        init = true;
        measure(0, 0);
    }
}
