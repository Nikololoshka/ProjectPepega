package com.github.nikololoshka.pepegaschedule.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import androidx.annotation.NonNull;

import com.github.nikololoshka.pepegaschedule.R;
import com.github.nikololoshka.pepegaschedule.schedule.Schedule;
import com.github.nikololoshka.pepegaschedule.schedule.pair.Pair;
import com.github.nikololoshka.pepegaschedule.settings.ApplicationPreference;
import com.github.nikololoshka.pepegaschedule.settings.SchedulePreference;

import org.json.JSONException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TreeSet;

/**
 * Адаптер для виджета с расписанием.
 */
public class ScheduleAppWidgetRemoteFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final String TAG = "ScheduleAppWgtFactoryLog";

    private String mPackageName;
    private WeakReference<Context> mContext;
    private int mScheduleAppWidgetId;

    private ArrayList<TreeSet<Pair>> mPairs;
    private ArrayList<String> mTitles;
    private ArrayList<Long> mTimes;

    private int mLectureColor;
    private int mSeminarColor;
    private int mLaboratoryColor;

    private String mScheduleName;

    ScheduleAppWidgetRemoteFactory(@NonNull Context context, @NonNull Intent intent) {
        mContext = new WeakReference<>(context);
        mPackageName = context.getPackageName();
        mScheduleAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        mPairs = new ArrayList<>();
        mTitles = new ArrayList<>();
        mTimes = new ArrayList<>();
    }

    @Override
    public void onDataSetChanged() {
        mPairs.clear();
        mTitles.clear();
        mTimes.clear();

        // загрузка цветов
        mLectureColor = ApplicationPreference.pairColor(mContext.get(),
                ApplicationPreference.LECTURE_COLOR);
        mSeminarColor = ApplicationPreference.pairColor(mContext.get(),
                ApplicationPreference.SEMINAR_COLOR);
        mLaboratoryColor = ApplicationPreference.pairColor(mContext.get(),
                ApplicationPreference.LABORATORY_COLOR);

        mScheduleName = ScheduleAppWidgetConfigureActivity.loadPref(mContext.get(), mScheduleAppWidgetId);
        if (mScheduleName == null) {
            return;
        }

        String schedulePath = SchedulePreference.createPath(mContext.get(), mScheduleName);

        Schedule schedule = new Schedule();
        try {
            // TODO добавить информацию для пользователя об исключении
            schedule.load(schedulePath);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        Calendar now = new GregorianCalendar();
        Calendar date = new GregorianCalendar(now.get(Calendar.YEAR),
                now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));

        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = mContext.get().getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = mContext.get().getResources().getConfiguration().locale;
        }

        SimpleDateFormat formatter = new SimpleDateFormat("EE, dd MMMM", locale);

        for (int i = 0; i < 14; i++) {
            TreeSet<Pair> pairs = schedule.pairsByDate(date);

            mPairs.add(pairs);

            String dayFormat = formatter.format(date.getTime());
            String dayTitle = dayFormat.substring(0, 1).toUpperCase() + dayFormat.substring(1);

            mTitles.add(dayTitle);
            mTimes.add(date.getTimeInMillis());

            date.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mTitles.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews dayView = new RemoteViews(mPackageName,
                R.layout.widget_item_schedule_app);
        dayView.removeAllViews(R.id.schedule_day_pairs);

        dayView.setTextViewText(R.id.schedule_day_title, mTitles.get(position));
        TreeSet<Pair> pairs = mPairs.get(position);

        if (!pairs.isEmpty()) {
            for (Pair pair : pairs) {
                RemoteViews pairView = new RemoteViews(mPackageName,
                        R.layout.widget_item_schedule_app_pair);

                pairView.setTextViewText(R.id.widget_schedule_title, pair.title().title());
                pairView.setTextViewText(R.id.widget_schedule_time, pair.time().toString());
                pairView.setTextViewText(R.id.widget_schedule_classroom, pair.classroom().classroom());

                int color = 0;
                switch (pair.type().type()) {
                    case LECTURE:
                        color = mLectureColor;
                        break;
                    case SEMINAR:
                        color = mSeminarColor;
                        break;
                    case LABORATORY:
                        color = mLaboratoryColor;
                        break;
                }

                pairView.setInt(R.id.widget_schedule_type, "setColorFilter", color);

                dayView.addView(R.id.schedule_day_pairs, pairView);
            }
        } else {
            RemoteViews pairView = new RemoteViews(mPackageName,
                    R.layout.widget_item_schedule_app_no_pairs);
            dayView.addView(R.id.schedule_day_pairs, pairView);
        }

        // для обратного вызова приложения с расписанием на дне
        Intent intent = new Intent();
        intent.putExtra(ScheduleAppWidget.SCHEDULE_DAY_TIME, mTimes.get(position));
        intent.putExtra(ScheduleAppWidget.SCHEDULE_NAME, mScheduleName);
        dayView.setOnClickFillInIntent(R.id.widget_item_schedule_app, intent);

        return dayView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return new RemoteViews(mPackageName, R.layout.widget_item_schedule_app_pair_shimmer);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
