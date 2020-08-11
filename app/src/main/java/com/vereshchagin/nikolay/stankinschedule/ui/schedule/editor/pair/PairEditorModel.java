package com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.pair;

import android.app.Application;
import android.appwidget.AppWidgetManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.JsonParseException;
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.model.Schedule;
import com.vereshchagin.nikolay.stankinschedule.utils.WidgetUtils;
import com.vereshchagin.nikolay.stankinschedule.widget.ScheduleWidget;
import com.vereshchagin.nikolay.stankinschedule.widget.ScheduleWidgetConfigureActivity;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ViewModel для редактора пары.
 */
public class PairEditorModel extends AndroidViewModel {

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

    private PairEditorModel(@NonNull Application application, @NonNull String schedulePath) {
        super(application);

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

                    // обновить виджет расписания, если есть
                    String scheduleName = FilenameUtils.getBaseName(mSchedulePath);
                    List<Integer> ids = WidgetUtils.scheduleWidgets(getApplication());
                    for (int id : ids) {
                        ScheduleWidgetConfigureActivity.WidgetData widgetData =
                                ScheduleWidgetConfigureActivity.loadPref(getApplication(), id);

                        String name = widgetData.scheduleName();
                        if (name != null && name.equals(scheduleName)) {
                            ScheduleWidget.updateAppWidget(getApplication(),
                                    AppWidgetManager.getInstance(getApplication()), id);
                            break;
                        }
                    }

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
    public static class Factory extends ViewModelProvider.AndroidViewModelFactory {

        /**
         * Путь к расписанию.
         */
        private final String mSchedulePath;

        private final Application mApplication;


        Factory(@NonNull Application application, @NonNull String schedulePath) {
            super(application);
            mApplication = application;
            mSchedulePath = schedulePath;
        }

        @SuppressWarnings("unchecked")
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new PairEditorModel(mApplication, mSchedulePath);
        }
    }
}
