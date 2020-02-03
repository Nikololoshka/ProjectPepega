package com.github.nikololoshka.pepegaschedule.schedule.model.pair;

import android.os.Parcel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.nikololoshka.pepegaschedule.schedule.model.exceptions.InvalidDateFrequencyException;
import com.github.nikololoshka.pepegaschedule.schedule.model.exceptions.InvalidDateParseException;
import com.github.nikololoshka.pepegaschedule.utils.CommonUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;

/**
 * Единождая дата пары.
 */
public class DateSingle extends DateItem {

    @NonNull
    private Calendar mDate;

    public DateSingle(@NonNull String stringDate, @NonNull String pattern) {

        DateFormat format = new SimpleDateFormat(pattern, CommonUtils.locale(null));
        format.setLenient(false);

        Calendar date = new GregorianCalendar();
        try {
            // игнор, т.к. parse кидает исключения
            date.setTime(format.parse(stringDate));

        } catch (ParseException e) {
            throw new InvalidDateParseException(stringDate, e);
        }

        // проверка дня недели
        DayOfWeek.of(date);
        mDate = date;
    }

    public DateSingle(@NonNull String stringDate) {
        this(stringDate, "yyyy.MM.dd");
    }

    public DateSingle(@NonNull Calendar date) {
        mDate = CommonUtils.normalizeDate(date);
    }

    private DateSingle(@NonNull Parcel in) {
        Serializable date = in.readSerializable();
        if (date == null) {
            throw new IllegalArgumentException("No parsable single date pair: " + in);
        }

        mDate = (Calendar) date;
    }

    public static final Creator<DateSingle> CREATOR = new Creator<DateSingle>() {
        @Override
        public DateSingle createFromParcel(Parcel in) {
            return new DateSingle(in);
        }

        @Override
        public DateSingle[] newArray(int size) {
            return new DateSingle[size];
        }
    };

    /**
     * @return дата.
     */
    @NonNull
    public Calendar date() {
        return mDate;
    }

    @NonNull
    @Override
    public DayOfWeek dayOfWeek() {
        return DayOfWeek.of(mDate);
    }

    @NonNull
    @Override
    public String fullDate() {
        return CommonUtils.dateToString(mDate, "yyyy.MM.dd", Locale.ROOT);
    }

    @NonNull
    @Override
    public Calendar firstDay() {
        return mDate;
    }

    @NonNull
    @Override
    public Calendar lastDay() {
        return mDate;
    }

    @NonNull
    @Override
    public FrequencyEnum frequency() {
        return FrequencyEnum.ONCE;
    }

    @Override
    public boolean intersect(@NonNull DateItem dateItem) {
        if (dateItem instanceof DateSingle) {
            DateSingle dateSingle = (DateSingle) dateItem;

            return this.equals(dateSingle);
        }

        if (dateItem instanceof DateRange) {
            DateRange dateRange = (DateRange) dateItem;

            return dateRange.intersect(this);
        }

        throw new IllegalArgumentException("Not intersect object");
    }

    /**
     * Создает DateSingle из json объекта.
     * @param object json объект.
     * @return единождая дата пары.
     */
    public static DateSingle fromJson(@NonNull JsonObject object) {
        String frequency = object.get(JSON_FREQUENCY).getAsString();
        String inputDate = object.get(JSON_DATE).getAsString();

        if (!frequency.equals(FrequencyEnum.ONCE.toString())) {
            throw new InvalidDateFrequencyException(
                    String.format("Date: %s; Frequency: %s", inputDate, frequency));
        }

        return new DateSingle(inputDate);
    }

    @Override
    public void toJson(@NonNull JsonArray array) {
        JsonObject dateObject = new JsonObject();

        dateObject.addProperty(JSON_DATE, fullDate());
        dateObject.addProperty(JSON_FREQUENCY, frequency().toString());

        array.add(dateObject);
    }

    @Override
    public int compareTo(@NonNull DateItem o) {
        if (o instanceof DateSingle) {
            DateSingle dateSingle = (DateSingle) o;

            return mDate.compareTo(dateSingle.mDate);
        }
        if (o instanceof DateRange) {
            DateRange dateRange = (DateRange) o;

            return mDate.compareTo(dateRange.firstDate());
        }

        throw new IllegalArgumentException(String.format("Not compare objects: '%s' and '%s'",
                this.toString(), o.toString()));
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DateSingle that = (DateSingle) o;
        return Objects.equals(mDate, that.mDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mDate);
    }

    @NonNull
    @Override
    public String toString() {
        return CommonUtils.dateToString(mDate, "dd.MM.yyyy", Locale.ROOT);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(mDate);
    }
}
