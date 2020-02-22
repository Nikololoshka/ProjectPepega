package com.vereshchagin.nikolay.stankinschedule.schedule.model.exceptions;

import androidx.annotation.NonNull;

/**
 * Исключение, генерирующиеся если не удалось распарсить дату.
 */
public class InvalidDateParseException extends IllegalArgumentException {

    private final String mDate;

    public InvalidDateParseException(@NonNull String date) {
        mDate = date;
    }

    public InvalidDateParseException(@NonNull String date, @NonNull Throwable cause) {
        super(cause);
        mDate = date;
    }

    /**
     * @return дата, которую не удалось распарсить.
     */
    @NonNull
    public String parseDate() {
        return mDate;
    }
}
