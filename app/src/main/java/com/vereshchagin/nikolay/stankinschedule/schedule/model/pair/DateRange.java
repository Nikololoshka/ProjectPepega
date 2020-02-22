package com.vereshchagin.nikolay.stankinschedule.schedule.model.pair;

import android.os.Parcel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vereshchagin.nikolay.stankinschedule.schedule.model.exceptions.InvalidDateFrequencyException;
import com.vereshchagin.nikolay.stankinschedule.schedule.model.exceptions.InvalidDateParseException;
import com.vereshchagin.nikolay.stankinschedule.utils.CommonUtils;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Objects;

/**
 * Дата пары с диапазоном.
 */
public class DateRange extends DateItem {

    @NonNull
    private Calendar mFirstDate;

    @NonNull
    private Calendar mLastDate;

    @NonNull
    private FrequencyEnum mFrequency;

    public DateRange(@NonNull String stringFirst, @NonNull String stringLast,
                     @NonNull FrequencyEnum frequency, @NonNull String pattern) {

        DateFormat format = new SimpleDateFormat(pattern, CommonUtils.locale(null));
        format.setLenient(false);

        Calendar dateStart = new GregorianCalendar();
        Calendar dateEnd = new GregorianCalendar();
        try {
            // игнор, т.к. parse кидает исключения
            dateStart.setTime(format.parse(stringFirst));
            dateEnd.setTime(format.parse(stringLast));

        } catch (ParseException e) {
            throw new InvalidDateParseException(
                    String.format("%s-%s", stringFirst, stringLast), e);
        }

        // проверка дня недели
        DayOfWeek.of(dateStart);
        DayOfWeek.of(dateEnd);

        long diffInMillis = dateEnd.getTimeInMillis() - dateStart.getTimeInMillis();
        if (diffInMillis == 0 || (diffInMillis / 1000 * 60 * 60 * 24) % frequency.period() != 0) {
            throw new InvalidDateFrequencyException(
                    String.format("Start: %s; End: %s; Frequency: %s", stringFirst, stringLast, frequency));
        }

        mFirstDate = dateStart;
        mLastDate = dateEnd;
        mFrequency = frequency;
    }

    public DateRange(@NonNull String stringFirst, @NonNull String stringLast, @NonNull FrequencyEnum frequency) {
        this(stringFirst, stringLast, frequency, "yyyy.MM.dd");
    }

    private DateRange(@NonNull Parcel in) {
        Serializable start =  in.readSerializable();
        Serializable end = in.readSerializable();
        Serializable frequency = in.readSerializable();

        if (start == null || end == null || frequency == null) {
            throw new IllegalArgumentException("No parsable range date pair: " + in);
        }

        mFirstDate = (Calendar) start;
        mLastDate = (Calendar) end;
        mFrequency = (FrequencyEnum) frequency;
    }

    public static final Creator<DateRange> CREATOR = new Creator<DateRange>() {
        @Override
        public DateRange createFromParcel(Parcel in) {
            return new DateRange(in);
        }

        @Override
        public DateRange[] newArray(int size) {
            return new DateRange[size];
        }
    };

    /**
     * @return первая дата диапазона.
     */
    @NonNull
    public Calendar firstDate() {
        return mFirstDate;
    }

    /**
     * @return последняя дата диапазона.
     */
    @NonNull
    public Calendar lastDate() {
        return mLastDate;
    }

    @NonNull
    @Override
    public FrequencyEnum frequency() {
        return mFrequency;
    }

    @NonNull
    @Override
    public DayOfWeek dayOfWeek() {
        return DayOfWeek.of(mFirstDate);
    }

    @NonNull
    @Override
    public String fullDate() {
        return CommonUtils.dateToString(mFirstDate, "yyyy.MM.dd")
                + "-" + CommonUtils.dateToString(mLastDate, "yyyy.MM.dd");
    }

    @NonNull
    @Override
    public Calendar firstDay() {
        return mFirstDate;
    }

    @NonNull
    @Override
    public Calendar lastDay() {
        return mLastDate;
    }

    /**
     * Создает DateRange из json объекта.
     * @param object json объект.
     * @return дата пары с диапазоном.
     */
    public static DateRange fromJson(@NonNull JsonObject object) {
        String inputFrequency = object.get(JSON_FREQUENCY).getAsString();
        String inputDate = object.get(JSON_DATE).getAsString();

        FrequencyEnum frequency = FrequencyEnum.of(inputFrequency);
        if (frequency == FrequencyEnum.ONCE) {
            throw new InvalidDateFrequencyException("Invalid frequency: " + frequency);
        }

        String[] dates = inputDate.split("-");
        if (dates.length != 2) {
            throw new InvalidDateParseException(inputDate);
        }

        return new DateRange(dates[0], dates[1], frequency);
    }

    @Override
    public void toJson(@NonNull JsonArray array) {
        JsonObject dateObject = new JsonObject();

        dateObject.addProperty(JSON_DATE, fullDate());
        dateObject.addProperty(JSON_FREQUENCY, frequency().toString());

        array.add(dateObject);
    }

    @Override
    public boolean intersect(@NonNull DateItem dateItem) {
        // пересечение с единождной датой
        if (dateItem instanceof DateSingle) {
            Calendar dateSingle = ((DateSingle) dateItem).date();

            Calendar iteratorDate = CommonUtils.normalizeDate(mFirstDate);
            while (iteratorDate.compareTo(mLastDate) <= 0) {
                if (iteratorDate.equals(dateSingle)) {
                    return true;
                }
                iteratorDate.add(Calendar.DAY_OF_MONTH, mFrequency.period());
            }

            return false;
        }
        // пересечение с другим диапазоном
        if (dateItem instanceof DateRange) {
            DateRange dateRange = (DateRange) dateItem;

            Calendar iteratorFirst = CommonUtils.normalizeDate(dateRange.mFirstDate);
            Calendar iteratorSecond = CommonUtils.normalizeDate(mFirstDate);

            int i = 0;
            while (iteratorFirst.compareTo(dateRange.lastDate()) <= 0) {
                iteratorSecond.add(Calendar.DAY_OF_MONTH, i * mFrequency.period());
                i = 0;
                while (iteratorSecond.compareTo(mLastDate) <= 0) {
                    if (iteratorFirst.equals(iteratorSecond)) {
                        return true;
                    }
                    iteratorSecond.add(Calendar.DAY_OF_MONTH, mFrequency.period());
                    i--;
                }
                iteratorFirst.add(Calendar.DAY_OF_MONTH, dateRange.frequency().period());
            }
            return false;
        }

        throw new IllegalArgumentException(String.format("Can not intersect date: '%s' and '%s'",
                this.toString(), dateItem.toString()));
    }

    @Override
    public int compareTo(DateItem o) {
        if (o instanceof DateSingle) {
            DateSingle dateSingle = (DateSingle) o;
            return fullDate().compareTo(dateSingle.fullDate());
        }
        if (o instanceof DateRange) {
            DateRange dateRange = (DateRange) o;
            return fullDate().compareTo(dateRange.fullDate());
        }
        throw new IllegalArgumentException(String.format("Not compare objects: '%s' and '%s'",
                this.toString(), o.toString()));
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DateRange dateRange = (DateRange) o;
        return mFirstDate.equals(dateRange.mFirstDate) && mLastDate.equals(dateRange.mLastDate)
                && mFrequency == dateRange.mFrequency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mFirstDate, mLastDate, mFrequency);
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("%s - %s",
                CommonUtils.dateToString(mFirstDate, "dd.MM.yyyy"),
                CommonUtils.dateToString(mLastDate, "dd.MM.yyyy"));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(mFirstDate);
        dest.writeSerializable(mLastDate);
        dest.writeSerializable(mFrequency);
    }
}
