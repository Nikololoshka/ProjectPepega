package com.vereshchagin.nikolay.stankinschedule.core.ui.utils

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import coil.imageLoader
import com.vereshchagin.nikolay.stankinschedule.core.ui.R

fun newsImageLoader(
    context: Context,
    cacheName: String = "image_cache",
): ImageLoader {
    return context.imageLoader.newBuilder()
        .crossfade(true)
        .crossfade(300)
        .placeholder(R.drawable.news_preview_placeholder)
        .diskCache(
            DiskCache.Builder()
                .directory(context.cacheDir.resolve(cacheName))
                .maximumMaxSizeBytes(1024 * 1024 * 64)
                .build()
        )
        .build()
}