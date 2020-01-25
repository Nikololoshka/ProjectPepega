package com.github.nikololoshka.pepegaschedule.utils;

import androidx.annotation.NonNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CommonUtils {

    @NonNull
    public static String of(@NonNull Calendar calendar, @NonNull String pattern) {
        DateFormat format = new SimpleDateFormat(pattern, Locale.ROOT);
        return format.format(calendar.getTime());
    }
}
