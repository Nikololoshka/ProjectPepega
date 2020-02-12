package com.github.nikololoshka.pepegaschedule.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.transition.Fade;
import androidx.transition.TransitionManager;

import com.github.nikololoshka.pepegaschedule.R;

import java.util.LinkedHashMap;


/**
 * Компонент, который в себе содержит отображает одну view с возможностью переключения.
 */
public class StatefulLayout extends FrameLayout {

    private static final String TAG = "StatefulLayoutLog";

    private static final String LOAD_STATE = "load_state";

    private static int DEFAULT_DURATION = 300;

    /**
     * Список всех view.
     */
    private LinkedHashMap<String, View> mStateViews = new LinkedHashMap<>();

    /**
     * Текущая view.
     */
    private String mCurrentState;

    /**
     * Будет ли анимирован переход между состояниями.
     */
    private boolean mIsAnimated;

    public StatefulLayout(@NonNull Context context) {
        super(context);
        initialization();
    }

    public StatefulLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialization();
    }

    public StatefulLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialization();
    }

    public StatefulLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialization();
    }

    /**
     * Инициализуриет view.
     */
    private void initialization() {
        mCurrentState = "";
        mIsAnimated = true;
        DEFAULT_DURATION = getResources().getInteger(android.R.integer.config_shortAnimTime);
    }

    /**
     * Добавляет view для отображения.
     * @param state - название view.
     * @param view - сама view.
     */
    public void addState(@NonNull String state, @NonNull View view) {
        // если такое состояние уже было, то удалить
        mStateViews.remove(state);

        mStateViews.put(state, view);
        view.setVisibility(GONE);

        addView(view);
    }

    /**
     * Установить отображаемую view.
     * @param state - название view, которую необходимо отобразить.
     */
    public void setState(@NonNull String state) {
        if (!mStateViews.containsKey(state)) {
            throw new IllegalStateException(String.format("Cannot switch to state: %s", state));
        }

        if (mCurrentState.equals(state)) {
            return;
        }

        final View oldView = mStateViews.get(mCurrentState);
        final View newView = mStateViews.get(state);

        if (!mIsAnimated || mCurrentState.isEmpty()) {
            if (newView != null) {
                newView.setVisibility(VISIBLE);
            }

            if (oldView != null) {
                oldView.setVisibility(GONE);
            }

            mCurrentState = state;
            return;
        }

        mCurrentState = state;

        TransitionManager.beginDelayedTransition(this, new Fade());
        if (newView != null) {
            newView.setVisibility(VISIBLE);
        }

        TransitionManager.beginDelayedTransition(this, new Fade());
        if (oldView != null) {
            oldView.setVisibility(GONE);
        }

//        if (newView != null) {
//            newView.setAlpha(0f);
//            newView.setVisibility(VISIBLE);
//
//            newView.animate()
//                    .alpha(1f)
//                    .setDuration(DEFAULT_DURATION)
//                    .setListener(new AnimatorListenerAdapter() {
//                        @Override
//                        public void onAnimationEnd(Animator animation) {
//                            newView.setVisibility(VISIBLE);
//                        }
//                    });
//        }
//
//        if (oldView != null) {
//            oldView.animate()
//                    .alpha(0f)
//                    .setDuration(DEFAULT_DURATION)
//                    .setListener(new AnimatorListenerAdapter() {
//                        @Override
//                        public void onAnimationEnd(Animator animation) {
//                            oldView.setVisibility(GONE);
//                        }
//                    });
//        }
    }

    /**
     * Отвечает за анимацию переходов между состояниями.
     * По умолчанию: true.
     * @param animate true - плавный перехож, false - резкий.
     */
    public void setAnimation(boolean animate) {
        mIsAnimated = animate;
    }

    /**
     * Установить отображаемую view из XML разметки.
     * @param id - id view, которую необходимо отобразить.
     */
    public void setState(@IdRes int id) {
        String stringID = String.valueOf(id);
        setState(stringID);
    }

    /**
     * Отображает view загрузки.
     */
    @SuppressLint("InflateParams")
    public void setLoadState() {
        if (!mStateViews.containsKey(LOAD_STATE)) {
            addState(LOAD_STATE, LayoutInflater.from(getContext()).inflate(R.layout.view_loading, null));
        }
        setState(LOAD_STATE);
    }

    /**
     * Добавляет все view, объявленных в XML разметке.
     */
    public void addXMLViews() {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);

            int id = view.getId();
            if (id == NO_ID) {
                throw new IllegalStateException("Child view not have ID!");
            }

            view.setVisibility(GONE);
            mStateViews.put(String.valueOf(id), view);
        }
    }

    /**
     * Проверяет, является ли переданный id слоя текущим.
     * @param id слой.
     * @return true если да, иначе false.
     */
    public boolean isCurrentState(@IdRes int id) {
        return mCurrentState.equals(String.valueOf(id));
    }
}