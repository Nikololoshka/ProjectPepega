package com.github.nikololoshka.pepegaschedule.schedule.view.paging;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nikololoshka.pepegaschedule.R;
import com.github.nikololoshka.pepegaschedule.schedule.model.pair.Pair;
import com.github.nikololoshka.pepegaschedule.schedule.view.PairCardView;
import com.github.nikololoshka.pepegaschedule.utils.CommonUtils;

import java.util.ArrayList;

/**
 * Адаптер для просмотра расписания.
 */
public class ScheduleDayItemAdapter extends PagedListAdapter<ScheduleDayItem, ScheduleDayItemAdapter.DayHolder> {

    /**
     * Интерфейс callback'а для обработки нажатия на пару.
     */
    public interface OnPairCardListener {
        /**
         * Вызывается, если была нажата пара.
         * @param pair нажатая пара.
         */
        void onPairClicked(@Nullable Pair pair);
    }

    /**
     * Listener для нажатия на пару.
     */
    private OnPairCardListener mListener;

    public ScheduleDayItemAdapter(@NonNull OnPairCardListener listener) {
        super(new DiffUtil.ItemCallback<ScheduleDayItem>() {
            @Override
            public boolean areItemsTheSame(@NonNull ScheduleDayItem oldItem, @NonNull ScheduleDayItem newItem) {
                return oldItem.day().equals(newItem.day());
            }

            @Override
            public boolean areContentsTheSame(@NonNull ScheduleDayItem oldItem, @NonNull ScheduleDayItem newItem) {
                return oldItem.equals(newItem);
            }
        });

        mListener = listener;
    }

    @NonNull
    @Override
    public ScheduleDayItemAdapter.DayHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_schedule_day_common, parent, false);
        return new DayHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleDayItemAdapter.DayHolder holder, int position) {
        holder.bind(getItem(position));
    }

    /**
     * Holder для расписания.
     */
    class DayHolder extends RecyclerView.ViewHolder {

        private ArrayList<PairCardView> mPairCardViews;
        private LinearLayout mPairsLayout;
        private TextView mTitleView;
        private View mEmptyDay;

        DayHolder(@NonNull View itemView) {
            super(itemView);

            mPairCardViews = new ArrayList<>();
            mPairsLayout = itemView.findViewById(R.id.schedule_day_pairs);
            mTitleView = itemView.findViewById(R.id.schedule_day_title);
            mEmptyDay = itemView.findViewById(R.id.no_pairs);
        }

        /**
         * Обновляет данные в holder'е.
         * @param dayItem элемент с парами на день.
         */
        void bind(@Nullable ScheduleDayItem dayItem) {
            if (dayItem == null) {
                return;
            }

            String title = CommonUtils.dateToString(dayItem.day(),
                    "EEEE, dd MMMM", CommonUtils.locale(mTitleView.getContext()));
            mTitleView.setText(CommonUtils.toTitleCase(title));

            mPairsLayout.removeAllViews();

            int i = 0;
            for (Pair pair : dayItem.pairs()) {
                PairCardView cardView;

                // до создаем view пары, если не хватает
                if (i < mPairCardViews.size()) {
                    cardView = mPairCardViews.get(i);
                    cardView.updatePair(pair);
                } else {
                    cardView = new PairCardView(itemView.getContext(), pair);
                    cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mListener.onPairClicked(((PairCardView) v).pair());
                        }
                    });
                    mPairCardViews.add(cardView);
                }

                mPairsLayout.addView(cardView);
                i++;
            }

            // если нет пар
            if (dayItem.pairs().isEmpty()) {
                mEmptyDay.setVisibility(View.VISIBLE);
                mPairsLayout.setVisibility(View.GONE);
            } else {
                mEmptyDay.setVisibility(View.GONE);
                mPairsLayout.setVisibility(View.VISIBLE);
            }
        }
    }
}
