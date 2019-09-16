package com.github.nikololoshka.pepegaschedule.schedule.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nikololoshka.pepegaschedule.R;

import java.util.ArrayList;
import java.util.List;

public class MySchedulesAdapter
        extends RecyclerView.Adapter<MySchedulesAdapter.MySchedulesViewHolder> {

    public interface OnItemClickListener {
        void onScheduleItemClicked(int pos);
        void onScheduleFavoriteSelected(String favorite);
    }

    private List<String> mSchedules;
    private final OnItemClickListener mClickListener;
    private String mFavoriteSchedule;

    MySchedulesAdapter(OnItemClickListener clickListener) {
        mClickListener = clickListener;
        mSchedules = new ArrayList<>();
        mFavoriteSchedule = "";
    }

    public void update(List<String> schedules, String favoriteSchedule) {
        mSchedules = schedules;
        mFavoriteSchedule = favoriteSchedule;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MySchedulesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_schedule, parent, false);
        return new MySchedulesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MySchedulesViewHolder holder, int position) {
        holder.setTitle(mSchedules.get(position));
        holder.setFavorite(mFavoriteSchedule.equals(mSchedules.get(position)));
    }

    @Override
    public int getItemCount() {
        return mSchedules.size();
    }


    class MySchedulesViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private final TextView mTitle;
        private final RadioButton mFavorite;

        private MySchedulesViewHolder(@NonNull View itemView) {
            super(itemView);

            mTitle = itemView.findViewById(R.id.schedule_info);
            mFavorite = itemView.findViewById(R.id.favorite_schedule);

            itemView.setOnClickListener(this);
            itemView.findViewById(R.id.favorite_schedule).setOnClickListener(this);
        }

        private void setTitle(String title) {
            mTitle.setText(title);
        }

        private void setFavorite(boolean check) {
            mFavorite.setChecked(check);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.schedule_item:
                    mClickListener.onScheduleItemClicked(getAdapterPosition());
                    break;
                case R.id.favorite_schedule:
                    mFavoriteSchedule = mTitle.getText().toString();
                    mClickListener.onScheduleFavoriteSelected(mFavoriteSchedule);
                    notifyDataSetChanged();
                    break;
            }
        }
    }
}
