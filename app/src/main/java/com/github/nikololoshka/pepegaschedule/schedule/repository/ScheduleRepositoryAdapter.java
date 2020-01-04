package com.github.nikololoshka.pepegaschedule.schedule.repository;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nikololoshka.pepegaschedule.R;

import java.util.ArrayList;

/**
 * Адаптер для отображения расписаний в репозитории.
 */
public class ScheduleRepositoryAdapter
        extends RecyclerView.Adapter<ScheduleRepositoryAdapter.ScheduleRepositoryHolder> {

    private ArrayList<String> mNamesLoaded;
    private ArrayList<String> mPathsLoaded;

    private ArrayList<String> mNamesFilter;
    private ArrayList<String> mPathsFilter;

    final private OnRepositoryClickListener mListener;

    public interface OnRepositoryClickListener {
        void onScheduleItemClicked(String name, String path);
    }

    ScheduleRepositoryAdapter(OnRepositoryClickListener listener) {
        mNamesLoaded = new ArrayList<>();
        mPathsLoaded = new ArrayList<>();
        mNamesFilter = mNamesLoaded;
        mPathsFilter = mPathsLoaded;

        mListener = listener;
    }

    public void update(ArrayList<String> names, ArrayList<String> paths) {
        mNamesLoaded = names;
        mPathsLoaded = paths;

        filter("");
    }

    /**
     * Показывает только элементы удовлетворяющие запросу.
     * @param query запрос.
     */
    void filter(String query) {
        if (query.isEmpty()) {
            mNamesFilter = mNamesLoaded;
            mPathsFilter = mPathsLoaded;

            notifyDataSetChanged();
            return;
        }

        mNamesFilter = new ArrayList<>();
        mPathsFilter = new ArrayList<>();

        String right = query.toLowerCase();
        for (int i = 0; i < mNamesLoaded.size(); i++) {
            String left = mNamesLoaded.get(i).toLowerCase();

            if (left.contains(right)) {
                mNamesFilter.add(mNamesLoaded.get(i));
                mPathsFilter.add(mPathsLoaded.get(i));
            }
        }

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
        holder.bind(mNamesFilter.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mNamesFilter.size();
    }

    /**
     * Расписание, отображаемое в RecyclerView.
     */
    class ScheduleRepositoryHolder extends RecyclerView.ViewHolder {

        private TextView mTitle;
        private int mNumber;

        ScheduleRepositoryHolder(@NonNull final View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.schedule_repository_item_text);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onScheduleItemClicked(mNamesFilter.get(mNumber),
                            mPathsFilter.get(mNumber));
                }
            });
        }

        /**
         * Обновляет данные в элементе.
         * @param title название расписания.
         * @param number номер расписания в полученном списке.
         */
        void bind(String title, int number) {
            mTitle.setText(title);
            mNumber = number;
        }
    }
}
