package com.github.nikololoshka.pepegaschedule.modulejournal.view.paging;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nikololoshka.pepegaschedule.R;
import com.github.nikololoshka.pepegaschedule.modulejournal.network.ModuleJournalError;
import com.github.nikololoshka.pepegaschedule.modulejournal.view.model.SemestersMarks;
import com.github.nikololoshka.pepegaschedule.utils.CommonUtils;
import com.github.nikololoshka.pepegaschedule.utils.StretchTable;

/**
 * Адаптер для модульного журнала с семестрами с оценками.
 */
public class SemestersAdapter extends PagedListAdapter<SemestersMarks, SemestersAdapter.SemestersHolder> {


    public SemestersAdapter() {
        super(new DiffUtil.ItemCallback<SemestersMarks>() {
            @Override
            public boolean areItemsTheSame(@NonNull SemestersMarks oldItem, @NonNull SemestersMarks newItem) {
                return oldItem == newItem;
            }

            @Override
            public boolean areContentsTheSame(@NonNull SemestersMarks oldItem, @NonNull SemestersMarks newItem) {
                return oldItem.equals(newItem);
            }
        });
    }

    @NonNull
    @Override
    public SemestersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_mj_semester, parent, false);
        return new SemestersHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SemestersHolder holder, int position) {
        holder.bind(getItem(position));
    }

    /**
     * Holder семетра с оценками. Представляет из себя таблицу.
     */
    class SemestersHolder extends RecyclerView.ViewHolder {

        private static final int MARKS_LAYOUT = 1;
        private static final int LOADING_LAYOUT = 2;
        private static final int ERROR_LAYOUT = 3;

        private View mLoadingView;
        private TextView mTimeView;
        private StretchTable mMarksTable;

        private TextView mErrorTitleView;
        private TextView mErrorDescriptionView;

        SemestersHolder(@NonNull View itemView) {
            super(itemView);

            mLoadingView = itemView.findViewById(R.id.progress_circular);
            mTimeView = itemView.findViewById(R.id.mj_semester_time);
            mMarksTable = itemView.findViewById(R.id.mj_marks_table);

            mErrorTitleView = itemView.findViewById(R.id.mj_error_title);
            mErrorDescriptionView = itemView.findViewById(R.id.mj_error_description);
        }

        /**
         * Обновляет данные в таблице.
         * @param marks семестр с оценками.
         */
        void bind(@Nullable SemestersMarks marks) {
            if (marks == null) {
                showView(LOADING_LAYOUT);
                return;
            }

            ModuleJournalError error = marks.error();
            if (error != null) {
                showView(ERROR_LAYOUT);
                mErrorTitleView.setText(error.errorTitle());
                mErrorDescriptionView.setText(error.errorDescription());
                return;
            }

            mTimeView.setText(mTimeView.getContext().getString(R.string.mj_last_update,
                    CommonUtils.of(marks.time(), "hh:mm:ss dd.MM.yyyy")));
            showView(MARKS_LAYOUT);

            MarksTableAdapter adapter2 = new MarksTableAdapter();
            adapter2.addItems(marks.createColumnsData(), marks.createRowsData(), marks.createCellsData());
            mMarksTable.setAdapter(adapter2);
        }

        void showView(int layout) {
            switch (layout) {
                case LOADING_LAYOUT: {
                    mLoadingView.setVisibility(View.VISIBLE);

                    mMarksTable.setVisibility(View.GONE);
                    mTimeView.setVisibility(View.GONE);

                    mErrorTitleView.setVisibility(View.GONE);
                    mErrorDescriptionView.setVisibility(View.GONE);
                    break;
                }
                case MARKS_LAYOUT: {
                    mLoadingView.setVisibility(View.GONE);

                    mMarksTable.setVisibility(View.VISIBLE);
                    mTimeView.setVisibility(View.VISIBLE);

                    mErrorTitleView.setVisibility(View.GONE);
                    mErrorDescriptionView.setVisibility(View.GONE);
                    break;
                }
                case ERROR_LAYOUT: {
                    mLoadingView.setVisibility(View.GONE);

                    mMarksTable.setVisibility(View.GONE);
                    mTimeView.setVisibility(View.GONE);

                    mErrorTitleView.setVisibility(View.GONE);
                    mErrorDescriptionView.setVisibility(View.GONE);
                    break;
                }
            }
        }
    }
}
