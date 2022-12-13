package com.vereshchagin.nikolay.stankinschedule.journal.core.domain.repository

import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.exceptions.StudentAuthorizedException
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.model.StudentCredentials
import java.security.GeneralSecurityException

interface JournalSecureRepository {

    @Throws(GeneralSecurityException::class)
    suspend fun signIn(credentials: StudentCredentials)

    suspend fun signOut()

    @Throws(StudentAuthorizedException::class)
    suspend fun signCredentials(): StudentCredentials
}