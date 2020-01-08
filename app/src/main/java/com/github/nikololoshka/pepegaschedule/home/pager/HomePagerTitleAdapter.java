package com.github.nikololoshka.pepegaschedule.home.pager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.github.nikololoshka.pepegaschedule.R;

import java.util.ArrayList;

/**
 * Адаптер для pager'а с заголовками дней.
 */
public class HomePagerTitleAdapter extends PagerAdapter {

    /**
     * Массив с заголовками.
     */
    private ArrayList<String> mTitleData = new ArrayList<>();;

    public HomePagerTitleAdapter() {
        super();
    }

    /**
     * Обновляет данные в адапторе.
     * @param titleData массив с заголовками.
     */
    public void update(ArrayList<String> titleData) {
        mTitleData = titleData;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mTitleData.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        View view = inflater.inflate(R.layout.item_pager_day_title, container, false);
        ((TextView) view.findViewById(R.id.pager_day_title)).setText(mTitleData.get(position));

        // для каждой position свой container
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
}
