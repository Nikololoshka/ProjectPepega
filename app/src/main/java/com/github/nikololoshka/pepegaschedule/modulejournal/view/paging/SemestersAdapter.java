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
import com.github.nikololoshka.pepegaschedule.utils.StatefulLayout;

import java.lang.ref.WeakReference;

/**
 * Адаптер для модульного журнала с семестрами с оценками.
 */
public class SemestersAdapter extends PagedListAdapter<SemestersMarks, SemestersAdapter.SemestersHolder> {

    public interface OnSemestersListener {
        void onUpdateSemesters();
    }

    @Nullable
    private WeakReference<OnSemestersListener> mListener;

    public SemestersAdapter() {
        super(new DiffUtil.ItemCallback<SemestersMarks>() {
            @Override
            public boolean areItemsTheSame(@NonNull SemestersMarks oldItem, @NonNull SemestersMarks newItem) {
                return oldItem.time().equals(newItem.time());
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

    public void setUpdateListener(OnSemestersListener listener) {
        mListener = new WeakReference<>(listener);
    }

    /**
     * Holder семетра с оценками. Представляет из себя таблицу.
     */
    class SemestersHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private StatefulLayout mStatefulLayout;

        private MarksTable mMarksTable;

        private TextView mErrorTitleView;
        private TextView mErrorDescriptionView;

        SemestersHolder(@NonNull View itemView) {
            super(itemView);

            mStatefulLayout = itemView.findViewById(R.id.stateful_layout);
            mStatefulLayout.addXMLViews();
            mStatefulLayout.setAnimation(false);
            mStatefulLayout.setLoadState();

            mMarksTable = itemView.findViewById(R.id.mj_marks_table);

            mErrorTitleView = itemView.findViewById(R.id.mj_error_title);
            mErrorDescriptionView = itemView.findViewById(R.id.mj_error_description);

            itemView.findViewById(R.id.mj_update_marks).setOnClickListener(this);
        }

        /**
         * Обновляет данные в таблице.
         * @param marks семестр с оценками.
         */
        void bind(@Nullable SemestersMarks marks) {
            // нет оценок
            if (marks == null) {
                mStatefulLayout.setLoadState();
                return;
            }

            // ошибка при загрузке
            ModuleJournalError error = marks.error();
            if (error != null && marks.isEmpty()) {
                mStatefulLayout.setState(R.id.mj_semester_error);

                String title = error.errorTitle();
                if (title != null) {
                    mErrorTitleView.setText(title);
                } else {
                    mErrorTitleView.setText(error.errorTitleRes());
                }

                String description = error.errorDescription();
                if (description != null) {
                    mErrorDescriptionView.setText(description);
                } else {
                    mErrorDescriptionView.setText(error.errorDescriptionRes());
                }
                return;
            }

            // отображаем таблицу
            mMarksTable.submitLists(marks.createColumnsData(), marks.createRowsData(), marks.createCellsData());
            mStatefulLayout.setState(R.id.mj_semester_table);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.mj_update_marks) {
                if (mListener != null && mListener.get() != null) {
                    mListener.get().onUpdateSemesters();
                }
            }
        }
    }
}
