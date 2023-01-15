package com.vereshchagin.nikolay.stankinschedule.core.ui.ext

import android.content.res.Resources
import android.util.TypedValue

fun dpToPx(value: Float, resources: Resources): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, value, resources.displayMetrics
    )
}

fun spToPx(value: Float, resources: Resources): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP, value, resources.displayMetrics
    )
}