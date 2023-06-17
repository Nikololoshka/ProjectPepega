package com.vereshchagin.nikolay.stankinschedule.core.domain.ext

fun <T> MutableCollection<T>.removeIf7(filter: (item: T) -> Boolean): Boolean {
    var removed = false
    val it = iterator()
    while (it.hasNext()) {
        if (filter(it.next())) {
            it.remove()
            removed = true
        }
    }
    return removed
}