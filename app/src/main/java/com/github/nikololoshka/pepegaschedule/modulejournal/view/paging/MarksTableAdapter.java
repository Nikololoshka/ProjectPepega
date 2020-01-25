package com.github.nikololoshka.pepegaschedule.modulejournal.view.paging;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.github.nikololoshka.pepegaschedule.R;
import com.github.nikololoshka.pepegaschedule.modulejournal.view.model.SemestersMarks;
import com.github.nikololoshka.pepegaschedule.utils.StretchTable;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class MarksTableAdapter
        extends StretchTable.StretchTableAdapter<MarksTableAdapter.ColumnHeaderHolder,
        MarksTableAdapter.RowHeaderHolder, MarksTableAdapter.MarkCellHolder> {

    private List<String> mColumns;
    private List<String> mRows;
    private List<List<String>> mCells;

    MarksTableAdapter() {
        super();

        mColumns = new ArrayList<>();
        mRows = new ArrayList<>();
        mCells = new ArrayList<>();
    }

    @NonNull
    @Override
    public ColumnHeaderHolder onCreateColumnHeader(@NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_mj_marks_column_header, parent, false);
        return new ColumnHeaderHolder(view);
    }

    @Override
    public void onBindColumnHeader(@NonNull ColumnHeaderHolder holder, int column) {
        holder.bind(mColumns.get(column));
    }

    @NonNull
    @Override
    public RowHeaderHolder onCreateRowHeader(@NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_mj_marks_row_header, parent, false);
        return new RowHeaderHolder(view);
    }

    @Override
    public void onBindRowHeader(@NonNull RowHeaderHolder holder, int row) {
        holder.bind(mRows.get(row));
    }

    @NonNull
    @Override
    public MarkCellHolder onCreateCell(@NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_mj_marks_cell, parent, false);
        return new MarkCellHolder(view);
    }

    @Override
    public void onBindCell(@NonNull MarkCellHolder holder, int row, int column) {
        holder.bind(mCells.get(row).get(column));
    }

    @NonNull
    @Override
    public View onCreateCorner(@NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return inflater.inflate(R.layout.item_mj_marks_corner, parent, false);
    }

    void addItems(@NonNull List<String> columns, @NonNull List<String> rows, @NonNull List<List<String>> cells) {
        mColumns = columns;
        mRows = rows;
        mCells = cells;
    }

    @Override
    public int columnCount() {
        return mColumns.size();
    }

    @Override
    public int rowCount() {
        return mRows.size();
    }

    /**
     * Holder заголовка столбца таблицы.
     */
    class ColumnHeaderHolder extends StretchTable.StretchTableHolder {

        private TextView mTextView;

        ColumnHeaderHolder(@NonNull View itemView) {
            super(itemView);

            mTextView = itemView.findViewById(R.id.mj_marks_column);
        }

        void bind(@NonNull String text) {
            mTextView.setText(text);
        }
    }

    /**
     * Holder заголовка строки таблицы.
     */
    class RowHeaderHolder extends StretchTable.StretchTableHolder {

        private TextView mTextView;

        RowHeaderHolder(@NonNull View itemView) {
            super(itemView);

            mTextView = itemView.findViewById(R.id.mj_marks_row);
        }

        void bind(@NonNull String text) {
            mTextView.setText(text);

            if (text.equals(SemestersMarks.ACCUMULATED_RATING) || text.equals(SemestersMarks.RATING)) {
                mTextView.setTypeface(mTextView.getTypeface(), Typeface.BOLD);
            } else {
                mTextView.setTypeface(mTextView.getTypeface(), Typeface.NORMAL);
            }
        }
    }

    /**
     * Holder ячейки таблицы.
     */
    class MarkCellHolder extends StretchTable.StretchTableHolder {

        private TextView mTextView;

        MarkCellHolder(@NonNull View itemView) {
            super(itemView);

            mTextView = itemView.findViewById(R.id.mj_marks_cell);
        }

        void bind(@NonNull String text) {
            mTextView.setText(text);

            // не ставится оценка
            if (text.isEmpty()) {
                mTextView.setBackgroundResource(R.drawable.background_mj_cell_no_mark);
            } else {
                mTextView.setBackgroundResource(R.drawable.background_mj_semester);
            }
        }
    }
}
