package com.github.nikololoshka.pepegaschedule.schedule.view;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.ColorUtils;

import com.github.nikololoshka.pepegaschedule.R;
import com.github.nikololoshka.pepegaschedule.schedule.pair.Pair;
import com.github.nikololoshka.pepegaschedule.settings.ApplicationPreference;

/**
 * Карточка пары в расписании.
 */
public class PairCardView extends CardView {

    private TextView mTitleView;
    private TextView mLecturerView;
    private TextView mTypeView;
    private TextView mClassroomView;
    private TextView mSubgroupView;
    private TextView mTimeStartView;
    private TextView mTimeEndView;

    private int mLectureColor;
    private int mSeminarColor;
    private int mLaboratoryColor;
    private int mSubgroupAColor;
    private int mSubgroupBColor;
    private float mRectRound;

    private int mColorWhite = Color.WHITE;
    private int mColorBlack = Color.BLACK;

    private Pair mPair;

    public PairCardView(@NonNull Context context, @NonNull Pair pair) {
        super(context);
        initialization(context);
        updatePair(pair);
    }

    public PairCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialization(context);
    }

    public PairCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialization(context);
    }

    private void initialization(@NonNull Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.view_pair_card, this);

        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setClickable(true);
        setFocusable(true);

        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, value, true);
        setForeground(context.getDrawable(value.resourceId));
        setRadius(0);
        setCardElevation(0);
        setMaxCardElevation(0);

        mTitleView = findViewById(R.id.pair_card_title);
        mLecturerView = findViewById(R.id.pair_card_lecturer);
        mTypeView = findViewById(R.id.pair_card_type);
        mClassroomView = findViewById(R.id.pair_card_classroom);
        mSubgroupView = findViewById(R.id.pair_card_subgroup);
        mTimeStartView = findViewById(R.id.pair_card_start);
        mTimeEndView = findViewById(R.id.pair_card_end);

        mClassroomView.setPaintFlags(mClassroomView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        mLectureColor = ApplicationPreference.pairColor(getContext(), ApplicationPreference.LECTURE_COLOR);
        mSeminarColor = ApplicationPreference.pairColor(getContext(), ApplicationPreference.SEMINAR_COLOR);
        mLaboratoryColor = ApplicationPreference.pairColor(getContext(), ApplicationPreference.LABORATORY_COLOR);
        mSubgroupAColor = ApplicationPreference.pairColor(getContext(), ApplicationPreference.SUBGROUP_A_COLOR);
        mSubgroupBColor = ApplicationPreference.pairColor(getContext(), ApplicationPreference.SUBGROUP_B_COLOR);

        mRectRound = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                10, getResources().getDisplayMetrics());
    }

    public void updatePair(@NonNull Pair pair) {
        mPair = pair;

        // title
        mTitleView.setText(mPair.title().title());

        // lecturer
        if (!mPair.lecturer().isValid()) {
            mLecturerView.setVisibility(GONE);
        } else {
            mLecturerView.setText(mPair.lecturer().lecturer());
            mLecturerView.setVisibility(VISIBLE);
        }

        // type
        mTypeView.setText(mPair.type().type().text());
        switch (mPair.type().type()) {
            case LECTURE:
                setupDrawableBackground(mTypeView, mLectureColor);
                break;
            case SEMINAR:
                setupDrawableBackground(mTypeView, mSeminarColor);
                break;
            case LABORATORY:
                setupDrawableBackground(mTypeView, mLaboratoryColor);
                break;
        }

        // classroom
        if (!mPair.classroom().isValid()) {
            mClassroomView.setVisibility(GONE);
        } else {
            mClassroomView.setText(mPair.classroom().classroom());
            mClassroomView.setVisibility(VISIBLE);
        }

        // subgroup
        if (!mPair.subgroup().isValid()) {
            mSubgroupView.setVisibility(GONE);
        } else {
            mSubgroupView.setText(mPair.subgroup().subgroup().text());

            switch (mPair.subgroup().subgroup()) {
                case A:
                    setupDrawableBackground(mSubgroupView, mSubgroupAColor);
                    break;
                case B:
                    setupDrawableBackground(mSubgroupView, mSubgroupBColor);
                    break;
            }

            mSubgroupView.setVisibility(VISIBLE);
        }

        // time
        mTimeStartView.setText(mPair.time().start());
        mTimeEndView.setText(mPair.time().end());
    }

    private void setupDrawableBackground(@NonNull TextView view, int color) {
        Drawable drawable = view.getBackground();

        if (drawable instanceof GradientDrawable) {
            GradientDrawable gradientDrawable = (GradientDrawable) drawable;
            gradientDrawable.setColor(color);
            return;
        }

        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(mRectRound);
        gradientDrawable.setColor(color);

        view.setBackground(gradientDrawable);
        view.setTextColor(isDark(color) ? mColorWhite : mColorBlack);

    }

    /**
     * Определяет является ли цвет темным.
     * @param color цвет.
     * @return true если темный, иначе false.
     */
    boolean isDark(int color) {
        return ColorUtils.calculateLuminance(color) < 0.5;
    }

    @Nullable
    public Pair pair() {
        return mPair;
    }
}
