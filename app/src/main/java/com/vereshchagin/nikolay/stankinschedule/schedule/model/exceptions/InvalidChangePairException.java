package com.vereshchagin.nikolay.stankinschedule.schedule.model.exceptions;

import androidx.annotation.NonNull;

/**
 * Исключение, генерирующийся когда невозможно заменить пару.
 */
public class InvalidChangePairException extends IllegalArgumentException {

    private final String mConflictPair;

    public InvalidChangePairException(@NonNull String message, @NonNull String conflictPair) {
        super(message);
        mConflictPair = conflictPair;
    }

    /**
     * @return возвращает пару из-за которой случился конфликт.
     */
    @NonNull
    public String conflictPair() {
        return mConflictPair;
    }
}