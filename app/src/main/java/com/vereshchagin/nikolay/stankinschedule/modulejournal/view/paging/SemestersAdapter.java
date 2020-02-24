package com.vereshchagin.nikolay.stankinschedule.modulejournal.view.paging;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.vereshchagin.nikolay.stankinschedule.R;
import com.vereshchagin.nikolay.stankinschedule.modulejournal.network.ModuleJournalError;
import com.vereshchagin.nikolay.stankinschedule.modulejournal.view.model.SemesterMarks;
import com.vereshchagin.nikolay.stankinschedule.utils.StatefulLayout;

import java.lang.ref.WeakReference;

/**
 * Адаптер для модульного журнала с семестрами с оценками.
 */
public class SemestersAdapter extends PagedListAdapter<SemesterMarks, SemestersAdapter.SemestersHolder> {

    public interface OnSemestersListener {
        void onUpdateSemesters();
    }

    @Nullable
    private WeakReference<OnSemestersListener> mListener;

    public SemestersAdapter() {
        super(new DiffUtil.ItemCallback<SemesterMarks>() {
            @Override
            public boolean areItemsTheSame(@NonNull SemesterMarks oldItem, @NonNull SemesterMarks newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areContentsTheSame(@NonNull SemesterMarks oldItem, @NonNull SemesterMarks newItem) {
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
     * Holder семестра с оценками. Представляет собой таблицу.
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
            mStatefulLayout.setAnimation(StatefulLayout.NO_ANIMATION);
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
        void bind(@Nullable SemesterMarks marks) {
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