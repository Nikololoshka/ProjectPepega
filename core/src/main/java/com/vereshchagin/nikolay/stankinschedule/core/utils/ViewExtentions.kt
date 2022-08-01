package com.vereshchagin.nikolay.stankinschedule.core.utils

import android.view.View

fun View.setVisibility(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}