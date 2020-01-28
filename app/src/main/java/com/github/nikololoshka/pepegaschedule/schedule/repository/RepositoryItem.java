package com.github.nikololoshka.pepegaschedule.schedule.repository;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * Класс элемента расписания в репозитории.
 */
public class RepositoryItem {

    /**
     * Название расписания.
     */
    @NonNull
    private String mScheduleName;
    /**
     * Путь к расписанию.
     */
    @NonNull
    private String mSchedulePath;

    public RepositoryItem() {
        mScheduleName = "";
        mSchedulePath = "";
    }

    @NonNull
    public String name() {
        return mScheduleName;
    }

    public void setName(@NonNull String scheduleName) {
        mScheduleName = scheduleName;
    }

    @NonNull
    public String path() {
        return mSchedulePath;
    }

    public void setPath(@NonNull String schedulePath) {
        mSchedulePath = schedulePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RepositoryItem that = (RepositoryItem) o;
        return mScheduleName.equals(that.mScheduleName) && mSchedulePath.equals(that.mSchedulePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mScheduleName, mSchedulePath);
    }
}
