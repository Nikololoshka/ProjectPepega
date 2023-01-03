package com.vereshchagin.nikolay.stankinschedule.table.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.repository.AndroidPublicProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class AndroidPublicProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AndroidPublicProvider {

    override fun createUri(name: String, bitmap: Bitmap): Uri {
        val folder = getSharedFolder()

        val file = File(folder, "$name.jpeg")
        file.outputStream().use { stream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
        }

        return FileProvider.getUriForFile(context, APP_PROVIDER_AUTHOR, file)
    }

    override fun createUri(name: String, pdf: PdfDocument): Uri {
        val folder = getSharedFolder()

        val file = File(folder, "$name.pdf")
        file.outputStream().use { stream ->
            pdf.writeTo(stream)
        }

        return FileProvider.getUriForFile(context, APP_PROVIDER_AUTHOR, file)
    }

    override fun exportPdf(pdf: PdfDocument, uri: Uri) {
        val contentResolver = context.contentResolver
        contentResolver.openOutputStream(uri).use { stream ->
            if (stream == null) throw IllegalAccessException("Failed to get file descriptor")
            pdf.writeTo(stream)
        }
    }

    private fun getSharedFolder(): File {
        val folder = File(context.cacheDir, "shared_data")
        folder.deleteRecursively()
        folder.mkdirs()

        return folder
    }

    companion object {
        private const val APP_PROVIDER_AUTHOR = "com.vereshchagin.nikolay.stankinschedule.provider"
    }
}