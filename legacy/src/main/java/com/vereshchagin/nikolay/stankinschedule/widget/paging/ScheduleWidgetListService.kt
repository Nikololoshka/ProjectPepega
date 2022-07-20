package com.vereshchagin.nikolay.stankinschedule.widget.paging

import android.content.Intent
import android.widget.RemoteViewsService
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Сервис, который создает адаптер по обновлению данных виджета.
 */
@AndroidEntryPoint
class ScheduleWidgetListService : RemoteViewsService() {

    @Inject
    lateinit var repository: ScheduleRepository

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return ScheduleWidgetListRemoteFactory(applicationContext, intent, repository)
    }
}