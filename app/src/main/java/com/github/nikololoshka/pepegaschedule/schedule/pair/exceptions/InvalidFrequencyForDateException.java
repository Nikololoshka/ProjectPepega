package com.github.nikololoshka.pepegaschedule.schedule.pair.exceptions;

public class InvalidFrequencyForDateException extends IllegalArgumentException {
    public InvalidFrequencyForDateException(String inputDate) {
        super(inputDate);
    }
}
