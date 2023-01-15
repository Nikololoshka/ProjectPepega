package com.vereshchagin.nikolay.stankinschedule.schedule.repository.data.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

abstract class FirebaseService {

    protected val storage = Firebase.storage

    protected fun createRef(vararg paths: String): StorageReference {
        return storage.getReference(paths.joinToString("/"))
    }

    protected suspend fun <T> Task<T>.await(): T {
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
        const val SCHEDULES_ROOT = "schedules"
    }
}