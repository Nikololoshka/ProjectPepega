package com.vereshchagin.nikolay.stankinschedule.di

import com.google.gson.GsonBuilder
import com.vereshchagin.nikolay.stankinschedule.api.ModuleJournalAPI2
import com.vereshchagin.nikolay.stankinschedule.api.ScheduleRemoteRepositoryAPI
import com.vereshchagin.nikolay.stankinschedule.api.StankinNewsAPI
import com.vereshchagin.nikolay.stankinschedule.api.StankinNewsPostsAPI
import com.vereshchagin.nikolay.stankinschedule.model.news.NewsPost
import com.vereshchagin.nikolay.stankinschedule.model.schedule.json.JsonScheduleItem
import com.vereshchagin.nikolay.stankinschedule.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideNewsPostsService(client: OkHttpClient): StankinNewsPostsAPI {
        return Retrofit.Builder()
            .baseUrl(Constants.STANKIN_URL)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .registerTypeAdapter(
                            NewsPost::class.java, NewsPost.NewsPostDeserializer()
                        )
                        .create()
                )
            )
            .client(client)
            .build()
            .create(StankinNewsPostsAPI::class.java)
    }

    @Provides
    fun provideNewsService(client: OkHttpClient): StankinNewsAPI {
        return Retrofit.Builder()
            .baseUrl(Constants.STANKIN_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(StankinNewsAPI::class.java)
    }

    @Provides
    fun provideModuleJournalService2(client: OkHttpClient): ModuleJournalAPI2 {
        return Retrofit.Builder()
            .baseUrl(Constants.MODULE_JOURNAL_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ModuleJournalAPI2::class.java)
    }

    @Provides
    fun provideScheduleRemoteService(client: OkHttpClient): ScheduleRemoteRepositoryAPI {
        return Retrofit.Builder()
            .baseUrl(Constants.SCHEDULE_REMOTE_REPOSITORY_URL)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .registerTypeAdapter(
                            JsonScheduleItem::class.java,
                            JsonScheduleItem.ScheduleResponseSerializer()
                        )
                        .create()
                )
            )
            .client(client)
            .build()
            .create(ScheduleRemoteRepositoryAPI::class.java)
    }
    /*
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        // включение лога в DEBUG
        if (BuildConfig.DEBUG) {
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BODY

            return OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()
        }
        return OkHttpClient()
    }*/
}