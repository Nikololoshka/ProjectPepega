package com.vereshchagin.nikolay.stankinschedule.utils

import com.google.gson.Gson
import java.io.File

/**
 * Класс для работы с кэшом приложения.
 *
 * @param cacheDir корневая папка кэша приложения.
 */
class CacheFolder(
    private val cacheDir: File
) {
    /**
     * Gson для сохранения в JSON файлы.
     */
    var gson = Gson()

    /**
     * Начальный путь (корневой). Откуда начинают сохраняться данные в кэш.
     */
    private val startedPaths = arrayListOf<String>()

    /**
     * Добавляет начальный (корневой путь).
     *
     * @param paths путь, относительно которого в дальнейшем пойдем работа с кэшом.
     */
    fun addStartedPath(vararg paths: String) {
        startedPaths.addAll(paths)
    }

    /**
     * Сохраняет данные в кэш.
     *
     * @param data данные для сохранения.
     * @param paths путь для сохранения.
     */
    fun saveToCache(data: Any, vararg paths: String) {
        try {
            val writer = fileFromPaths(paths).writer()
            gson.toJson(data, writer)

        } catch (ignored: Exception) {

        }

    }

    /**
     * Загружает данные из кэша. Если не удалось или данные не были найдены,
     * то возвращается null.
     *
     * @param type тип данных для загрузки.
     * @param paths путь для загрузки.
     */
    fun <T> loadFromCache(type: Class<T>, vararg paths: String): T? {
        try {
            val reader = fileFromPaths(paths).reader()
            return gson.fromJson(reader, type)

        } catch (ignored: Exception) {

        }
        return null
    }

    /**
     * Отчищает папку с кэшом.
     */
    fun clearCache(count: Int = 10) {
        val root = fileFromPaths(emptyArray())
        if (root.walkBottomUp().count() > count) {
            root.deleteRecursively()
        }
    }

    /**
     * Возвращает путь до JSON файла в кэше по путям.
     *
     * @param paths набор путей до файла.
     */
    private fun fileFromPaths(paths: Array<out String>): File {
        var file = cacheDir
        for (i in startedPaths.indices) {
            file = File(file, startedPaths[i])
        }
        for (i in paths.indices) {
            file = File(file, if (i == paths.size - 1) "${paths[i]}.json" else paths[i])
        }
        return file
    }
}