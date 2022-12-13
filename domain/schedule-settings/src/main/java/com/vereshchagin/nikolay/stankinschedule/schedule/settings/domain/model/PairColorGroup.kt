package com.vereshchagin.nikolay.stankinschedule.schedule.settings.domain.model

data class PairColorGroup(
    val lectureColor: String,
    val seminarColor: String,
    val laboratoryColor: String,
    val subgroupAColor: String,
    val subgroupBColor: String
) {
    companion object {
        fun default() = PairColorGroup(
            lectureColor = PairColorType.Lecture.hex,
            seminarColor = PairColorType.Seminar.hex,
            laboratoryColor = PairColorType.Laboratory.hex,
            subgroupAColor = PairColorType.SubgroupA.hex,
            subgroupBColor = PairColorType.SubgroupB.hex
        )
    }
}