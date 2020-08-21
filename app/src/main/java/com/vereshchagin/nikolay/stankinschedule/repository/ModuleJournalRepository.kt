package com.vereshchagin.nikolay.stankinschedule.repository

import com.google.gson.Gson
import com.vereshchagin.nikolay.stankinschedule.BuildConfig
import com.vereshchagin.nikolay.stankinschedule.MainApplication
import com.vereshchagin.nikolay.stankinschedule.api.ModuleJournalApi2
import com.vereshchagin.nikolay.stankinschedule.model.modulejournal.MarkType
import com.vereshchagin.nikolay.stankinschedule.model.modulejournal.SemesterMarks
import com.vereshchagin.nikolay.stankinschedule.model.modulejournal.StudentData
import com.vereshchagin.nikolay.stankinschedule.ui.settings.ModuleJournalPreference
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.apache.commons.io.FileUtils
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.nio.charset.StandardCharsets


class ModuleJournalRepository(private val cacheDir: File) {

    private var retrofit: Retrofit
    private var api: ModuleJournalApi2

    private val gson = Gson()

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

    fun loadStudentData(force: Boolean = false) : StudentData? {
        if (force) {
            val networkData = loadNetworkStudentData()
            if (networkData != null) {
                saveCacheStudentData(networkData)
                return networkData
            }
            return null
        }

        val cacheData = loadCacheStudentData()
        if (cacheData == null || !cacheData.isValid()) {

            val networkData = loadNetworkStudentData()
            if (networkData != null) {
                saveCacheStudentData(networkData)
                return networkData

            } else if (cacheData != null) {
                return cacheData
            }
        }
        return null
    }

    fun computeRating(marks: SemesterMarks): Double {
        var ratingSum = 0.0
        var ratingCount = 0.0
        for (discipline in marks.disciplines) {
            var disciplineSum = 0.0
            var disciplineCount = 0.0
            for (type in MarkType.values()) {
                discipline.marks[type]?.let {
                    disciplineSum += it * type.weight
                    disciplineCount += type.weight
                }
            }
            ratingSum += (disciplineSum / disciplineCount) * discipline.factor
            ratingCount += discipline.factor
        }
        return ratingSum / ratingCount
    }

    fun loadSemesterMarks(semester: String, force: Boolean = false): SemesterMarks? {
        if (force) {
            val cacheMarks = loadCacheSemesterMarks(semester)
            if (cacheMarks != null) {
                saveCacheSemesterMarks(cacheMarks, semester)
                return cacheMarks
            }
            return null
        }

        val cacheMarks = loadCacheSemesterMarks(semester)
        if (cacheMarks == null || !cacheMarks.isValid()) {
            val networkMarks = loadNetworkSemesterMarks(semester)
            if (networkMarks != null) {
                saveCacheSemesterMarks(networkMarks, semester)
                return networkMarks
            } else if (cacheMarks != null) {
                return cacheMarks
            }
        }
        return null
    }

    private fun loadLoginData(): Pair<String, String> {
        if (login == null || password == null) {
            val signData = ModuleJournalPreference.loadSignData(MainApplication.getInstance())
            login = signData.first!!
            password = signData.second!!
        }
        return Pair(login!!, password!!)
    }

    private fun loadNetworkSemesterMarks(semester: String): SemesterMarks? {
        val (login, password) = loadLoginData()

        val response = api.getMarks(login, password, semester).execute()
        if (response.isSuccessful) {
            val marksResponse = response.body()!!
            return SemesterMarks.fromResponse(marksResponse)
        }
        return null
    }

    private fun loadNetworkStudentData(): StudentData? {
        val (login, password) = loadLoginData()

        val response = api.getSemesters(login, password).execute()
        if (response.isSuccessful) {
            val semestersResponse = response.body()!!
            return StudentData(semestersResponse)
        }
        return null
    }

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

    private fun saveCacheSemesterMarks(marks: SemesterMarks, semester: String) {
        val file = FileUtils.getFile(cacheDir, SEMESTERS_FOLDER, "$semester.json")
        try {
            val json = gson.toJson(marks)
            FileUtils.writeStringToFile(file, json, StandardCharsets.UTF_8, false)
        } catch (ignored: Exception) {

        }
    }

    private fun saveCacheStudentData(data: StudentData) {
        val file = FileUtils.getFile(cacheDir, STUDENT_FOLDER, STUDENT_FILE)
        try {
            val json = gson.toJson(data)
            FileUtils.writeStringToFile(file, json, StandardCharsets.UTF_8, false)
        } catch (ignored: Exception) {

        }
    }

    private fun clearCache() {
        val cacheStudent = FileUtils.getFile(cacheDir, STUDENT_FOLDER, STUDENT_FILE)
        FileUtils.deleteQuietly(cacheStudent)
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