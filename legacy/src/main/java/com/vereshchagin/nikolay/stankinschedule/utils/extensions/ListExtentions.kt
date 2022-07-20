package com.vereshchagin.nikolay.stankinschedule.utils.extensions


fun <T> MutableList<T>.swap(a: Int, b: Int) {
    val t = this[a]
    this[a] = this[b]
    this[b] = t
}