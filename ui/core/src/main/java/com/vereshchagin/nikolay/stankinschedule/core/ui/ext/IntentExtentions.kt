package com.vereshchagin.nikolay.stankinschedule.core.ui.ext

import android.content.Intent
import android.net.Uri

fun shareDataIntent(uri: Uri, memeType: String): Intent {
    return Intent(Intent.ACTION_SEND).apply {
        type = memeType
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
}
