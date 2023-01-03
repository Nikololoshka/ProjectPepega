package com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.repository

import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleModel
import com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.model.TableConfig

interface AndroidTableCreator {

    fun createPdf(schedule: ScheduleModel, config: TableConfig): PdfDocument

    fun createImage(schedule: ScheduleModel, config: TableConfig): Bitmap

}