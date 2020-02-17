package com.github.nikololoshka.pepegaschedule.modulejournal.view.paging;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;
import androidx.paging.PositionalDataSource;

import com.github.nikololoshka.pepegaschedule.BuildConfig;
import com.github.nikololoshka.pepegaschedule.modulejournal.view.model.SemesterMarks;

import java.util.List;

/**
 * Загружает данные для адаптера.
 */
public class SemestersDataSources extends PositionalDataSource<SemesterMarks> {

    private static final String TAG = "SemestersDSLog";

    /**
     * Хранилище с семестрами.
     */
    private final SemestersStorage mSemestersStorage;

    private SemestersDataSources(@NonNull SemestersStorage semestersStorage) {
        mSemestersStorage = semestersStorage;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<SemesterMarks> callback) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, String.format("loadInitial, requestedStartPosition = %d, requestedLoadSize = %d",
                    params.requestedStartPosition, params.requestedLoadSize));
        }

        int pos = mSemestersStorage.semestersCount() - 1;
        List<SemesterMarks> marks = mSemestersStorage.loadData(pos);
        if (params.placeholdersEnabled) {
            callback.onResult(marks, pos, mSemestersStorage.semestersCount());
        } else {
            callback.onResult(marks, pos);
        }
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<SemesterMarks> callback) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, String.format("loadRange, startPosition = %d, loadSize = %d",
                    params.startPosition, params.loadSize));
        }

        List<SemesterMarks> marks = mSemestersStorage.loadData(params.startPosition);
        callback.onResult(marks);
    }

    /**
     * Фабрика для создания источника данных семестров с оценками.
     */
    public static class Factory extends DataSource.Factory<Integer, SemesterMarks> {

        private final SemestersStorage mStorage;

        public Factory(@NonNull SemestersStorage storage) {
            mStorage = storage;
        }

        @NonNull
        @Override
        public DataSource<Integer, SemesterMarks> create() {
            return new SemestersDataSources(mStorage);
        }
    }
}
