package com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.repository

import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.model.StudentCredentials
import java.security.GeneralSecurityException

interface JournalSecureRepository {

    @Throws(GeneralSecurityException::class)
    suspend fun signIn(credentials: StudentCredentials)

    suspend fun signOut()

    @Throws(GeneralSecurityException::class)
    suspend fun signCredentials(): StudentCredentials?
}