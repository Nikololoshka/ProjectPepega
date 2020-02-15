package com.github.nikololoshka.pepegaschedule.modulejournal.view.paging;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;
import androidx.paging.PositionalDataSource;

import com.github.nikololoshka.pepegaschedule.BuildConfig;
import com.github.nikololoshka.pepegaschedule.modulejournal.view.model.SemestersMarks;

import java.util.List;

/**
 * Загружает данные для адаптера.
 */
public class SemestersDataSources extends PositionalDataSource<SemestersMarks> {

    private static final String TAG = "SemestersDSLog";

    /**
     * Хранилище с семестрами.
     */
    private final SemestersStorage mSemestersStorage;

    private SemestersDataSources(@NonNull SemestersStorage semestersStorage) {
        mSemestersStorage = semestersStorage;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<SemestersMarks> callback) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, String.format("loadInitial, requestedStartPosition = %d, requestedLoadSize = %d",
                    params.requestedStartPosition, params.requestedLoadSize));
        }

        int pos = mSemestersStorage.semestersCount() - 1;
        List<SemestersMarks> marks = mSemestersStorage.loadData(pos);
        if (params.placeholdersEnabled) {
            callback.onResult(marks, pos, mSemestersStorage.semestersCount());
        } else {
            callback.onResult(marks, pos);
        }
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<SemestersMarks> callback) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, String.format("loadRange, startPosition = %d, loadSize = %d",
                    params.startPosition, params.loadSize));
        }

        List<SemestersMarks> marks = mSemestersStorage.loadData(params.startPosition);
        callback.onResult(marks);
    }

    /**
     * Фабрика для создания источника данных семестров с оценками.
     */
    public static class Factory extends DataSource.Factory<Integer, SemestersMarks> {

        private final SemestersStorage mStorage;

        public Factory(@NonNull SemestersStorage storage) {
            mStorage = storage;
        }

        @NonNull
        @Override
        public DataSource<Integer, SemestersMarks> create() {
            return new SemestersDataSources(mStorage);
        }
    }
}
