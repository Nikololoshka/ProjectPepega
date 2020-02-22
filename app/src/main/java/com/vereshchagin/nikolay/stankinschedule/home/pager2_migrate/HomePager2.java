package com.vereshchagin.nikolay.stankinschedule.home.pager2_migrate;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.vereshchagin.nikolay.stankinschedule.schedule.model.pair.Pair;

import java.util.ArrayList;

/**
 * Pager для просмотра расписания на главной странице.
 */
public class HomePager2 extends LinearLayout {

    private ViewPager2 mDayTitlePager;
    private ViewPager2 mDayPairsPager;
    private TabLayout mDayTabLayout;

    private HomePager2TitleAdapter mTitleAdapter;
    private HomePager2PairsAdapter mPairsAdapter;

    public HomePager2(Context context) {
        super(context);
        initialization(context);
    }

    public HomePager2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialization(context);
    }

    public HomePager2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialization(context);
    }

    public HomePager2(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialization(context);
    }

    private void initialization(@NonNull Context context) {
        setOrientation(VERTICAL);

        mDayTitlePager = new ViewPager2(context);
        mDayPairsPager = new ViewPager2(context);
        mDayTabLayout = new TabLayout(context);

        mDayTitlePager.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mDayPairsPager.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mDayTabLayout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        addView(mDayTitlePager);
        addView(mDayTabLayout);
        addView(mDayPairsPager);

        mTitleAdapter = new HomePager2TitleAdapter();
        mDayTitlePager.setAdapter(mTitleAdapter);

        mPairsAdapter = new HomePager2PairsAdapter(context);
        mDayPairsPager.setAdapter(mPairsAdapter);

        new TabLayoutMediator(mDayTabLayout, mDayPairsPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                mDayPairsPager.setCurrentItem(position, true);
            }
        }).attach();
    }

    public void update(ArrayList<String> titleData, ArrayList<ArrayList<Pair>> pairsData) {
        mTitleAdapter.update(titleData);
        mPairsAdapter.update(pairsData);
    }

    private float createDipUnitSize(int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
