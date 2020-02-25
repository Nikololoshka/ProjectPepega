package com.vereshchagin.nikolay.stankinschedule.schedule.repository;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.vereshchagin.nikolay.stankinschedule.BuildConfig;
import com.vereshchagin.nikolay.stankinschedule.R;
import com.vereshchagin.nikolay.stankinschedule.settings.SchedulePreference;
import com.vereshchagin.nikolay.stankinschedule.utils.NotificationUtils;

import org.apache.commons.io.output.FileWriterWithEncoding;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;


/**
 * Service по скачиванию расписания.
 */
public class ScheduleDownloaderService extends IntentService {

    public static final String SCHEDULE_DOWNLOADED_EVENT = "schedule_downloaded_event";
    public static final String ARG_SCHEDULE_DOWNLOADED = "schedule_downloaded";

    private static final String EXTRA_SCHEDULE_URL = "schedule_url";
    private static final String EXTRA_SCHEDULE_NAME = "schedule_name";
    private static final String EXTRA_NOTIFICATION_ID = "notification_id";

    private static final int SERVICE_NOTIFICATION_ID = 1;

    /**
     * Пул ID для уведомлений.
     */
    private static int DOWNLOAD_NOTIFICATION_ID = 100;

    private static final String TAG = "MyLog";

    private static final int MAX_PROGRESS = 100;

    NotificationCompat.Builder mNotificationBuilder;
    NotificationManager mNotificationManager;

    /**
     * Создает задачу на скачивание расписания с уведомлением.
     * @param context - контекст приложения.
     * @param name - название расписания.
     * @param url - ссылка на расписание.
     */
    public static void createTask(@NonNull Context context, String name, String url) {
        Intent intent = new Intent(context, ScheduleDownloaderService.class);
        intent.putExtra(ScheduleDownloaderService.EXTRA_SCHEDULE_NAME, name);
        intent.putExtra(ScheduleDownloaderService.EXTRA_SCHEDULE_URL,  url);

        NotificationCompat.Builder builder = NotificationUtils.createCommonNotification(context)
                .setContentTitle(name)
                .setContentText(context.getString(R.string.notification_awaiting_download))
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_notification_file_download);

        NotificationUtils.notifyCommon(context, context.getSystemService(NotificationManager.class),
                DOWNLOAD_NOTIFICATION_ID, builder.build());

        if (BuildConfig.DEBUG) {
            Log.d(TAG, String.format("createTask: name: %s, ID: %d", name, DOWNLOAD_NOTIFICATION_ID));
        }

        intent.putExtra(EXTRA_NOTIFICATION_ID, DOWNLOAD_NOTIFICATION_ID++);

        ContextCompat.startForegroundService(context, intent);
    }

    public ScheduleDownloaderService() {
        super("ScheduleDownloaderService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // общие уведомление сервера
        mNotificationBuilder = NotificationUtils.createCommonNotification(this)
                .setContentText(getString(R.string.repository_loading_schedule))
                .setWhen(System.currentTimeMillis())
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_notification_file_download)
                .setProgress(MAX_PROGRESS, 0, true);

        mNotificationManager = getSystemService(NotificationManager.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(SERVICE_NOTIFICATION_ID, mNotificationBuilder.build());
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            // реализация для расписаний поставляющихся вместе с приложением!!!
            String schedulePath = intent.getStringExtra(EXTRA_SCHEDULE_URL);
            String scheduleName = intent.getStringExtra(EXTRA_SCHEDULE_NAME);
            int notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, SERVICE_NOTIFICATION_ID);

            mNotificationManager.cancel(notificationId);

            if (schedulePath == null || scheduleName == null) {
                return;
            }

            if (SchedulePreference.contains(this, scheduleName)) {
                return;
            }

            if (BuildConfig.DEBUG) {
                Log.d(TAG, "onHandleIntent: loading: " + scheduleName);
            }

            // уведомляет пользователя, что сейчас делаем
            mNotificationBuilder.setContentTitle(scheduleName)
                    .setContentTitle(scheduleName)
                    .setWhen(System.currentTimeMillis());

            NotificationUtils.notifyCommon(this, mNotificationManager,
                    SERVICE_NOTIFICATION_ID, mNotificationBuilder.build());

            // загрузка (чтение)
            String resultPath = SchedulePreference.createPath(this, scheduleName);
            try (FileWriterWithEncoding fileWriter = new FileWriterWithEncoding(resultPath, StandardCharsets.UTF_8)) {

                AssetManager manager = getAssets();
                Scanner scanner = new Scanner(manager.open(schedulePath));

                while (scanner.hasNextLine()) {
                    fileWriter.write(scanner.nextLine());
                }

                SchedulePreference.add(this, scheduleName);

                // уведомляем о загруженном расписании
                Intent msgIntent = new Intent(SCHEDULE_DOWNLOADED_EVENT);
                msgIntent.putExtra(ARG_SCHEDULE_DOWNLOADED, scheduleName);
                LocalBroadcastManager.getInstance(this).sendBroadcast(msgIntent);

            } catch (IOException e) {
                // TODO: 30/01/20 обработать ошибку о невозможности загрузить расписание
                e.printStackTrace();
            }

            // окончательное уведомление
            NotificationCompat.Builder builder = NotificationUtils.createCommonNotification(this)
                    .setContentText(getString(R.string.notification_schedule_downloaded))
                    .setContentTitle(scheduleName)
                    .setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0, new Intent(), 0))
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_notification_file_download)
                    .setAutoCancel(true);

            NotificationUtils.notifyCommon(this, mNotificationManager,
                    notificationId, builder.build());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // убираем уведомления сервера
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true);
        } else {
            mNotificationManager.cancel(SERVICE_NOTIFICATION_ID);
        }
    }
}
