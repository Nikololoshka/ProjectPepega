package com.vereshchagin.nikolay.stankinschedule.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonParseException;
import com.vereshchagin.nikolay.stankinschedule.R;
import com.vereshchagin.nikolay.stankinschedule.schedule.model.Schedule;
import com.vereshchagin.nikolay.stankinschedule.schedule.model.pair.Pair;
import com.vereshchagin.nikolay.stankinschedule.schedule.model.pair.SubgroupEnum;
import com.vereshchagin.nikolay.stankinschedule.settings.ApplicationPreference;
import com.vereshchagin.nikolay.stankinschedule.settings.SchedulePreference;
import com.vereshchagin.nikolay.stankinschedule.utils.CommonUtils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TreeSet;

import static com.vereshchagin.nikolay.stankinschedule.settings.ApplicationPreference.LABORATORY_COLOR;
import static com.vereshchagin.nikolay.stankinschedule.settings.ApplicationPreference.LECTURE_COLOR;
import static com.vereshchagin.nikolay.stankinschedule.settings.ApplicationPreference.SEMINAR_COLOR;

/**
 * Адаптер для виджета с расписанием.
 */
public class ScheduleWidgetRemoteFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final String TAG = "ScheduleAppWgtFactoryLog";

    /**
     * Пакет.
     */
    @NonNull
    private String mPackageName;
    /**
     * ID виджета.
     */
    private int mScheduleAppWidgetId;

    /**
     * Контекст.
     */
    private WeakReference<Context> mContext;
    /**
     * Локаль.
     */
    @NonNull
    private Locale mLocale;

    /**
     * Список дней.
     */
    private ArrayList<ScheduleWidgetDayItem> mDays;

    private int mLectureColor;
    private int mSeminarColor;
    private int mLaboratoryColor;

    /**
     * Название расписание.
     */
    @Nullable
    private String mScheduleName;
    /**
     * Путь к расписанию.
     */
    @Nullable
    private String mSchedulePath;
    /**
     * Подгруппа в расписании.
     */
    @Nullable
    private SubgroupEnum mSubgroup;

    // ошибка при загрузке
    private boolean mLoadingError;
    private String mErrorMessage;


    private ScheduleWidgetRemoteFactory(@NonNull Context context, @NonNull Intent intent) {
        mContext = new WeakReference<>(context);
        mPackageName = context.getPackageName();
        mLocale = CommonUtils.locale(context);

        mScheduleAppWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        mDays = new ArrayList<>();
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        mDays.clear();

        Context context = mContext.get();

        // загрузка цветов
        if (context != null) {
            mLectureColor = ApplicationPreference.pairColor(context, LECTURE_COLOR);
            mSeminarColor = ApplicationPreference.pairColor(context, SEMINAR_COLOR);
            mLaboratoryColor = ApplicationPreference.pairColor(context, LABORATORY_COLOR);
        }

        if ((mScheduleName == null || mSchedulePath == null || mSubgroup == null) && context == null) {
                // не было контекста и данные о расписании не загружены.
                mErrorMessage = mContext.get().getString(R.string.widget_schedule_error);
                mLoadingError = true;
                return;
        }

        if (context != null) {
            ScheduleWidgetConfigureActivity.WidgetData widgetData =
                    ScheduleWidgetConfigureActivity.loadPref(context, mScheduleAppWidgetId);

            mScheduleName = widgetData.scheduleName();
            mSchedulePath = SchedulePreference.createPath(context, mScheduleName);
            mSubgroup = widgetData.subgroup();
        }

        @NonNull Schedule schedule;
        try {
            File file = new File(mSchedulePath);
            String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            schedule = Schedule.fromJson(json);

            mLoadingError = false;

        } catch (JsonParseException | IOException e) {
            e.printStackTrace();

            mErrorMessage = mContext.get().getString(R.string.widget_schedule_error);
            mLoadingError = true;
            return;
        }

        SimpleDateFormat formatter = new SimpleDateFormat("EE, dd MMMM", mLocale);

        Calendar iterator = CommonUtils.normalizeDate(new GregorianCalendar());
        for (int i = 0; i < 7; i++) {
            ScheduleWidgetDayItem item = new ScheduleWidgetDayItem();
            item.pairs = schedule.pairsByDate(iterator);

            String dayFormat = formatter.format(iterator.getTime());
            item.dayTitle = CommonUtils.toTitleCase(dayFormat);

            item.dayTime = (Calendar) iterator.clone();

            mDays.add(item);
            iterator.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mLoadingError ? 1 : mDays.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (mLoadingError) {
            // отображаем ошибку
            RemoteViews errorView = new RemoteViews(mPackageName, R.layout.view_error);
            errorView.setTextViewText(R.id.error_title, mErrorMessage);
            return errorView;
        }

        // создаем view с днем
        RemoteViews dayView = new RemoteViews(mPackageName, R.layout.widget_item_schedule);
        dayView.removeAllViews(R.id.schedule_day_pairs);

        ScheduleWidgetDayItem item = mDays.get(position);

        // заголовок дня
        dayView.setTextViewText(R.id.schedule_day_title, item.dayTitle);

        // добавляем пары
        int addedPairs = 0;
        for (Pair pair : item.pairs) {
            // если не подходит по подгруппе
            SubgroupEnum subgroup = pair.subgroup().subgroup();
            if (subgroup != SubgroupEnum.COMMON && subgroup != mSubgroup) {
                continue;
            }

            RemoteViews pairView = new RemoteViews(mPackageName, R.layout.widget_item_schedule_pair);

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
            addedPairs++;
        }

        // если нет пар
        if (addedPairs == 0) {
            RemoteViews pairView = new RemoteViews(mPackageName, R.layout.widget_item_schedule_no_pairs);
            dayView.addView(R.id.schedule_day_pairs, pairView);
        }

        // для обратного вызова приложения с расписанием на определенном дне
        Intent intent = new Intent();
        intent.putExtra(ScheduleWidget.SCHEDULE_DAY_TIME, item.dayTime);
        intent.putExtra(ScheduleWidget.SCHEDULE_NAME, mScheduleName);
        dayView.setOnClickFillInIntent(R.id.widget_item_schedule_app, intent);

        return dayView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return new RemoteViews(mPackageName, R.layout.widget_item_schedule_shimmer);
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

    /**
     * Сервис, который создает адаптер по обновлению данных в виджете.
     */
    public static class Service extends RemoteViewsService {
        @Override
        public RemoteViewsFactory onGetViewFactory(Intent intent) {
            return new ScheduleWidgetRemoteFactory(getApplicationContext(), intent);
        }
    }

    /**
     * Информация о дне в виджете с расписанием.
     */
    private static class ScheduleWidgetDayItem {
        /**
         * Заголовок дня.
         */
        String dayTitle;
        /**
         * Пары дня.
         */
        TreeSet<Pair> pairs;
        /**
         * Время дня.
         */
        Calendar dayTime;
    }
}
