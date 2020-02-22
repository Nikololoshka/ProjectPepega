package com.vereshchagin.nikolay.stankinschedule.schedule.editor.pair.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.vereshchagin.nikolay.stankinschedule.R;
import com.vereshchagin.nikolay.stankinschedule.schedule.model.pair.DateItem;

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
    private AsyncListDiffer<DateItem> mDiffer;
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
        mListener = listener;
        mEveryWeekSuffix = everyWeekSuffix;
        mThroughWeekSuffix = throughWeekSuffix;

        mDiffer = new AsyncListDiffer<>(this, new DiffUtil.ItemCallback<DateItem>() {
            @Override
            public boolean areItemsTheSame(@NonNull DateItem oldItem, @NonNull DateItem newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areContentsTheSame(@NonNull DateItem oldItem, @NonNull DateItem newItem) {
                return oldItem.equals(newItem);
            }
        });
    }

    /**
     * Обновляет данные в адапторе.
     * @param data новые данные.
     */
    public void submitList(@NonNull List<DateItem> data) {
        mDiffer.submitList(data);
    }

    /**
     * Обновляет данные в адапторе.
     * @param data новые данные.
     * @param refreshPosition элемент, необходимый перерисовать.
     */
    public void submitList(@NonNull List<DateItem> data, final int refreshPosition) {
        mDiffer.submitList(data, new Runnable() {
            @Override
            public void run() {
                notifyItemChanged(refreshPosition);
            }
        });
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
        holder.bind(mDiffer.getCurrentList().get(position));
    }

    @Override
    public int getItemCount() {
        return mDiffer.getCurrentList().size();
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
         * Обновялет данные в элементе.
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
