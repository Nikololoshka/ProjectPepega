package com.github.nikololoshka.pepegaschedule.modulejournal.network;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

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
    @Nullable
    private String mErrorTitle;
    @StringRes
    private int mErrorTitleRes;
    /**
     * Описание ошибки.
     */
    @Nullable
    private String mErrorDescription;
    @StringRes
    private int mErrorDescriptionRes;


    ModuleJournalError() {
        mErrorCode = -1;
        mErrorTitleRes = -1;
        mErrorDescriptionRes = -1;
    }

    public int errorCode() {
        return mErrorCode;
    }

    @Nullable
    public String errorTitle() {
        return mErrorTitle;
    }

    @StringRes
    public int errorTitleRes() {
        return mErrorTitleRes;
    }

    @StringRes
    public int errorDescriptionRes() {
        return mErrorDescriptionRes;
    }

    @Nullable
    public String errorDescription() {
        return mErrorDescription;
    }

    void setErrorTitleRes(int errorTitleRes) {
        mErrorTitleRes = errorTitleRes;
    }

    void setErrorDescriptionRes(int errorDescriptionRes) {
        mErrorDescriptionRes = errorDescriptionRes;
    }

    void setErrorCode(int errorCode) {
        mErrorCode = errorCode;
    }

    void setErrorTitle(@Nullable String errorTitle) {
        mErrorTitle = errorTitle;
    }

    void setErrorDescription(@Nullable String errorDescription) {
        mErrorDescription = errorDescription;
    }
}
