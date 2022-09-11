package com.vereshchagin.nikolay.stankinschedule.schedule.creator.utils

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns

fun Uri.extractFileName(contentResolver: ContentResolver): String? {
    return contentResolver.query(this, null, null, null, null)?.use { cursor ->
        cursor.moveToFirst()
        cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
    }
}