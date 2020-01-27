package com.github.nikololoshka.pepegaschedule.modulejournal.view;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.github.nikololoshka.pepegaschedule.modulejournal.view.model.SemestersMarks;
import com.github.nikololoshka.pepegaschedule.modulejournal.view.paging.SemestersDataSourcesFactory;
import com.github.nikololoshka.pepegaschedule.modulejournal.view.paging.SemestersStorage;

import java.util.concurrent.Executors;

/**
 * ViewModel для хранения PagedList с семестрами.
 */
public class ModuleJournalViewModel extends ViewModel {

    private LiveData<PagedList<SemestersMarks>> mSemestersLiveData;
    private SemestersStorage mSemestersStorage;

    public ModuleJournalViewModel() {

        mSemestersStorage = new SemestersStorage();
        SemestersDataSourcesFactory semestersFactory = new SemestersDataSourcesFactory(mSemestersStorage);

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(1)
                .setPageSize(1)
                .setPrefetchDistance(2)
                .build();

        mSemestersLiveData = new LivePagedListBuilder<>(semestersFactory, config)
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
