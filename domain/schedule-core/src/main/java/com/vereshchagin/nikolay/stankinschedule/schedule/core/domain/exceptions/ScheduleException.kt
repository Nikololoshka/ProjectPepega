package com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.exceptions

import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.DateItem
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.Frequency
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.PairModel


open class PairException(
    message: String?, cause: Throwable? = null,
) : RuntimeException(message, cause)


class PairIntersectException(
    message: String?, val first: PairModel, val second: PairModel, cause: Throwable? = null,
) : PairException(message, cause)

/**
 * Исключение, возникающие во время работы с датами.
 */
sealed class DateException(
    message: String?, cause: Throwable? = null,
) : RuntimeException(message, cause)

/**
 * Неправильная периодичность пары.
 */
class DateFrequencyException(
    message: String?, val date: String, val frequency: Frequency, cause: Throwable? = null,
) : DateException(message, cause)

/**
 * Неправильный день недели.
 */
class DateDayOfWeekException(
    message: String?, cause: Throwable? = null,
) : DateException(message, cause)

/**
 * Не удалось считать дату.
 */
class DateParseException(
    message: String?, val parseDate: String, cause: Throwable? = null,
) : DateException(message, cause)

/**
 * Даты пересекаются.
 */
class DateIntersectException(
    message: String?, val first: DateItem, val second: DateItem, cause: Throwable? = null,
) : DateException(message, cause)
