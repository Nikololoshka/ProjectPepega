package com.github.nikololoshka.pepegaschedule.schedule.repository;

import android.app.Application;
import android.content.res.AssetManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.github.nikololoshka.pepegaschedule.schedule.repository.list.RepositoryItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ViewModel для репозитория с расписаниями.
 */
public class ScheduleRepositoryModel extends AndroidViewModel {

    /**
     * Сотстояния.
     */
    enum State {
        OK,
        LOADING,
        ERROR
    }

    /**
     * Пул фоновых потоков.
     */
    private final ExecutorService mExecutor;
    /**
     * Расписания для репозитория.
     */
    private MutableLiveData<List<RepositoryItem>> mSchedulesData;
    /**
     * Состояние загрузки расписаний.
     */
    private MutableLiveData<State> mStateData;


    private ScheduleRepositoryModel(@NonNull Application application) {
        super(application);

        mSchedulesData = new MutableLiveData<>();
        mStateData = new MutableLiveData<>(State.LOADING);

        mExecutor = Executors.newSingleThreadExecutor();
        loadSchedules();
    }

    /**
     * Загружает расписания для репозитория.
     */
    public void loadSchedules() {
        mStateData.setValue(State.LOADING);
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final String ROOT = "schedules";

                try {
                    AssetManager manager = getApplication().getAssets();
                    ArrayList<RepositoryItem> repositoryItems = new ArrayList<>();
                    String[] schedules = manager.list(ROOT);

                    if (schedules != null) {
                        for (String schedule : schedules) {
                            RepositoryItem item = new RepositoryItem();
                            item.setName(schedule.substring(0, schedule.length() - 5));
                            item.setPath(ROOT + "/" + schedule);
                            repositoryItems.add(item);
                        }
                    }

                    mSchedulesData.postValue(repositoryItems);
                    mStateData.postValue(State.OK);

                } catch (IOException e) {
                    e.printStackTrace();
                    mStateData.postValue(State.ERROR);
                }
            }
        });
    }

    /**
     * @return расписания для репозитория.
     */
    public MutableLiveData<List<RepositoryItem>> schedules() {
        return mSchedulesData;
    }

    /**
     * @return состояние загрузки расписаний.
     */
    public MutableLiveData<State> state() {
        return mStateData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mExecutor.shutdown();
    }

    /**
     * Фабрика для создания ViewModel.
     */
    public static class Factory extends ViewModelProvider.AndroidViewModelFactory {

        private Application mApplication;

        public Factory(@NonNull Application application) {
            super(application);
            mApplication = application;
        }

        @SuppressWarnings("unchecked")
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new ScheduleRepositoryModel(mApplication);
        }
    }
}
