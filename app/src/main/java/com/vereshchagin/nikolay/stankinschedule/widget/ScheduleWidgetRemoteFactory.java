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
import com.vereshchagin.nikolay.stankinschedule.model.schedule.Schedule;
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Pair;
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Subgroup;
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepository;
import com.vereshchagin.nikolay.stankinschedule.ui.settings.ApplicationPreference;
import com.vereshchagin.nikolay.stankinschedule.ui.settings.SchedulePreference;
import com.vereshchagin.nikolay.stankinschedule.utils.CommonUtils;

import org.joda.time.LocalDate;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.vereshchagin.nikolay.stankinschedule.ui.settings.ApplicationPreference.LABORATORY_COLOR;
import static com.vereshchagin.nikolay.stankinschedule.ui.settings.ApplicationPreference.LECTURE_COLOR;
import static com.vereshchagin.nikolay.stankinschedule.ui.settings.ApplicationPreference.SEMINAR_COLOR;

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
    private Subgroup mSubgroup;

    // ошибка при загрузке
    private boolean mLoadingError;
    private String mErrorMessage;


    private ScheduleWidgetRemoteFactory(@NonNull Context context, @NonNull Intent intent) {
        mContext = new WeakReference<>(context);
        mPackageName = context.getPackageName();

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
            ScheduleRepository repository = new ScheduleRepository();
            schedule = repository.load(mSchedulePath);
            mLoadingError = false;

        } catch (JsonParseException | IOException e) {
            e.printStackTrace();

            mErrorMessage = mContext.get().getString(R.string.widget_schedule_error);
            mLoadingError = true;
            return;
        }

        LocalDate iterator = LocalDate.now();
        for (int i = 0; i < 7; i++) {
            ScheduleWidgetDayItem item = new ScheduleWidgetDayItem();

            String dayFormat = iterator.toString("EE, dd MMMM");
            item.dayTitle = CommonUtils.toTitleCase(dayFormat);
            item.dayTime = iterator;
            item.pairs = schedule.pairsByDate(iterator);
            mDays.add(item);

            iterator = iterator.plusDays(1);
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
            if (!pair.isCurrently(mSubgroup)) {
                continue;
            }

            RemoteViews pairView = new RemoteViews(mPackageName, R.layout.widget_item_schedule_pair);

            pairView.setTextViewText(R.id.widget_schedule_title, pair.getTitle());
            pairView.setTextViewText(R.id.widget_schedule_time, pair.getTime().toString());
            pairView.setTextViewText(R.id.widget_schedule_classroom, pair.getClassroom());

            int color = 0;
            switch (pair.getType()) {
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
        List<Pair> pairs;
        /**
         * Время дня.
         */
        LocalDate dayTime;
    }
}
