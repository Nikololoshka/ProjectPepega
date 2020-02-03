package com.github.nikololoshka.pepegaschedule.utils;

import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.NonNull;

public abstract class TextWatcherWrapper implements TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        onTextChanged(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    public abstract void onTextChanged(@NonNull String s);
}
