package com.vereshchagin.nikolay.stankinschedule.ui.modulejournal.view.paging;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.vereshchagin.nikolay.stankinschedule.R;
import com.vereshchagin.nikolay.stankinschedule.ui.modulejournal.view.model.SemesterMarks;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Таблица с растягивающися столбцами. Сначало растягивается 1 столбец, затем
 * если все строки у первого столбца помещаются, то растягиваюся другие столбцы
 * поровну. Используется для отображения оценок.
 */
public class MarksTable extends TableLayout {

    private static final String TAG = "StretchTableLog";

    private ArrayList<TableRow> mTableRows;

    private View mCornerHolder;
    private ArrayList<ColumnHeaderHolder> mColumnHolders;
    private ArrayList<RowHeaderHolder> mRowHolders;
    private ArrayList<ArrayList<MarkCellHolder>> mCellHolders;

    private List<String> mColumnsData;
    private List<String> mRowsData;
    private List<List<String>> mCellsData;

    public MarksTable(Context context) {
        super(context);
        initialization(context);
    }

    public MarksTable(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialization(context);
    }

    private void initialization(@NonNull Context context) {
        mTableRows = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            TableRow tableRow = new TableRow(context);
            TableLayout.LayoutParams params = new TableLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            tableRow.setLayoutParams(params);

            mTableRows.add(tableRow);
            addView(tableRow);
        }

        mColumnHolders = new ArrayList<>(10);
        mRowHolders = new ArrayList<>(10);
        mCellHolders = new ArrayList<>(10);

        mColumnsData = new ArrayList<>();
        mRowsData = new ArrayList<>();
        mCellsData = new ArrayList<>();

        // предварительная инициализация
//        for (int i = 0; i < 10; i++) {
//            mRowsData.add("");
//
//            ArrayList<String> row = new ArrayList<>(6);
//            for (int j = 0; j < 6; j++) {
//                row.add("");
//            }
//            mCellsData.add(row);
//        }
//
//        for (int j = 0; j < 6; j++) {
//            mColumnsData.add("");
//        }
//
//        rebind();
    }

    /**
     * Обновляет таблицу.
     */
    private void rebind() {
        for (int i = 0; i < rowCount() + 1; i++) {

            TableRow tableRow;
            if (i >= mTableRows.size()) {
                tableRow = new TableRow(getContext());
                TableLayout.LayoutParams params = new TableLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                tableRow.setLayoutParams(params);

                mTableRows.add(tableRow);
                addView(tableRow);
            } else {
                tableRow = mTableRows.get(i);
            }

            ArrayList<MarkCellHolder> rowCells = null;
            if (i > 0) {
                if (i - 1 >= mCellHolders.size()) {
                    rowCells = new ArrayList<>(columnCount());
                    mCellHolders.add(rowCells);
                }
                rowCells = mCellHolders.get(i - 1);
            }

            for (int j = 0; j < columnCount() + 1; j++) {
                // верхний левый уголок
                if (i == 0 && j == 0) {
                    if (mCornerHolder == null) {
                        mCornerHolder = createCorner(tableRow);

                        tableRow.addView(mCornerHolder);
                    }
                    // заголовки столбцов
                } else if (i == 0) {
                    if (j - 1 >= mColumnHolders.size()) {
                        ColumnHeaderHolder holder = createColumnHeader(tableRow);
                        bindColumnHeader(holder, j - 1);

                        mColumnHolders.add(holder);
                        tableRow.addView(holder.itemView());
                    } else {
                        bindColumnHeader(mColumnHolders.get(j - 1), j - 1);
                    }
                    // заголовки строк
                } else if (j == 0) {
                    if (i - 1 >= mRowHolders.size()) {
                        RowHeaderHolder holder = createRowHeader(tableRow);
                        bindRowHeader(holder, i - 1);

                        mRowHolders.add(holder);
                        tableRow.addView(holder.itemView());
                    } else {
                        bindRowHeader(mRowHolders.get(i - 1), i - 1);
                    }
                    // ячейки
                } else {
                    if (j - 1 >= rowCells.size()) {
                        MarkCellHolder holder = createCell(tableRow);
                        bindCell(holder, i - 1, j - 1);

                        rowCells.add(holder);
                        tableRow.addView(holder.itemView());
                    } else {
                        bindCell(rowCells.get(j - 1), i - 1, j - 1);
                    }
                }
            }

            // убираем лишнии столбцы
            if (rowCells != null && columnCount() < rowCells.size()) {
                // DEBUG:
                // Условие: 4 < 6
                // Список: [0 1 2 3 4 5]
                // Индекс: 4 удаляет 4 и т.д.
                ListIterator iterator = rowCells.listIterator(columnCount());
                while (iterator.hasNext()) {
                    TableHolder holder = (TableHolder) iterator.next();
                    tableRow.removeView(holder.itemView());

                    iterator.remove();
                }
            }
        }

        // убираем лишнии строки
        if (rowCount() + 1 < mTableRows.size()) {
            // DEBUG:
            // Условие: 10 + 1 < 12 (кол-во строк вместе с заголовком)
            // Список: [0 1 2 3 4 5 6 7 8 9 10 11]
            // Индекс: 10 + 1 удаляет 11 элемент и т.д.
            ListIterator iterator = mTableRows.listIterator(rowCount() + 1);
            while (iterator.hasNext()) {
                TableRow tableRow = (TableRow) iterator.next();
                tableRow.removeAllViews();
                removeView(tableRow);

                iterator.remove();
            }
        }

        setColumnShrinkable(0, true);
        for (int i = 1; i < columnCount() + 1; i++) {
            setColumnStretchable(i, true);
        }
    }

    /**
     * Создает holder столбца.
     * @param parent контейнер для view.
     * @return holder столбца.
     */
    @NonNull
    public ColumnHeaderHolder createColumnHeader(@NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_mj_marks_column_header, parent, false);
        return new ColumnHeaderHolder(view);
    }

    /**
     * Обновляет данные в holder'е столбца.
     * @param holder holder строки.
     * @param column номер столбца.
     */
    public void bindColumnHeader(@NonNull ColumnHeaderHolder holder, int column) {
        holder.bind(mColumnsData.get(column));
    }

    /**
     * Создает holder строки.
     * @param parent контейнер для view.
     * @return holder строки.
     */
    @NonNull
    public RowHeaderHolder createRowHeader(@NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_mj_marks_row_header, parent, false);
        return new RowHeaderHolder(view);
    }

    /**
     * Обновляет данные в holder'е строки.
     * @param holder holder строки.
     * @param row номер строки.
     */
    public void bindRowHeader(@NonNull RowHeaderHolder holder, int row) {
        holder.bind(mRowsData.get(row));
    }

    /**
     * Создает holder ячейки.
     * @param parent контейнер для view.
     * @return holder ячейки.
     */
    @NonNull
    public MarkCellHolder createCell(@NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_mj_marks_cell, parent, false);
        return new MarkCellHolder(view);
    }

    /**
     * Обновляет данные в holder'е ячейки.
     * @param holder holder ячейки.
     * @param row номер строки.
     * @param column номер столбца.
     */
    public void bindCell(@NonNull MarkCellHolder holder, int row, int column) {
        holder.bind(mCellsData.get(row).get(column));
    }

    /**
     * Создает view углового элемента таблицы.
     * @param parent контейнер для view.
     * @return view углового элемента.
     */
    @NonNull
    public View createCorner(@NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return inflater.inflate(R.layout.item_mj_marks_corner, parent, false);
    }

    /**
     * Обновляет данные в таблице.
     * @param columns заголовки столбцов.
     * @param rows заголовки строк.
     * @param cells ячейки.
     */
    void submitLists(@NonNull List<String> columns, @NonNull List<String> rows, @NonNull List<List<String>> cells) {
        mColumnsData = columns;
        mRowsData = rows;
        mCellsData = cells;

        rebind();
    }

    /**
     * @return количество столбцов в таблице.
     */
    public int columnCount() {
        return mColumnsData.size();
    }

    /**
     * @return количество строк в таблице.
     */
    public int rowCount() {
        return mRowsData.size();
    }

    /**
     * Holder элемента таблицы.
     */
    private class TableHolder {

        @NonNull
        private View mItemView;

        TableHolder(@NonNull View itemView) {
            mItemView = itemView;
        }

        @NonNull
        public View itemView() {
            return mItemView;
        }
    }

    /**
     * Holder заголовка столбца таблицы.
     */
    private class ColumnHeaderHolder extends TableHolder {

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
    private class RowHeaderHolder extends TableHolder {

        private TextView mTextView;

        RowHeaderHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.mj_marks_row);
        }

        void bind(@NonNull String text) {
            mTextView.setText(text);

            if (text.equals(SemesterMarks.ACCUMULATED_RATING) || text.equals(SemesterMarks.RATING)) {
                mTextView.setTypeface(null, Typeface.BOLD);
            } else {
                mTextView.setTypeface(null, Typeface.NORMAL);
            }
        }
    }

    /**
     * Holder ячейки таблицы.
     */
    private class MarkCellHolder extends TableHolder {

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
