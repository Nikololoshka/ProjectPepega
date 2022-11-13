package com.vereshchagin.nikolay.stankinschedule.schedule.widget.ui.base

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViewsService.RemoteViewsFactory
import kotlinx.coroutines.runBlocking

abstract class CoroutinesRemoteFactory(
    context: Context,
    intent: Intent
) : RemoteViewsFactory {

    val packageName: String = context.packageName

    val appWidgetId: Int = intent.getIntExtra(
        AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID
    )

    override fun onCreate() {}

    override fun onDataSetChanged() {
        runBlocking { onDataChanged() }
    }

    abstract suspend fun onDataChanged()

    override fun onDestroy() {}

    override fun getViewTypeCount(): Int = 1

    override fun hasStableIds(): Boolean = true

    override fun getItemId(position: Int): Long = position.toLong()

}