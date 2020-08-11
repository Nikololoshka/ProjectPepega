package com.vereshchagin.nikolay.stankinschedule.ui.schedule.myschedules;

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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.vereshchagin.nikolay.stankinschedule.R;
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.name.ScheduleNameEditorActivity;
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.model.Schedule;
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.myschedules.list.DragToMoveCallback;
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.myschedules.list.MySchedulesAdapter;
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.ScheduleDownloaderService;
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.view.ScheduleViewFragment;
import com.vereshchagin.nikolay.stankinschedule.ui.settings.SchedulePreference;
import com.vereshchagin.nikolay.stankinschedule.utils.DividerItemDecoration;
import com.vereshchagin.nikolay.stankinschedule.utils.StatefulLayout;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.name.ScheduleNameEditorActivity.EXTRA_SCHEDULE_NAME;
import static com.vereshchagin.nikolay.stankinschedule.ui.schedule.model.pair.SubgroupEnum.A;
import static com.vereshchagin.nikolay.stankinschedule.ui.schedule.model.pair.SubgroupEnum.B;
import static com.vereshchagin.nikolay.stankinschedule.ui.schedule.model.pair.SubgroupEnum.COMMON;
import static com.vereshchagin.nikolay.stankinschedule.ui.settings.SchedulePreference.ROOT_PATH;

/**
 * Фрагмент списка расписаний.
 */
public class MyScheduleFragment extends Fragment
        implements MySchedulesAdapter.OnItemClickListener, DragToMoveCallback.OnStartDragListener {

    private static final int REQUEST_NEW_SCHEDULE = 0;
    private static final int REQUEST_LOAD_SCHEDULE = 1;

    private static final int REQUEST_PERMISSION_READ_STORAGE = 0;

    private StatefulLayout mStatefulLayout;
    private MySchedulesAdapter mSchedulesAdapter;
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

            DragToMoveCallback.RecyclerViewBackground background = new DragToMoveCallback.RecyclerViewBackground(getContext());
            background.attachRecyclerView(recyclerView);
            recyclerView.setBackground(background);
        }

        DragToMoveCallback dragToMoveCallback = new DragToMoveCallback() {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();

                mSchedulesAdapter.moveItem(fromPosition, toPosition);
                onScheduleItemMove(fromPosition, toPosition);

                return true;
            }
        };

        mItemTouchHelper = new ItemTouchHelper(dragToMoveCallback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        updateSchedules();

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        LocalBroadcastManager.getInstance(context)
                .registerReceiver(mScheduleDownloaderReceiver,
                        new IntentFilter(ScheduleDownloaderService.SCHEDULE_DOWNLOADED_EVENT));
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
            // из репозитория
            case R.id.from_repository: {
                if (getActivity() != null) {
                    NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host);
                    navController.navigate(R.id.toScheduleRepositoryFragment);
                }
                return true;
            }
            // создать новое
            case R.id.create_schedule: {
                Intent intent = new Intent(getActivity(), ScheduleNameEditorActivity.class);
                startActivityForResult(intent, REQUEST_NEW_SCHEDULE);
                return true;
            }
            // загрузить расписание
            case R.id.load_schedule: {
                if (getActivity() != null) {
                    int permissionStatus = ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.READ_EXTERNAL_STORAGE);
                    if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                        loadSchedule();
                    } else {
                        requestPermissions(new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
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

                // TODO: 31/01/20 обработка ошибок

                Schedule schedule = new Schedule();
                try {
                    String json = schedule.toJson();
                    FileUtils.writeStringToFile(file, json, StandardCharsets.UTF_8);

                } catch (IOException e) {
                    e.printStackTrace();

                    showMessage(getString(R.string.sch_failed_add, scheduleName));

                    return;
                }

                SchedulePreference.add(getActivity(), scheduleName);
                updateSchedules();

                showMessage(getString(R.string.sch_successfully_added, scheduleName));

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

                    Schedule schedule = Schedule.fromJson(stream);

                    String pathToFile = uri.getPath();
                    if (pathToFile == null) {
                        return;
                    }

                    File file = new File(pathToFile);
                    File outputFile = new File(getContext()
                            .getExternalFilesDir(ROOT_PATH), file.getName());

                    String json = schedule.toJson();
                    FileUtils.writeStringToFile(outputFile, json, StandardCharsets.UTF_8);

                    SchedulePreference.add(getContext(),
                            file.getName().replace(".json", ""));
                    updateSchedules();

                } catch (IOException e) {
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
    public void onScheduleItemClicked(@NonNull String scheduleName) {
        if (getActivity() != null) {

            String schedulePath = SchedulePreference.createPath(getActivity(), scheduleName);
            Bundle args = ScheduleViewFragment.createBundle(scheduleName, schedulePath);

            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host);
            navController.navigate(R.id.fromScheduleFragmentToScheduleViewFragment, args);
        }
    }

    @Override
    public void onScheduleFavoriteSelected(@NonNull String favorite) {
        if (getContext() != null) {
            SchedulePreference.setFavorite(getContext(), favorite);
        }
    }

    /**
     * Расписание перемещено.
     * @param fromPosition старая позиция.
     * @param toPosition новая позиция.
     */
    private void onScheduleItemMove(int fromPosition, int toPosition) {
        if (getContext() != null) {
            SchedulePreference.move(getContext(), fromPosition, toPosition);
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
        Context context = getContext();
        if (context == null) {
            return;
        }

        List<String> schedules = SchedulePreference.schedules(context);
        String favorite = SchedulePreference.favorite(context);

        if (mSchedulesAdapter != null) {
            mSchedulesAdapter.submitList(schedules, favorite);
            schedulesCountChanged();
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

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
}
