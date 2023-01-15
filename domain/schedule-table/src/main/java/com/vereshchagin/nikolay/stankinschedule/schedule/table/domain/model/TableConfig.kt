package com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.model

import android.graphics.Color

data class TableConfig(
    val color: Int,
    val longScreenSize: Float,
    val mode: TableMode,
    val page: Int
) {
    companion object {
        fun default() = TableConfig(Color.BLACK, 720f, TableMode.Full, 0)
    }
}