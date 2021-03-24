package com.vereshchagin.nikolay.stankinschedule.repository

import android.util.Log
import com.google.gson.GsonBuilder
import com.vereshchagin.nikolay.stankinschedule.BuildConfig
import com.vereshchagin.nikolay.stankinschedule.MainApplication
import com.vereshchagin.nikolay.stankinschedule.api.ModuleJournalAPI2
import com.vereshchagin.nikolay.stankinschedule.model.modulejournal.MarkType
import com.vereshchagin.nikolay.stankinschedule.model.modulejournal.SemesterMarks
import com.vereshchagin.nikolay.stankinschedule.model.modulejournal.StudentData
import com.vereshchagin.nikolay.stankinschedule.settings.ModuleJournalPreference
import com.vereshchagin.nikolay.stankinschedule.utils.State
import com.vereshchagin.nikolay.stankinschedule.utils.convertors.gson.DateTimeTypeConverter
import com.vereshchagin.nikolay.stankinschedule.utils.convertors.gson.MarkTypeTypeConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.apache.commons.io.FileUtils
import org.joda.time.DateTime
import retrofit2.Retrofit
import retrofit2.await
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.nio.charset.StandardCharsets

/**
 * Репозиторий для работы с модульным журналом.
 */
class ModuleJournalRepository(private val cacheDir: File) {

    private val mutex = Mutex()

    private var retrofit: Retrofit
    private var api: ModuleJournalAPI2

    private val gson = GsonBuilder()
        .registerTypeAdapter(DateTime::class.java, DateTimeTypeConverter())
        .registerTypeAdapter(MarkType::class.java, MarkTypeTypeConverter())
        .create()

    /**
     * Кэш данных для входа в модульный журнал.
     */
    private var userData: Pair<String, String>? = null

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
        api = retrofit.create(ModuleJournalAPI2::class.java)
    }

    /**
     * Возвращает flow авторизации в модульном журнале.
     */
    fun signIn(userLogin: String, userPassword: String) = flow<State<Boolean>> {
        emit(State.loading())
        try {
            val response = api.getSemesters(userLogin, userPassword).await()
            saveCacheStudentData(StudentData.fromResponse(response))
            ModuleJournalPreference.signIn(
                MainApplication.instance.applicationContext,
                userLogin,
                userPassword
            )
            emit(State.success(true))

        } catch (e: Exception) {
            emit(State.failed(e))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Выполняет выход из модульного журнала.
     */
    fun signOut() {
        ModuleJournalPreference.signOut(MainApplication.instance.applicationContext)
        clearCache()
    }

    /**
     * Возвращает flow информации о студенте.
     */
    fun studentData(useCache: Boolean = true) = flow<State<StudentData>> {
        try {
            val studentData = loadStudentData(!useCache)
            emit(State.success(studentData))
        } catch (e: Exception) {
            emit(State.failed(e))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Возвращает flow прогнозируемого рейтинга студента.
     */
    fun predictedRating() = flow {
        emit(null)
        emit(computePredictedRating())
    }.flowOn(Dispatchers.IO)

    /**
     * Возвращает flow текущего рейтинга студента.
     */
    fun currentRating() = flow {
        emit(null)
        emit(computeCurrentRating())
    }.flowOn(Dispatchers.IO)

    /**
     * Загружает данные о студенте из модульного журнала
     */
    suspend fun loadStudentData(refresh: Boolean = false): StudentData = mutex.withLock {
        if (refresh) {
            val networkData = loadNetworkStudentData()
            saveCacheStudentData(networkData)
            return networkData
        }

        val cacheData = loadCacheStudentData()
        if (cacheData == null || !cacheData.isValid()) {
            return try {
                val networkData = loadNetworkStudentData()
                saveCacheStudentData(networkData)
                networkData

            } catch (e: Exception) {
                if (cacheData == null) {
                    throw e
                }
                cacheData
            }
        }

        return cacheData
    }

    /**
     * Вычисляет прогнозируемый рейтинг студента, исходя из оценок в семестрах.
     */
    private suspend fun computePredictedRating(): String {
        val student = loadStudentData()

        if (student.semesters.isEmpty()) {
            return "--.--"
        }

        val lastSemester = student.semesters.last()
        val lastSemesterMarks = loadSemesterMarks(lastSemester)

        // накопленный рейтинг
        var accumulatedRating = 0
        for (i in student.semesters.size - 2 downTo 0) {
            val semester = student.semesters[i]
            val rating = loadSemesterMarks(semester).accumulatedRating
            if (rating != null) {
                accumulatedRating = rating
                break
            }
        }

        // отсутствует накопленный рейтинг (первый семестр)
        if (accumulatedRating == 0) {
            val average = lastSemesterMarks.average()
            if (average == 0) {
                return "--.--"
            }
            accumulatedRating = average
        }

        val rating = lastSemesterMarks.computePredictedRating(accumulatedRating)
        if (rating == 0.0) {
            return "--.--"
        }

        return "%.2f".format(rating)
    }

    /**
     * Вычисляет текущий рейтинг студента, исходя из оценок в семестрах.
     */
    private suspend fun computeCurrentRating(): String {
        val data = loadStudentData()
        for (semester in data.semesters.reversed()) {
            val marks = loadSemesterMarks(semester)
            if (marks.isCompleted()) {
                return "%.2f".format(marks.computeRating())
            }
        }
        return "--.--"
    }

    /**
     * Загружает данные об оценках студента в семестре из модульного журнала
     */
    suspend fun loadSemesterMarks(
        semester: String,
        refresh: Boolean = false,
        last: Boolean = false,
    ): SemesterMarks = mutex.withLock {
        if (refresh) {
            val networkMarks = loadNetworkSemesterMarks(semester)
            saveCacheSemesterMarks(networkMarks, semester)
            return networkMarks
        }

        val cacheMarks = loadCacheSemesterMarks(semester)
        if (cacheMarks == null || !cacheMarks.isValid(last)) {
            return try {
                val networkMarks = loadNetworkSemesterMarks(semester)
                saveCacheSemesterMarks(networkMarks, semester)
                networkMarks

            } catch (e: Exception) {
                if (cacheMarks == null) {
                    throw e
                }
                cacheMarks
            }
        }

            return cacheMarks
        }

    /**
     * Возвращает данные для входа в модульный журнал.
     */
    private fun loadLoginData(): Pair<String, String> {
        val data = userData
        if (data == null) {
            val newUserData = ModuleJournalPreference.signInData(
                MainApplication.instance.applicationContext
            )
            userData = newUserData
            return newUserData
        }
        return data
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
    fun loadCacheSemesterMarks(semester: String): SemesterMarks? {
        val file = FileUtils.getFile(cacheDir, SEMESTERS_FOLDER, "$semester.json")
        if (!file.exists()) {
            return null
        }

        try {
            val json = FileUtils.readFileToString(file, StandardCharsets.UTF_8)
            return gson.fromJson(json, SemesterMarks::class.java)
        } catch (ignored: Exception) {
            ignored.printStackTrace()
        }

        return null
    }

    /**
     * Загружает данные о студенте из кэша.
     */
    fun loadCacheStudentData(): StudentData? {
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
            Log.d(TAG, "saveCacheSemesterMarks: $json")
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

        private const val TAG = "ModuleJournalRepoLog"
    }
}