package com.vereshchagin.nikolay.stankinschedule.core.data.repository

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.vereshchagin.nikolay.stankinschedule.core.domain.repository.DeviceRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DeviceRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : DeviceRepository {

    override fun extractFilename(path: String): String? {
        val uri = Uri.parse(path)
        return uri.extractFileName(context.contentResolver)
    }


    private fun Uri.extractFileName(contentResolver: ContentResolver): String? {
        return contentResolver.query(this, null, null, null, null)?.use { cursor ->
            cursor.moveToFirst()
            val columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (columnIndex >= 0) cursor.getString(columnIndex) else null
        }
    }
}