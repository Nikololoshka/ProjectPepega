package com.vereshchagin.nikolay.stankinschedule.modulejournal.login;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.vereshchagin.nikolay.stankinschedule.modulejournal.network.ModuleJournalError;
import com.vereshchagin.nikolay.stankinschedule.modulejournal.network.ModuleJournalErrorUtils;
import com.vereshchagin.nikolay.stankinschedule.modulejournal.network.ModuleJournalService;
import com.vereshchagin.nikolay.stankinschedule.modulejournal.network.response.SemestersResponse;
import com.vereshchagin.nikolay.stankinschedule.modulejournal.view.model.StudentData;
import com.vereshchagin.nikolay.stankinschedule.settings.ModuleJournalPreference;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Response;

/**
 * ViewModel для входа в модульный журнал.
 */
public class ModuleJournalLoginModel extends AndroidViewModel{

    /**
     * Состояния авторизации.
     */
    enum State {
        AUTHORIZED,
        LOADING,
        ERROR,
        WAIT
    }

    /**
     * Пул фоновых потоков.
     */
    @NonNull
    private ExecutorService mExecutor;
    /**
     * Состояние авторизации.
     */
    @NonNull
    private MutableLiveData<State> mStateData;
    /**
     * Ошибка при авторизации.
     */
    @Nullable
    private ModuleJournalError mModuleJournalError;

    private ModuleJournalLoginModel(@NonNull Application application) {
        super(application);

        mExecutor = Executors.newSingleThreadExecutor();
        mStateData = new MutableLiveData<>(State.WAIT);
    }

    void singIn(@NonNull final String login, @NonNull final String password) {
        mStateData.setValue(State.LOADING);
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Response<SemestersResponse> response = ModuleJournalService.getInstance()
                            .api2()
                            .getSemesters(login, password)
                            .execute();

                    if (response.isSuccessful()) {
                        ModuleJournalPreference.saveSignData(getApplication(), login, password);
                        ModuleJournalPreference.setSignIn(getApplication(), true);

                        if (response.body() != null) {
                            StudentData.saveCacheData(StudentData.fromResponse(response.body()),
                                    getApplication().getCacheDir());
                        }

                        mStateData.postValue(State.AUTHORIZED);
                        return;

                    } else {
                        mModuleJournalError = ModuleJournalErrorUtils.responseError(response);
                    }

                } catch (IOException e) {
                    mModuleJournalError = ModuleJournalErrorUtils.exceptionError(e);

                } catch (GeneralSecurityException e) {
                    e.printStackTrace();

                    Toast.makeText(getApplication(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }

                mStateData.postValue(State.ERROR);
            }
        });
    }

    @NonNull
    MutableLiveData<State> stateData() {
        return mStateData;
    }

    @Nullable
    ModuleJournalError error() {
        return mModuleJournalError;
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        mExecutor.shutdown();
    }

    /**
     * Фабрика для создания ViewModel для авторизации в модульный журнал.
     */
    public static class Factory extends ViewModelProvider.AndroidViewModelFactory {

        private Application mApplication;

        public Factory(@NonNull Application application) {
            super(application);
            mApplication =  application;
        }

        @SuppressWarnings("unchecked")
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new ModuleJournalLoginModel(mApplication);
        }
    }
}
