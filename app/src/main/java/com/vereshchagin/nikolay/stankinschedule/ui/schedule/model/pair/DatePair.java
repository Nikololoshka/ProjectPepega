package com.vereshchagin.nikolay.stankinschedule.ui.schedule.model.pair;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vereshchagin.nikolay.stankinschedule.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

import static com.vereshchagin.nikolay.stankinschedule.ui.schedule.model.pair.DateItem.JSON_FREQUENCY;

/**
 * Дата пары.
 */
public class DatePair implements Comparable<DatePair>, Parcelable {

    private static final String JSON_TAG = "dates";

    /**
     * Даты пары.
     */
    @NonNull
    private TreeSet<DateItem> mDates;

    @NonNull
    private DayOfWeek mDayOfWeek;

    public DatePair(@NonNull Collection<? extends DateItem> items) {
        if (!items.isEmpty()) {
            mDayOfWeek = items.iterator().next().dayOfWeek();
        } else {
            throw new IllegalArgumentException("Collection of DateItem is empty");
        }

        mDates = new TreeSet<>();
        for (DateItem item : items) {
            addDate(item);
        }
    }

    private DatePair(@NonNull Parcel in) {
        Parcelable[] dateItems = in.readParcelableArray(DateItem.class.getClassLoader());
        mDates = new TreeSet<>();

        if (dateItems == null || dateItems.length == 0) {
            throw new IllegalArgumentException("Collection of DateItem is empty");
        } else {
            mDayOfWeek = ((DateItem) dateItems[0]).dayOfWeek();
        }

        for (Parcelable item : dateItems) {
            addDate((DateItem) item);
        }
    }

    public static final Creator<DatePair> CREATOR = new Creator<DatePair>() {
        @Override
        public DatePair createFromParcel(Parcel in) {
            return new DatePair(in);
        }

        @Override
        public DatePair[] newArray(int size) {
            return new DatePair[size];
        }
    };

    /**
     * Определяет, возможно ли заменить дату на другую без конфликта между ними.
     * @param dateItems коллекция с датами.
     * @param removedItem заменяемая дата.
     * @param addedItem заменяющпя дата.
     * @return true если возможно, иначе false.
     */
    public static boolean possibleChangeDate(@NonNull Collection<DateItem> dateItems,
                                             @Nullable DateItem removedItem,
                                             @NonNull DateItem addedItem) {
        for (DateItem item : dateItems) {
            if (!Objects.equals(removedItem, item)) {
                if (item.intersect(addedItem) ||
                        item.dayOfWeek() != addedItem.dayOfWeek()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Добавляет дату к датам пары.
     * @param dateItem добавляемая дата.
     */
    public void addDate(@NonNull DateItem dateItem) {
        if (mDayOfWeek == dateItem.dayOfWeek()) {
            if (!intersect(dateItem)) {
                mDates.add(dateItem);
            } else {
                throw new IllegalArgumentException("Intersect date");
            }
        } else {
            throw new IllegalArgumentException("No added date");
        }
    }

    /**
     * Удаляет дату из дат пары.
     * @param removedDateItem удаляемая дата.
     */
    public void removeDate(@NonNull DateItem removedDateItem) {
        mDates.remove(removedDateItem);
    }

    /**
     * @return день недели.
     */
    @NonNull
    public DayOfWeek dayOfWeek() {
        return mDayOfWeek;
    }

    /**
     * Определяет, пересекается ли даты пары с сравниваемой датой.
     * @param dateItem сравниваемая дата.
     * @return true пересекаются, иначе false.
     */
    public boolean intersect(@NonNull DateItem dateItem) {
        for (DateItem item : mDates) {
            if (item.intersect(dateItem)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Аналогично {@link #intersect(DateItem)}, но сравнивает с датами другой пары.
     * @param datePair сравниваемая дата пары.
     * @return true пересекаются, иначе false.
     */
    public boolean intersect(@NonNull DatePair datePair) {
        for (DateItem firstDate : mDates) {
            for (DateItem secondDate : datePair.mDates) {
                if (firstDate.intersect(secondDate)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @return первый день дат пары.
     */
    @NonNull
    public Calendar firstDay() {
        return CommonUtils.normalizeDate(mDates.first().firstDay());
    }

    /**
     * @return последний день дат пары.
     */
    @NonNull
    public Calendar lastDay() {
        return CommonUtils.normalizeDate(Collections.max(mDates, new Comparator<DateItem>() {
            @Override
            public int compare(DateItem o1, DateItem o2) {
                return o1.lastDay().compareTo(o2.lastDay());
            }
        }).lastDay());
    }

    /**
     * Создает DatePair из json объекта.
     * @param object json объект.
     * @return дата пары.
     */
    public static DatePair fromJson(@NonNull JsonObject object) {
        JsonArray dateArray = object.getAsJsonArray(JSON_TAG);

        List<DateItem> dateItems = new ArrayList<>();
        for (JsonElement dateElement : dateArray) {
            JsonObject dateObject = dateElement.getAsJsonObject();
            FrequencyEnum frequency = FrequencyEnum.of(dateObject.get(JSON_FREQUENCY).getAsString());

            if (frequency == FrequencyEnum.ONCE) {
                dateItems.add(DateSingle.fromJson(dateObject));
            } else {
                dateItems.add(DateRange.fromJson(dateObject));
            }
        }

        return new DatePair(dateItems);
    }

    /**
     * Добавляет DatePair в json объект.
     * @param object json объект.
     */
    public void toJson(@NonNull JsonObject object) {
        JsonArray dateArray = new JsonArray();

        for (DateItem item : mDates) {
            item.toJson(dateArray);
        }

        object.add(JSON_TAG, dateArray);
    }

    @Override
    public int compareTo(DatePair o) {
        return mDates.first().compareTo(o.mDates.first());
    }

    /**
     * @return список дат пары.
     */
    @NonNull
    public ArrayList<DateItem> toList() {
        return new ArrayList<>(mDates);
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append('[');

        Iterator iterable = mDates.iterator();
        while (iterable.hasNext()) {
            builder.append(iterable.next());
            if (iterable.hasNext()) {
                builder.append(", ");
            }
        }
        builder.append(']');
        return builder.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        DateItem[] items = mDates.toArray(new DateItem[0]);
        dest.writeParcelableArray(items, flags);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DatePair datePair = (DatePair) o;
        return mDates.equals(datePair.mDates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mDates, mDayOfWeek);
    }
}
