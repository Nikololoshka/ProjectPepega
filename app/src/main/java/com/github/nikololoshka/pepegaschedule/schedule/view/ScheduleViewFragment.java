package com.github.nikololoshka.pepegaschedule.schedule.view;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nikololoshka.pepegaschedule.R;
import com.github.nikololoshka.pepegaschedule.schedule.editor.PairEditorActivity;
import com.github.nikololoshka.pepegaschedule.schedule.editor.ScheduleEditorActivity;
import com.github.nikololoshka.pepegaschedule.schedule.pair.Pair;
import com.github.nikololoshka.pepegaschedule.settings.ApplicationPreference;
import com.github.nikololoshka.pepegaschedule.settings.SchedulePreference;
import com.github.nikololoshka.pepegaschedule.utils.StatefulLayout;
import com.github.nikololoshka.pepegaschedule.utils.StickHeaderItemDecoration;
import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Objects;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.github.nikololoshka.pepegaschedule.schedule.editor.PairEditorActivity.EXTRA_PAIR;
import static com.github.nikololoshka.pepegaschedule.schedule.editor.PairEditorActivity.RESULT_PAIR_ADD;
import static com.github.nikololoshka.pepegaschedule.schedule.editor.PairEditorActivity.RESULT_PAIR_EDIT;
import static com.github.nikololoshka.pepegaschedule.schedule.editor.PairEditorActivity.RESULT_PAIR_REMOVE;
import static com.github.nikololoshka.pepegaschedule.schedule.editor.ScheduleEditorActivity.EXTRA_SCHEDULE_NAME;


/**
 * Фрагмент просмотра расписания.
 */
public class ScheduleViewFragment extends Fragment
        implements OnPairCardCallback, LoaderManager.LoaderCallbacks<ScheduleViewLoader.ScheduleDataView> {

    public static final String ARG_SCHEDULE_PATH = "schedule_path";
    public static final String ARG_SCHEDULE_NAME = "schedule_name";
    public static final String ARG_SCHEDULE_DAY = "schedule_day";

    private static final int SCHEDULE_VIEW_LOADER = 0;

    private static final int REQUEST_SCHEDULE_NAME = 0;
    private static final int REQUEST_PAIR = 1;
    private static final int REQUEST_SAVE_SCHEDULE = 2;

    private static final int REQUEST_PERMISSION_WRITE_STORAGE = 3;

    private static final String ARG_PAIR_CACHE = "pair_cache";
    private static final String ARG_CURRENT_POSITION = "current_position";
    private static final String ARG_LOADING_SCHEDULE = "loading_schedule";

    /**
     * Редактируемая пара.
     */
    private Pair mPairCache;
    /**
     * Информация, загруженная загрузчиком.
     */
    // TODO: попытаться убрать (переделать)
    private ScheduleViewLoader.ScheduleDataView mScheduleDataView;
    /**
     * Загрузчик расписания.
     */
    private ScheduleViewLoader mViewLoader;

    @Nullable
    private String mScheduleName;
    @Nullable
    private String mSchedulePath;

    private StatefulLayout mStatefulLayout;

    private RecyclerView mRecyclerSchedule;
    private ScheduleViewAdapter mScheduleAdapter;

    private int mCurrentViewPosition = RecyclerView.NO_POSITION;
    private int mTodayPosition = RecyclerView.NO_POSITION;

    private long mRequestTimeDay= System.currentTimeMillis();

    private boolean mIsLoading = true;
    private boolean mIsHorizontalView = false;

    /**
     * Конструктор фрагмента.
     */
    public ScheduleViewFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mSchedulePath = savedInstanceState.getString(ARG_SCHEDULE_PATH);
            mScheduleName = savedInstanceState.getString(ARG_SCHEDULE_NAME);

            mPairCache = savedInstanceState.getParcelable(ARG_PAIR_CACHE);
            mCurrentViewPosition = savedInstanceState.getInt(ARG_CURRENT_POSITION, RecyclerView.NO_POSITION);

            mIsLoading = savedInstanceState.getBoolean(ARG_LOADING_SCHEDULE, true);

        } else if (getArguments() != null) {
            // если создаемся первый раз
            mScheduleName = getArguments().getString(ARG_SCHEDULE_NAME);
            mSchedulePath = getArguments().getString(ARG_SCHEDULE_PATH);
            mRequestTimeDay = getArguments().getLong(ARG_SCHEDULE_DAY, System.currentTimeMillis());

            if (mSchedulePath == null && getContext() != null) {
                mSchedulePath = SchedulePreference.createPath(getContext(), mScheduleName);
            }
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_view, container, false);

        mStatefulLayout = view.findViewById(R.id.stateful_layout);
        mStatefulLayout.addXMLViews();
        mStatefulLayout.setLoadState();

        mRecyclerSchedule = view.findViewById(R.id.recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mRecyclerSchedule.setLayoutManager(manager);

        // отображать горизонтально или вертикально расписание
        if (getContext() != null) {
            String method = ApplicationPreference.scheduleViewMethod(getContext());
            mIsHorizontalView = method.equals(ApplicationPreference.SCHEDULE_VIEW_HORIZONTAL);
        }

        if (mIsHorizontalView) {
            mScheduleAdapter = new ScheduleHorizontalAdapter(this);

            manager.setOrientation(RecyclerView.HORIZONTAL);
            LinearSnapHelper snapHelper = new LinearSnapHelper();
            snapHelper.attachToRecyclerView(mRecyclerSchedule);

        } else {
            mScheduleAdapter = new ScheduleVerticalAdapter(this);

            // "липкий" заголовок
            mRecyclerSchedule.addItemDecoration(new StickHeaderItemDecoration(
                    (StickHeaderItemDecoration.StickyHeaderInterface) mScheduleAdapter));
            // разделитель между днями
            mRecyclerSchedule.addItemDecoration(new ScheduleViewSpaceItemDecoration(
                    getResources().getDimensionPixelSize(R.dimen.vertical_view_space)));
        }

        mRecyclerSchedule.setAdapter(mScheduleAdapter);

        // listener на отображение кнопки возврата к текущему дню.
        mRecyclerSchedule.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                LinearLayoutManager layout = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (layout == null) {
                    return;
                }

                mCurrentViewPosition = mScheduleAdapter
                        .unTranslateIndex(layout.findFirstCompletelyVisibleItemPosition());
            }
            }
        });

        // загружаем расписание
        mViewLoader = (ScheduleViewLoader) LoaderManager.getInstance(this)
                .initLoader(SCHEDULE_VIEW_LOADER, null, this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        updateActionBar();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_schedule_view, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (mIsLoading || mScheduleDataView.hasErrors) {
            return super.onOptionsItemSelected(item);
        }

        switch (item.getItemId()) {
            // если нужно переименновать расписание
            case R.id.rename_schedule: {
                Intent intent = new Intent(getActivity(), ScheduleEditorActivity.class);
                intent.putExtra(EXTRA_SCHEDULE_NAME, mScheduleName);
                startActivityForResult(intent, REQUEST_SCHEDULE_NAME);
                return true;
            }
            // к текущему дню
            case R.id.show_current_day: {
                mScheduleAdapter.scrollTo(mRecyclerSchedule, mTodayPosition, true);
                return true;
            }
            case R.id.go_to_day: {
                if (mCurrentViewPosition == RecyclerView.NO_POSITION) {
                    return true;
                }

                Calendar date = Calendar.getInstance();
                date.setTimeInMillis(mScheduleDataView.dayDates.get(mCurrentViewPosition));
                int year = date.get(Calendar.YEAR);
                int month = date.get(Calendar.MONTH);
                int dayOfMonth = date.get(Calendar.DAY_OF_MONTH);

                if (getContext() == null) {
                    return true;
                }

                new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar date = new GregorianCalendar(year, month, dayOfMonth);
                        mRequestTimeDay = date.getTimeInMillis();
                        mScheduleDataView = null;
                        reloadSchedule(ScheduleViewLoader.REQUEST_LOAD_SCHEDULE);
                    }
                }, year, month, dayOfMonth).show();

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
                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                alertDialog.setTitle(R.string.warning);
                alertDialog.setMessage(getString(R.string.schedule_will_be_deleted));
                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                        getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,
                        getString(R.string.yes_continue),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeSchedule();
                                if (getActivity() != null) {
                                    getActivity().onBackPressed();
                                }
                            }
                        });
                alertDialog.show();
                return true;
            }
            // если добавить пару
            case R.id.add_pair: {

                Intent intent = new Intent(getContext(), PairEditorActivity.class);
                intent.putExtra(PairEditorActivity.EXTRA_SCHEDULE, mScheduleDataView.schedule);
                startActivityForResult(intent, REQUEST_PAIR);
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
        outState.putParcelable(ARG_PAIR_CACHE, mPairCache);
        outState.putInt(ARG_CURRENT_POSITION, mCurrentViewPosition);
        outState.putBoolean(ARG_LOADING_SCHEDULE, mIsLoading);
    }

    /**
     * Если было нажатие на пару.
     * @param pair - нажатая пара.
     */
    @Override
    public void onPairCardClicked(@Nullable Pair pair) {
        mPairCache = pair;
        Intent intent = new Intent(getContext(), PairEditorActivity.class);
        intent.putExtra(PairEditorActivity.EXTRA_SCHEDULE, mScheduleDataView.schedule);
        intent.putExtra(PairEditorActivity.EXTRA_PAIR, pair);
        startActivityForResult(intent, REQUEST_PAIR);
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
        if (resultCode == RESULT_CANCELED) {
            return;
        }

        switch (requestCode) {
            // запрос связанный с парой
            case REQUEST_PAIR: {
                if (data == null) {
                    return;
                }

                Pair pair = data.getParcelableExtra(EXTRA_PAIR);

                switch (resultCode) {
                    // если требовалось добавить пару
                    case RESULT_PAIR_ADD:
                        mScheduleDataView.schedule.addPair(pair);
                        break;
                    // если требовалось отредактировать пару
                    case RESULT_PAIR_EDIT:
                        mScheduleDataView.schedule.removePair(mPairCache);
                        mScheduleDataView.schedule.addPair(pair);
                        break;
                    // если требовалось удалить пару
                    case RESULT_PAIR_REMOVE:
                        mScheduleDataView.schedule.removePair(pair);
                        break;
                    default:
                        return;
                }

                SchedulePreference.addChange();
                reloadSchedule(ScheduleViewLoader.REQUEST_SAVE_SCHEDULE);

                break;
            }
            // запрос на изменение имени
            case REQUEST_SCHEDULE_NAME: {
                if (resultCode != RESULT_OK || data == null || mScheduleName == null) {
                    return;
                }

                String newScheduleName = data.getStringExtra(EXTRA_SCHEDULE_NAME);

                File oldFile;
                File newFile;

                if (getContext() != null) {
                    oldFile = new File(SchedulePreference.createPath(getContext(), mScheduleName));
                    newFile = new File(SchedulePreference.createPath(getContext(), newScheduleName));
                } else {
                    return;
                }


                try {
                    // пытаемся переименновать расписание
                    FileUtils.moveFile(oldFile, newFile);

                } catch (IOException e) {
                    e.printStackTrace();
                    showMessage(getString(R.string.unable_schedule_rename));
                    return;
                }

                // если удалось переименновать расписание
                SchedulePreference.remove(getContext(), mScheduleName);
                SchedulePreference.add(getContext(), newScheduleName);

                if (mScheduleName.equals(SchedulePreference.favorite(getContext()))) {
                    SchedulePreference.setFavorite(getContext(), newScheduleName);
                }

                // новые значения расписания
                mScheduleName = newScheduleName;
                mSchedulePath = newFile.getAbsolutePath();

                showMessage(getString(R.string.schedule_renamed));
                reloadSchedule(ScheduleViewLoader.REQUEST_LOAD_SCHEDULE);
                break;
            }
            // запрос на сохранения расписания
            case REQUEST_SAVE_SCHEDULE: {
                if (resultCode != RESULT_OK || data == null) {
                    return;
                }

                // uri папки пути сохранения
                Uri uriFolder = data.getData();
                if (uriFolder == null) {
                    return;
                }

                try {
                    if (getContext() != null) {

                        // получаем объект файла по пути
                        DocumentFile documentFile = DocumentFile.fromTreeUri(getContext(), uriFolder);
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
                        ContentResolver resolver = getContext().getContentResolver();
                        OutputStream stream = resolver.openOutputStream(uriFile);

                        mScheduleDataView.schedule.save(stream);

                        showMessage(getString(R.string.schedule_saved));
                    }

                } catch (FileNotFoundException | JSONException e) {
                    e.printStackTrace();
                }

                break;
            }
        }
    }

    /**
     * Удаляет текущие расписание.
     */
    private void removeSchedule() {
        if (getContext() == null) {
            return;
        }

        if (mSchedulePath != null) {
            try {
                FileUtils.forceDelete(new File(mSchedulePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        SchedulePreference.remove(getContext(), mScheduleName);
        showMessage(getString(R.string.schedule_removed));
    }

    /**
     * Вызывает службу для выбора места сохранения файла.
     */
    private void saveSchedule() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_folder)),
                REQUEST_SAVE_SCHEDULE);
    }

    /**
     * Перезагружает расписание.
     */
    private void reloadSchedule(int request) {
        mStatefulLayout.setLoadState();

        mIsLoading = true;

        long showingPosition = mRequestTimeDay;
        if (mScheduleDataView != null && !mScheduleDataView.dayDates.isEmpty()) {
            showingPosition = mScheduleDataView.dayDates.get(mCurrentViewPosition);
        }

        mViewLoader.reloadData(mSchedulePath,
                mScheduleDataView != null ? mScheduleDataView.schedule : null,
                request,
                showingPosition);
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
                    bar.setTitle(R.string.schedule_view);
                }
            }
        }
    }

    /**
     * Показывает информационное сообщение на экран пользователю.
     * @param message - сообщение.
     */
    private void showMessage(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }


    @NonNull
    @Override
    public Loader<ScheduleViewLoader.ScheduleDataView> onCreateLoader(int id, @Nullable Bundle args) {
        mViewLoader = new ScheduleViewLoader(Objects.requireNonNull(getActivity()));
        reloadSchedule(ScheduleViewLoader.REQUEST_LOAD_SCHEDULE);
        return mViewLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ScheduleViewLoader.ScheduleDataView> loader,
                               ScheduleViewLoader.ScheduleDataView data) {
        if (data == null || data.schedule == null || data.hasErrors) {
            showMessage(getString(R.string.error_loading_schedule));
            return;
        }

        mScheduleDataView = data;

        if (mIsLoading || mCurrentViewPosition == RecyclerView.NO_POSITION) {
            mCurrentViewPosition = data.currentShowPosition;
        }
        mTodayPosition = data.todayPosition;

        mScheduleAdapter.update(data.dayPairs, data.dayTitles);
        mScheduleAdapter.scrollTo(mRecyclerSchedule, mCurrentViewPosition, false);

        mStatefulLayout.setState(R.id.recycler_view);
        updateActionBar();

        mIsLoading = false;
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ScheduleViewLoader.ScheduleDataView> loader) {
    }
}
