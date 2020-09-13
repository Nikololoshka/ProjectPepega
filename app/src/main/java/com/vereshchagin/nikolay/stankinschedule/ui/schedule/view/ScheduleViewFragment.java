package com.vereshchagin.nikolay.stankinschedule.ui.schedule.view;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.vereshchagin.nikolay.stankinschedule.R;
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Pair;
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepository;
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.name.ScheduleNameEditorDialog;
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.pair.PairEditorActivity;
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.view.paging.ScheduleDayItem;
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.view.paging.ScheduleDayItemAdapter;
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.view.paging.ScheduleViewSpaceItemDecoration;
import com.vereshchagin.nikolay.stankinschedule.ui.settings.ApplicationPreference;
import com.vereshchagin.nikolay.stankinschedule.ui.settings.SchedulePreference;
import com.vereshchagin.nikolay.stankinschedule.utils.StatefulLayout;
import com.vereshchagin.nikolay.stankinschedule.utils.StorageErrorData;

import org.apache.commons.io.FileUtils;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.chrono.ISOChronology;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

import static android.app.Activity.RESULT_CANCELED;

/**
 * Фрагмент просмотра расписания.
 */
public class ScheduleViewFragment extends Fragment
        implements ScheduleDayItemAdapter.OnPairCardListener {

    private static final String TAG = "ScheduleViewFragmentLog";

    private static final String ARG_SCHEDULE_PATH = "schedule_path";
    private static final String ARG_SCHEDULE_NAME = "schedule_name";
    private static final String ARG_SCHEDULE_DAY = "schedule_day";

    private static final int REQUEST_SCHEDULE_NAME = 0;
    private static final int REQUEST_PAIR = 1;
    private static final int REQUEST_SAVE_SCHEDULE = 2;

    private static final int REQUEST_PERMISSION_WRITE_STORAGE = 3;

    private static final String PAIR_CACHE = "pair_cache";
    private static final String SCROLL_DATE = "scroll_date";

    /**
     * Редактируемая пара.
     */
    @Nullable
    private Pair mPairCache;
    /**
     * Название расписания.
     */
    private String mScheduleName;
    /**
     * Путь к расписанию.
     */
    private String mSchedulePath;
    /**
     * Горизонтальный ли режим отображения.
     */
    private boolean mIsHorizontalMode;
    /**
     * Дата, которую необходимо показать.
     */
    private LocalDate mScrollDate;

    private StatefulLayout mStatefulLayout;

    private TextView mErrorTitle;
    private TextView mErrorDescription;

    private RecyclerView mRecyclerSchedule;
    private ScheduleDayItemAdapter mScheduleDayItemAdapter;
    private ScheduleViewModel mScheduleViewModel;

    public ScheduleViewFragment() {
        super();

        mIsHorizontalMode = true;
    }

    /**
     * Создает bundle с данными, требуемыми для фрагмента.
     * @param name название расписания.
     * @param path путь к расписанию.
     * @return bundle с данными.
     */
    public static Bundle createBundle(@NonNull String name, @Nullable String path) {
        return createBundle(name, path, null);
    }

    /**
     * Создает bundle с данными, требуемыми для фрагмента.
     * @param name название расписания.
     * @param path путь к расписанию.
     * @param date дата, которую необходимо показать.
     * @return bundle с данными.
     */
    public static Bundle createBundle(@NonNull String name, @Nullable String path, @Nullable Calendar date) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_SCHEDULE_NAME, name);
        bundle.putString(ARG_SCHEDULE_PATH, path);
        bundle.putSerializable(ARG_SCHEDULE_DAY, date);
        return bundle;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            // если было предыдущие состояние
            mSchedulePath = savedInstanceState.getString(ARG_SCHEDULE_PATH);
            mScheduleName = savedInstanceState.getString(ARG_SCHEDULE_NAME);
            mPairCache = savedInstanceState.getParcelable(PAIR_CACHE);
            mScrollDate = (LocalDate) savedInstanceState.getSerializable(SCROLL_DATE);

        } else if (getArguments() != null) {
            // если создаемся первый раз
            mScheduleName = getArguments().getString(ARG_SCHEDULE_NAME);
            mSchedulePath = getArguments().getString(ARG_SCHEDULE_PATH);

            Context context = getContext();
            if (mSchedulePath == null && context != null) {
                mSchedulePath = SchedulePreference.createPath(context, mScheduleName);
            }
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_view, container, false);

        mStatefulLayout = view.findViewById(R.id.stateful_layout);
        mStatefulLayout.addXMLViews();
        mStatefulLayout.setLoadState();

        mErrorTitle = view.findViewById(R.id.error_title);
        mErrorDescription = view.findViewById(R.id.error_description);

        mRecyclerSchedule = view.findViewById(R.id.sch_view_container);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mRecyclerSchedule.setLayoutManager(manager);

        // отображать горизонтально или вертикально расписание
        Context context = getContext();
        if (context != null) {
            String method = ApplicationPreference.scheduleViewMethod(context);
            mIsHorizontalMode = method.equals(ApplicationPreference.SCHEDULE_VIEW_HORIZONTAL);
        }

        if (mIsHorizontalMode) {
            // если горизонтальное отображение
            manager.setOrientation(RecyclerView.HORIZONTAL);
            LinearSnapHelper snapHelper = new LinearSnapHelper();
            snapHelper.attachToRecyclerView(mRecyclerSchedule);

        } else {
            // если вертикальное отображение
            mRecyclerSchedule.addItemDecoration(new ScheduleViewSpaceItemDecoration(
                    getResources().getDimensionPixelSize(R.dimen.vertical_view_space)));
        }

        mScheduleDayItemAdapter = new ScheduleDayItemAdapter(this);
        mRecyclerSchedule.setAdapter(mScheduleDayItemAdapter);

        // установка ViewModel
        mScheduleViewModel = new ViewModelProvider(this,
                new ScheduleViewModel.Factory(mSchedulePath)).get(ScheduleViewModel.class);

        // установка отображаемого дня, если был указан
        if (savedInstanceState == null && getArguments() != null) {
            Calendar date = (Calendar) getArguments().getSerializable(ARG_SCHEDULE_DAY);
            mScheduleViewModel.storage().setInitialKey(new LocalDate(date, ISOChronology.getInstanceUTC()));
        }

        mScheduleViewModel.statesData().observe(getViewLifecycleOwner(), new Observer<ScheduleViewModel.States>() {
            @Override
            public void onChanged(ScheduleViewModel.States state) {
                stateChanged(state);
            }
        });

        //                scheduleDayItems.addWeakCallback(scheduleDayItems.snapshot(), new PagedList.Callback() {
        //                    @Override
        //                    public void onChanged(int position, int count) {
        //                    }
        //
        //                    @Override
        //                    public void onInserted(int position, int count) {
        //                        if (BuildConfig.DEBUG) {
        //                            Log.d(TAG, "onInserted: " + position + ", count: " + count);
        //                        }
        //
        //                        if (mScrollDate != null) {
        //                            ScheduleDayItem itemFirst = scheduleDayItems.get(0);
        //                            if (itemFirst != null && itemFirst.day().equals(mScrollDate)) {
        //                                mRecyclerSchedule.scrollToPosition(0);
        //                            } else {
        //                                ScheduleDayItem itemSecond = scheduleDayItems.get(count);
        //                                if (itemSecond != null && itemSecond.day().equals(mScrollDate)) {
        //                                    mRecyclerSchedule.scrollToPosition(count);
        //                                } else {
        //                                    if (position == 0) {
        //                                        mRecyclerSchedule.scrollToPosition(count);
        //                                    } else {
        //                                        mRecyclerSchedule.scrollToPosition(0);
        //                                    }
        //                                }
        //                            }
        //                            mScrollDate = null;
        //                        }
        //                    }
        //
        //                    @Override
        //                    public void onRemoved(int position, int count) {
        //
        //                    }
        //                });
        //                final ScheduleDayItem item;
        //                if (mScrollDate != null) {
        //                    item = scheduleDayItems.get(0);
        //                    mScrollDate = null;
        //                } else {
        //                    item = null;
        //                }
        //                mScheduleDayItemAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
        //                    @Override
        //                    public void onChanged() {
        //                        super.onChanged();
        //                        Log.d(TAG, "onChangedAdapter: ");
        //                    }
        //                });
        //                        if (item != null) {
        //                            mRecyclerSchedule.postDelayed(new Runnable() {
        //                                @Override
        //                                public void run() {
        //                                    int pos = scheduleDayItems.indexOf(item);
        //                                    if (pos != -1) {
        //                                        mRecyclerSchedule.scrollToPosition(pos);
        //                                    }
        //
        //                                    if (BuildConfig.DEBUG) {
        //                                        Log.d(TAG, "run: " + pos);
        //                                    }
        //
        //                                    stateChanged(ScheduleViewModel.States.SUCCESS);
        //                                }
        //                            }, 400);
        //                        } else {
        //                        }

        mScheduleViewModel.scheduleData().observe(getViewLifecycleOwner(), new Observer<PagedList<ScheduleDayItem>>() {
            @Override
            public void onChanged(final PagedList<ScheduleDayItem> scheduleDayItems) {
                if (scheduleDayItems.isEmpty()) {
                    stateChanged(ScheduleViewModel.States.ZERO_ITEMS);
                    return;
                }

                // TODO: 04/02/20 Баг, когда в paging library вызывается несколько раз один и тот же метод.
                //                После чего в RecyclerView отображается не тот элемент.

                scheduleDayItems.addWeakCallback(scheduleDayItems.snapshot(), new PagedList.Callback() {
                    @Override
                    public void onChanged(int position, int count) {

                    }

                    @Override
                    public void onInserted(int position, int count) {
                        if (mScrollDate != null) {

                            ScheduleDayItem itemFirst = scheduleDayItems.get(0);
                            if (itemFirst != null && itemFirst.day().equals(mScrollDate)) {
                                mRecyclerSchedule.scrollToPosition(0);
                            }

                            ScheduleDayItem itemSecond = scheduleDayItems.get(count);
                            if (itemSecond != null && itemSecond.day().equals(mScrollDate)) {
                                mRecyclerSchedule.scrollToPosition(count);
                            }

                            mScrollDate = null;
                            scheduleDayItems.removeWeakCallback(this);
                        }
                    }

                    @Override
                    public void onRemoved(int position, int count) {

                    }
                });

//                scheduleDayItems.addWeakCallback(scheduleDayItems.snapshot(), new PagedList.Callback() {
//                    @Override
//                    public void onChanged(int position, int count) {
//                    }
//
//                    @Override
//                    public void onInserted(int position, int count) {
//                        if (BuildConfig.DEBUG) {
//                            Log.d(TAG, "onInserted: " + position + ", count: " + count);
//                        }
//
//                        if (mScrollDate != null) {
//                            ScheduleDayItem itemFirst = scheduleDayItems.get(0);
//                            if (itemFirst != null && itemFirst.day().equals(mScrollDate)) {
//                                mRecyclerSchedule.scrollToPosition(0);
//                            } else {
//                                ScheduleDayItem itemSecond = scheduleDayItems.get(count);
//                                if (itemSecond != null && itemSecond.day().equals(mScrollDate)) {
//                                    mRecyclerSchedule.scrollToPosition(count);
//                                } else {
//                                    if (position == 0) {
//                                        mRecyclerSchedule.scrollToPosition(count);
//                                    } else {
//                                        mRecyclerSchedule.scrollToPosition(0);
//                                    }
//                                }
//                            }
//                            mScrollDate = null;
//                        }
//                    }
//
//                    @Override
//                    public void onRemoved(int position, int count) {
//
//                    }
//                });

//                final ScheduleDayItem item;
//                if (mScrollDate != null) {
//                    item = scheduleDayItems.get(0);
//                    mScrollDate = null;
//                } else {
//                    item = null;
//                }

//                mScheduleDayItemAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
//                    @Override
//                    public void onChanged() {
//                        super.onChanged();
//                        Log.d(TAG, "onChangedAdapter: ");
//                    }
//                });

                mScheduleDayItemAdapter.submitList(scheduleDayItems, new Runnable() {
                    @Override
                    public void run() {

//                        if (item != null) {
//                            mRecyclerSchedule.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    int pos = scheduleDayItems.indexOf(item);
//                                    if (pos != -1) {
//                                        mRecyclerSchedule.scrollToPosition(pos);
//                                    }
//
//                                    if (BuildConfig.DEBUG) {
//                                        Log.d(TAG, "run: " + pos);
//                                    }
//
//                                    stateChanged(ScheduleViewModel.States.SUCCESS);
//                                }
//                            }, 400);
//                        } else {

//                        }

                        stateChanged(ScheduleViewModel.States.SUCCESS);
//                        Log.d(TAG, "run: submitList");
                    }
                });
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateActionBar();
    }

    @Override
    public void onResume() {
        super.onResume();

        Context context = getContext();
        if (context != null) {
            boolean limit = ApplicationPreference.scheduleLimit(context);
            boolean changed = mScheduleViewModel.storage().setLimit(limit);
            if (changed) {
                reloadSchedule();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_schedule_view, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            // если нужно переименовать расписание
            case R.id.rename_schedule: {
                ScheduleNameEditorDialog dialog = ScheduleNameEditorDialog.newInstance(mScheduleName);
                dialog.setTargetFragment(this, REQUEST_SCHEDULE_NAME);
                dialog.show(getParentFragmentManager(), dialog.getTag());
                return true;
            }
            // к выбранному дню
            case R.id.go_to_day: {
                Context context = getContext();
                if (context != null) {

                    LocalDate initialDate = null;

                    // текущий отображаемый день
                    int pos = currentPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        PagedList<ScheduleDayItem> items = mScheduleDayItemAdapter.getCurrentList();
                        if (items != null) {
                            ScheduleDayItem dayItem = items.get(pos);
                            if (dayItem != null) {
                                initialDate = dayItem.day();
                            }
                        }
                    }

                    // если не получилось то текущая дата.
                    if (initialDate == null) {
                        initialDate = LocalDate.now();
                    }

                    int year = initialDate.getYear();
                    int month = initialDate.getMonthOfYear() - 1;
                    int dayOfMonth = initialDate.getDayOfMonth();

                    // picker даты
                    DatePickerDialog dialog = new DatePickerDialog(context,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    scrollScheduleTo(new LocalDate(year, month + 1, dayOfMonth));
                                }
                    }, year, month, dayOfMonth);
                    dialog.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.sch_view_today),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    scrollScheduleTo(LocalDate.now());
                                }
                            });
                    dialog.show();
                }
                return true;
            }
            // если нужно сохранить расписание
            case R.id.save_schedule: {
                if (getContext() != null) {
                    // проверка возможности записи
                    int permissionStatus = ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                        saveSchedule();
                    } else {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_PERMISSION_WRITE_STORAGE);
                    }
                }
                return true;
            }
            // если удалить расписание
            case R.id.remove_schedule: {
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.warning)
                        .setMessage(getString(R.string.sch_view_will_be_deleted))
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton(getString(R.string.yes_continue), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeSchedule();
                                Activity activity = getActivity();
                                if (activity != null) {
                                    activity.onBackPressed();
                                }
                            }
                        }).show();

                return true;
            }
            // если добавить пару
            case R.id.add_pair: {
                Context context = getContext();
                if (context != null) {
                    Intent intent = PairEditorActivity.Companion.newPairIntent(context, mScheduleName);
                    startActivityForResult(intent, REQUEST_PAIR);
                }
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_SCHEDULE_NAME, mScheduleName);
        outState.putString(ARG_SCHEDULE_PATH, mSchedulePath);
        outState.putParcelable(PAIR_CACHE, mPairCache);
        outState.putSerializable(SCROLL_DATE, mScrollDate);
    }

    /**
     * Если было нажатие на пару.
     * @param pair - нажатая пара.
     */
    @Override
    public void onPairClicked(@Nullable Pair pair) {
        mPairCache = pair;

        Context context = getContext();
        if (context != null) {
            Intent intent = PairEditorActivity.Companion.editPairIntent(context, mScheduleName, pair);
            startActivityForResult(intent, REQUEST_PAIR);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // удалось ли получить запрос на запись
        if (requestCode == REQUEST_PERMISSION_WRITE_STORAGE) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // если да, сохраняем расписание
                saveSchedule();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED || data == null) {
            return;
        }

        switch (requestCode) {
            // запрос связанный с парой
            case REQUEST_PAIR: {
                SchedulePreference.addChange();
                reloadSchedule();
                break;
            }
            // запрос на изменение имени
            case REQUEST_SCHEDULE_NAME: {
                Context context = getContext();
                if (context == null) {
                    return;
                }

                String newScheduleName = data.getStringExtra(ScheduleNameEditorDialog.SCHEDULE_NAME);
                if (newScheduleName != null) {
                    ScheduleRepository repository = new ScheduleRepository();
                    repository.renameSchedule(context, mScheduleName, newScheduleName);

                    // новые значения расписания
                    mScheduleName = newScheduleName;
                    mSchedulePath = SchedulePreference.createPath(context, newScheduleName);

                    mScheduleViewModel.storage().setSchedulePath(mSchedulePath);
                    reloadSchedule();
                    updateActionBar();

                    showMessage(getString(R.string.sch_view_renamed));
                }
                break;
            }
            // запрос на сохранения расписания
            case REQUEST_SAVE_SCHEDULE: {
                // uri папки пути сохранения
                Uri uriFolder = data.getData();
                if (uriFolder == null) {
                    return;
                }

                try {
                    Context context = getContext();
                    if (context != null) {
                        // TODO: 30/01/20 переделать сохранение

                        // получаем объект файла по пути
                        DocumentFile documentFile = DocumentFile.fromTreeUri(context, uriFolder);
                        if (documentFile == null) {
                            return;
                        }

                        // регистрируем файл
                        documentFile = documentFile.createFile("application/json",
                                mScheduleName + SchedulePreference.fileExtension());
                        if (documentFile == null) {
                            return;
                        }

                        // uri файла сохранения
                        Uri uriFile = documentFile.getUri();

                        // открывает поток для записи
                        ContentResolver resolver = context.getContentResolver();
                        OutputStream stream = resolver.openOutputStream(uriFile);
                        if (stream == null) {
                            return;
                        }

                        FileUtils.copyFile(new File(mSchedulePath), stream);

                        showMessage(getString(R.string.sch_view_saved));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    showMessage("Cannot save schedule!");
                }

                break;
            }
        }
    }

    /**
     * Удаляет текущие расписание.
     */
    private void removeSchedule() {
        Context context = getContext();
        if (context == null) {
            return;
        }

        new ScheduleRepository()
                .removeSchedule(context, mScheduleName);
        showMessage(getString(R.string.sch_removed));
    }

    /**
     * Вызывает службу для выбора места сохранения файла.
     */
    private void saveSchedule() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_folder)), REQUEST_SAVE_SCHEDULE);
    }

    /**
     * Обновляет текст на название расписания в action bar.
     */
    private void updateActionBar() {
        if (getActivity() != null) {
            ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();

            if (bar != null)  {
                if (mScheduleName != null) {
                    bar.setTitle(mScheduleName);
                } else {
                    bar.setTitle(R.string.sch_view);
                }
            }
        }
    }

    /**
     * Показывает информационное сообщение на экран пользователю.
     * @param message - сообщение.
     */
    private void showMessage(@NonNull String message) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @return текущая отображаемая позиция в списке.
     */
    private int currentPosition() {
        RecyclerView.LayoutManager manager = mRecyclerSchedule.getLayoutManager();
        if (manager == null) {
            return RecyclerView.NO_POSITION;
        }

        LinearLayoutManager linearManager = (LinearLayoutManager) manager;
        return linearManager.findFirstCompletelyVisibleItemPosition();
    }

    /**
     * Перезагружает расписание.
     */
    private void reloadSchedule() {
        // отображаемый список
        PagedList<ScheduleDayItem> items = mScheduleDayItemAdapter.getCurrentList();
        if (items == null) {
            return;
        }

        // установка текущего дня
        int pos = currentPosition();
        if (pos != RecyclerView.NO_POSITION) {
            ScheduleDayItem item = items.get(pos);
            if (item != null) {
                mScrollDate = item.day();
                mScheduleViewModel.storage().setInitialKey(item.day());
            }
        }

        stateChanged(ScheduleViewModel.States.LOADING);
        items.getDataSource().invalidate();
    }

    /**
     * Отображает в расписании необходимый день.
     * @param targetDay необходимый день.
     */
    private void scrollScheduleTo(@NonNull LocalDate targetDay) {
        PagedList<ScheduleDayItem> items = mScheduleDayItemAdapter.getCurrentList();
        if (items == null) {
            return;
        }

        int pos = currentPosition();
        if (pos != RecyclerView.NO_POSITION) {

            ScheduleDayItem item = items.get(pos);
            if (item != null) {

                boolean limit = mScheduleViewModel.storage().limit();
                if (limit) {
                    LocalDate first = mScheduleViewModel.storage().firstDate();
                    if (first != null && targetDay.isBefore(first)) {
                        targetDay = first;
                    }

                    LocalDate last = mScheduleViewModel.storage().lastDate();
                    if (last != null && targetDay.isAfter(last)) {
                        targetDay = last;
                    }
                }

                LocalDate currentDay = item.day();
                int dayDiff = Days.daysBetween(targetDay, currentDay).getDays();
                int scrollItemPos = pos + dayDiff;

                if (scrollItemPos >= 0 && scrollItemPos < items.size()) {
                    mRecyclerSchedule.scrollToPosition(scrollItemPos);
                    return;
                }
            }
        }

        stateChanged(ScheduleViewModel.States.LOADING);
        mScrollDate = targetDay;
        mScheduleViewModel.storage().setInitialKey(targetDay);
        items.getDataSource().invalidate();
    }

    private void stateChanged(@NonNull ScheduleViewModel.States state) {
        switch (state) {
            case SUCCESS: {
                mStatefulLayout.setState(R.id.sch_view_container);
                break;
            }
            case ZERO_ITEMS: {
                if (mStatefulLayout.isCurrentState(R.id.sch_view_error)) {
                    return;
                }
                mStatefulLayout.setState(R.id.sch_view_empty);
                break;
            }
            case LOADING: {
                mStatefulLayout.setLoadState();
                break;
            }
            case ERROR: {
                StorageErrorData data = mScheduleViewModel.storage().errorData();
                if (data != null) {
                    data.resolveTitle(mErrorTitle);
                    data.resolveDescription(mErrorDescription);

                    mStatefulLayout.setState(R.id.sch_view_error);
                }
            }
        }
    }
}
