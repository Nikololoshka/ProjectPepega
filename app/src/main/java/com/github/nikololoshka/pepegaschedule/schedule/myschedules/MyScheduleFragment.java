package com.github.nikololoshka.pepegaschedule.schedule.myschedules;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nikololoshka.pepegaschedule.R;
import com.github.nikololoshka.pepegaschedule.schedule.Schedule;
import com.github.nikololoshka.pepegaschedule.schedule.editor.ScheduleEditorActivity;
import com.github.nikololoshka.pepegaschedule.schedule.repository.ScheduleDownloaderService;
import com.github.nikololoshka.pepegaschedule.settings.SchedulePreference;
import com.github.nikololoshka.pepegaschedule.utils.DividerItemDecoration;
import com.github.nikololoshka.pepegaschedule.utils.StatefulLayout;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.github.nikololoshka.pepegaschedule.schedule.editor.ScheduleEditorActivity.EXTRA_SCHEDULE_NAME;
import static com.github.nikololoshka.pepegaschedule.schedule.pair.SubgroupEnum.A;
import static com.github.nikololoshka.pepegaschedule.schedule.pair.SubgroupEnum.B;
import static com.github.nikololoshka.pepegaschedule.schedule.pair.SubgroupEnum.COMMON;
import static com.github.nikololoshka.pepegaschedule.schedule.view.ScheduleViewFragment.ARG_SCHEDULE_NAME;
import static com.github.nikololoshka.pepegaschedule.schedule.view.ScheduleViewFragment.ARG_SCHEDULE_PATH;
import static com.github.nikololoshka.pepegaschedule.settings.SchedulePreference.ROOT_PATH;

/**
 * Фрагмент списка расписаний.
 */
public class MyScheduleFragment extends Fragment
        implements MySchedulesAdapter.OnItemClickListener,
        DragToMoveCallback.OnStartDragListener,
        LoaderManager.LoaderCallbacks<MySchedulesLoader.DataView> {

    private static final int MY_SCHEDULES_LOADER = 0;

    private static final int REQUEST_NEW_SCHEDULE = 0;
    private static final int REQUEST_LOAD_SCHEDULE = 1;

    private static final int REQUEST_PERMISSION_READ_STORAGE = 0;

    private StatefulLayout mStatefulLayout;

    private MySchedulesAdapter mSchedulesAdapter;
    private Loader<MySchedulesLoader.DataView> mMySchedulesLoader;
    private ItemTouchHelper mItemTouchHelper;

    private BroadcastReceiver mScheduleDownloaderReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateSchedules();
        }
    };

    public MyScheduleFragment() {
        super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        mStatefulLayout = view.findViewById(R.id.stateful_layout);
        mStatefulLayout.addXMLViews();
        mStatefulLayout.setLoadState();

        Spinner subgroupSpinner = view.findViewById(R.id.subgroup_selector);
        if (getActivity() != null) {
            switch (SchedulePreference.subgroup(getActivity())) {
                case COMMON:
                    subgroupSpinner.setSelection(0);
                    break;
                case A:
                    subgroupSpinner.setSelection(1);
                    break;
                case B:
                    subgroupSpinner.setSelection(2);
                    break;
            }
        }
        subgroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (getActivity() != null) {
                    switch (position) {
                        case 0:
                            SchedulePreference.setSubgroup(getActivity(), COMMON);
                            break;
                        case 1:
                            SchedulePreference.setSubgroup(getActivity(), A);
                            break;
                        case 2:
                            SchedulePreference.setSubgroup(getActivity(), B);
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.recycler_my_schedules);
        mSchedulesAdapter = new MySchedulesAdapter(this, this);
        recyclerView.setAdapter(mSchedulesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // добавление разделителя
        if (getContext() != null) {
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), R.drawable.divider));
        }

        DragToMoveCallback dragToMoveCallback = new DragToMoveCallback() {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                mSchedulesAdapter.moveItem(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }
        };

        mItemTouchHelper = new ItemTouchHelper(dragToMoveCallback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        mMySchedulesLoader = LoaderManager.getInstance(this)
                .initLoader(MY_SCHEDULES_LOADER, null, this);

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        LocalBroadcastManager.getInstance(context)
                .registerReceiver(mScheduleDownloaderReceiver,
                        new IntentFilter(ScheduleDownloaderService.SCHEDULE_DOWNLOADED_EVEN));
    }

    @Override
    public void onDestroy() {
        if (getContext() != null) {
            LocalBroadcastManager.getInstance(getContext())
                    .unregisterReceiver(mScheduleDownloaderReceiver);
        }

        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_schedule, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.from_repository: {
                if (getActivity() != null) {
                    NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host);
                    navController.navigate(R.id.toScheduleRepositoryFragment);
                }
                return true;
            }
            case R.id.new_schedule: {
                Intent intent = new Intent(getActivity(), ScheduleEditorActivity.class);
                startActivityForResult(intent, REQUEST_NEW_SCHEDULE);
                return true;
            }
            case R.id.load_schedule: {
                if (getActivity() != null) {
                    int permissionStatus = ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.READ_EXTERNAL_STORAGE);
                    if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                        loadSchedule();
                    } else {
                        requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_PERMISSION_READ_STORAGE);
                    }
                }
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // запрос разрешения на чтения внешнего хранилища
        if (requestCode == REQUEST_PERMISSION_READ_STORAGE) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                loadSchedule();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            // добавление нового расписания
            case REQUEST_NEW_SCHEDULE: {
                if (data == null || getView() == null || getActivity() == null) {
                    return;
                }
                String scheduleName = data.getStringExtra(EXTRA_SCHEDULE_NAME);

                File dir = getActivity().getExternalFilesDir(ROOT_PATH);
                if (dir == null || !dir.exists())
                    return;

                File file = new File(dir, scheduleName + ".json");

                Schedule schedule = new Schedule();
                try {
                    schedule.save(file.getAbsolutePath());
                } catch (JSONException | IOException e) {
                    e.printStackTrace();

                    showMessage(String.format("%s: %s %s",
                            getString(R.string.schedule),
                            scheduleName,
                            getString(R.string.failed_add)));

                    return;
                }

                SchedulePreference.add(getActivity(), scheduleName);
                updateSchedules();

                showMessage(String.format("%s: %s %s",
                        getString(R.string.schedule),
                        scheduleName,
                        getString(R.string.successfully_added)));

                break;
            }
            // загрузка расписания из вне
            case REQUEST_LOAD_SCHEDULE: {
                if (data == null) {
                    return;
                }

                Uri uri = data.getData();
                if (uri == null) {
                    return;
                }

                try {
                    if (getContext() == null) {
                        return;
                    }

                    ContentResolver resolver = getContext().getContentResolver();
                    InputStream stream = resolver.openInputStream(uri);

                    if (stream == null) {
                        return;
                    }

                    Schedule schedule = new Schedule();
                    schedule.load(stream);

                    String pathToFile = uri.getPath();
                    if (pathToFile == null) {
                        return;
                    }

                    File file = new File(pathToFile);
                    File outputFile = new File(getContext()
                            .getExternalFilesDir(ROOT_PATH), file.getName());

                    schedule.save(outputFile.getAbsolutePath());

                    SchedulePreference.add(getContext(),
                            file.getName().replace(".json", ""));
                    updateSchedules();

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

                break;
            }
        }
    }

    /**
     * Создает Intent для выбора расписания из вне, которое необходимо загрузить.
     */
    private void loadSchedule() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");

        startActivityForResult(intent, REQUEST_LOAD_SCHEDULE);
    }

    @Override
    public void onScheduleItemClicked(String schedule) {
        if (getActivity() != null) {
            Bundle args = new Bundle();

            String path = SchedulePreference.createPath(getActivity(), schedule);
            args.putString(ARG_SCHEDULE_PATH, path);
            args.putString(ARG_SCHEDULE_NAME, schedule);

            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host);
            navController.navigate(R.id.fromScheduleFragmentToScheduleViewFragment, args);
        }
    }

    @Override
    public void onScheduleItemMove(int fromPosition, int toPosition) {
        if (getActivity() != null) {
            SchedulePreference.move(getActivity(), fromPosition, toPosition);
        }
    }

    @Override
    public void onScheduleFavoriteSelected(String favorite) {
        if (getActivity() != null) {
            SchedulePreference.setFavorite(getActivity(), favorite);
        }
    }

    /**
     * Показывает view с информацией об отсутствии расписаний если нет ни
     * одного расписания. Иначе показывает список с расписаниями.
     */
    private void schedulesCountChanged() {
        if (mSchedulesAdapter.getItemCount() == 0) {
            mStatefulLayout.setState(R.id.empty_my_schedules);
        } else {
            mStatefulLayout.setState(R.id.recycler_my_schedules);
        }
    }

    /**
     * Обновляет список расписаний.
     */
    private void updateSchedules() {
        mStatefulLayout.setLoadState();
        mMySchedulesLoader.forceLoad();
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
    public Loader<MySchedulesLoader.DataView> onCreateLoader(int id, @Nullable Bundle args) {
        mMySchedulesLoader = new MySchedulesLoader(Objects.requireNonNull(getActivity()));
        updateSchedules();
        return mMySchedulesLoader;
    }


    @Override
    public void onLoadFinished(@NonNull Loader<MySchedulesLoader.DataView> loader,
                               MySchedulesLoader.DataView data) {
        if (data.changeCount != SchedulePreference.changeCount()) {
            updateSchedules();
            return;
        }

        mSchedulesAdapter.update(data.schedules, data.favorite);

        schedulesCountChanged();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<MySchedulesLoader.DataView> loader) {
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
}