package com.github.nikololoshka.pepegaschedule.schedule.model.pair;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class Pair implements Parcelable {

    private TitlePair mTitle;
    private LecturerPair mLecturer;
    private TypePair mType;
    private SubgroupPair mSubgroup;
    private ClassroomPair mClassroom;
    private TimePair mTime;
    private DatePair mDate;

    public Pair() {
        mTitle = new TitlePair();
        mLecturer = new LecturerPair();
        mType = new TypePair();
        mSubgroup = new SubgroupPair();
        mClassroom = new ClassroomPair();
        mTime = new TimePair();
        mDate = new DatePair();
    }

    protected Pair(Parcel in) {
        mTitle = in.readParcelable(TitlePair.class.getClassLoader());
        mLecturer = in.readParcelable(LecturerPair.class.getClassLoader());
        mType = in.readParcelable(TypePair.class.getClassLoader());
        mSubgroup = in.readParcelable(SubgroupPair.class.getClassLoader());
        mClassroom = in.readParcelable(ClassroomPair.class.getClassLoader());
        mTime = in.readParcelable(TimePair.class.getClassLoader());
        mDate = in.readParcelable(DatePair.class.getClassLoader());
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

    public void load(JSONObject loadObject) throws JSONException {
        mTitle.load(loadObject);
        mLecturer.load(loadObject);
        mType.load(loadObject);
        mSubgroup.load(loadObject);
        mClassroom.load(loadObject);
        mTime.load(loadObject);
        mDate.load(loadObject);
    }

    public JSONObject save() throws JSONException {
        JSONObject jsonPair = new JSONObject();

        mTitle.save(jsonPair);
        mLecturer.save(jsonPair);
        mType.save(jsonPair);
        mSubgroup.save(jsonPair);
        mClassroom.save(jsonPair);
        mTime.save(jsonPair);
        mDate.save(jsonPair);

        return jsonPair;
    }

    public TitlePair title() {
        return mTitle;
    }

    public LecturerPair lecturer() {
        return mLecturer;
    }

    public TypePair type() {
        return mType;
    }

    public SubgroupPair subgroup() {
        return mSubgroup;
    }

    public ClassroomPair classroom() {
        return mClassroom;
    }

    public TimePair time() {
        return mTime;
    }

    public DatePair date() {
        return mDate;
    }

    public void setTitle(TitlePair title) {
        mTitle = title;
    }

    public void setLecturer(LecturerPair lecturer) {
        mLecturer = lecturer;
    }

    public void setType(TypePair type) {
        mType = type;
    }

    public void setSubgroup(SubgroupPair subgroup) {
        mSubgroup = subgroup;
    }

    public void setClassroom(ClassroomPair classroom) {
        mClassroom = classroom;
    }

    public void setTime(TimePair time) {
        mTime = time;
    }

    public void setDate(DatePair date) {
        mDate = date;
    }

    @Override
    public @NonNull String toString() {
        return "pair{" +
                "mTitle=" + mTitle +
                ", mLecturer=" + mLecturer +
                ", mType=" + mType +
                ", mSubgroup=" + mSubgroup +
                ", mClassroom=" + mClassroom +
                ", mTime=" + mTime +
                ", mDate=" + mDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair pair = (Pair) o;
        return Objects.equals(mTitle, pair.mTitle) &&
                Objects.equals(mLecturer, pair.mLecturer) &&
                Objects.equals(mType, pair.mType) &&
                Objects.equals(mSubgroup, pair.mSubgroup) &&
                Objects.equals(mClassroom, pair.mClassroom) &&
                Objects.equals(mTime, pair.mTime) &&
                Objects.equals(mDate, pair.mDate);
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
}
