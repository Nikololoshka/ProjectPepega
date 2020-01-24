package com.github.nikololoshka.pepegaschedule.modulejournal.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit клиент-сервис для рвботы с модульным журналом.
 */
public class ModuleJournalService {

    public static final String URL = "https://lk.stankin.ru";
    public static final boolean DEBUG = false;

    private static ModuleJournalService mService;
    private Retrofit mRetrofit;

    private ModuleJournalService() {
        if (DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor);

            mRetrofit = new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client.build())
                    .build();

        } else {
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
    }

    public static ModuleJournalService getInstance() {
        if (mService == null) {
            mService = new ModuleJournalService();
        }
        return mService;
    }

    public ModuleJournalHttpApi2 api2() {
        return mRetrofit.create(ModuleJournalHttpApi2.class);
    }
}
