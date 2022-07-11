package com.vereshchagin.nikolay.stankinschedule.news.ui.components

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable

fun defaultImageLoader(
    context: Context,
    cacheName: String = "image_cache",
): ImageLoader {
    return ImageLoader(context).newBuilder()
        .crossfade(true)
        .crossfade(300)
        .placeholder(
            ShimmerDrawable().apply {
                setShimmer(
                    Shimmer.AlphaHighlightBuilder()
                        .setDuration(2000)
                        .setBaseAlpha(0.7f)
                        .setHighlightAlpha(0.6f)
                        .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
                        .setAutoStart(true)
                        .build()
                )
            }
        )
        .diskCache(
            DiskCache.Builder()
                .directory(context.cacheDir.resolve(cacheName))
                .maximumMaxSizeBytes(1024 * 1024 * 64)
                .build()
        )
        .build()
}