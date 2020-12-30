package com.vereshchagin.nikolay.stankinschedule.repository

import com.google.gson.Gson
import com.vereshchagin.nikolay.stankinschedule.BuildConfig
import com.vereshchagin.nikolay.stankinschedule.MainApplication
import com.vereshchagin.nikolay.stankinschedule.api.ModuleJournalApi2
import com.vereshchagin.nikolay.stankinschedule.model.modulejournal.SemesterMarks
import com.vereshchagin.nikolay.stankinschedule.model.modulejournal.StudentData
import com.vereshchagin.nikolay.stankinschedule.ui.settings.ModuleJournalPreference
import com.vereshchagin.nikolay.stankinschedule.utils.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.apache.commons.io.FileUtils
import retrofit2.Retrofit
import retrofit2.await
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.nio.charset.StandardCharsets

/**
 * Репозиторий для работы с модульным журналом.
 */
class ModuleJournalRepository(private val cacheDir: File) {

    private var retrofit: Retrofit
    private var api: ModuleJournalApi2

    private val gson = Gson()

    /**
     * Кэш данных для входа в модульный журнал.
     */
    private var login: String? = null
    private var password: String? = null

    init {
        val builder = Retrofit.Builder()
            .baseUrl(BASE_URL)
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

        retrofit = builder.build()
        api = retrofit.create(ModuleJournalApi2::class.java)
    }

    /**
     * Возвращает flow авторизации в модульном журнале.
     */
    fun signIn(userLogin: String, userPassword: String) = flow {
        emit(State.loading())
        val response = api.getSemesters(userLogin, userPassword).await()
        saveCacheStudentData(StudentData.fromResponse(response))
        ModuleJournalPreference.saveSignData(MainApplication.instance, userLogin, userPassword)
        emit(State.success(true))
    }.flowOn(Dispatchers.IO)

    /**
     * Загружает данные о студенте из модульного журнала
     */
    suspend fun loadStudentData(refresh: Boolean = false): StudentData {
        if (refresh) {
            val networkData = loadNetworkStudentData()
            saveCacheStudentData(networkData)
            return networkData
        }

        val cacheData = loadCacheStudentData()
        if (cacheData == null || !cacheData.isValid()) {
            val networkData = loadNetworkStudentData()
            saveCacheStudentData(networkData)
            return networkData
        }

        return cacheData
    }

    /**
     * Вычисляет рейтинг студента, исходя из оценок в семестрах.
     */
    suspend fun computePredictedRating(semesters: List<String>): Double {
        var rating = 0.0
        for (semester in semesters) {
            rating += loadSemesterMarks(semester).computeRating()
        }
        return rating / semesters.size
    }

    /**
     * Загружает данные об оценках студента в семестре из модульного журнала
     */
    suspend fun loadSemesterMarks(semester: String, refresh: Boolean = false): SemesterMarks {
        if (refresh) {
            val networkMarks = loadNetworkSemesterMarks(semester)
            saveCacheSemesterMarks(networkMarks, semester)
            return networkMarks
        }

        val cacheMarks = loadCacheSemesterMarks(semester)
        if (cacheMarks == null || !cacheMarks.isValid()) {
            val networkMarks = loadNetworkSemesterMarks(semester)
            saveCacheSemesterMarks(networkMarks, semester)
            return networkMarks
        }

        return cacheMarks
    }

    /**
     * Возвращает данные для входа в модульный журнал.
     */
    private fun loadLoginData(): Pair<String, String> {
        if (login == null || password == null) {
            val signData = ModuleJournalPreference.loadSignData(MainApplication.instance)
            login = signData.first!!
            password = signData.second!!
        }
        return Pair(login!!, password!!)
    }

    /**
     * Загружает данные семестра студента по сети.
     */
    private suspend fun loadNetworkSemesterMarks(semester: String): SemesterMarks {
        val (login, password) = loadLoginData()

        val response = api.getMarks(login, password, semester).await()
        return SemesterMarks.fromResponse(response)
    }

    /**
     * Загружает данные о студенте по сети.
     */
    private suspend fun loadNetworkStudentData(): StudentData {
        val (login, password) = loadLoginData()

        val response = api.getSemesters(login, password).await()
        return StudentData.fromResponse(response)
    }

    /**
     * Загружает данные семестра из кэша.
     */
    private fun loadCacheSemesterMarks(semester: String): SemesterMarks? {
        val file = FileUtils.getFile(cacheDir, SEMESTERS_FOLDER, "$semester.json")
        if (!file.exists()) {
            return null
        }

        try {
            val json = FileUtils.readFileToString(file, StandardCharsets.UTF_8)
            return gson.fromJson(json, SemesterMarks::class.java)
        } catch (ignored: Exception) {

        }

        return null
    }

    /**
     * Загружает данные о студенте из кэша.
     */
    private fun loadCacheStudentData(): StudentData? {
        val file = FileUtils.getFile(cacheDir, STUDENT_FOLDER, STUDENT_FILE)
        if (!file.exists()) {
            return null
        }

        try {
            val json = FileUtils.readFileToString(file, StandardCharsets.UTF_8)
            return gson.fromJson(json, StudentData::class.java)
        } catch (ignored: Exception) {

        }

        return null
    }

    /**
     * Сохраняет в кэш семестр с оценками.
     */
    private fun saveCacheSemesterMarks(marks: SemesterMarks, semester: String) {
        val file = FileUtils.getFile(cacheDir, SEMESTERS_FOLDER, "$semester.json")
        try {
            val json = gson.toJson(marks)
            FileUtils.writeStringToFile(file, json, StandardCharsets.UTF_8, false)
        } catch (ignored: Exception) {

        }
    }

    /**
     * Сохраняет в кэш данные о студенте.
     */
    private fun saveCacheStudentData(data: StudentData) {
        val file = FileUtils.getFile(cacheDir, STUDENT_FOLDER, STUDENT_FILE)
        try {
            val json = gson.toJson(data)
            FileUtils.writeStringToFile(file, json, StandardCharsets.UTF_8, false)
        } catch (ignored: Exception) {

        }
    }

    /**
     * Очищает весь кэш модульного журнала.
     */
    private fun clearCache() {
        val cacheStudent = FileUtils.getFile(cacheDir, STUDENT_FOLDER, STUDENT_FILE)
        cacheStudent.deleteRecursively()
    }

    companion object {
        /**
         * Адрес модульного журнала.
         */
        const val BASE_URL = "https://lk.stankin.ru"

        private const val STUDENT_FOLDER = "student_data"
        private const val STUDENT_FILE = "student.json"
        private const val SEMESTERS_FOLDER = "semesters_data"
    }
}