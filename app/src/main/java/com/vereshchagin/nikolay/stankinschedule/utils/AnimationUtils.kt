package com.vereshchagin.nikolay.stankinschedule.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.Interpolator
import androidx.transition.Fade
import androidx.transition.TransitionManager

object AnimationUtils {

    enum class Type {
        TRANSITION,
        PROPERTY
    }

    private const val DEFAULT_DURATION = 200L

    fun fade(
        view: View,
        fadeIn: Boolean,
        type: Type = Type.PROPERTY,
        duration: Long = DEFAULT_DURATION,
        interpolator: Interpolator = AccelerateInterpolator(2.0F)
    ) {
        when (type) {
            Type.TRANSITION -> fadeTransition(view, fadeIn, duration, interpolator)
            Type.PROPERTY -> fadeProperty(view, fadeIn, duration, interpolator)
        }
    }

    fun fadeTransition(view: View, fadeIn: Boolean, duration: Long, interpolator: Interpolator) {
        val fade = Fade(if (fadeIn) Fade.MODE_IN else Fade.MODE_OUT)
        fade.interpolator = interpolator
        fade.duration = duration

        val root = view.parent as ViewGroup?
        if (root != null) {
            TransitionManager.beginDelayedTransition(root, fade)
        }
    }

    fun fadeProperty(view: View, fadeIn: Boolean, duration: Long, interpolator: Interpolator) {
        if (fadeIn) {
            view.alpha = 0f
            view.visibility = View.VISIBLE

            view.animate()
                .setInterpolator(interpolator)
                .alpha(1f)
                .setDuration(duration)
                .setListener(
                    object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            view.visibility = View.VISIBLE
                        }
                    }
                )
        } else {
            view.animate()
                .setInterpolator(interpolator)
                .alpha(0f)
                .setDuration(duration)
                .setListener(
                    object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            view.visibility = View.GONE
                        }
                    }
                )
        }
    }
}