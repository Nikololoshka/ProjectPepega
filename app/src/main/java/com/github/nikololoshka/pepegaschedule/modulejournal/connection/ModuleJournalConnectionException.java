package com.github.nikololoshka.pepegaschedule.modulejournal.connection;

/**
 * Исключение вызванное в результате получения данных из модульного журнала.
 */
public class ModuleJournalConnectionException extends Exception {

    private int mCode = -1;
    private String mMessage = "";

    public ModuleJournalConnectionException(int code, String message) {
        mCode = code;
        mMessage = message;
    }

    public int code() {
        return mCode;
    }

    public String message() {
        return mMessage;
    }
}
