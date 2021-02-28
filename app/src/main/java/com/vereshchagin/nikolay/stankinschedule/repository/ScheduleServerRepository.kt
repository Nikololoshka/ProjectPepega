package com.vereshchagin.nikolay.stankinschedule.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.gson.GsonBuilder
import com.vereshchagin.nikolay.stankinschedule.BuildConfig
import com.vereshchagin.nikolay.stankinschedule.api.ScheduleRepositoryApi
import com.vereshchagin.nikolay.stankinschedule.model.schedule.ScheduleResponse
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Pair
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.RepositoryCategoryItem
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.RepositoryDescription
import com.vereshchagin.nikolay.stankinschedule.utils.State
import com.vereshchagin.nikolay.stankinschedule.utils.convertors.gson.DateTimeTypeConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.apache.commons.io.FileUtils
import org.joda.time.DateTime
import org.joda.time.Hours
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.nio.charset.StandardCharsets
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Удаленный репозиторий с расписаниями.
 */
class ScheduleServerRepository(
    private val cacheFolder: File
) {

    private val storage = Firebase.storage
    private val gson = GsonBuilder()
        .registerTypeAdapter(DateTime::class.java, DateTimeTypeConverter())
        .create()

    /**
     * Возвращает описание репозитория с расписаниями.
     *
     * Загружает данные из кэша. Или из сети, если кэш устарел.
     * @param useCache использовать кэш, если он есть.
     */
    fun description(useCache: Boolean) = flow {
        emit(State.loading())

        if (useCache) {
            val cache = loadDescription()
            if (cache != null && isValid(cache.date)) {
                emit(State.success(cache))
                return@flow
            }
        }

        val descriptionReference = storage.getReference(path(SCHEDULES_ROOT, SCHEDULES_DESCRIPTION))
        val file = File.createTempFile("description_temp", "json")

        val state = suspendCoroutine<State<RepositoryDescription>> { continuation ->
            descriptionReference.getFile(file)
                .addOnSuccessListener {
                    val json = FileUtils.readFileToString(file, StandardCharsets.UTF_8)
                    val description = gson.fromJson(json, RepositoryDescription::class.java)
                    description.date = DateTime.now()

                    saveDescription(description)
                    continuation.resume(State.success(description))
                }
                .addOnFailureListener {
                    continuation.resume(State.failed(it))
                }
        }
        emit(state)

    }.flowOn(Dispatchers.IO)

    /**
     * Возвращает элемент категории по имени.
     *
     * Загружает данные из кэша. Или из сети, если кэш устарел.
     * @param useCache использовать кэш, если он есть.
     */
    suspend fun category(categoryName: String?, useCache: Boolean): State<RepositoryCategoryItem> {

        if (categoryName == null) {
            return State.failed(NullPointerException("Category is null"))
        }

        if (useCache) {
            val cache = loadCategory(categoryName)
            if (cache != null && isValid(cache.date)) {
                return State.success(cache)
            }
        }

        val schedulesReference = storage.getReference(
            path(SCHEDULES_ROOT, categoryName)
        )

        return suspendCoroutine { continuation ->
            schedulesReference.listAll()
                .addOnSuccessListener { task ->
                    val data = RepositoryCategoryItem(
                        categoryName,
                        task.items.map { item ->
                            item.name.removeSuffix(".json")
                        }
                    )
                    saveCategory(data)
                    continuation.resume(State.success(data))
                }
                .addOnFailureListener {
                    continuation.resume(State.failed(it))
                }
        }
    }

    /**
     * Получение Uri расписания для скачивания.
     */
    suspend fun scheduleUri(categoryName: String, scheduleName: String): Uri {
        return suspendCoroutine { continuation ->
            storage.getReference(
                path(SCHEDULES_ROOT, categoryName, "$scheduleName.json")
            ).downloadUrl
                .addOnSuccessListener {
                    continuation.resume(it)
                }
                .addOnFailureListener {
                    continuation.resume(Uri.EMPTY)
                }
        }
    }

    /**
     * Возвращает API для скачивания с удаленного репозитория.
     */
    fun downloader(): ScheduleRepositoryApi {
        val builder = Retrofit.Builder()
            .baseUrl(FIREBASE_URL)
            .addConverterFactory(GsonConverterFactory.create(
                GsonBuilder()
                    .registerTypeAdapter(ScheduleResponse::class.java,
                        ScheduleResponse.Serializer())
                    .registerTypeAdapter(Pair::class.java, Pair.Serializer())
                    .create()
            ))

        // включение лога
        if (BuildConfig.DEBUG) {
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            builder.client(client)
        }

        return builder.build()
            .create(ScheduleRepositoryApi::class.java)
    }

    /**
     * Проверяет, является ли кэш актуальными.
     */
    private fun isValid(date: DateTime): Boolean {
        return Hours.hoursBetween(date, DateTime.now()).hours < 2
    }

    /**
     * Загружает описание из кэша.
     */
    private fun loadDescription(): RepositoryDescription? {
        try {
            val json = FileUtils.readFileToString(
                FileUtils.getFile(cacheFolder, REPOSITORY_FOLDER, "description.json"),
                StandardCharsets.UTF_8
            )

            return gson.fromJson(json, RepositoryDescription::class.java)

        } catch (ignored: Exception) {

        }

        return null
    }

    /**
     * Сохраняет описание в кэш.
     */
    private fun saveDescription(description: RepositoryDescription) {
        try {
            val json = gson.toJson(description)
            Log.d(TAG, "saveDescription: ${description.date}")
            Log.d(TAG, "saveDescription: $json")
            FileUtils.writeStringToFile(
                FileUtils.getFile(cacheFolder, REPOSITORY_FOLDER, "description.json"),
                json,
                StandardCharsets.UTF_8
            )

        } catch (ignored: Exception) {

        }
    }

    /**
     * Загружает категорию из кэша.
     */
    private fun loadCategory(categoryName: String): RepositoryCategoryItem? {
        try {
            val json = FileUtils.readFileToString(
                FileUtils.getFile(cacheFolder, REPOSITORY_FOLDER, "$categoryName.json"),
                StandardCharsets.UTF_8
            )
            return gson.fromJson(json, RepositoryCategoryItem::class.java)

        } catch (ignored: Exception) {

        }

        return null
    }

    /**
     * Сохраняет категорию в кэш.
     */
    private fun saveCategory(data: RepositoryCategoryItem) {
        try {
            val json = gson.toJson(data)
            FileUtils.writeStringToFile(
                FileUtils.getFile(cacheFolder, REPOSITORY_FOLDER, data.categoryName + ".json"),
                json,
                StandardCharsets.UTF_8
            )

        } catch (ignored: Exception) {

        }
    }

    /**
     * Создает путь для обращения к Firebase Storage.
     */
    private fun path(vararg paths: String) = paths.joinToString("/")

    companion object {
        const val FIREBASE_URL = "https://firebasestorage.googleapis.com/v0/b/stankinschedule.appspot.com/o/"
        const val SCHEDULES_ROOT = "schedules"
        const val SCHEDULES_DESCRIPTION = "description.json"
        const val REPOSITORY_FOLDER = "repository"
        const val TAG = "ScheduleServerLog"
    }
}