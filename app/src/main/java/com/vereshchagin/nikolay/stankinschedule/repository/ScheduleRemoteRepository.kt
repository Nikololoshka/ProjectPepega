package com.vereshchagin.nikolay.stankinschedule.repository

import android.content.Context
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.vereshchagin.nikolay.stankinschedule.BuildConfig
import com.vereshchagin.nikolay.stankinschedule.api.ScheduleRepositoryApi
import com.vereshchagin.nikolay.stankinschedule.db.MainApplicationDatabase
import com.vereshchagin.nikolay.stankinschedule.utils.extensions.await
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.await
import retrofit2.converter.gson.GsonConverterFactory

class ScheduleRemoteRepository(
    context: Context,
) {

    private val db = MainApplicationDatabase.database(context)
    private val dao = db.repository()

    private val storage = Firebase.storage
    private val api: ScheduleRepositoryApi

    init {
        val builder = Retrofit.Builder()
            .baseUrl(FIREBASE_URL)
            .addConverterFactory(GsonConverterFactory.create())

        // включение лога
        if (BuildConfig.DEBUG) {
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            builder.client(client)
        }

        api = builder.build().create(ScheduleRepositoryApi::class.java)
    }

    fun schedules(category: Int) = dao.schedulesSource(category)

    fun categories(parent: Int?) = dao.categoriesSource(parent)

    suspend fun isScheduleCategory(category: Int) = dao.isScheduleCategory(category)

    suspend fun loadRepositoryEntry() {
        val entry = storageReference(SCHEDULES_JSON, VERSION, API_ENTRY)
        val entryUri = entry.downloadUrl.await()
        val response = api.entry(entryUri.toString()).await()
        dao.insertRepositoryResponse(response)
    }

    private fun storageReference(vararg paths: String): StorageReference {
        return storage.getReference(paths.joinToString("/"))
    }

    companion object {
        private const val FIREBASE_URL =
            "https://firebasestorage.googleapis.com/v0/b/stankinschedule.appspot.com/o/"

        private const val SCHEDULES_JSON = "schedules-json"
        private const val VERSION = "v1"
        private const val API_ENTRY = "api_entry.json"
    }
}