package com.vereshchagin.nikolay.stankinschedule.schedule.widget.ui

import android.content.Intent
import android.widget.RemoteViewsService
import com.vereshchagin.nikolay.stankinschedule.schedule.widget.domain.usecase.ScheduleWidgetUseCase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ScheduleWidgetService : RemoteViewsService() {

    @Inject
    lateinit var useCase: ScheduleWidgetUseCase

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return ScheduleWidgetRemoteFactory(
            context = applicationContext,
            intent = intent,
            useCase = useCase
        )
    }
}