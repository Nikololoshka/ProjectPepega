package com.github.nikololoshka.pepegaschedule.modulejournal.network;

/**
 * POJO класс информации об ошибке.
 */
public class ModuleJournalError {
    /**
     * Код ошибки.
     */
    private int mErrorCode;
    /**
     * Заголовок ошибки.
     */
    private String mErrorTitle;
    /**
     * Описание ошибки.
     */
    private String mErrorDescription;

    public int errorCode() {
        return mErrorCode;
    }

    public String errorTitle() {
        return mErrorTitle;
    }

    public String errorDescription() {
        return mErrorDescription;
    }

    public void setErrorCode(int errorCode) {
        mErrorCode = errorCode;
    }

    public void setErrorTitle(String errorTitle) {
        mErrorTitle = errorTitle;
    }

    public void setErrorDescription(String errorDescription) {
        mErrorDescription = errorDescription;
    }
}
