package com.github.nikololoshka.pepegaschedule.schedule.model.pair.exceptions;

public class InvalidFrequencyForDateException extends IllegalArgumentException {
    public InvalidFrequencyForDateException(String inputDate) {
        super(inputDate);
    }
}
