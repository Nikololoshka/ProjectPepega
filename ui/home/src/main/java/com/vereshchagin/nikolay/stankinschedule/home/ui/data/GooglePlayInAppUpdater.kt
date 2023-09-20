package com.vereshchagin.nikolay.stankinschedule.home.ui.data

import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.tasks.Task
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.bytesDownloaded
import com.google.android.play.core.ktx.installStatus
import com.google.android.play.core.ktx.totalBytesToDownload
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class GooglePlayInAppUpdater constructor(
    context: Context,
) : InAppUpdater, InstallStateUpdatedListener {

    private val appUpdater = AppUpdateManagerFactory.create(context)

    private val _updateState = MutableStateFlow<UpdateState?>(null)
    override val updateState: StateFlow<UpdateState?> = _updateState.asStateFlow()

    init {
        appUpdater.registerListener(this)
    }

    override suspend fun checkUpdate() {
        try {
            val updateResult = appUpdater.appUpdateInfo.await()

            val stalenessDays = updateResult.clientVersionStalenessDays()
            if (updateResult.isUpdateAvailability(stalenessDays)) {
                _updateState.value = UpdateState.UpdateRequired(updateResult)
                return
            }
        } catch (ignored: Exception) {

        }

        _updateState.value = UpdateState.UpToDate
    }

    override fun startUpdate(
        info: AppUpdateInfo,
        launcher: ActivityResultLauncher<IntentSenderRequest>,
        options: AppUpdateOptions
    ) {
        appUpdater.startUpdateFlowForResult(info, launcher, options)
    }

    override fun onStateUpdate(state: InstallState) {
        if (state.installStatus == InstallStatus.DOWNLOADING) {
            val progress = state.bytesDownloaded / state.totalBytesToDownload.toFloat()
            _updateState.value = UpdateState.UpdateProgress(progress)
        }
        if (state.installStatus == InstallStatus.DOWNLOADED) {
            _updateState.value = UpdateState.UpdateRestart
        }
    }

    override fun later() {
        _updateState.value = UpdateState.UpToDate
    }

    override fun completeUpdate() {
        appUpdater.completeUpdate()
    }

    override fun onDestroy() {
        appUpdater.unregisterListener(this)
    }

    private fun AppUpdateInfo.isUpdateAvailability(stalenessDays: Int?): Boolean {
        return updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
                && stalenessDays != null
                && stalenessDays >= DAYS_FOR_FLEXIBLE_UPDATE
    }

    private suspend fun <T> Task<T>.await(): T {
        return suspendCoroutine { continuation ->
            addOnSuccessListener { result ->
                continuation.resume(result)
            }
            addOnFailureListener { error ->
                continuation.resumeWithException(error)
            }
        }
    }

    companion object {
        const val UPDATE_REQUEST = 1
        const val DAYS_FOR_FLEXIBLE_UPDATE = 7
    }
}