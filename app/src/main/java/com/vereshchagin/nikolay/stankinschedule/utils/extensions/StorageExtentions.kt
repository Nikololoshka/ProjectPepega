package com.vereshchagin.nikolay.stankinschedule.utils.extensions

import android.net.Uri
import com.google.android.gms.tasks.Task
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun Task<Uri>.await(): Uri {
    return suspendCoroutine { continuation ->
        this
            .addOnSuccessListener {
                continuation.resume(it)
            }
            .addOnFailureListener {
                continuation.resumeWithException(it)
            }
    }
}