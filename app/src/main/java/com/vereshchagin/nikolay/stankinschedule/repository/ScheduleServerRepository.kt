package com.vereshchagin.nikolay.stankinschedule.repository

import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.RepositoryCategoryItem
import com.vereshchagin.nikolay.stankinschedule.model.schedule.repository.RepositoryDescription
import com.vereshchagin.nikolay.stankinschedule.utils.State
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
class ScheduleServerRepository {

    private val storage = Firebase.storage

    /**
     *
     */
    fun description(callback: (State<RepositoryDescription>) -> Unit) {
        val descriptionReference = storage.getReference(path(SCHEDULES_ROOT, SCHEDULES_DESCRIPTION))

        val file = File.createTempFile("description_temp", "json")
        descriptionReference.getFile(file)
            .addOnSuccessListener {
                val json = FileUtils.readFileToString(file, StandardCharsets.UTF_8)
                val description = Gson().fromJson(json, RepositoryDescription::class.java)
                callback.invoke(State.success(description))
            }
            .addOnFailureListener {
                callback.invoke(State.failed(it))
            }

        callback.invoke(State.loading())
    }

    /**
     * Возвращает элемент категории по имени.
     *
     * Загружает данные из кэша. Или из сети, если кэш устарел.
     */
    suspend fun category(
        categoryName: String?, cacheFolder: File
    ): State<RepositoryCategoryItem> {

        if (categoryName == null) {
            return State.failed(NullPointerException("Category is null"))
        }

        val cache = loadCategory(categoryName, cacheFolder)
        if (cache != null && isValid(cache.date)) {
            return State.success(cache)
        }

        val schedulesReference = storage.getReference(
            path(SCHEDULES_ROOT, categoryName)
        )

        return suspendCoroutine { continuation ->
            schedulesReference.listAll()
                .addOnSuccessListener { task ->
                    val data = RepositoryCategoryItem(
                        categoryName, task.items.map { item -> item.name }
                    )
                    saveCategory(data, cacheFolder)
                    continuation.resume(State.success(data))
                }
                .addOnFailureListener {
                    continuation.resume(State.failed(it))
                }
        }
    }

    /**
     * Проверяет, является ли кэш валидным.
     */
    private fun isValid(date: DateTime): Boolean {
        return Hours.hoursBetween(date, DateTime.now()).hours < 6
    }

    /**
     * Загружает категорию из кэша.
     */
    private fun loadCategory(categoryName: String, cacheFolder: File): RepositoryCategoryItem? {
        try {
            val json = FileUtils.readFileToString(
                FileUtils.getFile(cacheFolder, REPOSITORY_FOLDER, categoryName + ".json"),
                StandardCharsets.UTF_8
            )
            return Gson().fromJson(json, RepositoryCategoryItem::class.java)

        } catch (ignored: Exception) {

        }

        return null
    }

    /**
     * Сохраняет категорию в кэш.
     */
    private fun saveCategory(data: RepositoryCategoryItem, cacheFolder: File) {
        try {
            val json = Gson().toJson(data)
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