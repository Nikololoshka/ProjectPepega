package com.vereshchagin.nikolay.stankinschedule.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.vereshchagin.nikolay.stankinschedule.R;
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Subgroup;
import com.vereshchagin.nikolay.stankinschedule.ui.settings.SchedulePreference;
import com.vereshchagin.nikolay.stankinschedule.utils.CommonUtils;

import java.util.List;

/**
 * Конфигурационное activity для виджета с расписанием.
 */
public class ScheduleWidgetConfigureActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String CONFIGURE_BUTTON_TEXT_EXTRA = "configure_button_text";

    private static final String SCHEDULE_WIDGET_PREFERENCE = "schedule_widget_preference";
    private static final String SCHEDULE_WIDGET = "schedule_app_widget_";
    private static final String NAME_SUFFIX = "_name";
    private static final String SUBGROUP_SUFFIX = "_subgroup";

    /**
     * ID виджета с расписанием.
     */
    private int mScheduleAppWidgetId;
    /**
     * Список с расписаниями.
     */
    private Spinner mSchedulesSpinner;
    /**
     * Список с подгруппами.
     */
    private Spinner mSubgroupSpinner;

    public ScheduleWidgetConfigureActivity() {
        super();
        mScheduleAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // устанавливаем RESULT_CANCELED, т.к. пользователь может
        // нажать кнопку назад и нужно отменить создание.
        setResult(RESULT_CANCELED);

        /// извлекаем ID конфигурируемого виджета
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            mScheduleAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // если не корректное ID виджета
        if (mScheduleAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        // настройка layout'а
        setContentView(R.layout.widget_schedule_configure);

        mSchedulesSpinner = findViewById(R.id.widget_schedule_selector);
        mSubgroupSpinner = findViewById(R.id.widget_subgroup_selector);

        List<String> schedules = SchedulePreference.schedules(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, schedules);
        mSchedulesSpinner.setAdapter(adapter);

        TextView viewAdd = findViewById(R.id.widget_schedule_add);
        viewAdd.setOnClickListener(this);

        String text = getIntent().getStringExtra(CONFIGURE_BUTTON_TEXT_EXTRA);
        if (text != null) {
            viewAdd.setText(text);
        }

        if (schedules.isEmpty()) {
            viewAdd.setEnabled(false);
            mSchedulesSpinner.setEnabled(false);
            mSubgroupSpinner.setEnabled(false);
        }

        // загрузка данных
        WidgetData data = loadPref(this, mScheduleAppWidgetId);
        String schedule = data.scheduleName();
        Subgroup subgroup = data.subgroup();

        if (schedule != null) {
            int position = schedules.indexOf(schedule);
            if (position != -1) {
                mSchedulesSpinner.setSelection(position);
            }
        }
        setSubgroupSpinner(subgroup);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        // сохраняем текст
        String schedule = mSchedulesSpinner.getSelectedItem().toString();
        savePref(this, mScheduleAppWidgetId, new WidgetData(schedule, currentSubgroup()));

        // обновляем виджет
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        ScheduleWidget.updateAppWidget(this, appWidgetManager, mScheduleAppWidgetId);

        // завершаем конфигурирование виджета
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mScheduleAppWidgetId);
        setResult(RESULT_OK, resultValue);

        finish();
    }

    /**
     * @return выбранная подгруппа.
     */
    @NonNull
    private Subgroup currentSubgroup() {
        int pos = mSubgroupSpinner.getSelectedItemPosition();
        switch (pos) {
            case 0:
                return Subgroup.COMMON;
            case 1:
                return Subgroup.A;
            case 2:
                return Subgroup.B;
        }
        throw new RuntimeException("Don't select subgroup pair. Position: " + pos);
    }

    /**
     * Устанавливает поле с подгруппой пары.
     * @param subgroup подгруппа пары.
     */
    private void setSubgroupSpinner(@NonNull Subgroup subgroup) {
        switch (subgroup) {
            case COMMON:
                mSubgroupSpinner.setSelection(0);
                break;
            case A:
                mSubgroupSpinner.setSelection(1);
                break;
            case B:
                mSubgroupSpinner.setSelection(2);
                break;
        }
    }

    /**
     * Сохраняет данные виджета в SharedPreferences.
     * @param context контекст.
     * @param appWidgetId ID виджета.
     * @param data данные виджета расписание.
     */
    public static void savePref(@NonNull Context context, int appWidgetId, @NonNull WidgetData data) {
        SharedPreferences preferences = context.getSharedPreferences(SCHEDULE_WIDGET_PREFERENCE, MODE_PRIVATE);

        preferences.edit()
                .putString(SCHEDULE_WIDGET + appWidgetId + NAME_SUFFIX, data.scheduleName())
                .putString(SCHEDULE_WIDGET + appWidgetId + SUBGROUP_SUFFIX, data.subgroup().getTag())
                .apply();
    }

    /**
     * Загружает данные виджета из SharedPreferences.
     * @param context контекст.
     * @param appWidgetId ID виджета.
     * @return данные виджета расписания, отображаемого на виджете.
     */
    @NonNull
    public static WidgetData loadPref(@NonNull Context context, int appWidgetId) {
        SharedPreferences preferences = context.getSharedPreferences(SCHEDULE_WIDGET_PREFERENCE, MODE_PRIVATE);

        String name = preferences.getString(
                SCHEDULE_WIDGET + appWidgetId + NAME_SUFFIX, null);
        Subgroup subgroup = Subgroup.of(CommonUtils.toTitleCase(preferences.getString(
                SCHEDULE_WIDGET + appWidgetId + SUBGROUP_SUFFIX, Subgroup.COMMON.getTag())));

        return new WidgetData(name, subgroup);
    }

    /**
     * Удаляет данные, связанные с виджетом, из SharedPreferences.
     * @param context контекст.
     * @param appWidgetId ID виджета.
     */
    public static void deletePref(@NonNull Context context, int appWidgetId) {
        SharedPreferences preferences = context.getSharedPreferences(SCHEDULE_WIDGET_PREFERENCE, MODE_PRIVATE);

        preferences.edit()
                .remove(SCHEDULE_WIDGET + appWidgetId + NAME_SUFFIX)
                .remove(SCHEDULE_WIDGET + appWidgetId + SUBGROUP_SUFFIX)
                .apply();
    }

    /**
     * Информация виджета.
     */
    public static class WidgetData {
        @Nullable
        private String mScheduleName;
        @NonNull
        private Subgroup mSubgroup;

        WidgetData(@Nullable String scheduleName, @NonNull Subgroup subgroup) {
            mScheduleName = scheduleName;
            mSubgroup = subgroup;
        }

        @Nullable
        public String scheduleName() {
            return mScheduleName;
        }

        @NonNull
        public Subgroup subgroup() {
            return mSubgroup;
        }
    }
}

