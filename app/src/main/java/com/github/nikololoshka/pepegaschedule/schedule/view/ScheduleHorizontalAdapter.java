package com.github.nikololoshka.pepegaschedule.schedule.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nikololoshka.pepegaschedule.R;
import com.github.nikololoshka.pepegaschedule.schedule.pair.Pair;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Adapter для RecyclerView с горизонтальным отображением расписания расписанием.
 */
public class ScheduleHorizontalAdapter
        extends ScheduleViewAdapter<ScheduleHorizontalAdapter.ScheduleViewHolder> {

    private static final String TAG = "ScheduleHorAdapterTAG";

    private ArrayList<TreeSet<Pair>> mDaysPairs;
    private ArrayList<String> mDaysFormats;
    private WeakReference<Context> mContext;
    private OnPairCardCallback mListener;

    ScheduleHorizontalAdapter(OnPairCardCallback listener) {
        mDaysPairs = new ArrayList<>();
        mDaysFormats = new ArrayList<>();

        mListener = listener;
    }

    @Override
    public void update(ArrayList<TreeSet<Pair>> daysPair, ArrayList<String> daysTitles) {
        mDaysPairs = daysPair;
        mDaysFormats = daysTitles;

        notifyDataSetChanged();
    }

    @Override
    public int translateIndex(int position) {
        return position;
    }

    @Override
    public boolean scrolledNext(int firstPosition, int lastPosition, int todayPosition) {
        int firstOffset = firstPosition - todayPosition;
        int lastOffset = lastPosition - todayPosition;

        return firstOffset > 0 && lastOffset > 0;
    }

    @Override
    public boolean scrolledPrev(int firstPosition, int lastPosition, int todayPosition) {
        int firstOffset = firstPosition - todayPosition;
        int lastOffset = lastPosition - todayPosition;

        return firstOffset < 0 && lastOffset < 0;
    }

    @Override
    public void scrollTo(RecyclerView attachedRecyclerView, int position) {
        attachedRecyclerView.smoothScrollToPosition(position);
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = new WeakReference<>(parent.getContext());
        }

        // создаем
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ScheduleViewHolder(inflater.inflate(R.layout.item_schedule_day_common,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        holder.bind(mDaysPairs.get(position), mDaysFormats.get(position));
    }

    @Override
    public int getItemCount() {
        return mDaysPairs.size();
    }

    /**
     * День с парами в расписании.
     */
    class ScheduleViewHolder extends RecyclerView.ViewHolder {

        private ArrayList<PairCardView>  mPairCardViews;
        private LinearLayout mPairsLayout;
        private TextView mTitle;
        private View mEmptyDay;

        ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);

            mPairCardViews = new ArrayList<>();
            mPairsLayout = itemView.findViewById(R.id.schedule_day_pairs);
            mTitle = itemView.findViewById(R.id.schedule_day_title);
            mEmptyDay = itemView.findViewById(R.id.no_pairs);

            // разделитель не нужен, т.к. день находится в карточке
            ((LinearLayout) itemView.findViewById(R.id.day_pairs_container))
                    .setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
        }

        /**
         * Соединяем данные с view.
         * @param pairs - пары.
         * @param title - подпись дня.
         */
        void bind(TreeSet<Pair> pairs, String title) {
            mTitle.setText(title);

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
                    cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mListener.onPairCardClicked(((PairCardView) v).pair());
                        }
                    });
                    mPairCardViews.add(cardView);
                }

                mPairsLayout.addView(cardView);
                i++;
            }

            // если нет пар
            if (pairs.isEmpty()) {
                mEmptyDay.setVisibility(View.VISIBLE);
                mPairsLayout.setVisibility(View.GONE);
            } else {
                mEmptyDay.setVisibility(View.GONE);
                mPairsLayout.setVisibility(View.VISIBLE);
            }
        }
    }
}
