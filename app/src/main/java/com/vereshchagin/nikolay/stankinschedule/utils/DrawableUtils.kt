package com.vereshchagin.nikolay.stankinschedule.utils

import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable

/**
 * Вспомогательный класс для Drawable объектов.
 */
object DrawableUtils {

    /**
     * Создает объект Glide для фрагмента.
     */
    fun createGlide(fragment: Fragment) = Glide.with(fragment)
        .setDefaultRequestOptions(
            RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
        )

    /**
     * Создает Drawable с shimmer эффектом.
     */
    fun createShimmerDrawable() = ShimmerDrawable().apply {
        setShimmer(
            Shimmer.AlphaHighlightBuilder()
            .setDuration(2000)
            .setBaseAlpha(0.7f)
            .setHighlightAlpha(0.6f)
            .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
            .setAutoStart(true)
            .build())
    }
}