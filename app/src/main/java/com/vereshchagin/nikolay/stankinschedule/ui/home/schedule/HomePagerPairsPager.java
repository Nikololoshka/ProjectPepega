package com.vereshchagin.nikolay.stankinschedule.ui.home.schedule;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

/**
 * Pager для отображения пар.
 */
public class HomePagerPairsPager extends ViewPager {

    private static final String TAG = "HomePagerPairsPagerLog";

    private ValueAnimator mHeightAnimator;
    private int mCurrentTargetHeight;
    private boolean mIsDragging;
    private boolean mIsInitialization;

    @Nullable
    private View mCurrentView;

    public HomePagerPairsPager(@NonNull Context context) {
        super(context);
        initialization(context);
    }

    public HomePagerPairsPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialization(context);
    }

    /**
     * Инициализация pager'а.
     * @param context контекст.
     */
    private void initialization(@NonNull Context context) {
        mHeightAnimator = new ValueAnimator();
        mHeightAnimator.setDuration(300);
        mHeightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                HomePagerPairsPager.this.getLayoutParams().height = (int) animation.getAnimatedValue();
                HomePagerPairsPager.this.requestLayout();
            }
        });
    }

    /**
     * Создает и запускает анимацию изменения размеров pager'а до необходимой.
     * @param targetHeight необходимая высота.
     */
    private void createAnimation(int targetHeight) {
        if (mCurrentTargetHeight == targetHeight) {
            return;
        }
        mCurrentTargetHeight = targetHeight;

        if (!mIsInitialization) {
            mIsInitialization = true;

            getLayoutParams().height = targetHeight;
            requestLayout();
            return;
        }

        mHeightAnimator.cancel();
        mHeightAnimator.setIntValues(getMeasuredHeight(), targetHeight);
        mHeightAnimator.start();
    }

    /**
     * Обновляет текущую отображаемую view. Вызывается adapter'ом, если
     * тот установил отображаемый сейчас элемент.
     *
     * @param view отображаемая сейчас view.
     */
    public void updateCurrentView(@NonNull View view) {
        mCurrentView = view;
        setDragging(false);
    }

    /**
     * Двигают ли сейчас pager.
     * @param dragging true - двигают, иначе false.
     */
    public void setDragging(boolean dragging) {
        mIsDragging = dragging;
        requestLayout();
    }

    /**
     * Устанавливает, нужно ли пересчитывать размеры pager.
     */
    public void remeasure() {
        mIsInitialization = false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // если нечего отображать
        if (mCurrentView == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        // измеряем размеры view
        mCurrentView.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        // получаем её высоту
        int viewHeight = mCurrentView.getMeasuredHeight();

        // если сейчас нас двигают, то смотрим высоту ближайших child элементов.
        // getChildCount() - возвращает 3-ку, т.е. левый, наш и правый элемент.
        if (mIsDragging) {
            for(int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                // измеряем размеры view
                child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                int h = child.getMeasuredHeight();
                if(h > viewHeight) {
                    viewHeight = h;
                }
            }
        }

        if (!mIsInitialization) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY);
        }

        createAnimation(viewHeight);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
