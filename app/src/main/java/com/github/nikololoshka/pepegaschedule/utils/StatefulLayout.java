package com.github.nikololoshka.pepegaschedule.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.transition.Fade;
import androidx.transition.TransitionManager;
import androidx.transition.TransitionSet;

import com.github.nikololoshka.pepegaschedule.R;

import java.util.HashMap;


/**
 * Компонент, который в себе содержит отображает одну view с возможностью переключения.
 */
public class StatefulLayout extends LinearLayout {

    private static final String LOAD_STATE = "load_state";
    private static final String ERROR_STATE = "error_state";

    /**
     * Список всех view.
     */
    private HashMap<String, View> mStateViews = new HashMap<>();

    /**
     * Текущая view.
     */
    private String mCurrentState = "";

    /**
     * Будет ли анимирован переход между состояниями.
     */
    private boolean mIsAnimated = true;

    public StatefulLayout(@NonNull Context context) {
        super(context);
        initialization(context);
    }

    public StatefulLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialization(context);
    }

    public StatefulLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialization(context);
    }

    public StatefulLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialization(context);
    }

    /**
     * Инициализуриет view.
     * @param context - контекст приложения.
     */
    private void initialization(@NonNull Context context) {
        setOrientation(VERTICAL);
    }

    /**
     * Добавляет view для отображения.
     * @param state - название view.
     * @param view - сама view.
     */
    public void addState(String state, View view) {
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

        if (!mCurrentState.isEmpty() && mIsAnimated) {
            TransitionSet transition = new TransitionSet();
            transition.addTransition(new Fade());
            transition.setDuration(300);
            TransitionManager.beginDelayedTransition(this, transition);
        }

        View oldView = mStateViews.get(mCurrentState);
        if (oldView != null) {
            oldView.setVisibility(GONE);
        }

        mCurrentState = state;

        View newView = mStateViews.get(mCurrentState);
        if (newView != null) {
            newView.setVisibility(VISIBLE);
        }
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
    public void setState(int id) {
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
     * Устанавливает текст ошибки на стандартное view.
     * @param errorMessage - текст ошибки.
     */
    public void setErrorText(@NonNull String errorMessage) {
        setErrorText(errorMessage, null);
    }

    /**
     * Устанавливает текст ошибки на стандартное view.
     * @param errorMessage - текст ошибки.
     * @param errorDescription - описание ошибки.
     */
    public void setErrorText(@NonNull String errorMessage, @Nullable String errorDescription) {
        View view = mStateViews.get(ERROR_STATE);
        if (view == null) {
            return;
        }

        TextView title = view.findViewById(R.id.error_title);
        title.setText(errorMessage);

        TextView description = view.findViewById(R.id.error_description);
        description.setText(errorDescription != null ? errorDescription : "");
    }

    /**
     * Отображает стандартное view "ошибки" с текстом.
     * @param errorMessage - текст ошибки.
     *
     */
    public void setErrorStateWithMessage(@NonNull String errorMessage) {
        setErrorStateWithMessage(errorMessage, null);
    }

    /**
     * Отображает стандартное view "ошибки" с текстом.
     * @param errorMessage - текст ошибки.
     * @param errorDescription - описание ошибки.
     */
    @SuppressLint("InflateParams")
    public void setErrorStateWithMessage(@NonNull String errorMessage, @Nullable String errorDescription) {
        setErrorText(errorMessage, errorDescription);

        if (!mStateViews.containsKey(ERROR_STATE)) {
            addState(ERROR_STATE, LayoutInflater.from(getContext()).inflate(R.layout.view_error, null));
        }

        setState(ERROR_STATE);
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
     * Скрывает все view с layout'а.
     */
    public void hideAll() {
        for (View view : mStateViews.values()) {
            view.setVisibility(GONE);
        }

        mCurrentState = "";
    }

    /**
     * Проверяет, является ли переданный id слоя текущим.
     * @param id слой.
     * @return true если да, иначе false.
     */
    public boolean isCurrentState(@IdRes int id) {
        return mCurrentState.equals(String.valueOf(id));
    }

    /**
     * @return - количество состояний.
     */
    public int stateCount() {
        return mStateViews.size();
    }
}