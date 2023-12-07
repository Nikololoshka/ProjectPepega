package com.vereshchagin.nikolay.stankinschedule.home.ui.data

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateOptions
import kotlinx.coroutines.flow.StateFlow

interface InAppUpdater {

    val updateState: StateFlow<UpdateState?>

    suspend fun checkUpdate()

    fun startUpdate(
        info: AppUpdateInfo,
        launcher: ActivityResultLauncher<IntentSenderRequest>,
        options: AppUpdateOptions
    )

    fun later()

    fun completeUpdate()

    fun onDestroy()

}

interface UpdateState {
    class UpdateRequired(val info: AppUpdateInfo) : UpdateState
    class UpdateProgress(val progress: Float) : UpdateState
    object UpdateRestart : UpdateState
    object UpToDate : UpdateState
}