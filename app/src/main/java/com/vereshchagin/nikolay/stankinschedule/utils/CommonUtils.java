package com.vereshchagin.nikolay.stankinschedule.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;

import com.vereshchagin.nikolay.stankinschedule.R;
import com.vereshchagin.nikolay.stankinschedule.settings.ApplicationPreference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class CommonUtils {

    /**
     * Форматирует дату с соответсвующими настроками.
     * @param calendar дата.
     * @param pattern шаблон форматирования.
     * @param locale локаль.
     * @return отформатированная дата.
     */
    @NonNull
    public static String dateToString(@NonNull Calendar calendar, @NonNull String pattern, @NonNull Locale locale) {
        DateFormat format = new SimpleDateFormat(pattern, locale);
        return format.format(calendar.getTime());
    }

    /**
     * Аналогично {@link #dateToString(Calendar, String, Locale)}, но использует локаль по умолчанию.
     * @param calendar дата.
     * @param pattern шаблон форматирования.
     * @return отформатированная дата.
     */
    @NonNull
    public static String dateToString(@NonNull Calendar calendar, @NonNull String pattern) {
        return dateToString(calendar, pattern, Locale.ENGLISH);
    }

    /**
     * Возвращает локаль выбранную на устройстве.
     * @param context контекст.
     * @return локаль.
     */
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

    /**
     * Нормализует дату, убирая данные о часе, минуте и т.д.
     * @param calendar дата.
     * @return нормализированная дата.
     */
    @NonNull
    public static Calendar normalizeDate(@NonNull Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        return new GregorianCalendar(year, month, dayOfMonth);
    }

    /**
     * Делает первую букву заглавной, как в предложении.
     * @param s входная строка.
     * @return выходная строка с заглавной буквой.
     */
    @NonNull
    public static String toTitleCase(@NonNull String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    /**
     * Возвращает разницу между двумя датами: first - second.
     * @param first первая даа.
     * @param second вторая дата.
     * @return разница между датами.
     */
    public static long calendarDiff(@NonNull Calendar first, @NonNull Calendar second) {
        long diff = first.getTimeInMillis() - second.getTimeInMillis();
        return (diff / (1000 * 60 * 60 * 24));
    }

    public static void openBrowser(@NonNull Context context, @NonNull String url) {
        if(ApplicationPreference.useAppBrowser(context)) {
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            builder.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary));
            builder.setSecondaryToolbarColor(ContextCompat.getColor(context, R.color.colorAccent));
            builder.addDefaultShareMenuItem();

            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(context, Uri.parse(url));
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            ContextCompat.startActivity(context, intent, null);
        }
    }
}
