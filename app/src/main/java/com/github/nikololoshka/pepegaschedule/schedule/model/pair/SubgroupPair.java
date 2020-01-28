package com.github.nikololoshka.pepegaschedule.schedule.model.pair;

import android.os.Parcel;

import com.github.nikololoshka.pepegaschedule.schedule.model.pair.exceptions.InvalidPairParseException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class SubgroupPair extends AttributePair implements Comparable<SubgroupPair> {

    private SubgroupEnum mSubgroup;

    SubgroupPair() {
        mSubgroup = null;
    }

    private SubgroupPair(Parcel in) {
        mSubgroup = (SubgroupEnum) in.readSerializable();
    }

    public SubgroupPair(SubgroupPair subgroupPair) {
        mSubgroup = subgroupPair.mSubgroup;
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

    public static SubgroupPair of(SubgroupEnum subgroup) {
        SubgroupPair subgroupPair = new SubgroupPair();
        subgroupPair.mSubgroup = subgroup;
        return subgroupPair;
    }

    public SubgroupEnum subgroup() {
        return mSubgroup;
    }

    @Override
    public void load(JSONObject loadObject) throws JSONException {
        String subgroup = loadObject.getString("subgroup");
        for (SubgroupEnum subgroupEnum: SubgroupEnum.values()) {
            if (subgroupEnum.tag().equals(subgroup)) {
                mSubgroup = subgroupEnum;
                return;
            }
        }
        throw new InvalidPairParseException("Not parse subgroup: " + subgroup);
    }

    public boolean isConcurrently(SubgroupPair o) {
        if ((mSubgroup == SubgroupEnum.A || mSubgroup == SubgroupEnum.B)
                && (o.mSubgroup == SubgroupEnum.A || o.mSubgroup == SubgroupEnum.B)) {
            return mSubgroup == o.mSubgroup;
        }
        return true;
    }

    @Override
    public void save(JSONObject saveObject) throws JSONException {
        saveObject.put("subgroup", mSubgroup.tag());
    }

    @Override
    public boolean isValid() {
        return mSubgroup != null && mSubgroup != SubgroupEnum.COMMON;
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

    @Override
    public String toString() {
        return mSubgroup.text();
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

