package com.vereshchagin.nikolay.stankinschedule.ui.settings.view.widget;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vereshchagin.nikolay.stankinschedule.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Адаптер для RecyclerView для отображения виджетов.
 */
public class SettingsWidgetAdapter
        extends RecyclerView.Adapter<SettingsWidgetHolder> {

    interface OnWidgetClickListener {
        void OnWidgetClicked(int widgetID);
    }

    private final OnWidgetClickListener mWidgetClickListener;
    private List<String> mScheduleNames;
    private List<Integer> mScheduleWidgetIDs;

    public SettingsWidgetAdapter(OnWidgetClickListener listener) {
        super();

        mWidgetClickListener = listener;
        mScheduleNames = new ArrayList<>();
        mScheduleWidgetIDs = new ArrayList<>();
    }

    @NonNull
    @Override
    public SettingsWidgetHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_widget_settings, parent, false);
        return new SettingsWidgetHolder(view, mWidgetClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingsWidgetHolder holder, int position) {
        holder.bind(mScheduleNames.get(position), mScheduleWidgetIDs.get(position));
    }

    @Override
    public int getItemCount() {
        return mScheduleNames.size();
    }

    /**
     * Обновляет данные в адаптере.
     *
     * @param scheduleNames      список названий расписаний.
     * @param scheduleWidgetsIDs список ID виджетов расписаний.
     */
    public void submitList(@NonNull List<String> scheduleNames, @NonNull List<Integer> scheduleWidgetsIDs) {
        mScheduleNames = scheduleNames;
        mScheduleWidgetIDs = scheduleWidgetsIDs;

        notifyDataSetChanged();
    }
}
