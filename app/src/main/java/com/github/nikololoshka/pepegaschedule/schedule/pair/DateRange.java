package com.github.nikololoshka.pepegaschedule.schedule.pair;

import android.os.Parcel;

import androidx.annotation.Nullable;

import com.github.nikololoshka.pepegaschedule.schedule.pair.exceptions.InvalidDateException;
import com.github.nikololoshka.pepegaschedule.schedule.pair.exceptions.InvalidFrequencyForDateException;
import com.github.nikololoshka.pepegaschedule.schedule.pair.exceptions.InvalidIntersectDateException;
import com.github.nikololoshka.pepegaschedule.schedule.pair.exceptions.InvalidPairParseException;

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

public class DateRange extends DateItem {

    @Nullable
    private Calendar mDateStart;
    @Nullable
    private Calendar mDateEnd;

    private FrequencyEnum mFrequency;

    DateRange() {
        mDateStart = null;
        mDateEnd = null;
        mFrequency = FrequencyEnum.EVERY;
    }

    private DateRange(Parcel in) {
        mDateStart = (Calendar) in.readSerializable();
        mDateEnd = (Calendar) in.readSerializable();
        mFrequency = (FrequencyEnum) in.readSerializable();
    }

    public DateRange(DateRange dateRange) {
        mDateStart = (Calendar) (dateRange.mDateStart != null ? dateRange.mDateStart.clone() : null);
        mDateEnd = (Calendar) (dateRange.mDateEnd != null ? dateRange.mDateEnd.clone() : null);
        mFrequency = dateRange.mFrequency;
    }

    public static DateRange of(String start, String end, FrequencyEnum frequency) {
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.ROOT);
        format.setLenient(false);
        Calendar dateStart = new GregorianCalendar();
        Calendar dateEnd = new GregorianCalendar();
        try {
            dateStart.setTime(format.parse(start));
            dateEnd.setTime(format.parse(end));
        } catch (ParseException ignored) {
            throw new InvalidDateException(String.format("Start: %s; End: %s; Frequency: %s",
                    start, end, frequency.tag()));
        }

        // check day of week
        DayOfWeek.valueOf(dateStart);
        DayOfWeek.valueOf(dateEnd);

        long diffInMillis = dateEnd.getTimeInMillis() - dateStart.getTimeInMillis();
        if ((diffInMillis / 86400000) % frequency.period() != 0) {
            throw new InvalidFrequencyForDateException(String.format("Start: %s; End: %s; Frequency: %s",
                    start, end, frequency.tag()));
        }

        DateRange dateRange = new DateRange();
        dateRange.mDateStart = dateStart;
        dateRange.mDateEnd = dateEnd;
        dateRange.mFrequency = frequency;
        return dateRange;
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

    @Nullable
    public Calendar dateStart() {
        return mDateStart;
    }

    @Nullable
    public Calendar dateEnd() {
        return mDateEnd;
    }

    @Override
    public FrequencyEnum frequency() {
        return mFrequency;
    }

    @Override
    public DayOfWeek dayOfWeek() {
        return DayOfWeek.valueOf(mDateStart);
    }

    @Override
    public String compactDate() {
        if (mDateStart == null || mDateEnd == null) {
            return "";
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM", Locale.ROOT);
        return dateFormat.format(mDateStart.getTime()) + "-"
                + dateFormat.format(mDateEnd.getTime()) + " " + mFrequency.text();
    }

    @Override
    public String fullDate() {
        if (mDateStart == null || mDateEnd == null) {
            return "";
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.ROOT);
        return dateFormat.format(mDateStart.getTime()) + "-"
                + dateFormat.format(mDateEnd.getTime());
    }

    @Override
    public Calendar minDate() {
        return mDateStart;
    }

    @Override
    public Calendar maxDate() {
        return mDateEnd;
    }

    @Override
    public void load(JSONObject loadObject) throws JSONException {
        String frequency = loadObject.getString("frequency");
        String date = loadObject.getString("date");

        if (frequency.equals(FrequencyEnum.EVERY.tag())) {
            mFrequency = FrequencyEnum.EVERY;
        } else if (frequency.equals(FrequencyEnum.THROUGHOUT.tag())) {
            mFrequency = FrequencyEnum.THROUGHOUT;
        } else {
            throw new InvalidPairParseException("Invalid frequency: " + frequency);
        }

        String[] dates = date.split("-");
        if (dates.length != 2) {
            throw new InvalidDateException(date);
        }

        String startDate = dates[0];
        String endDate = dates[1];

        DateFormat format = new SimpleDateFormat("yyyy.MM.dd", Locale.ROOT);
        format.setLenient(false);
        Calendar dateStart = new GregorianCalendar();
        Calendar dateEnd = new GregorianCalendar();

        try {
            dateStart.setTime(format.parse(startDate));
            dateEnd.setTime(format.parse(endDate));
        } catch (ParseException ignored) {
            throw new InvalidDateException(String.format("Start: %s; End: %s; Frequency: %s",
                    startDate, endDate, mFrequency.tag()));
        }

        long diffInMillis = dateEnd.getTimeInMillis() - dateStart.getTimeInMillis();
        if ((diffInMillis / 86400000) % mFrequency.period() != 0) {
            throw new InvalidFrequencyForDateException(String.format("Start: %s; End: %s; Frequency: %s",
                    startDate, endDate, mFrequency.tag()));
        }

        mDateStart = dateStart;
        mDateEnd = dateEnd;
    }

    @Override
    public boolean intersect(DateItem dateItem) {
        if (mDateStart != null) {
            if (dateItem instanceof DateSingle) {
                Calendar dateSingle = ((DateSingle) dateItem).date();
                Calendar date = (Calendar) mDateStart.clone();

                while (date.compareTo(mDateEnd) <= 0) {
                    if (Objects.equals(date, dateSingle)) {
                        return true;
                    }
                    date.add(Calendar.DAY_OF_MONTH, mFrequency.period());
                }
                return false;
            }
            if (dateItem instanceof DateRange) {
                DateRange dateRange = (DateRange) dateItem;

                if (dateRange.dateStart() == null) {
                    throw new InvalidIntersectDateException(String.format("Can not intersect date: '%s' and '%s'",
                            this.toString(), dateItem.toString()));
                }

                Calendar firstDate = (Calendar) dateRange.dateStart().clone();
                Calendar secondDate = (Calendar) mDateStart.clone();

                int i = 0;
                while (firstDate.compareTo(dateRange.dateEnd()) <= 0) {
                    secondDate.add(Calendar.DAY_OF_MONTH, i * mFrequency.period());
                    i = 0;
                    while (secondDate.compareTo(mDateEnd) <= 0) {
                        if (firstDate.equals(secondDate)) {
                            return true;
                        }
                        secondDate.add(Calendar.DAY_OF_MONTH, mFrequency.period());
                        i--;
                    }
                    firstDate.add(Calendar.DAY_OF_MONTH, dateRange.frequency().period());
                }
                return false;
            }
        }
        throw new InvalidIntersectDateException(String.format("Can not intersect date: '%s' and '%s'",
                this.toString(), dateItem.toString()));
    }

    @Override
    public void save(JSONArray saveArray) throws JSONException {
        JSONObject dateObject = new JSONObject();
        dateObject.put("frequency", mFrequency.tag());
        dateObject.put("date", fullDate());
        saveArray.put(dateObject);
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DateRange dateRange = (DateRange) o;
        return Objects.equals(mDateStart, dateRange.mDateStart) &&
                Objects.equals(mDateEnd, dateRange.mDateEnd) &&
                mFrequency == dateRange.mFrequency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mDateStart, mDateEnd, mFrequency);
    }

    @Override
    public String toString() {
        if (mDateStart == null || mDateEnd == null) {
            return "null - null null";
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.ROOT);
        return String.format("%s - %s %s", dateFormat.format(mDateStart.getTime()),
                dateFormat.format(mDateEnd.getTime()), mFrequency.text());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(mDateStart);
        dest.writeSerializable(mDateEnd);
        dest.writeSerializable(mFrequency);
    }
}
