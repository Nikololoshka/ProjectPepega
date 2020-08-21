package com.vereshchagin.nikolay.stankinschedule.model.schedule.pair

/**
 * Исключение, возникающие во время работы с датами.
 */
open class DateException(
    message: String?, cause: Throwable? = null
) : RuntimeException(message, cause)

/**
 * Неправильная периодичность пары.
 */
class DateFrequencyException(
    message: String?, val date: String, val frequency: Frequency, cause: Throwable? = null
) : DateException(message, cause)

/**
 * Неправильный день недели.
 */
class DateDayOfWeekException(
    message: String?, cause: Throwable? = null
) : DateException(message, cause)

/**
 * Не удалось распарсить дату.
 */
class DateParseException(
    message: String?, val parseDate: String, cause: Throwable? = null
) : DateException(message, cause)

/**
 * Даты пересекаются.
 */
class DateIntersectException(
    message: String?, val first: DateItem, val second: DateItem, cause: Throwable? = null
) : DateException(message, cause)

