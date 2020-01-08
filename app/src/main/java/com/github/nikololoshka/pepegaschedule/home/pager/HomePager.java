package com.github.nikololoshka.pepegaschedule.home.pager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.github.nikololoshka.pepegaschedule.schedule.pair.Pair;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import static androidx.viewpager.widget.ViewPager.SCROLL_STATE_SETTLING;
import static androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_IDLE;

/**
 * Pager для просмотра пар на несколько дней вперед.
 */
public class HomePager extends LinearLayout {

    private HomePagerTitlePager mTitlePager;
    private HomePagerPairsPager mPairsPager;
    private TabLayout mTabLayout;

    private HomePagerTitleAdapter mTitleAdapter;
    private HomePagerPairsAdapter mPairsAdapter;

    public HomePager(Context context) {
        super(context);
        initialization(context);
    }

    public HomePager(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialization(context);
    }

    public HomePager(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialization(context);
    }

    public HomePager(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialization(context);
    }

    /**
     * Инициализирует HomePager.
     * @param context контекст.
     */
    private void initialization(@NonNull Context context) {
        setOrientation(VERTICAL);

        mTitlePager = new HomePagerTitlePager(context);
        mPairsPager = new HomePagerPairsPager(context);
        mTabLayout = new TabLayout(context);

        mTitlePager.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mPairsPager.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mTabLayout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        addView(mTitlePager);
        addView(mTabLayout);
        addView(mPairsPager);

        mTitleAdapter = new HomePagerTitleAdapter();
        mTitlePager.setAdapter(mTitleAdapter);

        mPairsAdapter = new HomePagerPairsAdapter();
        mPairsPager.setAdapter(mPairsAdapter);

        mTabLayout.setupWithViewPager(mPairsPager, true);

        mTitlePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mPairsPager.scrollTo(mTitlePager.getScrollX(), mTitlePager.getScrollY());
                mTabLayout.setScrollPosition(position, positionOffset, false);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state != SCROLL_STATE_SETTLING) {
                    mPairsPager.setDragging(state != SCROLL_STATE_IDLE);
                }
                if (state == SCROLL_STATE_IDLE) {
                    mPairsPager.setCurrentItem(mTitlePager.getCurrentItem(), true);
                }
            }
        });

        mPairsPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mTitlePager.scrollTo(mPairsPager.getScrollX(), mPairsPager.getScrollY());
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state != SCROLL_STATE_SETTLING) {
                    mPairsPager.setDragging(state != SCROLL_STATE_IDLE);
                }
                if (state == SCROLL_STATE_IDLE) {
                    mTitlePager.setCurrentItem(mPairsPager.getCurrentItem(), true);
                }
            }
        });
    }

    /**
     * Обновляет данные в pager'ах и устанавливает отображать в
     * них центральный (средний) элемент.
     * @param titleData массив с заголовками.
     * @param pairsData массив с парами в дне.
     */
    public void update(ArrayList<String> titleData, ArrayList<ArrayList<Pair>> pairsData) {
        mTitleAdapter.update(titleData);
        mPairsAdapter.update(pairsData);

        mTitlePager.setCurrentItem((titleData.size() + 1) / 2);
        mPairsPager.setCurrentItem((pairsData.size() + 1) / 2);
    }
}
