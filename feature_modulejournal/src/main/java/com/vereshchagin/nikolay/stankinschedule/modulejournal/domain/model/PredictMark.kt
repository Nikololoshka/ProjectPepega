package com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.model

class PredictMark(
    val discipline: String,
    val type: MarkType,
    val isExposed: Boolean,
    var value: Int,
)