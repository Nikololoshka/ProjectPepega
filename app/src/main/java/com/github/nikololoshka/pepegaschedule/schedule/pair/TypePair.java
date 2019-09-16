package com.github.nikololoshka.pepegaschedule.schedule.pair;

import android.os.Parcel;

import com.github.nikololoshka.pepegaschedule.schedule.pair.exceptions.InvalidPairParseException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class TypePair extends AttributePair implements Comparable<TypePair> {

    private TypeEnum mType;

    TypePair() {
        mType = null;
    }

    private TypePair(Parcel in) {
        mType = (TypeEnum) in.readSerializable();
    }

    public TypePair(TypePair typePair) {
        mType = typePair.mType;
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

    public static TypePair of(TypeEnum type) {
        TypePair typePair = new TypePair();
        typePair.mType = type;
        return typePair;
    }

    public TypeEnum type() {
        return mType;
    }

    @Override
    public void load(JSONObject loadObject) throws JSONException {
        String type = loadObject.getString("type");
        for (TypeEnum typeEnum: TypeEnum.values()) {
            if (typeEnum.tag().equals(type)) {
                mType = typeEnum;
                return;
            }
        }
        throw new InvalidPairParseException("Not parse type: " + type);
    }

    @Override
    public void save(JSONObject saveObject) throws JSONException {
        saveObject.put("type", mType.tag());
    }

    @Override
    public boolean isValid() {
        return mType != null;
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

    @Override
    public String toString() {
        return mType.text();
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
