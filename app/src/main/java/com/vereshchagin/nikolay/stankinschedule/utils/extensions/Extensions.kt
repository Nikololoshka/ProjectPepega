package com.vereshchagin.nikolay.stankinschedule.utils.extensions

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.vereshchagin.nikolay.stankinschedule.R
import java.util.*

/**
 * Текущая позиция в DropDown.
 */
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

/**
 * Установить позицию в DropDown.
 */
fun MaterialAutoCompleteTextView.setCurrentPosition(position: Int) {
    val item = adapter.getItem(position) as String
    setText(item, false)
}

/**
 * Добавляет кнопку "ОК" в диалог.
 */
fun MaterialAlertDialogBuilder.setOkButton(): MaterialAlertDialogBuilder {
    setNeutralButton(R.string.ok) { dialog, _ -> dialog.dismiss() }
    return this
}

/**
 * Реализация removeIf для Java 7.
 * Аналогично {@link TreeSet#removeIf}.
 */
fun <T> AbstractSet<T>.removeIfJava7(filter: (item: T) -> Boolean): Boolean {
    var removed = false
    val each = iterator()
    while (each.hasNext()) {
        if (filter.invoke(each.next())) {
            each.remove()
            removed = true
        }
    }
    return removed
}

/**
 * Showing the Android Keyboard Reliably:
 * https://developer.squareup.com/blog/showing-the-android-keyboard-reliably/
 */
fun View.focusAndShowKeyboard() {
    /**
     * This is to be called when the window already has focus.
     */
    fun View.showTheKeyboardNow() {
        if (isFocused) {
            post {
                // We still post the call, just in case we are being notified of the windows focus
                // but InputMethodManager didn't get properly setup yet.
                val imm = ContextCompat.getSystemService(context, InputMethodManager::class.java)
                imm?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

    requestFocus()
    if (hasWindowFocus()) {
        // No need to wait for the window to get focus.
        showTheKeyboardNow()
    } else {
        // We need to wait until the window gets focus.
        viewTreeObserver.addOnWindowFocusChangeListener(
            object : ViewTreeObserver.OnWindowFocusChangeListener {
                override fun onWindowFocusChanged(hasFocus: Boolean) {
                    // This notification will arrive just before the InputMethodManager gets set up.
                    if (hasFocus) {
                        this@focusAndShowKeyboard.showTheKeyboardNow()
                        // It’s very important to remove this listener once we are done.
                        viewTreeObserver.removeOnWindowFocusChangeListener(this)
                    }
                }
            })
    }
}

fun Uri.extractFilename(context: Context): String? {
    var result: String? = null
    if (this.scheme == "content") {
        val cursor = context.contentResolver.query(this, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
        cursor.use {
            if (it != null && it.moveToFirst()) {
                result = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        }
    }
    if (result == null) {
        result = this.path
        val cut = result?.lastIndexOf("/")
        if (cut != null && cut != -1) {
            result = result?.substring(cut + 1)
        }
    }

    result = result?.substringBeforeLast('.')

    return result
}

fun View.setVisibility(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}