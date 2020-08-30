package com.vereshchagin.nikolay.stankinschedule.ui.home.pager;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.vereshchagin.nikolay.stankinschedule.R;
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Pair;

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
        setDividerDrawable(ContextCompat.getDrawable(context, R.drawable.divider));
        setShowDividers(SHOW_DIVIDER_END);

        mTitlePager = new HomePagerTitlePager(context);
        mPairsPager = new HomePagerPairsPager(context);
        mTabLayout = new TabLayout(context);

        mTitlePager.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mPairsPager.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mTabLayout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, createPixelSize(1)));

        addView(mTitlePager);
        addView(mTabLayout);
        addView(mPairsPager);

        mTitleAdapter = new HomePagerTitleAdapter();
        mTitlePager.setAdapter(mTitleAdapter);

        mPairsAdapter = new HomePagerPairsAdapter();
        mPairsPager.setAdapter(mPairsAdapter);

        mTabLayout.setupWithViewPager(mPairsPager, true);
        mTabLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorDivider));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);

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
                    mPairsPager.setCurrentItem(mTitlePager.getCurrentItem(), false);
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
                    mTitlePager.setCurrentItem(mPairsPager.getCurrentItem(), false);
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
        mPairsAdapter = new HomePagerPairsAdapter();
        mPairsPager.setAdapter(mPairsAdapter);

        mPairsAdapter.update(pairsData);
        mTitleAdapter.update(titleData);

        mPairsPager.remeasure();
        mTitlePager.remeasure();

        mPairsPager.setCurrentItem((pairsData.size() - 1) / 2);
        mTitlePager.setCurrentItem((titleData.size() - 1) / 2);
    }

    private int createPixelSize(int dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics()));
    }
}

