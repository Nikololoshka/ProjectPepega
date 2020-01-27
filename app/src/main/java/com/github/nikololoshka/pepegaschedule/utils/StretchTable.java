package com.github.nikololoshka.pepegaschedule.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Таблица с растягивающися столбцами. Сначало растягивается 1 столбец, затем
 * если все строки у первого столбца помещаются, то растягиваюся другие столбцы
 * поровну.
 */
public class StretchTable extends TableLayout {

    private static final String TAG = "StretchTableLog";

    private StretchTableAdapter<StretchTableHolder, StretchTableHolder, StretchTableHolder> mAdapter;

    private ArrayList<TableRow> mTableRows;

    private View mCornerView;
    private ArrayList<StretchTableHolder> mColumnHeaders;
    private ArrayList<StretchTableHolder> mRowHeaders;
    private ArrayList<ArrayList<StretchTableHolder>> mCells;

    public StretchTable(Context context) {
        super(context);
        initialization(context);
    }

    public StretchTable(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialization(context);
    }

    private void initialization(@NonNull Context context) {
        mTableRows = new ArrayList<>();

        mColumnHeaders = new ArrayList<>();
        mRowHeaders = new ArrayList<>();
        mCells = new ArrayList<>();
    }

    public void setAdapter(StretchTableAdapter adapter) {
        mAdapter = adapter;
        mAdapter.setTable(this);

        rebind();
    }

    void rebind() {
        for (int i = 0; i < mAdapter.rowCount() + 1; i++) {

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

            ArrayList<StretchTableHolder> rowCells = null;
            if (i > 0) {
                if (i - 1 >= mCells.size()) {
                    rowCells = new ArrayList<>(mAdapter.columnCount());
                    mCells.add(rowCells);
                }
                rowCells = mCells.get(i - 1);
            }

            for (int j = 0; j < mAdapter.columnCount() + 1; j++) {
                // верхний левый уголок
                if (i == 0 && j == 0) {
                    if (mCornerView == null) {
                        mCornerView = mAdapter.onCreateCorner(tableRow);

                        tableRow.addView(mCornerView);
                    }
                    // заголовки столбцов
                } else if (i == 0) {
                    if (j - 1 >= mColumnHeaders.size()) {
                        StretchTableHolder holder = mAdapter.onCreateColumnHeader(tableRow);
                        mAdapter.onBindColumnHeader(holder, j - 1);

                        mColumnHeaders.add(holder);
                        tableRow.addView(holder.itemView());
                    } else {
                        mAdapter.onBindColumnHeader(mColumnHeaders.get(j - 1), j - 1);
                    }
                    // заголовки строк
                } else if (j == 0) {
                    if (i - 1 >= mRowHeaders.size()) {
                        StretchTableHolder holder = mAdapter.onCreateRowHeader(tableRow);
                        mAdapter.onBindRowHeader(holder, i - 1);

                        mRowHeaders.add(holder);
                        tableRow.addView(holder.itemView());
                    } else {
                        mAdapter.onBindRowHeader(mRowHeaders.get(i - 1), i - 1);
                    }
                    // ячейки
                } else {
                    if (j - 1 >= rowCells.size()) {
                        StretchTableHolder holder = mAdapter.onCreateCell(tableRow);
                        mAdapter.onBindCell(holder, i - 1, j - 1);

                        rowCells.add(holder);
                        tableRow.addView(holder.itemView());
                    } else {
                        mAdapter.onBindCell(rowCells.get(j - 1), i - 1, j - 1);
                    }
                }
            }

            // убираем лишнии столбцы
            if (rowCells != null && mAdapter.columnCount() < rowCells.size()) {
                // DEBUG:
                // Условие: 4 < 6
                // Список: [0 1 2 3 4 5]
                // Индекс: 4 удаляет 4 и т.д.
                ListIterator iterator = rowCells.listIterator(mAdapter.columnCount());
                while (iterator.hasNext()) {
                    StretchTableHolder holder = (StretchTableHolder) iterator.next();
                    tableRow.removeView(holder.itemView());

                    iterator.remove();
                }
            }
        }

        // убираем лишнии строки
        if (mAdapter.rowCount() + 1 < mTableRows.size()) {
            // DEBUG:
            // Условие: 10 + 1 < 12 (кол-во строк вместе с заголовком)
            // Список: [0 1 2 3 4 5 6 7 8 9 10 11]
            // Индекс: 10 + 1 удаляет 11 элемент и т.д.
            ListIterator iterator = mTableRows.listIterator(mAdapter.rowCount() + 1);
            while (iterator.hasNext()) {
                TableRow tableRow = (TableRow) iterator.next();
                tableRow.removeAllViews();
                removeView(tableRow);

                iterator.remove();
            }
        }

        setColumnShrinkable(0, true);
        for (int i = 1; i < mAdapter.columnCount() + 1; i++) {
            setColumnStretchable(i, true);
        }
    }

    public static abstract class StretchTableAdapter<CH extends StretchTableHolder, RH extends StretchTableHolder, C extends StretchTableHolder> {

        @Nullable
        private WeakReference<StretchTable> mStretchTable;

        @NonNull
        public abstract CH onCreateColumnHeader(@NonNull ViewGroup parent);
        public abstract void onBindColumnHeader(@NonNull CH holder, int column);

        @NonNull
        public abstract RH onCreateRowHeader(@NonNull ViewGroup parent);
        public abstract void onBindRowHeader(@NonNull RH holder, int row);

        @NonNull
        public abstract C onCreateCell(@NonNull ViewGroup parent);
        public abstract void onBindCell(@NonNull C holder, int row, int column);

        @NonNull
        public abstract View onCreateCorner(@NonNull ViewGroup parent);

        public abstract int columnCount();
        public abstract int rowCount();

        public void notifyDataSetChanged() {
            if (mStretchTable != null && mStretchTable.get() != null) {
                mStretchTable.get().rebind();
            }
        }

        void setTable(@NonNull StretchTable table) {
            mStretchTable = new WeakReference<>(table);
        }
    }

    public static abstract class StretchTableHolder {

        private View mItemView;

        public StretchTableHolder(@NonNull View itemView) {
            mItemView = itemView;
        }

        @NonNull
        public View itemView() {
            return mItemView;
        }
    }
}
