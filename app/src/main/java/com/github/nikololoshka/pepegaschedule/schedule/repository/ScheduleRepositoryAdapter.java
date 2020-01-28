package com.github.nikololoshka.pepegaschedule.schedule.repository;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nikololoshka.pepegaschedule.R;

import java.util.List;

/**
 * Адаптер для отображения расписаний в репозитории.
 */
public class ScheduleRepositoryAdapter
        extends RecyclerView.Adapter<ScheduleRepositoryAdapter.ScheduleRepositoryHolder> {

    private AsyncListDiffer<RepositoryItem> mDiffer;

    final private OnRepositoryClickListener mListener;

    /**
     * Listener для репозитория.
     */
    public interface OnRepositoryClickListener {
        /**
         * Вызывается если элемент был нажат.
         * @param item нажатый элемент.
         */
        void onRepositoryItemClicked(@NonNull RepositoryItem item);
    }

    ScheduleRepositoryAdapter(@NonNull OnRepositoryClickListener listener) {
        mDiffer = new AsyncListDiffer<>(this, new DiffUtil.ItemCallback<RepositoryItem>() {
            @Override
            public boolean areItemsTheSame(@NonNull RepositoryItem oldItem, @NonNull RepositoryItem newItem) {
                return oldItem.name().equals(newItem.name());
            }

            @Override
            public boolean areContentsTheSame(@NonNull RepositoryItem oldItem, @NonNull RepositoryItem newItem) {
                return oldItem.equals(newItem);
            }
        });

        mListener = listener;
    }


    /**
     * Обновляе данные в адапторе.
     * @param data данные.
     */
    void submitList(@Nullable List<RepositoryItem> data) {
        mDiffer.submitList(data);
    }

    @NonNull
    @Override
    public ScheduleRepositoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_schedule_repository, parent, false);
        return new ScheduleRepositoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleRepositoryHolder holder, int position) {
        holder.bind(mDiffer.getCurrentList().get(position).name());
    }

    @Override
    public int getItemCount() {
        return mDiffer.getCurrentList().size();
    }

    /**
     * Расписание, отображаемое в RecyclerView.
     */
    class ScheduleRepositoryHolder extends RecyclerView.ViewHolder {

        private TextView mScheduleNameTextView;

        ScheduleRepositoryHolder(@NonNull final View itemView) {
            super(itemView);

            mScheduleNameTextView = itemView.findViewById(R.id.schedule_repository_item_text);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onRepositoryItemClicked(mDiffer.getCurrentList().get(getAdapterPosition()));
                }
            });
        }

        /**
         * Обновляет данные в элементе.
         * @param scheduleName название расписания.
         */
        void bind(@NonNull String scheduleName) {
            mScheduleNameTextView.setText(scheduleName);
        }
    }
}
