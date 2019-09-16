package com.github.nikololoshka.pepegaschedule.schedule.fragments.repository;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nikololoshka.pepegaschedule.R;

import java.util.ArrayList;

public class ScheduleRepositoryAdapter
        extends RecyclerView.Adapter<ScheduleRepositoryAdapter.ScheduleRepositoryHolder> {

    private ArrayList<String> mNames;
    private ArrayList<String> mPaths;
    final private OnRepositoryClickListener mListener;

    public interface OnRepositoryClickListener {
        void onScheduleItemClicked(String name, String path);
    }

    ScheduleRepositoryAdapter(OnRepositoryClickListener listener) {
        mNames = new ArrayList<>();
        mPaths = new ArrayList<>();
        mListener = listener;
    }

    public void update(ArrayList<String> names, ArrayList<String> paths) {
        mNames = names;
        mPaths = paths;
        notifyDataSetChanged();
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
        holder.setTitle(mNames.get(position));
        holder.setNumber(position);
    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }

    class ScheduleRepositoryHolder extends RecyclerView.ViewHolder {

        private TextView mTitle;
        private int mNumber;

        ScheduleRepositoryHolder(@NonNull final View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.schedule_repository_item_text);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onScheduleItemClicked(mNames.get(mNumber), mPaths.get(mNumber));
                }
            });
        }

        void setTitle(String title) {
            mTitle.setText(title);
        }

        void setNumber(int number) {
            mNumber = number;
        }
    }
}
