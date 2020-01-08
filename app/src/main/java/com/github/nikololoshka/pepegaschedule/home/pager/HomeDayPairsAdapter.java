package com.github.nikololoshka.pepegaschedule.home.pager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.github.nikololoshka.pepegaschedule.R;
import com.github.nikololoshka.pepegaschedule.home.HomeLoader;
import com.github.nikololoshka.pepegaschedule.schedule.pair.Pair;
import com.github.nikololoshka.pepegaschedule.schedule.view.PairCardView;

import java.util.ArrayList;


public class HomeDayPairsAdapter extends PagerAdapter {

    private ArrayList<ArrayList<Pair>> mDays;
    private int mCurrentPosition = -1;

    public HomeDayPairsAdapter() {
        mDays = new ArrayList<>(HomeLoader.DAY_COUNT);
    }

    @Override
    public int getCount() {
        return mDays.size();
    }

    public void setDays(ArrayList<ArrayList<Pair>> days) {
        if (days == null) {
            return;
        }

        mDays.clear();
        mDays.addAll(days);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LinearLayout layout = new LinearLayout(container.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        ArrayList<Pair> pairs = mDays.get(position);
        if (pairs == null || pairs.isEmpty()) {
            LayoutInflater inflater = LayoutInflater.from(container.getContext());
            View view = inflater.inflate(R.layout.item_no_pairs, container, false);
            layout.addView(view);
        } else {
            layout.setDividerDrawable(container.getContext().getDrawable(R.drawable.divider));
            layout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE | LinearLayout.SHOW_DIVIDER_END);
            for (Pair pair : pairs) {
                PairCardView cardView = new PairCardView(container.getContext(), pair);
                cardView.setClickable(false);
                cardView.setFocusable(false);
                layout.addView(cardView);
            }
        }

        container.addView(layout, 0);
        return layout;
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);

        if (position != mCurrentPosition) {
            HomeDayPairsPager pager = (HomeDayPairsPager) container;
            mCurrentPosition = position;

            pager.measureCurrentView((View) object);
        }
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}