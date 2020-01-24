package com.github.nikololoshka.pepegaschedule.modulejournal.view.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.github.nikololoshka.pepegaschedule.modulejournal.view.data.SemestersMarks;
import com.github.nikololoshka.pepegaschedule.modulejournal.view.paging.SemestersDataSourcesFactory;
import com.github.nikololoshka.pepegaschedule.modulejournal.view.paging.SemestersStorage;

import java.util.concurrent.Executors;

public class ModuleJournalViewModel extends ViewModel {

    private LiveData<PagedList<SemestersMarks>> mSemestersLiveData;
    private SemestersDataSourcesFactory mSemestersFactory;
    private SemestersStorage mSemestersStorage;

    ModuleJournalViewModel() {

        mSemestersStorage = new SemestersStorage();
        mSemestersFactory = new SemestersDataSourcesFactory(mSemestersStorage);

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(1)
                .setPageSize(1)
                .setPrefetchDistance(0)
                .build();

        mSemestersLiveData = new LivePagedListBuilder<>(mSemestersFactory, config)
                .setFetchExecutor(Executors.newSingleThreadExecutor())
                .build();
    }

    public LiveData<PagedList<SemestersMarks>> semestersLiveData() {
        return mSemestersLiveData;
    }

    public SemestersStorage semestersStorage() {
        return mSemestersStorage;
    }
}
