package com.github.nikololoshka.pepegaschedule.schedule.editor.pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.github.nikololoshka.pepegaschedule.schedule.model.Schedule;
import com.google.gson.JsonParseException;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ViewModel для редактора пары.
 */
public class PairEditorModel extends ViewModel {

    /**
     * Состояние ViewModel.
     */
    public enum States {
        SUCCESSFULLY_SAVED,
        SUCCESSFULLY_LOADED,
        LOADING,
        ERROR
    }

    /**
     * Пул фоновых потоков.
     */
    private final ExecutorService mExecutor;
    /**
     * Путь к расписанию.
     */
    private final String mSchedulePath;

    /**
     * Расписание.
     */
    private MutableLiveData<Schedule> mScheduleLiveData;
    /**
     * Состояние.
     */
    private MutableLiveData<States> mState;

    private PairEditorModel(@NonNull String schedulePath) {
        super();

        mExecutor = Executors.newSingleThreadExecutor();

        mSchedulePath = schedulePath;
        mScheduleLiveData = new MutableLiveData<>();

        mState = new MutableLiveData<>();
        loadSchedule();
    }

    /**
     * Загружает расписание.
     */
    private void loadSchedule() {
        mState.setValue(States.LOADING);
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    File scheduleFile = new File(mSchedulePath);
                    String json = FileUtils.readFileToString(scheduleFile, StandardCharsets.UTF_8);
                    Schedule loadSchedule = Schedule.fromJson(json);

                    mScheduleLiveData.postValue(loadSchedule);
                    mState.postValue(States.SUCCESSFULLY_LOADED);
                    return;

                } catch (IOException | JsonParseException e) {
                    e.printStackTrace();
                }

                mScheduleLiveData.postValue(null);
                mState.postValue(States.ERROR);
            }
        });
    }

    /**
     * Сохранить расписание.
     */
    void saveSchedule() {
        mState.setValue(States.LOADING);
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Schedule saveSchedule = mScheduleLiveData.getValue();
                    if (saveSchedule == null) {
                        return;
                    }

                    String json = saveSchedule.toJson();
                    File scheduleFile = new File(mSchedulePath);
                    FileUtils.writeStringToFile(scheduleFile, json, StandardCharsets.UTF_8);

                    mState.postValue(States.SUCCESSFULLY_SAVED);
                    return;

                } catch (IOException | JsonParseException e) {
                    e.printStackTrace();
                }

                mState.postValue(States.ERROR);
            }
        });
    }

    /**
     * @return расписание.
     */
    @NonNull
    MutableLiveData<Schedule> schedule() {
        return mScheduleLiveData;
    }

    /**
     * @return состояние.
     */
    @NonNull
    MutableLiveData<States> state() {
        return mState;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mExecutor.shutdown();
    }

    /**
     * Фабрика для создания ViewModel.
     */
    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        /**
         * Путь к расписанию.
         */
        private final String mSchedulePath;

        Factory(@NonNull String schedulePath) {
            super();
            mSchedulePath = schedulePath;
        }

        @SuppressWarnings("unchecked")
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new PairEditorModel(mSchedulePath);
        }
    }
}
