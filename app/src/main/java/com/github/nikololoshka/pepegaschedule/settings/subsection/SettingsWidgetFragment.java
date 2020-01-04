package com.github.nikololoshka.pepegaschedule.settings.subsection;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nikololoshka.pepegaschedule.R;
import com.github.nikololoshka.pepegaschedule.utils.StatefulLayout;
import com.github.nikololoshka.pepegaschedule.widget.ScheduleAppWidgetConfigureActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Категория настроек виджетов приложения.
 */
public class SettingsWidgetFragment extends Fragment
        implements SettingsWidgetAdapter.OnWidgetClickListener {

    private static final String TAG = "SettingsWgtFragmentTag";

    private StatefulLayout mStatefulLayout;
    private SettingsWidgetAdapter mWidgetAdapter;

    public SettingsWidgetFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_widget_settings, container, false);

        mStatefulLayout = view.findViewById(R.id.stateful_layout);
        mStatefulLayout.addXMLViews();
        mStatefulLayout.setLoadState();

        RecyclerView widgetsRecyclerView = view.findViewById(R.id.recycler_widgets);
        widgetsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mWidgetAdapter = new SettingsWidgetAdapter(this);
        widgetsRecyclerView.setAdapter(mWidgetAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getContext() == null) {
            mStatefulLayout.setLoadState();
            return;
        }

        AppWidgetManager widgetManager = AppWidgetManager.getInstance(getContext());
        List<AppWidgetProviderInfo> infoList = widgetManager.getInstalledProviders();

        ArrayList<Integer> scheduleIDs = new ArrayList<>();
        ArrayList<String> scheduleNames = new ArrayList<>();
        for (AppWidgetProviderInfo info : infoList) {
            if (info.provider.getPackageName().equals(getContext().getPackageName())) {
                Log.d(TAG, info.provider.toString());

                int[] ids = widgetManager.getAppWidgetIds(info.provider);

                for (int id : ids) {
                    String scheduleName = ScheduleAppWidgetConfigureActivity.loadPref(getContext(), id);

                    if (scheduleName != null) {
                        scheduleNames.add(scheduleName);
                        scheduleIDs.add(id);
                    }
                }

                if (scheduleNames.isEmpty()) {
                    mStatefulLayout.setState(R.id.not_widgets);
                    return;
                }

                break;
            }
        }

        mWidgetAdapter.update(scheduleNames, scheduleIDs);
        mStatefulLayout.setState(R.id.schedule_widgets);
    }

    @Override
    public void OnWidgetClicked(int widgetID) {
        // вызываем конфигурационное окно для виджета
        Intent configIntent = new Intent(getContext(), ScheduleAppWidgetConfigureActivity.class);
        configIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
        configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
        configIntent.putExtra(ScheduleAppWidgetConfigureActivity.CONFIGURE_BUTTON_TEXT_EXTRA,
                getString(R.string.widget_schedule_update));

        PendingIntent configurationPendingIntent = PendingIntent.getActivity(getContext(),
                widgetID, configIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        try {
            configurationPendingIntent.send();
        } catch (PendingIntent.CanceledException ignored) {

        }
    }
}