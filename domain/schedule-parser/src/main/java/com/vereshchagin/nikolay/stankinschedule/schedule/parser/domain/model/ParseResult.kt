package com.vereshchagin.nikolay.stankinschedule.schedule.parser.domain.model

import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.PairModel

sealed interface ParseResult {

    class Success(val pair: PairModel) : ParseResult {
        override fun toString(): String {
            return "Success(pair=$pair)"
        }
    }

    class Missing(val context: String) : ParseResult {
        override fun toString(): String {
            return "Missing(context='$context')"
        }
    }

    class Error(val error: String, val context: String) : ParseResult {
        override fun toString(): String {
            return "Error(error='$error', context='$context')"
        }
    }
}