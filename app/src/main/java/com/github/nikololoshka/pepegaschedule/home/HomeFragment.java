package com.github.nikololoshka.pepegaschedule.home;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;

import com.github.nikololoshka.pepegaschedule.R;
import com.github.nikololoshka.pepegaschedule.home.pager.HomeDayPairsAdapter;
import com.github.nikololoshka.pepegaschedule.home.pager.HomeDayPairsPager;
import com.github.nikololoshka.pepegaschedule.home.pager.HomeDayTitlesAdapter;
import com.github.nikololoshka.pepegaschedule.home.pager.HomeDayTitlesPager;
import com.github.nikololoshka.pepegaschedule.settings.SchedulePreference;
import com.github.nikololoshka.pepegaschedule.utils.StatefulLayout;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import static com.github.nikololoshka.pepegaschedule.schedule.view.ScheduleViewFragment.ARG_SCHEDULE_NAME;
import static com.github.nikololoshka.pepegaschedule.schedule.view.ScheduleViewFragment.ARG_SCHEDULE_PATH;


public class HomeFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<HomeLoader.DataView> , View.OnClickListener {

    private static final int HOME_LOADER = 0;

    private StatefulLayout mStatefulLayout;
    private TextView mScheduleNameView;

    private HomeDayPairsPager mDayPager;
    private HomeDayTitlesPager mTitlePager;
    private HomeDayPairsAdapter mPagerDaysAdapter;
    private HomeDayTitlesAdapter mPagerTitlesAdapter;

    private Loader<HomeLoader.DataView> mHomeLoader;
    private HomeLoader.DataView mDataView;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mStatefulLayout = view.findViewById(R.id.stateful_layout);
        mStatefulLayout.addXMLViews();
        mStatefulLayout.setLoadState();

        mTitlePager = view.findViewById(R.id.dayTitle);
        mDayPager = view.findViewById(R.id.dayPager);

        mPagerTitlesAdapter = new HomeDayTitlesAdapter();
        mTitlePager.setAdapter(mPagerTitlesAdapter);

        mPagerDaysAdapter = new HomeDayPairsAdapter();
        mDayPager.setAdapter(mPagerDaysAdapter);

        final TabLayout dayIndicator = view.findViewById(R.id.dayIndicator);
        dayIndicator.setupWithViewPager(mDayPager, true);

        mScheduleNameView = view.findViewById(R.id.schedule_name);
        mScheduleNameView.setOnClickListener(this);

        mHomeLoader = LoaderManager.getInstance(this)
                .initLoader(HOME_LOADER, null, this);

        mTitlePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            private int mScrollState = ViewPager.SCROLL_STATE_IDLE;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                    return;
                }

                if (mScrollState != ViewPager.SCROLL_STATE_SETTLING) {
                    mDayPager.setDragging(true);
                }
                mDayPager.scrollTo(mTitlePager.getScrollX(), mTitlePager.getScrollY());
                dayIndicator.setScrollPosition(position, positionOffset, false);
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                mScrollState = state;
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    mDayPager.setCurrentItem(mTitlePager.getCurrentItem(), false);
                }
            }
        });

        mDayPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            private int mScrollState = ViewPager.SCROLL_STATE_IDLE;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                    return;
                }
                mTitlePager.scrollTo(mDayPager.getScrollX(), mDayPager.getScrollY());
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                mScrollState = state;
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    mTitlePager.setCurrentItem(mDayPager.getCurrentItem(), false);
                }
            }
        });

        return view;
    }

    @NonNull
    @Override
    public Loader<HomeLoader.DataView> onCreateLoader(int id, @Nullable Bundle args) {
        mHomeLoader = new HomeLoader(Objects.requireNonNull(getActivity()));
        updateScheduleData();
        return mHomeLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<HomeLoader.DataView> loader,
                               HomeLoader.DataView data) {
        if (data.changeCount != SchedulePreference.changeCount()) {
            updateScheduleData();
            return;
        }

        mDataView = data;
        mStatefulLayout.setState(R.id.schedule_card);
        updateScheduleView();
    }

    private void updateScheduleData() {
        mStatefulLayout.setLoadState();
        mScheduleNameView.setText("");

        mHomeLoader.forceLoad();
    }

    private void updateScheduleView() {
        if (mDataView.favorite == null || mDataView.favorite.isEmpty()) {
            mStatefulLayout.setState(R.id.no_favorite_schedule);
            mScheduleNameView.setText("");
        } else {
            mPagerDaysAdapter.setDays(mDataView.days);
            mPagerTitlesAdapter.setTitles(mDataView.titles);

            mTitlePager.update(2);
            mDayPager.update(2);

            mStatefulLayout.setState(R.id.schedule_card);
            mScheduleNameView.setText(mDataView.favorite);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<HomeLoader.DataView> loader) {
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.schedule_name) {
            if (getActivity() == null) {
                return;
            }

            Bundle args = new Bundle();
            String path = SchedulePreference.createPath(getActivity(), mDataView.favorite);
            args.putString(ARG_SCHEDULE_PATH, path);
            args.putString(ARG_SCHEDULE_NAME, mDataView.favorite);

            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host);
            navController.navigate(R.id.fromHomeFragmentToScheduleViewFragment, args);
        }
    }
}
