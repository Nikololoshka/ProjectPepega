package com.vereshchagin.nikolay.stankinschedule.ui.schedule.model.pair;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * POJO класс пары в расписании.
 */
@Deprecated
public class Pair implements Parcelable {

    @NonNull
    private TitlePair mTitle;
    @NonNull
    private LecturerPair mLecturer;
    @NonNull
    private TypePair mType;
    @NonNull
    private SubgroupPair mSubgroup;
    @NonNull
    private ClassroomPair mClassroom;
    @NonNull
    private TimePair mTime;
    @NonNull
    private DatePair mDate;

    public Pair(@NonNull TitlePair titlePair, @NonNull LecturerPair lecturerPair,
                @NonNull TypePair typePair, @NonNull SubgroupPair subgroupPair,
                @NonNull ClassroomPair classroomPair, @NonNull TimePair timePair,
                @NonNull DatePair datePair) {

        mTitle = titlePair;
        mLecturer = lecturerPair;
        mType = typePair;
        mSubgroup = subgroupPair;
        mClassroom = classroomPair;
        mTime = timePair;
        mDate = datePair;
    }

    private Pair(@NonNull Parcel in) {
        TitlePair titlePair = in.readParcelable(TitlePair.class.getClassLoader());
        LecturerPair lecturerPair = in.readParcelable(LecturerPair.class.getClassLoader());
        TypePair typePair = in.readParcelable(TypePair.class.getClassLoader());
        SubgroupPair subgroupPair = in.readParcelable(SubgroupPair.class.getClassLoader());
        ClassroomPair classroomPair = in.readParcelable(ClassroomPair.class.getClassLoader());
        TimePair timePair = in.readParcelable(TimePair.class.getClassLoader());
        DatePair datePair = in.readParcelable(DatePair.class.getClassLoader());

        if (titlePair == null || lecturerPair == null || typePair == null
                || subgroupPair == null || classroomPair == null
                || timePair == null || datePair == null) {
            throw new IllegalArgumentException("No parsable pair: " + in);
        }

        mTitle = titlePair;
        mLecturer = lecturerPair;
        mType = typePair;
        mSubgroup = subgroupPair;
        mClassroom = classroomPair;
        mTime = timePair;
        mDate = datePair;
    }

    public static final Creator<Pair> CREATOR = new Creator<Pair>() {
        @Override
        public Pair createFromParcel(Parcel in) {
            return new Pair(in);
        }

        @Override
        public Pair[] newArray(int size) {
            return new Pair[size];
        }
    };

    @NonNull
    public TitlePair title() {
        return mTitle;
    }

    public void setTitle(@NonNull TitlePair title) {
        mTitle = title;
    }

    @NonNull
    public LecturerPair lecturer() {
        return mLecturer;
    }

    public void setLecturer(@NonNull LecturerPair lecturer) {
        mLecturer = lecturer;
    }

    @NonNull
    public TypePair type() {
        return mType;
    }

    public void setType(@NonNull TypePair type) {
        mType = type;
    }

    @NonNull
    public SubgroupPair subgroup() {
        return mSubgroup;
    }

    public void setSubgroup(@NonNull SubgroupPair subgroup) {
        mSubgroup = subgroup;
    }

    @NonNull
    public ClassroomPair classroom() {
        return mClassroom;
    }

    public void setClassroom(@NonNull ClassroomPair classroom) {
        mClassroom = classroom;
    }

    @NonNull
    public TimePair time() {
        return mTime;
    }

    public void setTime(@NonNull TimePair time) {
        mTime = time;
    }

    @NonNull
    public DatePair date() {
        return mDate;
    }

    public void setDate(@NonNull DatePair date) {
        mDate = date;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("%s. %s. %s. %s. %s. %s. %s",
                mTitle, mLecturer, mType, mSubgroup, mClassroom, mTime, mDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair pair = (Pair) o;
        return mTitle.equals(pair.mTitle) &&
                mLecturer.equals(pair.mLecturer) &&
                mType.equals(pair.mType) &&
                mSubgroup.equals(pair.mSubgroup) &&
                mClassroom.equals(pair.mClassroom) &&
                mTime.equals(pair.mTime) &&
                mDate.equals(pair.mDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mTitle, mLecturer, mType,
                mSubgroup, mClassroom, mTime, mDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mTitle, flags);
        dest.writeParcelable(mLecturer, flags);
        dest.writeParcelable(mType, flags);
        dest.writeParcelable(mSubgroup, flags);
        dest.writeParcelable(mClassroom, flags);
        dest.writeParcelable(mTime, flags);
        dest.writeParcelable(mDate, flags);
    }

    /**
     * Правило сериализации пары.
     */
    public static class PairSerialize implements JsonSerializer<Pair> {

        @Override
        public JsonElement serialize(Pair src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();

            src.mTitle.toJson(jsonObject);
            src.mLecturer.toJson(jsonObject);
            src.mType.toJson(jsonObject);
            src.mSubgroup.toJson(jsonObject);
            src.mClassroom.toJson(jsonObject);
            src.mTime.toJson(jsonObject);
            src.mDate.toJson(jsonObject);

            return jsonObject;
        }
    }

    /**
     * Правило десериализиции пары.
     */
    public static class PairDeserialize implements JsonDeserializer<Pair> {

        @Override
        public Pair deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject pairObject = json.getAsJsonObject();

            return new Pair(TitlePair.fromJson(pairObject),
                    LecturerPair.fromJson(pairObject),
                    TypePair.fromJson(pairObject),
                    SubgroupPair.fromJson(pairObject),
                    ClassroomPair.fromJson(pairObject),
                    TimePair.fromJson(pairObject),
                    DatePair.fromJson(pairObject));
        }
    }
}
