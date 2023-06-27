package com.vereshchagin.nikolay.stankinschedule.schedule.parser.domain.model

import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.PairModel

sealed interface ParseResult {
    class Success(val pair: PairModel) : ParseResult
    class Error(val error: Throwable) : ParseResult
}