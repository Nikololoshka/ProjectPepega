package com.vereshchagin.nikolay.stankinschedule.ui.modulejournal.view;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.vereshchagin.nikolay.stankinschedule.ui.modulejournal.network.ModuleJournalError;
import com.vereshchagin.nikolay.stankinschedule.ui.modulejournal.network.ModuleJournalErrorUtils;
import com.vereshchagin.nikolay.stankinschedule.ui.modulejournal.network.ModuleJournalService;
import com.vereshchagin.nikolay.stankinschedule.ui.modulejournal.network.response.SemestersResponse;
import com.vereshchagin.nikolay.stankinschedule.ui.modulejournal.view.model.SemesterMarks;
import com.vereshchagin.nikolay.stankinschedule.ui.modulejournal.view.model.StudentData;
import com.vereshchagin.nikolay.stankinschedule.ui.modulejournal.view.paging.SemestersDataSources;
import com.vereshchagin.nikolay.stankinschedule.ui.modulejournal.view.paging.SemestersStorage;
import com.vereshchagin.nikolay.stankinschedule.ui.settings.ModuleJournalPreference;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Response;

/**
 * ViewModel для хранения PagedList с семестрами.
 */
public class ModuleJournalModel extends AndroidViewModel {

    /**
     * Состояние загрузки основной информации о студенте.
     */
    enum StudentState {
        OK,
        LOADING,
        ERROR,
    }

    /**
     * Список с семестрами.
     */
    @NonNull
    private LiveData<PagedList<SemesterMarks>> mSemestersData;
    /**
     * Хранилище семестров.
     */
    @NonNull
    private SemestersStorage mStorage;

    /**
     * Информация о студенте.
     */
    @NonNull
    private MutableLiveData<StudentData> mStudentData;
    /**
     * Состояние загрузки информации о студенте.
     */
    @NonNull
    private MutableLiveData<StudentState> mStudentStateData;
    /**
     * Использовать ли кэш при загрузке данных о студенте.
     */
    private boolean mStudentUseCache;

    /**
     * Пул фоновых потоков.
     */
    @NonNull
    private ExecutorService mExecutor;

    /**
     * Ошибка во время получения данных.
     */
    @Nullable
    private ModuleJournalError mError;

    /**
     * Выполнен ли вход в модульный журнал.
     */
    private boolean mIsSingIn;


    private ModuleJournalModel(@NonNull Application application) {
        super(application);

        mStorage = new SemestersStorage();
        mStorage.setCacheDirectory(application.getCacheDir());

        mExecutor = Executors.newSingleThreadExecutor();

        mStudentData = new MutableLiveData<>();
        mStudentStateData = new MutableLiveData<>(StudentState.LOADING);
        mStudentUseCache = true;

        SemestersDataSources.Factory semestersFactory = new SemestersDataSources.Factory(mStorage);

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(1)
                .setPageSize(1)
                .setPrefetchDistance(2)
                .build();

        mSemestersData = new LivePagedListBuilder<>(semestersFactory, config)
                .setFetchExecutor(mExecutor)
                .build();

        mIsSingIn = ModuleJournalPreference.signIn(application);
        if (mIsSingIn) {
            reload(true);
        }
    }

    /**
     * Перезагружает данные в модульном журнале.
     * @param useCache использовать ли кэш при загрузке.
     */
    void reload(boolean useCache) {
        mStudentUseCache = useCache;
        mStorage.setUseCache(useCache);

        loadStudentData();

        PagedList<SemesterMarks> data = mSemestersData.getValue();
        if (data != null) {
            data.getDataSource().invalidate();
        }
    }

    /**
     * Перезагружает информацию о студенте.
     */
    private void loadStudentData() {
        mStudentStateData.setValue(StudentState.LOADING);
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                @Nullable
                StudentData cacheData = mStudentUseCache ? StudentData.loadCacheData(getApplication().getCacheDir()) : null;
                mStudentUseCache = true;

                @Nullable
                StudentData result = null;

                try {
                    Pair<String, String> authorization = ModuleJournalPreference.loadSignData(getApplication());
                    String login = authorization.first == null ? "" : authorization.first;
                    String password = authorization.second == null ? "" : authorization.second;

                    if (login.isEmpty() || password.isEmpty()) {
                        // TODO: 17/02/20 добавить ошибку
                        mStudentStateData.postValue(StudentState.ERROR);
                        return;
                    }

                    result = cacheData;
                    if (cacheData != null) {
                        mStorage.setLogin(login);
                        mStorage.setPassword(password);
                        mStorage.setSemesters(cacheData.semesters());
                    }

                    if (cacheData != null && isOverData(cacheData.time())) {
                        // данные из кэша еще актуальные
                        mStudentData.postValue(cacheData);
                        mStudentStateData.postValue(StudentState.OK);
                        return;
                    }

                    Response<SemestersResponse> response = ModuleJournalService.getInstance()
                            .api2()
                            .getSemesters(login, password)
                            .execute();

                    if (response.isSuccessful()) {
                        // данные обновлены
                        if (response.body() != null) {
                            result = StudentData.fromResponse(response.body());
                            StudentData.saveCacheData(result, getApplication().getCacheDir());

                            mStorage.setLogin(login);
                            mStorage.setPassword(password);
                            mStorage.setSemesters(result.semesters());
                        }

                    } else {
                        if (cacheData == null) {
                            mError = ModuleJournalErrorUtils.responseError(response);
                        }
                    }

                } catch (GeneralSecurityException e) {
                    e.printStackTrace();

                    Toast.makeText(getApplication(), e.getMessage(), Toast.LENGTH_LONG).show();

                } catch (IOException e) {
                    if (cacheData == null) {
                        mError = ModuleJournalErrorUtils.exceptionError(e);
                    }
                }

                mStudentData.postValue(result);
                mStudentStateData.postValue(result != null ? StudentState.OK : StudentState.ERROR);
            }
        });
    }

    /**
     * Проверяет, истек ли срок хранения кэша.
     * @param calendar время загрузки данных в кэше.
     * @return true - время истекло, иначе false.
     */
    private boolean isOverData(@NonNull Calendar calendar) {
        Calendar today = new GregorianCalendar();
        // 3600000L - 60 минут
        return (today.getTimeInMillis() - calendar.getTimeInMillis()) < 3600000L;
    }

    /**
     * @return список семестров.
     */
    @NonNull
    LiveData<PagedList<SemesterMarks>> semesters() {
        return mSemestersData;
    }

    /**
     * @return хранилище.
     */
    @NonNull
    SemestersStorage storage() {
        return mStorage;
    }

    /**
     * @return состояние загрузки основной информации.
     */
    @NonNull
    MutableLiveData<StudentState> studentState() {
        return mStudentStateData;
    }

    /**
     * @return информация о студенте.
     */
    @NonNull
    MutableLiveData<StudentData> student() {
        return mStudentData;
    }

    /**
     * @return ошибка при загрузке.
     */
    @Nullable
    ModuleJournalError error() {
        return mError;
    }

    /**
     * @return выполнен ли вход.
     */
    boolean isSingIn() {
        return mIsSingIn;
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

        @NonNull
        private Application mApplication;

        public Factory(@NonNull Application application) {
            super(application);
            mApplication = application;
        }

        @SuppressWarnings("unchecked")
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new ModuleJournalModel(mApplication);
        }
    }
}
