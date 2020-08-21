package com.vereshchagin.nikolay.stankinschedule.utils

import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.DateItem
import java.util.*

/**
 * Текущая позиция в DropDown.
 * @see DropDownAdapter
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
 * Устанавить позицию в DropDown.
 * @see DropDownAdapter
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
fun TreeSet<DateItem>.removeIfJava7(filter: (item: DateItem) -> Boolean): Boolean {
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