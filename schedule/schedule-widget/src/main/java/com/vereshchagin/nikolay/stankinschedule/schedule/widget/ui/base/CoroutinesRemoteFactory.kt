package com.vereshchagin.nikolay.stankinschedule.schedule.widget.ui.base

import android.content.Context
import android.widget.RemoteViewsService.RemoteViewsFactory
import kotlinx.coroutines.runBlocking

abstract class CoroutinesRemoteFactory(context: Context) : RemoteViewsFactory {

    val packageName = context.packageName

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