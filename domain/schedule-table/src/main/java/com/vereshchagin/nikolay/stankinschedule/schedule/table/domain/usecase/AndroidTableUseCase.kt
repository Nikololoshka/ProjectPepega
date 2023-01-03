package com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.usecase

import android.graphics.Bitmap
import android.net.Uri
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleModel
import com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.model.TableConfig
import com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.repository.AndroidPublicProvider
import com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.repository.AndroidTableCreator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AndroidTableUseCase @Inject constructor(
    private val provider: AndroidPublicProvider,
    private val creator: AndroidTableCreator
) {

    fun createBitmap(schedule: ScheduleModel, config: TableConfig): Bitmap {
        return creator.createImage(schedule, config)
    }

    fun savePdfTable(
        schedule: ScheduleModel,
        config: TableConfig,
        uri: Uri
    ): Flow<Uri> = flow {
        val pdf = creator.createPdf(schedule, config)
        provider.exportPdf(pdf, uri)
        emit(uri)
    }.flowOn(Dispatchers.IO)

    fun createUriForPdf(
        name: String,
        schedule: ScheduleModel,
        config: TableConfig,
    ): Flow<Uri> = flow {
        val pdf = creator.createPdf(schedule, config)
        val uri = provider.createUri(name, pdf)
        emit(uri)
    }.flowOn(Dispatchers.IO)

    fun createUriForImage(
        name: String,
        schedule: ScheduleModel,
        config: TableConfig,
    ): Flow<Uri> = flow {
        val bitmap = creator.createImage(schedule, config)
        val uri = provider.createUri(name, bitmap)
        emit(uri)
    }.flowOn(Dispatchers.IO)
}