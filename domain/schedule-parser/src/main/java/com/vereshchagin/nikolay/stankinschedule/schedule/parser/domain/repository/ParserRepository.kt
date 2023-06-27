package com.vereshchagin.nikolay.stankinschedule.schedule.parser.domain.repository

import com.vereshchagin.nikolay.stankinschedule.schedule.parser.domain.model.ParseDetail

interface ParserRepository {

    suspend fun parsePDF(path: String): ParseDetail

}