package com.vereshchagin.nikolay.stankinschedule.ui.schedule.model.exceptions;

import androidx.annotation.NonNull;

/**
 * Исключение, генерирующийся когда периодичность даты не правильная.
 */
public class InvalidDateFrequencyException extends IllegalArgumentException {

    public InvalidDateFrequencyException(@NonNull String message) {
        super(message);
    }

}
