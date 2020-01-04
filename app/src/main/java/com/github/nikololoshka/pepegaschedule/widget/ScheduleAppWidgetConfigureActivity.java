package com.github.nikololoshka.pepegaschedule.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.github.nikololoshka.pepegaschedule.R;
import com.github.nikololoshka.pepegaschedule.settings.SchedulePreference;

import java.util.List;

/**
 * Конфигурационное activity для виджета с расписанием.
 */
public class ScheduleAppWidgetConfigureActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String CONFIGURE_BUTTON_TEXT_EXTRA = "configure_button_text";

    private static final String SCHEDULE_WIDGET_PREFERENCE = "schedule_widget_preference";
    private static final String SCHEDULE_WIDGET = "schedule_app_widget_";

    /**
     * ID виджета с расписанием.
     */
    private int mScheduleAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private Spinner mSchedulesSpinner;

    public ScheduleAppWidgetConfigureActivity() {
        super();
    }

    /**
     * Сохраняет данные виджета в SharedPreferences.
     * @param context контекст.
     * @param appWidgetId ID виджета.
     * @param schedule расписание, отображаемое на виджете.
     */
    public static void savePref(Context context, int appWidgetId, String schedule) {
        SharedPreferences preferences =
                context.getSharedPreferences(SCHEDULE_WIDGET_PREFERENCE, MODE_PRIVATE);

        // ApplicationPreference.addScheduleWidget(context, appWidgetId);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SCHEDULE_WIDGET + appWidgetId, schedule);
        editor.apply();
    }

    /**
     * Загружает данные виджета из SharedPreferences.
     * @param context контекст.
     * @param appWidgetId ID виджета.
     * @return расписание, отображаемое на виджете.
     */
    @Nullable
    public static String loadPref(Context context, int appWidgetId) {
        SharedPreferences preferences =
                context.getSharedPreferences(SCHEDULE_WIDGET_PREFERENCE, MODE_PRIVATE);


        return preferences.getString(SCHEDULE_WIDGET + appWidgetId, null);
    }

    /**
     * Удаляет данные, связанные с виджетом, из SharedPreferences.
     * @param context контекст.
     * @param appWidgetId ID виджета.
     */
    public static void deletePref(Context context, int appWidgetId) {
        SharedPreferences preferences =
                context.getSharedPreferences(SCHEDULE_WIDGET_PREFERENCE, MODE_PRIVATE);

        // ApplicationPreference.removeScheduleWidget(context, appWidgetId);

        preferences.edit()
                .remove(SCHEDULE_WIDGET + appWidgetId)
                .apply();
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

        // если не корректное ID у виджета
        if (mScheduleAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        // настройка layout'а
        setContentView(R.layout.widget_schedule_app_configure);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        mSchedulesSpinner = findViewById(R.id.widget_schedule_selector);

        List<String> schedules = SchedulePreference.schedules(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, schedules);
        mSchedulesSpinner.setAdapter(adapter);

        CardView viewAdd = findViewById(R.id.widget_schedule_add);
        viewAdd.setOnClickListener(this);

        String text = getIntent().getStringExtra(CONFIGURE_BUTTON_TEXT_EXTRA);
        if (text != null) {
            ((TextView) findViewById(R.id.widget_schedule_add_text)).setText(text);
        }

        // загрузка данных
        String schedule = loadPref(this, mScheduleAppWidgetId);
        if (schedule != null) {
            int position = schedules.indexOf(schedule);
            if (position != -1) {
                mSchedulesSpinner.setSelection(position);
            }
        }
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
        savePref(this, mScheduleAppWidgetId, schedule);

        // обновляем виджет
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        ScheduleAppWidget.updateAppWidget(this, appWidgetManager, mScheduleAppWidgetId);

        // завершаем конфигурирование виджета
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mScheduleAppWidgetId);
        setResult(RESULT_OK, resultValue);

        finish();
    }
}

