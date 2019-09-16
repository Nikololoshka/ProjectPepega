package com.github.nikololoshka.pepegaschedule.schedule.pair.exceptions;

public class InvalidDateException extends IllegalArgumentException {

    final private String mInputDate;

    public InvalidDateException(String inputDate) {
        super(inputDate);
        mInputDate = inputDate;
    }

    public String inputDate() {
        return mInputDate;
    }
}
