package com.vereshchagin.nikolay.stankinschedule.schedule.parser.domain.repository

import android.graphics.Bitmap
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.domain.model.ParseDetail

interface PDFRepository {

    suspend fun parsePDF(
        path: String,
        multilineTextThreshold: Float = 1f
    ): ParseDetail

    suspend fun renderPDF(path: String): Bitmap

}