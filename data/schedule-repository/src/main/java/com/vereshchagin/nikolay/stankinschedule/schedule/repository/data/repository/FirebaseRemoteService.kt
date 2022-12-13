package com.vereshchagin.nikolay.stankinschedule.schedule.repository.data.repository

import com.vereshchagin.nikolay.stankinschedule.schedule.repository.data.api.ScheduleItemResponse
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.data.api.ScheduleRepositoryAPI
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.data.mapper.toDescription
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.data.mapper.toItem
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.domain.model.RepositoryDescription
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.domain.model.RepositoryItem
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.domain.repository.ScheduleRemoteService
import retrofit2.await
import javax.inject.Inject


class FirebaseRemoteService @Inject constructor(
    private val api: ScheduleRepositoryAPI,
) : FirebaseService(), ScheduleRemoteService {

    override suspend fun description(): RepositoryDescription {
        val ref = createRef(SCHEDULES_ROOT, SCHEDULES_DESCRIPTION)
        val descriptionUrl = ref.downloadUrl.await().toString()
        return api.description(descriptionUrl).await().toDescription()
    }

    override suspend fun category(category: String): List<RepositoryItem> {
        val ref = createRef(SCHEDULES_ROOT, category)
        val list = ref.listAll().await()
        return list.items.map {
            ScheduleItemResponse(
                name = it.name.removeSuffix(".json"),
                path = it.name,
            ).toItem(category)
        }
    }

    companion object {
        private const val SCHEDULES_DESCRIPTION = "description.json"
    }
}