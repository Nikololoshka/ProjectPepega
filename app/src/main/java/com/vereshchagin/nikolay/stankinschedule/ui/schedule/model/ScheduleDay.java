package com.vereshchagin.nikolay.stankinschedule.ui.schedule.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vereshchagin.nikolay.stankinschedule.ui.schedule.model.exceptions.InvalidChangePairException;
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.model.pair.DateSingle;
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.model.pair.Pair;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

/**
 * День в расписании с парами.
 */
public class ScheduleDay implements Parcelable {

    /**
     * Список пар в дне.
     */
    @NonNull
    private ArrayList<Pair> mPairsList;

    ScheduleDay() {
        mPairsList = new ArrayList<>();
    }

    private ScheduleDay(@NonNull Parcel in) {
        mPairsList = new ArrayList<>();

        Parcelable[] pairs = in.readParcelableArray(Pair.class.getClassLoader());
        if (pairs != null) {
            for (Parcelable pair : pairs) {
                mPairsList.add((Pair) pair);
            }
        }
    }

    public static final Creator<ScheduleDay> CREATOR = new Creator<ScheduleDay>() {
        @Override
        public ScheduleDay createFromParcel(Parcel in) {
            return new ScheduleDay(in);
        }

        @Override
        public ScheduleDay[] newArray(int size) {
            return new ScheduleDay[size];
        }
    };

    /**
     * Проверяет, возможно ли заменить пару.
     * @param day день расписания с занятиями.
     * @param removedPair заменяемая пара.
     * @param addedPair заменяющая пара.
     * @throws InvalidChangePairException не удалось добавить пару, после всех проверок.
     */
    static void possibleChangePair(@NonNull ScheduleDay day, @Nullable Pair removedPair, @NonNull Pair addedPair) {
        for (Pair pair : day.mPairsList) {
            if (!Objects.equals(removedPair, pair)) {
                if (addedPair.time().intersect(pair.time())) {
                    if (addedPair.date().intersect(pair.date())) {
                        if (!addedPair.subgroup().isConcurrently(pair.subgroup())) {
                            throw new InvalidChangePairException(
                                    String.format("No change pairs. Conflict: '%s' and '%s'",
                                            String.valueOf(addedPair), String.valueOf(pair)),
                                    pair.toString());
                        }
                    }
                }
            }
        }
    }

    /**
     * @return список пар в дне.
     */
    List<Pair> dayPairs() {
        return mPairsList;
    }

    /**
     * Добавляет пару в день расписания.
     * @param addedPair добавляемая пара.
     * @throws InvalidChangePairException не удалось добавить пару, после всех проверок.
     */
    void addPair(@NonNull Pair addedPair) {
        possibleChangePair(this, null, addedPair);
        mPairsList.add(addedPair);
    }

    /**
     * Удаляет пару из дня расписания.
     * @param removedPair удаляемая пара.
     */
    void removePair(@NonNull Pair removedPair) {
        mPairsList.remove(removedPair);
    }

    /**
     * Возвращает пары, проходящие в определенную дату.
     * @param date дата.
     * @return список пар, проходящих в этот день.
     */
    @NonNull
    TreeSet<Pair> pairsByDate(@NonNull Calendar date) {
        TreeSet<Pair> pairs = new TreeSet<>(new PairComparator());

        DateSingle dateSingle = new DateSingle(date);
        for (Pair pair : mPairsList) {
            if (pair.date().intersect(dateSingle)) {
                pairs.add(pair);
            }
        }

        return pairs;
    }

    /**
     * Возращает первую дату в дне.
     * Если дата отсутствует, то возвращается {@code null}.
     * @return первая дата.
     */
    @Nullable
    Calendar firstDay() {
        if (mPairsList.isEmpty()) {
            return null;
        }

        Calendar first = null;
        for (Pair pair : mPairsList) {
            Calendar firstPair = pair.date().firstDay();
            if (first != null) {
                if (firstPair.compareTo(first) < 0) {
                    first = firstPair;
                }
            } else {
                first = firstPair;
            }
        }

        return first;
    }

    /**
     * Возвращает последнию дату в дне.
     * Если дата отсутствует, то возвращается {@code null}.
     * @return последняя дата.
     */
    @Nullable
    Calendar lastDay() {
        if (mPairsList.isEmpty()) {
            return null;
        }

        Calendar last = null;
        for (Pair pair : mPairsList) {
            Calendar lastPair = pair.date().lastDay();
            if (last != null) {
                if (lastPair.compareTo(last) > 0) {
                    last = lastPair;
                }
            } else {
                last = lastPair;
            }
        }

        return last;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Pair[] pairs = mPairsList.toArray(new Pair[0]);
        dest.writeParcelableArray(pairs, flags);
    }

    /**
     * Компаратор для сравнения пар. Отсортировывает пары по порядку.
     */
    public static class PairComparator implements Comparator<Pair> {
        @Override
        public int compare(Pair o1, Pair o2) {
            // по времени начала
            if (o1.time().startNumber() == o2.time().startNumber()) {
                // если равны то, по подгруппе
                return o1.subgroup().subgroup().compareTo(o2.subgroup().subgroup());
            }
            // если не равны по времени, то которая раньше
            return Integer.compare(o1.time().startNumber(), o2.time().startNumber());
        }
    }
}
