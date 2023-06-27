package com.vereshchagin.nikolay.stankinschedule.core.domain.repository

interface DeviceRepository {

    fun extractFilename(path: String): String?

}