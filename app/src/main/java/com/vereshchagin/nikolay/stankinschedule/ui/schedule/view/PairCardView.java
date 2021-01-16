package com.vereshchagin.nikolay.stankinschedule.ui.schedule.view;


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
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import com.vereshchagin.nikolay.stankinschedule.R;
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Pair;
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Subgroup;
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Type;
import com.vereshchagin.nikolay.stankinschedule.ui.settings.ApplicationPreference;

import java.util.Arrays;
import java.util.List;

import static com.vereshchagin.nikolay.stankinschedule.ui.settings.ApplicationPreference.LABORATORY_COLOR;
import static com.vereshchagin.nikolay.stankinschedule.ui.settings.ApplicationPreference.LECTURE_COLOR;
import static com.vereshchagin.nikolay.stankinschedule.ui.settings.ApplicationPreference.SEMINAR_COLOR;
import static com.vereshchagin.nikolay.stankinschedule.ui.settings.ApplicationPreference.SUBGROUP_A_COLOR;
import static com.vereshchagin.nikolay.stankinschedule.ui.settings.ApplicationPreference.SUBGROUP_B_COLOR;

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

    private List<String> mTypes;
    private List<String> mSubgroups;

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
        setForeground(ContextCompat.getDrawable(context, value.resourceId));
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

        mLectureColor = ApplicationPreference.pairColor(context, LECTURE_COLOR);
        mSeminarColor = ApplicationPreference.pairColor(context, SEMINAR_COLOR);
        mLaboratoryColor = ApplicationPreference.pairColor(context, LABORATORY_COLOR);
        mSubgroupAColor = ApplicationPreference.pairColor(context, SUBGROUP_A_COLOR);
        mSubgroupBColor = ApplicationPreference.pairColor(context, SUBGROUP_B_COLOR);

        mTypes = Arrays.asList(getResources().getStringArray(R.array.type_list));
        mSubgroups = Arrays.asList(getResources().getStringArray(R.array.subgroup_simple_list));

        mRectRound = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                10, getResources().getDisplayMetrics());
    }

    public void updatePair(@NonNull Pair pair) {
        mPair = pair;

        // title
        mTitleView.setText(mPair.getTitle());

        // lecturer
        if (mPair.getLecturer().isEmpty()) {
            mLecturerView.setVisibility(GONE);
        } else {
            mLecturerView.setText(mPair.getLecturer());
            mLecturerView.setVisibility(VISIBLE);
        }

        // type
        mTypeView.setText(typeForPair(mPair.getType()));
        switch (mPair.getType()) {
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
        if (mPair.getClassroom().isEmpty()) {
            mClassroomView.setVisibility(GONE);
        } else {
            mClassroomView.setText(mPair.getClassroom());
            mClassroomView.setVisibility(VISIBLE);
        }

        // subgroup
        if (mPair.getSubgroup() == Subgroup.COMMON) {
            mSubgroupView.setVisibility(GONE);
        } else {
            mSubgroupView.setText(subgroupForPair(mPair.getSubgroup()));

            switch (mPair.getSubgroup()) {
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
        mTimeStartView.setText(mPair.getTime().startString());
        mTimeEndView.setText(mPair.getTime().endString());

        // Log.d("MyLog", "updatePair: " + pair);
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
        view.setTextColor(isDark(color) ? Color.WHITE : Color.BLACK);
    }

    /**
     * @param type тип пары.
     * @return строка типа пары.
     */
    @NonNull
    private String typeForPair(@NonNull Type type) {
        switch (type) {
            case LECTURE:
                return mTypes.get(0);
            case SEMINAR:
                return mTypes.get(1);
            case LABORATORY:
                return mTypes.get(2);
        }

        throw new RuntimeException("No found string type resources");
    }

    /**
     * @param subgroup подгруппа пары.
     * @return строка подгруппы пары.
     */
    @NonNull
    private String subgroupForPair(@NonNull Subgroup subgroup) {
        switch (subgroup) {
            case COMMON:
                return "";
            case A:
                return mSubgroups.get(0);
            case B:
                return mSubgroups.get(1);
        }

        throw new RuntimeException("No found string subgroup resources");
    }

    /**
     * Определяет, является ли цвет темным.
     *
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
