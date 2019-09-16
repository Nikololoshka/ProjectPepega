package com.github.nikololoshka.pepegaschedule.home;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.github.nikololoshka.pepegaschedule.R;
import com.github.nikololoshka.pepegaschedule.schedule.fragments.view.PairCardView;
import com.github.nikololoshka.pepegaschedule.schedule.pair.Pair;
import com.github.nikololoshka.pepegaschedule.settings.SchedulePreference;

import java.util.Objects;


public class HomeFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<HomeLoader.DataView> {

    private static final int HOME_LOADER = 0;

    private LinearLayout mPairsView;
    private TextView mNoPairsView;
    private TextView mScheduleNameView;
    private TextView mNoFavoriteScheduleView;
    private LinearLayout mPairsContainer;
    private TextView mTodayView;
    private View mScheduleLoadView;

    private Loader<HomeLoader.DataView> mHomeLoader;
    private HomeLoader.DataView mDataView;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mTodayView = view.findViewById(R.id.schedule_card_title);

        mPairsView = view.findViewById(R.id.schedule_card);
        mNoPairsView = view.findViewById(R.id.no_pairs);
        mPairsContainer = view.findViewById(R.id.schedule_card_values);
        mScheduleNameView = view.findViewById(R.id.schedule_name);
        mNoFavoriteScheduleView = view.findViewById(R.id.no_favorite_schedule);

        mScheduleLoadView = view.findViewById(R.id.loading_fragment);
        mScheduleLoadView.setVisibility(View.GONE);

        mHomeLoader = LoaderManager.getInstance(this)
                .initLoader(HOME_LOADER, null, this);

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

        mScheduleLoadView.setVisibility(View.GONE);
        updateScheduleView();
    }

    private void updateScheduleData() {
        mScheduleLoadView.setVisibility(View.VISIBLE);
        mPairsView.setVisibility(View.GONE);
        mNoFavoriteScheduleView.setVisibility(View.GONE);
        mScheduleNameView.setText("");

        mHomeLoader.forceLoad();
    }

    private void updateScheduleView() {
        mTodayView.setText(mDataView.today);

        if (mDataView.favorite != null && mDataView.favorite.isEmpty()) {
            mNoFavoriteScheduleView.setVisibility(View.VISIBLE);
            mPairsView.setVisibility(View.GONE);
            mScheduleNameView.setText("");
            return;
        } else {
            mNoFavoriteScheduleView.setVisibility(View.GONE);
            mPairsView.setVisibility(View.VISIBLE);
            mScheduleNameView.setText(mDataView.favorite);
        }

        if (mDataView.pairs == null || mDataView.pairs.isEmpty()) {
            mNoPairsView.setVisibility(View.VISIBLE);
            return;
        } else {
            mNoPairsView.setVisibility(View.GONE);
        }

        if (getActivity() == null) {
            return;
        }

        mPairsContainer.removeAllViews();
        for (Pair pair : mDataView.pairs) {
            PairCardView cardView = new PairCardView(getActivity(), pair);
            cardView.setClickable(false);
            cardView.setFocusable(false);
            mPairsContainer.addView(cardView);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<HomeLoader.DataView> loader) {
    }
}
