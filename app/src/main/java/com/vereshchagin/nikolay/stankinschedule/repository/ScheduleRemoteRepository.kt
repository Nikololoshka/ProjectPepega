package com.vereshchagin.nikolay.stankinschedule.repository

import android.content.Context
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.gson.GsonBuilder
import com.vereshchagin.nikolay.stankinschedule.BuildConfig
import com.vereshchagin.nikolay.stankinschedule.api.ScheduleRepositoryApi
import com.vereshchagin.nikolay.stankinschedule.db.MainApplicationDatabase
import com.vereshchagin.nikolay.stankinschedule.model.schedule.ScheduleResponse
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Pair
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.v1.RepositoryDescriptionKt
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.v1.ScheduleEntry
import com.vereshchagin.nikolay.stankinschedule.utils.State
import com.vereshchagin.nikolay.stankinschedule.utils.convertors.gson.DateTimeTypeConverter
import com.vereshchagin.nikolay.stankinschedule.utils.extensions.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.apache.commons.io.FileUtils
import org.joda.time.DateTime
import retrofit2.Retrofit
import retrofit2.await
import retrofit2.converter.gson.GsonConverterFactory
import java.nio.charset.StandardCharsets

class ScheduleRemoteRepository(
    context: Context,
) {

    private val cacheFolder = context.cacheDir
    private val gson = GsonBuilder()
        .registerTypeAdapter(DateTime::class.java, DateTimeTypeConverter())
        .create()

    private val db = MainApplicationDatabase.database(context)
    private val dao = db.repository()

    private val storage = Firebase.storage
    private val api: ScheduleRepositoryApi

    init {
        val builder = Retrofit.Builder()
            .baseUrl(FIREBASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .registerTypeAdapter(
                            ScheduleResponse::class.java, ScheduleResponse.Serializer()
                        )
                        .registerTypeAdapter(
                            Pair::class.java, Pair.Serializer()
                        )
                        .create()
                )
            )

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

    suspend fun getScheduleEntry(scheduleId: Int): Flow<ScheduleEntry> {
        return dao.getScheduleEntry(scheduleId)
            .filterNotNull()
    }

    fun description(useCache: Boolean = true) = flow {
        emit(State.loading())

        // проверка кэша
        if (useCache) {
            val cache = loadDescription()
            if (cache != null && cache.isValid() && !dao.isRepositoryEmpty()) {
                Log.d("MyLog", "description: cache")
                emit(State.success(cache))
                return@flow
            }
        }

        Log.d("MyLog", "description: update")

        // обновление репозитория
        val entryRef = storageReference(SCHEDULES_JSON, VERSION, API_ENTRY)
        val entryUri = entryRef.downloadUrl.await()
        val response = api.entry(entryUri.toString()).await()
        dao.insertRepositoryResponse(response)

        val description = RepositoryDescriptionKt(response.lastUpdate)
        saveDescription(description)
        emit(State.success(description))
    }

    suspend fun loadRepositoryEntry() {
        val entry = storageReference(SCHEDULES_JSON, VERSION, API_ENTRY)
        val entryUri = entry.downloadUrl.await()
        val response = api.entry(entryUri.toString()).await()
        dao.insertRepositoryResponse(response)
    }

    suspend fun downloadSchedule(scheduleName: String, path: String) {
        val scheduleRef = storageReference(SCHEDULES_JSON, VERSION, path)
        val scheduleUri = scheduleRef.downloadUrl.await()
        val response = api.schedule(scheduleUri.toString()).await()
        db.schedules().insertScheduleResponse(scheduleName, response)
    }

    private fun storageReference(vararg paths: String): StorageReference {
        return storage.getReference(paths.joinToString("/"))
    }

    /**
     * Сохраняет описание репозитория в кэш.
     */
    private fun saveDescription(description: RepositoryDescriptionKt) {
        try {
            val json = gson.toJson(description)
            FileUtils.writeStringToFile(
                FileUtils.getFile(cacheFolder, REPOSITORY_FOLDER, "description.json"),
                json,
                StandardCharsets.UTF_8
            )

        } catch (ignored: Exception) {

        }
    }

    /**
     * Загружает описание репозитория из кэша.
     */
    private fun loadDescription(): RepositoryDescriptionKt? {
        try {
            return gson.fromJson(
                FileUtils.readFileToString(
                    FileUtils.getFile(cacheFolder, REPOSITORY_FOLDER, "description.json"),
                    StandardCharsets.UTF_8
                ),
                RepositoryDescriptionKt::class.java
            )

        } catch (ignored: Exception) {

        }

        return null
    }


    companion object {
        private const val TAG = "ScheduleRepositoryLog"

        private const val FIREBASE_URL =
            "https://firebasestorage.googleapis.com/v0/b/stankinschedule.appspot.com/o/"

        private const val REPOSITORY_FOLDER = "repository"

        private const val SCHEDULES_JSON = "schedules-json"
        private const val VERSION = "v1"
        private const val API_ENTRY = "api_entry.json"
    }
}