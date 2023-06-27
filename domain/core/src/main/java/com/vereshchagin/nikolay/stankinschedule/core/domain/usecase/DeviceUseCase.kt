package com.vereshchagin.nikolay.stankinschedule.core.domain.usecase

import com.vereshchagin.nikolay.stankinschedule.core.domain.repository.DeviceRepository
import javax.inject.Inject

class DeviceUseCase @Inject constructor(
    private val device: DeviceRepository
) {

    fun extractFilename(path: String, removeExtension: Boolean = false): String {
        val filename = device.extractFilename(path)
            ?: throw IllegalArgumentException("Can't extract filename")

        if (removeExtension) {
            return filename.substringBeforeLast('.')
        }

        return filename
    }
}