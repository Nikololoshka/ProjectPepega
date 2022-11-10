package com.vereshchagin.nikolay.stankinschedule.schedule.widget.ui

import android.content.Intent
import android.widget.RemoteViewsService

class ScheduleWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return ScheduleWidgetRemoteFactory(applicationContext)
    }
}