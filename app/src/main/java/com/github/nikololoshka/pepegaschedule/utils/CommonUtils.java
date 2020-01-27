package com.github.nikololoshka.pepegaschedule.utils;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CommonUtils {

    @NonNull
    public static String dateToString(@NonNull Calendar calendar, @NonNull String pattern, @NonNull Locale locale) {
        DateFormat format = new SimpleDateFormat(pattern, locale);
        return format.format(calendar.getTime());
    }

    @NonNull
    public static String dateToString(@NonNull Calendar calendar, @NonNull String pattern) {
        return dateToString(calendar, pattern, Locale.ENGLISH);
    }

    @NonNull
    public static Locale locale(@Nullable Context context) {
        if (context == null) {
            return Locale.ROOT;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.getResources().getConfiguration().getLocales().get(0);
        } else {
            return  context.getResources().getConfiguration().locale;
        }
    }
}
