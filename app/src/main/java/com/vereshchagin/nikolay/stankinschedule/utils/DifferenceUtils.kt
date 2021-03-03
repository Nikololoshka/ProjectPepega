package com.vereshchagin.nikolay.stankinschedule.utils

object DifferenceUtils {

    fun <T> applyDifference(currentList: MutableList<T>, newList: List<T>): Boolean {
        if (newList.isEmpty()) {
            return false
        }

        if (currentList.isEmpty()) {
            currentList.addAll(newList)
            return true
        }

        if (currentList == newList) {
            return false
        }

        currentList.clear()
        currentList.addAll(newList)

        return true
    }
}