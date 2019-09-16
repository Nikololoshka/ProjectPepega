package com.github.nikololoshka.pepegaschedule.schedule.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nikololoshka.pepegaschedule.R;
import com.github.nikololoshka.pepegaschedule.schedule.pair.DateItem;

import java.util.ArrayList;

public class PairEditorAdaptor extends RecyclerView.Adapter<PairEditorAdaptor.PairEditorHolder> {

    public interface OnItemClickListener {
        void onDateItemClicked(int pos);
        void onDateItemRemoved(int pos);
    }

    private ArrayList<DateItem> mDateItems;
    private final OnItemClickListener mListener;

    PairEditorAdaptor(ArrayList<DateItem> dateItems, OnItemClickListener listener) {
        mDateItems = dateItems;
        mListener = listener;
    }

    @NonNull
    @Override
    public PairEditorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new PairEditorHolder(inflater.inflate(R.layout.item_pair_creator_date,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PairEditorHolder holder, int position) {
        holder.bind(mDateItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mDateItems.size();
    }

    class PairEditorHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private TextView mDateInfo;

        PairEditorHolder(@NonNull View itemView) {
            super(itemView);

            mDateInfo = itemView.findViewById(R.id.date_info);
            itemView.setOnClickListener(this);
            itemView.findViewById(R.id.remove_date).setOnClickListener(this);
        }

        void bind(DateItem item) {
            mDateInfo.setText(item.toString());
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.date_item:
                    mListener.onDateItemClicked(getAdapterPosition());
                    break;
                case R.id.remove_date:
                    mListener.onDateItemRemoved(getAdapterPosition());
                    break;
            }
        }
    }
}
