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

    private LiveData<PagedList<SemestersMarks>> mSemestersData;
    private SemestersStorage mStorage;

    public ModuleJournalViewModel() {

        mStorage = new SemestersStorage();
        SemestersDataSourcesFactory semestersFactory = new SemestersDataSourcesFactory(mStorage);

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(1)
                .setPageSize(1)
                .setPrefetchDistance(2)
                .build();

        mSemestersData = new LivePagedListBuilder<>(semestersFactory, config)
                .setFetchExecutor(Executors.newSingleThreadExecutor())
                .build();
    }

    public LiveData<PagedList<SemestersMarks>> semesters() {
        return mSemestersData;
    }

    public SemestersStorage storage() {
        return mStorage;
    }
}
