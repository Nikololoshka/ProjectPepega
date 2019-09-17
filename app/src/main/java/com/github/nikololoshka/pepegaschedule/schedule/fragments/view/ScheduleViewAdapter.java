package com.github.nikololoshka.pepegaschedule.schedule.fragments.view;

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

public class ScheduleViewAdapter
        extends RecyclerView.Adapter<ScheduleViewAdapter.ScheduleViewHolder> {

    private ArrayList<TreeSet<Pair>> mDaysPair;
    private ArrayList<String> mDaysFormat;
    private WeakReference<Context> mContext;
    private OnPairCardClickListener mListener;

    public interface OnPairCardClickListener {
        void onPairCardClicked(Pair pair);
    }

    ScheduleViewAdapter(OnPairCardClickListener listener) {
        mDaysPair = new ArrayList<>();
        mDaysFormat = new ArrayList<>();
        mListener = listener;
    }

    public void update(ArrayList<TreeSet<Pair>> daysPair, ArrayList<String> daysFormat) {
        mDaysPair = daysPair;
        mDaysFormat = daysFormat;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = new WeakReference<>(parent.getContext());
        }

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ScheduleViewHolder(inflater.inflate(R.layout.fragment_schedule_card,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        holder.bind(mDaysPair.get(position), mDaysFormat.get(position));
    }

    @Override
    public int getItemCount() {
        return mDaysPair.size();
    }

    class ScheduleViewHolder extends RecyclerView.ViewHolder {

        private ArrayList<PairCardView>  mPairCardViews;
        private LinearLayout mLinearLayout;
        private TextView mTitle;
        private TextView mEmptyDay;

        ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);

            mPairCardViews = new ArrayList<>();
            mLinearLayout = itemView.findViewById(R.id.schedule_card_values);
            mTitle = itemView.findViewById(R.id.schedule_card_title);
            mEmptyDay = itemView.findViewById(R.id.no_pairs);
        }

        void bind(TreeSet<Pair> pairs, String title) {
            mTitle.setText(title);

            mLinearLayout.removeAllViews();

            int i = 0;
            for (Pair pair : pairs) {
                PairCardView cardView;

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

                mLinearLayout.addView(cardView);
                i++;
            }

            if (pairs.isEmpty()) {
                mEmptyDay.setVisibility(View.VISIBLE);
            } else {
                mEmptyDay.setVisibility(View.GONE);
            }
        }
    }
}