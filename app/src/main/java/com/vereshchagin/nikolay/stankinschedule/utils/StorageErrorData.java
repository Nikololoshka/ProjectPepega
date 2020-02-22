package com.vereshchagin.nikolay.stankinschedule.utils;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

/**
 * Класс для протаскивания ошибки к месту, где есть контекст или элемент,
 * в которым можно показать информацию об ошибке.
 */
public class StorageErrorData {

    /**
     * Номер ресурса с заголовком ошибки.
     */
    @StringRes
    private int mTitleRes;
    /**
     * Строка с заголовком ошибки.
     */
    @Nullable
    private String mTitleString;
    /**
     * Номер ресурса с описанием ошибки.
     */
    @StringRes
    private int mDescriptionRes;
    /**
     * Строка с описанием ошибки.
     */
    @Nullable
    private String mDescriptionString;


    public StorageErrorData(@StringRes int titleRes, @StringRes int descriptionRes) {
        this(titleRes, null, descriptionRes, null);
    }

    public StorageErrorData(@StringRes int titleRes, @Nullable String descriptionString) {
        this(titleRes, null, -1, descriptionString);
    }

    public StorageErrorData(@Nullable String titleString, @StringRes int descriptionRes) {
        this(-1, titleString, descriptionRes, null);
    }

    public StorageErrorData(@Nullable String titleString, @Nullable String descriptionString) {
        this(-1, titleString, -1, descriptionString);
    }

    private StorageErrorData(@StringRes int titleRes, @Nullable String titleString,
                             @StringRes int descriptionRes, @Nullable String descriptionString) {
        mTitleRes = titleRes;
        mTitleString = titleString;
        mDescriptionRes = descriptionRes;
        mDescriptionString = descriptionString;
    }

    /**
     * Устанавливает заголовок ошибки в текстовое поле.
     * @param textView текстовое поле.
     */
    public void resolveTitle(@NonNull TextView textView) {
        if (mTitleRes != -1) {
            textView.setText(mTitleRes);
        } else {
            textView.setText(mTitleString);
        }
    }

    /**
     * Устанавливает описание ошибки в текстовое поле.
     * @param textView текстовое поле.
     */
    public void resolveDescription(@NonNull TextView textView) {
        if (mDescriptionRes != -1) {
            textView.setText(mDescriptionRes);
        } else {
            textView.setText(mDescriptionString);
        }
    }

    @StringRes
    public int titleRes() {
        return mTitleRes;
    }

    @Nullable
    public String titleString() {
        return mTitleString;
    }

    @StringRes
    public int descriptionRes() {
        return mDescriptionRes;
    }

    @Nullable
    public String descriptionString() {
        return mDescriptionString;
    }
}
