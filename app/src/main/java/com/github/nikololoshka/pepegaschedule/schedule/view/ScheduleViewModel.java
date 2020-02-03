package com.github.nikololoshka.pepegaschedule.schedule.view;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.github.nikololoshka.pepegaschedule.schedule.view.paging.ScheduleDayItem;
import com.github.nikololoshka.pepegaschedule.schedule.view.paging.ScheduleDayItemDataSource;
import com.github.nikololoshka.pepegaschedule.schedule.view.paging.ScheduleDayItemStorage;

import java.util.concurrent.Executors;

import static com.github.nikololoshka.pepegaschedule.schedule.view.paging.ScheduleDayItemStorage.PAGE_SIZE;

/**
 * ViewModel для просмотра расписания.
 */
class ScheduleViewModel extends ViewModel {

    /**
     * Состояние загрузки расписания.
     */
    enum States {
        SUCCESSFUL,
        LOADING,
        ERROR
    }

    /**
     * Список с днями с парами.
     */
    @NonNull
    private LiveData<PagedList<ScheduleDayItem>> mScheduleDayItemData;
    /**
     * Хранилище с расписанием.
     */
    @NonNull
    private ScheduleDayItemStorage mStorage;
    /**
     * Состояние загрузки расписания.
     */
    @NonNull
    private MutableLiveData<States> mStatesData;


    private ScheduleViewModel(@NonNull String schedulePath) {
        mStorage = new ScheduleDayItemStorage(schedulePath);

        ScheduleDayItemDataSource.Factory factory = new ScheduleDayItemDataSource.Factory(mStorage);

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(PAGE_SIZE / 2)
                .setPageSize(PAGE_SIZE)
                .setInitialLoadSizeHint(PAGE_SIZE * 2)
                .setMaxSize(PAGE_SIZE * 5)
                .build();

        mScheduleDayItemData = new LivePagedListBuilder<>(factory, config)
                .setFetchExecutor(Executors.newSingleThreadExecutor())
                .build();

        mStatesData = new MutableLiveData<>();
    }

    @NonNull
    LiveData<PagedList<ScheduleDayItem>> scheduleDayItemData() {
        return mScheduleDayItemData;
    }

    @NonNull
    MutableLiveData<States> statesData() {
        return mStatesData;
    }

    @NonNull
    public ScheduleDayItemStorage storage() {
        return mStorage;
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        private final String mSchedulePath;

        Factory(@NonNull String schedulePath) {
            super();
            mSchedulePath = schedulePath;
        }

        @SuppressWarnings("unchecked")
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new ScheduleViewModel(mSchedulePath);
        }
    }
}
