package com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.repository

import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import android.net.Uri

interface AndroidPublicProvider {

    fun createUri(name: String, bitmap: Bitmap): Uri

    fun createUri(name: String, pdf: PdfDocument): Uri

    fun exportPdf(pdf: PdfDocument, uri: Uri)

}