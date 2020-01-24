package com.github.nikololoshka.pepegaschedule.modulejournal.view.paging;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nikololoshka.pepegaschedule.R;
import com.github.nikololoshka.pepegaschedule.modulejournal.view.data.SemestersMarks;
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

        private View mLoadingView;
        private StretchTable mMarksTable;

        SemestersHolder(@NonNull View itemView) {
            super(itemView);

            mLoadingView = itemView.findViewById(R.id.progress_circular);
            mMarksTable = itemView.findViewById(R.id.mj_marks_table);
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

            showView(MARKS_LAYOUT);

            MarksTableAdapter adapter2 = new MarksTableAdapter();
            adapter2.addItems(marks.createColumnsData(), marks.createRowsData(), marks.createCellsData());
            mMarksTable.setAdapter(adapter2);
        }

        void showView(int layout) {
            switch (layout) {
                case LOADING_LAYOUT:
                    mLoadingView.setVisibility(View.VISIBLE);
                    mMarksTable.setVisibility(View.GONE);
                    break;
                case MARKS_LAYOUT:
                    mLoadingView.setVisibility(View.GONE);
                    mMarksTable.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }
}
