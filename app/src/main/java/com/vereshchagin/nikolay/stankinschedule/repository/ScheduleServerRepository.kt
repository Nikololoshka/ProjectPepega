package com.vereshchagin.nikolay.stankinschedule.repository

import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.gson.GsonBuilder
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.RepositoryCategoryItem
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.RepositoryDescription
import com.vereshchagin.nikolay.stankinschedule.utils.DateTimeTypeConverter
import com.vereshchagin.nikolay.stankinschedule.utils.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.apache.commons.io.FileUtils
import org.joda.time.DateTime
import org.joda.time.Hours
import java.io.File
import java.nio.charset.StandardCharsets
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Удаленный репозиторий с расписаниеями.
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

        if (useCache) {
            val cache = loadDescription()
            Log.d(TAG, "description: ${cache?.date}")
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
                .addOnProgressListener {
                    Log.d(TAG, "description: ${it.bytesTransferred}/${it.totalByteCount}")
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

    fun scheduleDownloader(categoryName: String, scheduleName: String): StorageReference {
        return storage.getReference(
            path(SCHEDULES_ROOT, categoryName, "$scheduleName.json")
        )
    }

    /**
     * Проверяет, является ли кэш валидным.
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
        const val SCHEDULES_ROOT = "schedules"
        const val SCHEDULES_DESCRIPTION = "description.json"
        const val REPOSITORY_FOLDER = "repository"
        const val TAG = "ScheduleServerLog"
    }
}