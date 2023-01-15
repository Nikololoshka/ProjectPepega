package com.vereshchagin.nikolay.stankinschedule.core.domain.settings

enum class DarkMode(val tag: String) {
    Default("dark_mode_default"),
    Dark("dark_mode_dark"),
    Light("dark_mode_light");

    companion object {
        fun from(value: String?): DarkMode? {
            return DarkMode.values().find { it.tag == value }
        }
    }
}