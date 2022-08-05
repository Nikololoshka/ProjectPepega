package com.vereshchagin.nikolay.stankinschedule.schedule.data.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.vereshchagin.nikolay.stankinschedule.schedule.data.api.DescriptionResponse
import com.vereshchagin.nikolay.stankinschedule.schedule.data.api.PairResponse
import com.vereshchagin.nikolay.stankinschedule.schedule.data.api.ScheduleItemResponse
import com.vereshchagin.nikolay.stankinschedule.schedule.data.api.ScheduleRepositoryAPI
import com.vereshchagin.nikolay.stankinschedule.schedule.domain.repository.ScheduleRemoteService
import retrofit2.await
import javax.inject.Inject
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class FirebaseRemoteService @Inject constructor(
    private val api: ScheduleRepositoryAPI,
) : ScheduleRemoteService {

    private val storage = Firebase.storage

    override suspend fun description(): DescriptionResponse {
        val ref = createRef(SCHEDULES_ROOT, SCHEDULES_DESCRIPTION)
        val descriptionUrl = ref.downloadUrl.await().toString()
        return api.description(descriptionUrl).await()
    }

    override suspend fun category(category: String): List<ScheduleItemResponse> {
        val ref = createRef(SCHEDULES_ROOT, category)
        val list = ref.listAll().await()
        return list.items.map {
            ScheduleItemResponse(
                name = it.name.removeSuffix(".json"),
                path = it.name,
            )
        }
    }

    override suspend fun schedule(category: String, schedule: String): List<PairResponse> {
        val ref = createRef(SCHEDULES_ROOT, category, schedule)
        val scheduleUrl = ref.downloadUrl.await().toString()
        return api.schedule(scheduleUrl).await()
    }

    private fun createRef(vararg paths: String): StorageReference {
        return storage.getReference(paths.joinToString("/"))
    }

    private suspend fun <T> Task<T>.await(): T {
        return suspendCoroutine { continuation ->
            addOnSuccessListener {
                continuation.resumeWith(Result.success(it))
            }
            addOnFailureListener {
                continuation.resumeWithException(it)
            }
        }
    }

    companion object {
        private const val SCHEDULES_ROOT = "schedules"
        private const val SCHEDULES_DESCRIPTION = "description.json"

        private const val TAG = "ScheduleServerLog"
    }
}