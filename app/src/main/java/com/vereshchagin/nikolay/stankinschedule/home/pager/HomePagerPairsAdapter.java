package com.vereshchagin.nikolay.stankinschedule.home.pager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.vereshchagin.nikolay.stankinschedule.R;
import com.vereshchagin.nikolay.stankinschedule.schedule.model.pair.Pair;
import com.vereshchagin.nikolay.stankinschedule.schedule.view.PairCardView;

import java.util.ArrayList;

/**
 * Адаптер для отображения пар в дне.
 */
public class HomePagerPairsAdapter extends PagerAdapter {

    /**
     * Массив с парами на день.
     */
    private ArrayList<ArrayList<Pair>> mPairsData = new ArrayList<>();
    /**
     * Текущая отображаемая на соответственном pager'е позиция адаптера.
     */
    private int mCurrentPosition = -1;

    HomePagerPairsAdapter() {
        super();
    }

    /**
     * Обновляет данные  адапторе.
     * @param pairsData массив с парами на день.
     */
    public void update(@NonNull ArrayList<ArrayList<Pair>> pairsData) {
        mPairsData = pairsData;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mPairsData.size();
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);

        if (position != mCurrentPosition) {
            if (container instanceof HomePagerPairsPager) {
                HomePagerPairsPager pager = (HomePagerPairsPager) container;
                pager.updateCurrentView((View) object);
            }
            mCurrentPosition = position;
        }
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        View view = inflater.inflate(R.layout.item_pager_day_pairs, container, false);

        LinearLayout pairsLayout = view.findViewById(R.id.pager_day_pairs);
        View noPairs = view.findViewById(R.id.no_pairs);

        ArrayList<Pair> pairs = mPairsData.get(position);
        if (pairs == null || pairs.isEmpty()) {
            pairsLayout.setVisibility(View.GONE);
        } else {
            for (Pair pair : pairs) {
                PairCardView cardView = new PairCardView(container.getContext(), pair);
                cardView.setClickable(false);
                cardView.setFocusable(false);
                pairsLayout.addView(cardView);
            }
            noPairs.setVisibility(View.GONE);
        }

        // для каждой position свой container
        container.addView(view, 0);
        return view;
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
