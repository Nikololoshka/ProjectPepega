package com.vereshchagin.nikolay.stankinschedule.schedule.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.vereshchagin.nikolay.stankinschedule.schedule.model.pair.DayOfWeek;
import com.vereshchagin.nikolay.stankinschedule.schedule.model.pair.Pair;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.TreeSet;

/**
 * Расписание занятий.
 */
public class Schedule implements Parcelable {

    /**
     * Дни недели в расписании.
     */
    @NonNull
    private LinkedHashMap<DayOfWeek, ScheduleDay> mWeek;


    public Schedule() {
       mWeek = new LinkedHashMap<>(6);

       for (DayOfWeek day: DayOfWeek.values()) {
           mWeek.put(day, new ScheduleDay());
       }
    }

    private Schedule(@NonNull Parcel in) {
        mWeek = new LinkedHashMap<>(6);

        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            mWeek.put(dayOfWeek, (ScheduleDay) in.readParcelable(ScheduleDay.class.getClassLoader()));
        }
    }

    public static final Creator<Schedule> CREATOR = new Creator<Schedule>() {
        @Override
        public Schedule createFromParcel(Parcel in) {
            return new Schedule(in);
        }

        @Override
        public Schedule[] newArray(int size) {
            return new Schedule[size];
        }
    };

    /**
     * Определяет, возможно ли заменить пару в расписании.
     * @param schedule расписание.
     * @param removedPair заменяемая пара.
     * @param addedPair заменяющая пара.
     */
    public static void possibleChangePair(@NonNull Schedule schedule, @Nullable Pair removedPair,
                                          @NonNull Pair addedPair) {
        if (Objects.equals(removedPair, addedPair)) {
            return;
        }

        DayOfWeek day = addedPair.date().dayOfWeek();
        ScheduleDay scheduleDay = schedule.mWeek.get(day);
        if (scheduleDay != null) {
            ScheduleDay.possibleChangePair(scheduleDay, removedPair, addedPair);
        }
    }

    /**
     * Загружает расписание из строки с json расписанием.
     * @param json строка с расписанием.
     * @return расписание.
     * @throws JsonParseException не удалось распарсить json.
     */
    @NonNull
    public static Schedule fromJson(@NonNull String json) throws JsonParseException {
        return new GsonBuilder()
                .registerTypeAdapter(Pair.class, new Pair.PairDeserialize())
                .registerTypeAdapter(Schedule.class, new ScheduleDeserialize())
                .create()
                .fromJson(json, Schedule.class);
    }

    /**
     * Загружает расписание из входного потока с json расписанием.
     * @param stream входной поток.
     * @return расписание.
     * @throws JsonParseException не удалось распарсить json.
     */
    @NonNull
    public static Schedule fromJson(@NonNull InputStream stream) throws JsonParseException {
        return new GsonBuilder()
                .registerTypeAdapter(Pair.class, new Pair.PairDeserialize())
                .registerTypeAdapter(Schedule.class, new ScheduleDeserialize())
                .create()
                .fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), Schedule.class);
    }

    /**
     * Сохраняет расписание в строке с json расписанием.
     * @return json строка.
     */
    @NonNull
    public String toJson() {
        return new GsonBuilder()
                .registerTypeAdapter(Pair.class, new Pair.PairSerialize())
                .registerTypeAdapter(Schedule.class, new ScheduleSerialize())
                .setPrettyPrinting()
                .create()
                .toJson(this);
    }

    /**
     * Добавляет пару в расписание.
     * @param addedPair добавляемая пара.
     */
    public void addPair(@Nullable Pair addedPair) {
        if (addedPair == null) {
            return;
        }
        DayOfWeek dayOfWeek = addedPair.date().dayOfWeek();
        ScheduleDay day = mWeek.get(dayOfWeek);
        if (day != null) {
            day.addPair(addedPair);
        }
    }

    /**
     * Удаляет пару из расписания.
     * @param removedPair удаляемая пара.
     */
    public void removePair(@Nullable Pair removedPair) {
        if (removedPair == null) {
            return;
        }
        DayOfWeek dayOfWeek = removedPair.date().dayOfWeek();
        ScheduleDay day = mWeek.get(dayOfWeek);
        if (day != null) {
            day.removePair(removedPair);
        }
    }

    /**
     * Возвращает список пар, которые будут проводится в определенную дату.
     * @param date дата.
     * @return список пар, проходящих в этот день.
     */
    @NonNull
    public TreeSet<Pair> pairsByDate(@NonNull Calendar date) {
        // если воскресенье
        if (date.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            return new TreeSet<>(new ScheduleDay.PairComparator());
        }

        ScheduleDay day = mWeek.get(DayOfWeek.of(date));
        if (day != null) {
            return day.pairsByDate(date);
        }

        return new TreeSet<>(new ScheduleDay.PairComparator());
    }

    /**
     * Возращает первую дату в расписании.
     * Если дата отсутствует, то возвращается {@code null}.
     * @return первая дата.
     */
    @Nullable
    public Calendar firstDate() {
        Calendar first = null;

        for (ScheduleDay day : mWeek.values()) {
            Calendar firstDay = day.firstDay();
            if (firstDay != null) {
                if (first == null) {
                    first = firstDay;
                } else {
                    if (firstDay.compareTo(first) < 0) {
                        first = firstDay;
                    }
                }
            }
        }
        return first;
    }

    /**
     * Возвращает последнию дату в расписании.
     * Если дата отсутствует, то возвращается {@code null}.
     * @return последняя дата.
     */
    @Nullable
    public Calendar lastDate() {
        Calendar last = null;

        for (ScheduleDay day : mWeek.values()) {
            Calendar lastDay = day.lastDay();
            if (lastDay != null) {
                if (last == null) {
                    last = lastDay;
                } else {
                    if (lastDay.compareTo(last) > 0) {
                        last = lastDay;
                    }
                }
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
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            dest.writeParcelable(mWeek.get(dayOfWeek), flags);
        }
    }

    /**
     * Правило сериализации расписания.
     */
    public static class ScheduleSerialize implements JsonSerializer<Schedule> {

        @Override
        public JsonElement serialize(Schedule src, Type typeOfSrc, JsonSerializationContext context) {
            JsonArray scheduleArray = new JsonArray();

            for (ScheduleDay day : src.mWeek.values()) {
                for (Pair pair : day.dayPairs()) {
                    scheduleArray.add(context.serialize(pair, Pair.class));
                }
            }

            return scheduleArray;
        }
    }

    /**
     * Правило десериализации расписания.
     */
    public static class ScheduleDeserialize implements JsonDeserializer<Schedule> {

        @Override
        public Schedule deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Schedule schedule = new Schedule();

            JsonArray scheduleArray = json.getAsJsonArray();
            for (JsonElement pairElement : scheduleArray) {
                Pair pair = context.deserialize(pairElement, Pair.class);
                schedule.addPair(pair);
            }

            return schedule;
        }
    }
}
