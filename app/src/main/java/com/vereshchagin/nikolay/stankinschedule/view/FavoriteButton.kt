package com.vereshchagin.nikolay.stankinschedule.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.vereshchagin.nikolay.stankinschedule.R

/**
 * Кнопка "избранное" с анимацией.
 */
class FavoriteButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : AppCompatImageButton(context, attrs, defStyleAttr) {

    private var isFavorite = false

    private val notFavoriteDrawable =
        ContextCompat.getDrawable(context, R.drawable.ic_favorite_star_unchecked)!!

    private val favoriteDrawable =
        ContextCompat.getDrawable(context, R.drawable.ic_favorite_star_checked)!!

    private val favoriteActivatedDrawable =
        AnimatedVectorDrawableCompat.create(context, R.drawable.avd_favorite_button)!!

    init {
        setImageDrawable(notFavoriteDrawable)
    }

    /**
     * Устанавливает drawable на view.
     * @param toggle - состояние включить / выключить.
     * @param animate - включить с анимацией.
     */
    fun setToggle(toggle: Boolean, animate: Boolean) {
        if (toggle == isFavorite) {
            return
        }

        isFavorite = toggle
        if (animate) {
            setImageDrawable(favoriteActivatedDrawable)
            favoriteActivatedDrawable.start()
            return
        }

        val drawable = if (isFavorite) favoriteDrawable else notFavoriteDrawable
        setImageDrawable(drawable)
    }
}