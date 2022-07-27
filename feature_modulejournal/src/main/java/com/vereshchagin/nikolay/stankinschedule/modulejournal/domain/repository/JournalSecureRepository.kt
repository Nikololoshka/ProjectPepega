package com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.repository

import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.model.StudentCredentials
import com.vereshchagin.nikolay.stankinschedule.modulejournal.util.StudentAuthorizedException
import java.security.GeneralSecurityException

interface JournalSecureRepository {

    @Throws(GeneralSecurityException::class)
    suspend fun signIn(credentials: StudentCredentials)

    suspend fun signOut()

    @Throws(StudentAuthorizedException::class)
    suspend fun signCredentials(): StudentCredentials
}