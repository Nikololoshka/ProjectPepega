package com.github.nikololoshka.pepegaschedule.home.pager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.github.nikololoshka.pepegaschedule.R;
import com.github.nikololoshka.pepegaschedule.home.HomeLoader;

import java.util.ArrayList;

public class HomeDayTitlesAdapter extends PagerAdapter {

    private ArrayList<String> mTitles;

    public HomeDayTitlesAdapter() {
        mTitles = new ArrayList<>(HomeLoader.DAY_COUNT);
    }

    public void setTitles(ArrayList<String> titles) {
        if (titles == null) {
            return;
        }

        mTitles.clear();
        mTitles.addAll(titles);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        TextView view = (TextView) inflater.inflate(R.layout.item_day_title, container, false);
        view.setText(mTitles.get(position));

        container.addView(view, 0);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((TextView) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public int getCount() {
        return mTitles.size();
    }
}
