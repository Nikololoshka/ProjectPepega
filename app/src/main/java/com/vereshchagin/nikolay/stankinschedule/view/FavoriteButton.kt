package com.vereshchagin.nikolay.stankinschedule.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.vereshchagin.nikolay.stankinschedule.R

/**
 * Кнопка "избранное" с анимацией.
 */
class FavoriteButton : AppCompatImageButton {
    var mIsFavorite = false
    var mNotFavorite: Drawable? = null
    var mFavorite: Drawable? = null
    var mFavoriteActivated: AnimatedVectorDrawableCompat? = null

    constructor(context: Context?) : super(context!!) {
        initialization()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
        initialization()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!, attrs, defStyleAttr
    ) {
        initialization()
    }

    private fun initialization() {
        mIsFavorite = false
        mNotFavorite = ContextCompat.getDrawable(context, R.drawable.ic_favorite_star_unchecked)
        mFavorite = ContextCompat.getDrawable(context, R.drawable.ic_favorite_star_checked)
        mFavoriteActivated =
            AnimatedVectorDrawableCompat.create(context, R.drawable.avd_favorite_button)
        setImageDrawable(mNotFavorite)
    }

    /**
     * Устанавливает drawable на view.
     * @param toggle - состояние включить / выключить.
     * @param animate - включить с анимацией.
     */
    fun setToggle(toggle: Boolean, animate: Boolean) {
        if (toggle == mIsFavorite) {
            return
        }
        if (DEBUG) {
            Log.d(TAG, "setToggle: $toggle")
        }
        mIsFavorite = toggle
        if (animate) {
            setImageDrawable(mFavoriteActivated)
            mFavoriteActivated!!.start()
            return
        }
        val drawable = if (mIsFavorite) mFavorite else mNotFavorite
        setImageDrawable(drawable)
    }

    companion object {
        private const val TAG = "FavoriteButtonLog"
        private const val DEBUG = false
    }
}