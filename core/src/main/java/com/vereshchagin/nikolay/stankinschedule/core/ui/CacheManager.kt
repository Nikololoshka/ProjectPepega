package com.vereshchagin.nikolay.stankinschedule.core.ui

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.vereshchagin.nikolay.stankinschedule.core.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class CacheManager @Inject constructor(
    @ApplicationContext context: Context
) {

    /**
     * Корневая папка кэша приложения.
     */
    private val cacheDir: File = context.cacheDir

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
     * @param paths путь, относительно которого в дальнейшем пойдем работа с кэшом.
     */
    fun addStartedPath(vararg paths: String) {
        startedPaths.addAll(paths)
    }

    /**
     * Сохраняет данные в кэш.
     * @param data данные для сохранения.
     * @param paths путь для сохранения.
     */
    fun saveToCache(data: Any, vararg paths: String) {
        try {
            fileFromPaths(paths).bufferedWriter().use { writer ->
                gson.toJson(data, writer)
            }

        } catch (ignored: Exception) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "saveToCache: ", ignored)
            }
        }
    }

    /**
     * Загружает данные из кэша. Если не удалось или данные не были найдены,
     * то возвращается null.
     * @param type тип данных для загрузки.
     * @param paths путь для загрузки.
     */
    fun <T> loadFromCache(type: Class<T>, vararg paths: String): T? {
        try {
            val filePath = fileFromPaths(paths)
            if (!filePath.exists()) {
                return null
            }

            return filePath.reader().use { reader ->
                gson.fromJson(reader, type)
            }

        } catch (ignored: Exception) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "saveToCache: ", ignored)
            }
        }

        return null
    }

    /**
     * Отчищает папку с кэшом, если в ней больше задаваемого
     * количества файлов.
     */
    fun clearCache(count: Int = 10) {
        val root = fileFromPaths(emptyArray())
        if (root.walkBottomUp().count() > count) {
            root.deleteRecursively()
        }
    }

    /**
     * Отчищает всю папку с кэшом.
     */
    fun clearAll() {
        val root = fileFromPaths(emptyArray())
        root.deleteRecursively()
    }

    /**
     * Возвращает путь до JSON файла в кэше по путям.
     * @param paths набор путей до файла.
     */
    private fun fileFromPaths(paths: Array<out String>): File {
        var file = cacheDir
        for (i in startedPaths.indices) {
            file = File(file, startedPaths[i])
        }
        file.mkdirs()

        for (i in paths.indices) {
            file = File(file, if (i == paths.size - 1) "${paths[i]}.json" else paths[i])
        }

        return file
    }

    companion object {
        private const val TAG = "CacheFolderLog"
    }
}