package com.vereshchagin.nikolay.stankinschedule.repository.exceptions

import java.security.GeneralSecurityException

/**
 * Класс ошибки чтения данных для входа в модульный журнал.
 */
class InvalidReadLoginData(
    t: Throwable,
) : GeneralSecurityException(t)