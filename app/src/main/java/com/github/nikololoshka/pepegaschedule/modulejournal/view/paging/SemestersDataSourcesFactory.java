package com.github.nikololoshka.pepegaschedule.modulejournal.view.paging;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;

import com.github.nikololoshka.pepegaschedule.modulejournal.view.model.SemestersMarks;


public class SemestersDataSourcesFactory extends DataSource.Factory<Integer, SemestersMarks> {

    private final SemestersStorage mStorage;

    public SemestersDataSourcesFactory(@NonNull SemestersStorage storage) {
        mStorage = storage;
    }

    @NonNull
    @Override
    public DataSource<Integer, SemestersMarks> create() {
        return new SemestersDataSources(mStorage);
    }
}
