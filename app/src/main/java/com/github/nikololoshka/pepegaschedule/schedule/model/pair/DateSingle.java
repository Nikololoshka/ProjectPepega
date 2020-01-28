package com.github.nikololoshka.pepegaschedule.schedule.model.pair;

import android.os.Parcel;

import androidx.annotation.Nullable;

import com.github.nikololoshka.pepegaschedule.schedule.model.pair.exceptions.InvalidDateException;
import com.github.nikololoshka.pepegaschedule.schedule.model.pair.exceptions.InvalidPairParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;

public class DateSingle extends DateItem {

    @Nullable
    private Calendar mDate;

    DateSingle() {
        mDate = null;
    }

    public DateSingle(Calendar date) {
        mDate = (Calendar) date.clone();
    }

    private DateSingle(Parcel in) {
        mDate = (Calendar) in.readSerializable();
    }

    public DateSingle(DateSingle dateSingle) {
        mDate = (Calendar) (dateSingle.mDate != null ? dateSingle.mDate.clone() : null);
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

    static public DateSingle of(String inputDate) {
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.ROOT);
        format.setLenient(false);

        Calendar date = new GregorianCalendar();
        try {
            date.setTime(format.parse(inputDate));
        } catch (ParseException ignored) {
            throw new InvalidDateException(inputDate);
        }

        // check day of week
        DayOfWeek.valueOf(date);

        DateSingle dateSingle = new DateSingle();
        dateSingle.mDate = date;

        return dateSingle;
    }

    @Nullable
    public Calendar date() {
        return mDate;
    }

    @Override
    public DayOfWeek dayOfWeek() {
        return DayOfWeek.valueOf(mDate);
    }

    @Override
    public String compactDate() {
        if (mDate == null) {
            return "";
        }

        return new SimpleDateFormat("dd.MM", Locale.ROOT)
                .format(mDate.getTime());
    }

    @Override
    public String fullDate() {
        if (mDate == null) {
            return "";
        }

        return new SimpleDateFormat("yyyy.MM.dd", Locale.ROOT)
                .format(mDate.getTime());
    }

    @Override
    public Calendar minDate() {
        return mDate;
    }

    @Override
    public Calendar maxDate() {
        return mDate;
    }

    @Override
    public FrequencyEnum frequency() {
        return FrequencyEnum.ONCE;
    }

    @Override
    public boolean intersect(DateItem dateItem) {
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

    @Override
    public void load(JSONObject loadObject) throws JSONException {
        String frequency = loadObject.getString("frequency");
        String inputDate = loadObject.getString("date");

        if (!frequency.equals(FrequencyEnum.ONCE.tag())) {
            throw new InvalidPairParseException(String.format("Date: %s; Frequency: %s",
                    inputDate, frequency));
        }

        DateFormat format = new SimpleDateFormat("yyyy.MM.dd", Locale.ROOT);
        format.setLenient(false);

        Calendar date = new GregorianCalendar();
        try {
            date.setTime(format.parse(inputDate));
        } catch (ParseException ignored) {
            throw new InvalidDateException(inputDate);
        }
        // check day of week
        DayOfWeek.valueOf(date);

        mDate = date;
    }

    @Override
    public void save(JSONArray saveArray) throws JSONException {
       JSONObject dateObject = new JSONObject();
       dateObject.put("frequency", FrequencyEnum.ONCE.tag());
       dateObject.put("date", fullDate());
       saveArray.put(dateObject);
    }

    @Override
    public int compareTo(DateItem o) {
        if (mDate != null) {
            if (o instanceof DateSingle) {
                DateSingle dateSingle = (DateSingle) o;
                return mDate.compareTo(dateSingle.mDate);
            }
            if (o instanceof DateRange) {
                DateRange dateRange = (DateRange) o;
                return mDate.compareTo(dateRange.dateStart());
            }
        }
        throw new IllegalArgumentException(String.format("Not compare objects: '%s' and '%s'",
                this.toString(), o.toString()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DateSingle that = (DateSingle) o;
        return Objects.equals(mDate, that.mDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mDate);
    }

    @Override
    public String toString() {
        if (mDate == null) {
            return "null";
        }

        return new SimpleDateFormat("dd.MM.yyyy", Locale.ROOT)
                .format(mDate.getTime());
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
