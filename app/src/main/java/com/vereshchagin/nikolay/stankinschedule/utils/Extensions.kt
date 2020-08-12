package com.vereshchagin.nikolay.stankinschedule.utils

import com.google.android.material.textfield.MaterialAutoCompleteTextView

fun MaterialAutoCompleteTextView.currentPosition(): Int {
    val currentText = text.toString()
    for (i in 0 until adapter.count) {
        val item = adapter.getItem(i) as String
        if (item == currentText) {
            return i
        }
    }
    return -1
}

fun MaterialAutoCompleteTextView.setCurrentPosition(position: Int) {
    val item = adapter.getItem(position) as String
    setText(item, false)
}