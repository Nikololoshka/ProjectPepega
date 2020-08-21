package com.vereshchagin.nikolay.stankinschedule.model.schedule.pair

open class PairException(
    message: String?, cause: Throwable? = null
) : RuntimeException (message, cause)

class PairIntersectException(
    message: String?, val first: Pair, val second: Pair, cause: Throwable? = null
) : PairException(message, cause)
