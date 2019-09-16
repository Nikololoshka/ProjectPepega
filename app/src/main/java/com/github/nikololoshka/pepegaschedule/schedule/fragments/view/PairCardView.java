package com.github.nikololoshka.pepegaschedule.schedule.fragments.view;


import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.github.nikololoshka.pepegaschedule.R;
import com.github.nikololoshka.pepegaschedule.schedule.pair.Pair;

public class PairCardView extends CardView {

    private TextView mTitleView;
    private TextView mLecturerView;
    private TextView mTypeView;
    private TextView mClassroomView;
    private TextView mSubgroupView;
    private TextView mTimeStartView;
    private TextView mTimeEndView;

    private Pair mPair;

    public PairCardView(@NonNull Context context, Pair pair) {
        super(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.pair_card, this);
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        setClickable(true);
        setFocusable(true);
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, value, true);
        setForeground(context.getDrawable(value.resourceId));
        setRadius(0);

        mTitleView = findViewById(R.id.pair_card_title);
        mLecturerView = findViewById(R.id.pair_card_lecturer);
        mTypeView = findViewById(R.id.pair_card_type);
        mClassroomView = findViewById(R.id.pair_card_classroom);
        mSubgroupView = findViewById(R.id.pair_card_subgroup);
        mTimeStartView = findViewById(R.id.pair_card_start);
        mTimeEndView = findViewById(R.id.pair_card_end);

        mClassroomView.setPaintFlags(mClassroomView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        updatePair(pair);
    }

    public PairCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PairCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void updatePair(Pair pair) {
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
                mTypeView.setBackgroundResource(R.drawable.rectangle_lecture);
                break;
            case SEMINAR:
                mTypeView.setBackgroundResource(R.drawable.rectangle_seminar);
                break;
            case LABORATORY:
                mTypeView.setBackgroundResource(R.drawable.rectangle_laboratory);
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
                    mSubgroupView.setBackgroundResource(R.drawable.rectangle_subgroup_a);
                    break;
                case B:
                    mSubgroupView.setBackgroundResource(R.drawable.rectangle_subgroup_b);
                    break;
            }

            mSubgroupView.setVisibility(VISIBLE);
        }

        // time
        mTimeStartView.setText(mPair.time().start());
        mTimeEndView.setText(mPair.time().end());
    }

    public Pair pair() {
        return mPair;
    }
}
