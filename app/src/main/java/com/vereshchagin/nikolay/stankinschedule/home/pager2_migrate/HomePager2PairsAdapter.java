package com.vereshchagin.nikolay.stankinschedule.home.pager2_migrate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vereshchagin.nikolay.stankinschedule.R;
import com.vereshchagin.nikolay.stankinschedule.schedule.model.pair.Pair;
import com.vereshchagin.nikolay.stankinschedule.schedule.view.PairCardView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Адаптер для pager'а с парами в дне.
 */
public class HomePager2PairsAdapter
        extends RecyclerView.Adapter<HomePager2PairsAdapter.HomePagerPairsHolder> {

    /**
     * Массив с парами на день.
     */
    private ArrayList<ArrayList<Pair>> mPairsData;
    private WeakReference<Context> mContext;

    HomePager2PairsAdapter(Context context) {
        super();
        mContext = new WeakReference<>(context);
        mPairsData = new ArrayList<>();
    }

    /**
     * Обновляет данные  адапторе.
     * @param pairsData массив с парами на день.
     */
    public void update(ArrayList<ArrayList<Pair>> pairsData) {
        mPairsData = pairsData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HomePagerPairsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_pager_day_pairs, parent, false);
        return new HomePagerPairsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomePagerPairsHolder holder, int position) {
        holder.bind(mPairsData.get(position));
    }

    @Override
    public int getItemCount() {
        return mPairsData.size();
    }

    /**
     * Holder для адаптера.
     */
    class HomePagerPairsHolder extends RecyclerView.ViewHolder {

        private ArrayList<PairCardView>  mPairCardViews;
        private LinearLayout mPairsLayout;
        private View mNoPairsDay;

        HomePagerPairsHolder(@NonNull View itemView) {
            super(itemView);

            mPairCardViews = new ArrayList<>();
            mPairsLayout = itemView.findViewById(R.id.pager_day_pairs);
            mNoPairsDay = itemView.findViewById(R.id.no_pairs);
        }

        /**
         * Присоединяет данные к holder'у.
         * @param pairs пара дня.
         */
        void bind(ArrayList<Pair> pairs) {
            mPairsLayout.removeAllViews();

            int i = 0;
            for (Pair pair : pairs) {
                PairCardView cardView;

                // до создаем view пары, если не хватает
                if (i < mPairCardViews.size()) {
                    cardView = mPairCardViews.get(i);
                    cardView.updatePair(pair);
                } else {
                    cardView = new PairCardView(mContext.get(), pair);
                    cardView.setClickable(false);
                    cardView.setFocusable(false);
                    mPairCardViews.add(cardView);
                }

                mPairsLayout.addView(cardView);
                i++;
            }

            // если нет пар
            if (pairs.isEmpty()) {
                mNoPairsDay.setVisibility(View.VISIBLE);
            } else {
                mNoPairsDay.setVisibility(View.GONE);
            }
        }
    }
}
