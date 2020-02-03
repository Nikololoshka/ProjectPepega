package com.github.nikololoshka.pepegaschedule.schedule.model.pair;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.Objects;

/**
 * Тип пары.
 */
public class TypePair implements Comparable<TypePair>, Parcelable {

    private static final String JSON_TAG = "type";

    @NonNull
    private TypeEnum mType;

    public TypePair(@NonNull TypeEnum type) {
        mType = type;
    }

    private TypePair(@NonNull Parcel in) {
        Serializable type = in.readSerializable();
        if (type == null) {
            throw new IllegalArgumentException("No parsable type pair: " + in);
        }

        mType = (TypeEnum) type;
    }

    public static final Creator<TypePair> CREATOR = new Creator<TypePair>() {
        @Override
        public TypePair createFromParcel(Parcel in) {
            return new TypePair(in);
        }

        @Override
        public TypePair[] newArray(int size) {
            return new TypePair[size];
        }
    };

    /**
     * @return тип пары.
     */
    public TypeEnum type() {
        return mType;
    }

    /**
     * Создает TypePair из json объекта.
     * @param object json объект.
     * @return тип пары.
     */
    public static TypePair fromJson(@NonNull JsonObject object) {
        return new TypePair(TypeEnum.of(object.get(JSON_TAG).getAsString()));
    }

    /**
     * Добавляет TypePair в json объект.
     * @param object json объект.
     */
    public void toJson(@NonNull JsonObject object) {
        object.addProperty(JSON_TAG, mType.toString());
    }

    @Override
    public int compareTo(TypePair o) {
        return mType.compareTo(o.mType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypePair typePair = (TypePair) o;
        return mType == typePair.mType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mType);
    }

    @NonNull
    @Override
    public String toString() {
        return mType.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(mType);
    }
}
