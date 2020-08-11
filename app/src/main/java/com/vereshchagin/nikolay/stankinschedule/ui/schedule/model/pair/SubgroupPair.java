package com.vereshchagin.nikolay.stankinschedule.ui.schedule.model.pair;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.Objects;

/**
 * Подгруппа пары.
 */
public class SubgroupPair implements Comparable<SubgroupPair>, Parcelable {

    private static final String JSON_TAG = "subgroup";

    @NonNull
    private SubgroupEnum mSubgroup;

    public SubgroupPair(@NonNull SubgroupEnum subgroup) {
        mSubgroup = subgroup;
    }

    private SubgroupPair(@NonNull Parcel in) {
        Serializable subgroup = in.readSerializable();
        if (subgroup == null) {
            throw new IllegalArgumentException("No parsable type pair: " + in);
        }

        mSubgroup = (SubgroupEnum) subgroup;
    }

    public static final Creator<SubgroupPair> CREATOR = new Creator<SubgroupPair>() {
        @Override
        public SubgroupPair createFromParcel(Parcel in) {
            return new SubgroupPair(in);
        }

        @Override
        public SubgroupPair[] newArray(int size) {
            return new SubgroupPair[size];
        }
    };

    /**
     * @return подгруппа пары.
     */
    @NonNull
    public SubgroupEnum subgroup() {
        return mSubgroup;
    }

    /**
     * Создает SubgroupPair из json объекта.
     * @param object json объект.
     * @return подгруппа пары.
     */
    public static SubgroupPair fromJson(@NonNull JsonObject object) {
        return new SubgroupPair(SubgroupEnum.of(object.get(JSON_TAG).getAsString()));
    }

    /**
     * Добавляет SubgroupPair в json объект.
     * @param object json объект.
     */
    public void toJson(@NonNull JsonObject object) {
        object.addProperty(JSON_TAG, mSubgroup.toString());
    }

    /**
     * Определяет могут ли подгруппы существовать параллельно в расписании.
     * @param subgroup подгруппа другой пары.
     * @return true если текущая пара и другая могут быть в расписании в одно и тоже время, иначе false.
     */
    public boolean isConcurrently(@NonNull SubgroupPair subgroup) {
        if ((mSubgroup == SubgroupEnum.A || mSubgroup == SubgroupEnum.B)
                && (subgroup.mSubgroup == SubgroupEnum.A || subgroup.mSubgroup == SubgroupEnum.B)) {
            return mSubgroup != subgroup.mSubgroup;
        }
        // если обе общии пары
        return false;
    }

    @Override
    public int compareTo(SubgroupPair o) {
        return mSubgroup.compareTo(o.mSubgroup);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubgroupPair that = (SubgroupPair) o;
        return mSubgroup == that.mSubgroup;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mSubgroup);
    }

    @NonNull
    @Override
    public String toString() {
        return mSubgroup.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(mSubgroup);
    }
}

