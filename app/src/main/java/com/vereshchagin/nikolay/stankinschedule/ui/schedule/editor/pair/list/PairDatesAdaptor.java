package com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.pair.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.DiffUtil.Callback;
import androidx.recyclerview.widget.RecyclerView;

import com.vereshchagin.nikolay.stankinschedule.R;
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Date;
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.DateItem;

import java.util.ArrayList;
import java.util.List;

public class PairDatesAdaptor extends RecyclerView.Adapter<PairDatesAdaptor.PairEditorHolder> {

    /**
     * Listener для нажатия на дату.
     */
    public interface OnDateItemClickListener {
        /**
         * Вызывается, когда была нажата дата.
         * @param position нажатая позиция.
         */
        void onDateItemClicked(int position);
    }

    /**
     * Listener нажатия.
     */
    private final OnDateItemClickListener mListener;
    /**
     * Список.
     */
    private List<DateItem> mDataList;
    /**
     * Окончание даты "к.н."
     */
    private String mEveryWeekSuffix;
    /**
     * Окончание даты "ч.н."
     */
    private String mThroughWeekSuffix;


    public PairDatesAdaptor(@NonNull OnDateItemClickListener listener,
                            @NonNull String everyWeekSuffix, @NonNull String throughWeekSuffix) {
        mDataList = new ArrayList<>();
        mListener = listener;
        mEveryWeekSuffix = everyWeekSuffix;
        mThroughWeekSuffix = throughWeekSuffix;

        new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return 0;
            }

            @Override
            public int getNewListSize() {
                return 0;
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return false;
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return false;
            }
        };
    }

    /**
     * Обновляет данные в адаптере.
     * @param date новые данные.
     */
    public void submitList(@NonNull final Date date) {
        final List<DateItem> data = new ArrayList<>(date.toList());
        DiffUtil.Callback diffUtil = new Callback() {
            @Override
            public int getOldListSize() {
                return mDataList.size();
            }

            @Override
            public int getNewListSize() {
                return data.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return mDataList.get(oldItemPosition).equals(data.get(newItemPosition));
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return mDataList.get(oldItemPosition).equals(data.get(newItemPosition));
            }
        };

        DiffUtil.DiffResult result = DiffUtil.calculateDiff(diffUtil);
        result.dispatchUpdatesTo(this);

        mDataList = data;
    }


    @NonNull
    @Override
    public PairEditorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new PairEditorHolder(inflater.inflate(R.layout.item_dates_pair,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PairEditorHolder holder, int position) {
        holder.bind(mDataList.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    /**
     * Holder для даты в списке.
     */
    class PairEditorHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mDateInfo;

        PairEditorHolder(@NonNull View itemView) {
            super(itemView);

            mDateInfo = itemView.findViewById(R.id.date_info);
            itemView.setOnClickListener(this);
        }

        /**
         * Обновляет данные в элементе.
         * @param item дата.
         */
        void bind(@NonNull DateItem item) {
            String text = item.toString();

            switch (item.frequency()) {
                case EVERY:
                    text += " " + mEveryWeekSuffix;
                    break;
                case THROUGHOUT:
                    text += " " + mThroughWeekSuffix;
                    break;
            }

            mDateInfo.setText(text);
        }

        @Override
        public void onClick(View v) {
            // нажата дата
            if (v.getId() == R.id.date_item) {
                mListener.onDateItemClicked(getAdapterPosition());
            }
        }
    }
}
