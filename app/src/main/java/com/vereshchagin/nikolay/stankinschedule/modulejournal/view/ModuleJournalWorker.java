package com.vereshchagin.nikolay.stankinschedule.modulejournal.view;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.util.Pair;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.vereshchagin.nikolay.stankinschedule.R;
import com.vereshchagin.nikolay.stankinschedule.modulejournal.network.ModuleJournalService;
import com.vereshchagin.nikolay.stankinschedule.modulejournal.network.response.MarkResponse;
import com.vereshchagin.nikolay.stankinschedule.modulejournal.network.response.SemestersResponse;
import com.vereshchagin.nikolay.stankinschedule.modulejournal.view.model.Discipline;
import com.vereshchagin.nikolay.stankinschedule.modulejournal.view.model.MarkType;
import com.vereshchagin.nikolay.stankinschedule.modulejournal.view.model.SemesterMarks;
import com.vereshchagin.nikolay.stankinschedule.modulejournal.view.model.StudentData;
import com.vereshchagin.nikolay.stankinschedule.settings.ModuleJournalPreference;
import com.vereshchagin.nikolay.stankinschedule.utils.NotificationUtils;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

/**
 * Worker для просмотра обновлений в модульном журнале.
 */
public class ModuleJournalWorker extends Worker {

    public static final String WORK_TAG = "ModuleJournalWorker";

    private static final String TAG = "ModuleJournalWorkerLog";

    private static int NOTIFICATION_ID = 1;

    public ModuleJournalWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        // получение данных для входа
        String login;
        String password;

        try {
            Pair<String, String> authorization = ModuleJournalPreference.loadSignData(getApplicationContext());
            login = authorization.first == null ? "" : authorization.first;
            password = authorization.second == null ? "" : authorization.second;

        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            return Result.failure();
        }

        File cacheDirectory = getApplicationContext().getCacheDir();
        String semester = null;

        // просмотр семестров
        try {
            Response<SemestersResponse> response = ModuleJournalService.getInstance()
                    .api2()
                    .getSemesters(login, password)
                    .execute();

            if (response.isSuccessful() && response.body() != null) {
                StudentData oldData = StudentData.loadCacheData(cacheDirectory);
                StudentData newData = StudentData.fromResponse(response.body());

                if (oldData != null) {
                    String changes =compareStudentData(newData, oldData);
                    if (!changes.isEmpty()) {
                        Notification notification = NotificationUtils
                                .createModuleJournalNotification(getApplicationContext())
                                .setContentTitle(getString(R.string.notification_mj))
                                .setContentText(changes)
                                .setWhen(System.currentTimeMillis())
                                .setSmallIcon(R.drawable.ic_nav_module_journal)
                                .build();

                        NotificationUtils.notifyModuleJournal(getApplicationContext(),
                                getApplicationContext().getSystemService(NotificationManager.class),
                                NOTIFICATION_ID++, notification);
                    }
                }

                semester = newData.semesters().get(newData.semesters().size() - 1);
                StudentData.saveCacheData(newData, cacheDirectory);
            }

        } catch (IOException e) {
            return Result.failure();
        }

        if (semester == null) {
            return Result.retry();
        }

        // просмотр оценок
        try {
            Response<List<MarkResponse>> response = ModuleJournalService.getInstance()
                    .api2()
                    .getMarks(login, password, semester)
                    .execute();

            if (response.isSuccessful() && response.body() != null) {
                SemesterMarks oldMarks = SemesterMarks.loadCacheData(semester, cacheDirectory);
                SemesterMarks newMarks = SemesterMarks.fromResponse(response.body());

                if (oldMarks != null) {
                    String changes = compareSemesterMarks(newMarks, oldMarks);
                    if (!changes.isEmpty()) {
                        changes = getString(R.string.notification_mj_new_marks) + "\n" + changes;

                        Notification notification = NotificationUtils
                                .createModuleJournalNotification(getApplicationContext())
                                .setContentTitle(getString(R.string.notification_mj))
                                .setContentText(changes)
                                .setWhen(System.currentTimeMillis())
                                .setSmallIcon(R.drawable.ic_nav_module_journal)
                                .build();

                        NotificationUtils.notifyModuleJournal(getApplicationContext(),
                                getApplicationContext().getSystemService(NotificationManager.class),
                                NOTIFICATION_ID++, notification);
                    }
                }

                SemesterMarks.saveCacheData(newMarks, semester, cacheDirectory);
            }

        } catch (IOException e) {
            return Result.failure();
        }

        return Result.success();
    }

    /**
     * Сравнивает текущие данные студента с предыдущими.
     * @param newData новый данные.
     * @param oldData старые данные.
     * @return список изменений.
     */
    @NonNull
    private String compareStudentData(@NonNull StudentData newData, @NonNull StudentData oldData) {
        List<String> changes = new ArrayList<>();

        for (String newSemester : newData.semesters()) {
            if (!oldData.semesters().contains(newSemester)) {
                changes.add(getString(R.string.notification_mj_new_semester, newSemester));
            }
        }

        return TextUtils.join("\n", changes);
    }

    /**
     * Сравнивает текущие оценки студента с полученными ранее.
     * @param newMarks новый оценки.
     * @param oldMarks старые оценки.
     * @return список изменений.
     */
    @NonNull
    private String compareSemesterMarks(@NonNull SemesterMarks newMarks, @NonNull SemesterMarks oldMarks) {
        List<String> changes = new ArrayList<>();

        for (Discipline newDiscipline : newMarks.disciplines()) {
            for (Discipline oldDiscipline : oldMarks.disciplines()) {
                if (newDiscipline.discipline().equals(oldDiscipline.discipline())) {
                    for (MarkType type : MarkType.values()) {
                        Integer newMark = newDiscipline.mark(type);
                        Integer oldMark = oldDiscipline.mark(type);

                        if (newMark != null && oldMark != null) {
                            if (!newMark.equals(oldMark)) {
                                changes.add(MessageFormat.format("{0}: {1}({2})",
                                        newDiscipline.discipline(), newMark, type.toString()));
                            }
                        }
                    }
                    break;
                }
            }
        }

        return TextUtils.join("\n", changes);
    }

    @NonNull
    private String getString(@StringRes int id) {
        return getApplicationContext().getString(id);
    }

    @NonNull
    private String getString(@StringRes int id, @NonNull Object... formatArgs) {
        return getApplicationContext().getString(id, formatArgs);
    }
}
