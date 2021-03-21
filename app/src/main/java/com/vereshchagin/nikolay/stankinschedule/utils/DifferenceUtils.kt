package com.vereshchagin.nikolay.stankinschedule.utils

object DifferenceUtils {

    fun <T> hasDifference(currentList: List<T>, newList: List<T>): Boolean {
        if (currentList.isEmpty() || newList.isEmpty()) {
            return true
        }

        if (currentList == newList) {
            return false
        }

        return true
    }
}