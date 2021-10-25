package com.vereshchagin.nikolay.stankinschedule.repository

import com.google.gson.GsonBuilder
import com.vereshchagin.nikolay.stankinschedule.MainApplication
import com.vereshchagin.nikolay.stankinschedule.api.ModuleJournalAPI2
import com.vereshchagin.nikolay.stankinschedule.model.modulejournal.MarkType
import com.vereshchagin.nikolay.stankinschedule.model.modulejournal.SemesterMarks
import com.vereshchagin.nikolay.stankinschedule.model.modulejournal.StudentData
import com.vereshchagin.nikolay.stankinschedule.settings.ModuleJournalPreference
import com.vereshchagin.nikolay.stankinschedule.utils.CacheFolder
import com.vereshchagin.nikolay.stankinschedule.utils.State
import com.vereshchagin.nikolay.stankinschedule.utils.convertors.gson.DateTimeTypeConverter
import com.vereshchagin.nikolay.stankinschedule.utils.convertors.gson.MarkTypeTypeConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.joda.time.DateTime
import retrofit2.await
import javax.inject.Inject

/**
 * Репозиторий для работы с модульным журналом.
 */
class ModuleJournalRepository @Inject constructor(
    private val api: ModuleJournalAPI2,
    private val cacheFolder: CacheFolder,
) {
    /**
     * Mutex для синхронизации доступа к данным.
     */
    private val mutex = Mutex()

    /**
     * Кэш данных для входа в модульный журнал.
     */
    private var userData: Pair<String, String>? = null

    init {
        cacheFolder.addStartedPath(STUDENT_FOLDER)
        cacheFolder.gson = GsonBuilder()
            .registerTypeAdapter(DateTime::class.java, DateTimeTypeConverter())
            .registerTypeAdapter(MarkType::class.java, MarkTypeTypeConverter())
            .create()
    }

    /**
     * Возвращает flow авторизации в модульном журнале.
     */
    fun signIn(userLogin: String, userPassword: String) = flow {
        emit(State.loading())
        try {
            val response = api.getSemesters(userLogin, userPassword).await()
            cacheFolder.saveToCache(StudentData.fromResponse(response), STUDENT_FILE)
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
        cacheFolder.clearAll()
    }

    /**
     * Возвращает flow информации о студенте.
     */
    fun studentData(useCache: Boolean = true) = flow {
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
            cacheFolder.saveToCache(networkData, STUDENT_FILE)
            return networkData
        }

        val cacheData = cacheFolder.loadFromCache(StudentData::class.java, STUDENT_FILE)
        if (cacheData == null || !cacheData.isValid()) {
            return try {
                val networkData = loadNetworkStudentData()
                cacheFolder.saveToCache(networkData, STUDENT_FILE)
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
            val networkMarks = loadNetworkSemesterMarks("$semester.json")
            cacheFolder.saveToCache(networkMarks, "$semester.json")
            return networkMarks
        }

        val cacheMarks = cacheFolder.loadFromCache(SemesterMarks::class.java, "$semester.json")
        if (cacheMarks == null || !cacheMarks.isValid(last)) {
            return try {
                val networkMarks = loadNetworkSemesterMarks(semester)
                cacheFolder.saveToCache(networkMarks, "$semester.json")
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

    fun loadCacheStudentData(): StudentData? {
        return cacheFolder.loadFromCache(StudentData::class.java, STUDENT_FILE)
    }

    fun loadCacheSemesterMarks(semester: String): SemesterMarks? {
        return cacheFolder.loadFromCache(SemesterMarks::class.java, "$semester.json")
    }

    companion object {

        private const val STUDENT_FOLDER = "student_data"
        private const val STUDENT_FILE = "student.json"

        private const val TAG = "ModuleJournalRepoLog"
    }
}