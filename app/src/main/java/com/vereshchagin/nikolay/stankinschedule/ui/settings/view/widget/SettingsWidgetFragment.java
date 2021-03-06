package com.vereshchagin.nikolay.stankinschedule.ui.settings.view.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.vereshchagin.nikolay.stankinschedule.R;
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Subgroup;
import com.vereshchagin.nikolay.stankinschedule.utils.StatefulLayout2;
import com.vereshchagin.nikolay.stankinschedule.utils.WidgetUtils;
import com.vereshchagin.nikolay.stankinschedule.widget.ScheduleWidgetConfigureActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Категория настроек виджетов приложения.
 */
public class SettingsWidgetFragment extends Fragment implements SettingsWidgetAdapter.OnWidgetClickListener {

    private static final String TAG = "SettingsWgtFragmentTag";

    private StatefulLayout2 mStatefulLayout;
    private SettingsWidgetAdapter mWidgetAdapter;

    public SettingsWidgetFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_widget_settings, container, false);
        FrameLayout widgetsContainer = view.findViewById(R.id.stateful_layout);

        mStatefulLayout = new StatefulLayout2.Builder(widgetsContainer)
                .init(StatefulLayout2.LOADING, view.findViewById(R.id.widgets_loading))
                .addView(StatefulLayout2.CONTENT, view.findViewById(R.id.schedule_widgets))
                .addView(StatefulLayout2.EMPTY, view.findViewById(R.id.not_widgets))
                .setOwner(this)
                .create();

        RecyclerView widgetsRecyclerView = view.findViewById(R.id.recycler_widgets);
        mWidgetAdapter = new SettingsWidgetAdapter(this);
        widgetsRecyclerView.setAdapter(mWidgetAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        // обновляем список текущих виджетов с расписаниями
        Context context = requireContext();

        List<Integer> ids = WidgetUtils.scheduleWidgets(context);
        List<String> names = new ArrayList<>(ids.size());

        for (Integer id : ids) {
            ScheduleWidgetConfigureActivity.WidgetData data = ScheduleWidgetConfigureActivity.loadPref(context, id);

            String scheduleName = data.getScheduleName();
            if (scheduleName != null) {
                if (data.getDisplay() && data.getSubgroup() != Subgroup.COMMON) {
                    scheduleName += " " + data.getSubgroup().toString(context);
                }
                names.add(scheduleName);
            }
        }

        if (names.isEmpty()) {
            mStatefulLayout.setState(StatefulLayout2.EMPTY);
            return;
        }

        mWidgetAdapter.submitList(names, ids);
        mStatefulLayout.setState(StatefulLayout2.CONTENT);
    }

    @Override
    public void OnWidgetClicked(int widgetID) {
        // вызываем конфигурационное окно виджета
        Intent configIntent = new Intent(getContext(), ScheduleWidgetConfigureActivity.class);
        configIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
        configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
        configIntent.putExtra(ScheduleWidgetConfigureActivity.CONFIGURE_BUTTON_TEXT_EXTRA,
                getString(R.string.widget_schedule_update));

        PendingIntent configurationPendingIntent = PendingIntent.getActivity(getContext(),
                widgetID, configIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        try {
            configurationPendingIntent.send();
        } catch (PendingIntent.CanceledException ignored) {

        }
    }
}