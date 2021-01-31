package com.vereshchagin.nikolay.stankinschedule.ui.settings.view.widget;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vereshchagin.nikolay.stankinschedule.R;

/**
 * Holder элемента RecyclerView с виджетами.
 */
class SettingsWidgetHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final SettingsWidgetAdapter.OnWidgetClickListener listener;
    private final TextView mScheduleTitle;
    private int mWidgetID;

    SettingsWidgetHolder(@NonNull View itemView, SettingsWidgetAdapter.OnWidgetClickListener listener) {
        super(itemView);
        this.listener = listener;

        mScheduleTitle = itemView.findViewById(R.id.widget_schedule_name);
        itemView.findViewById(R.id.widget_item).setOnClickListener(this);
    }

    /**
     * Обновляет данные в holder.
     *
     * @param scheduleName название расписания.
     * @param widgetID     ID виджета расписания.
     */
    void bind(@NonNull String scheduleName, int widgetID) {
        mScheduleTitle.setText(scheduleName);
        mWidgetID = widgetID;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.widget_item) {
            listener.OnWidgetClicked(mWidgetID);
        }
    }
}
