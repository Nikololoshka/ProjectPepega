package com.github.nikololoshka.pepegaschedule.schedule.fragments;

import android.Manifest;
import android.content.ContentResolver;
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
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nikololoshka.pepegaschedule.R;
import com.github.nikololoshka.pepegaschedule.schedule.Schedule;
import com.github.nikololoshka.pepegaschedule.schedule.activities.ScheduleEditorActivity;
import com.github.nikololoshka.pepegaschedule.settings.SchedulePreference;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.github.nikololoshka.pepegaschedule.schedule.activities.ScheduleEditorActivity.EXTRA_SCHEDULE_NAME;
import static com.github.nikololoshka.pepegaschedule.schedule.fragments.view.ScheduleViewFragment.ARG_SCHEDULE_NAME;
import static com.github.nikololoshka.pepegaschedule.schedule.fragments.view.ScheduleViewFragment.ARG_SCHEDULE_PATH;
import static com.github.nikololoshka.pepegaschedule.schedule.pair.SubgroupEnum.A;
import static com.github.nikololoshka.pepegaschedule.schedule.pair.SubgroupEnum.B;
import static com.github.nikololoshka.pepegaschedule.schedule.pair.SubgroupEnum.COMMON;
import static com.github.nikololoshka.pepegaschedule.settings.SchedulePreference.ROOT_PATH;


public class ScheduleFragment extends Fragment
        implements MySchedulesAdapter.OnItemClickListener,
        LoaderManager.LoaderCallbacks<MySchedulesLoader.DataView> {

    private static final int MY_SCHEDULES_LOADER = 0;

    private static final int REQUEST_NEW_SCHEDULE = 0;
    private static final int REQUEST_LOAD_SCHEDULE = 1;

    private static final int REQUEST_PERMISSION_READ_STORAGE = 0;

    private TextView mEmptySchedules;
    private List<String> mSchedules;
    private MySchedulesAdapter mSchedulesAdapter;
    private LinearLayout mLayoutSchedulesCard;
    private LinearLayout mLayoutMySchedules;
    private Loader<MySchedulesLoader.DataView> mMySchedulesLoader;
    private View mLoadingView;

    public ScheduleFragment() {
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

        mLayoutSchedulesCard = view.findViewById(R.id.layout_schedules);
        mLayoutMySchedules = view.findViewById(R.id.my_schedules_container);
        mEmptySchedules = view.findViewById(R.id.empty_my_schedules);
        mLoadingView = view.findViewById(R.id.loading_fragment);
        mLoadingView.setVisibility(View.GONE);

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
        mSchedulesAdapter = new MySchedulesAdapter(this);
        recyclerView.setAdapter(mSchedulesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration decoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        decoration.setDrawable(getResources().getDrawable(R.drawable.divider));
        recyclerView.addItemDecoration(decoration);


        mMySchedulesLoader = LoaderManager.getInstance(this)
                .initLoader(MY_SCHEDULES_LOADER, null, this);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_schedule, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.from_repository: {
                if (getActivity() != null) {
                    NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host);
                    navController.navigate(R.id.toScheduleManagerFragment);
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
                boolean isSaved = false;

                try {
                    isSaved = schedule.save(file.getAbsolutePath());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (isSaved) {
                    SchedulePreference.add(getActivity(), scheduleName);
                    updateSchedules();

                    Snackbar.make(getView(),
                            String.format("%s: %s %s", getString(R.string.schedule),
                                    scheduleName, getString(R.string.successfully_added)),
                            Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(getView(),
                            String.format("%s: %s %s", getString(R.string.schedule),
                                    scheduleName, getString(R.string.failed_add)),
                            Snackbar.LENGTH_SHORT).show();
                }
                break;
            }
            case REQUEST_LOAD_SCHEDULE: {
                if (data == null) {
                    return;
                }

                Uri uri = data.getData();
                if (uri == null) {
                    return;
                }

                try {
                    if (getActivity() == null) {
                        return;
                    }

                    ContentResolver resolver = getActivity().getContentResolver();
                    InputStream stream = resolver.openInputStream(uri);

                    if (stream == null) {
                        return;
                    }

                    Schedule schedule = new Schedule();
                    schedule.load(stream);

                    File file = new File(uri.getPath());
                    File outputFile = new File(getActivity()
                            .getExternalFilesDir(ROOT_PATH), file.getName());

                    schedule.save(outputFile.getAbsolutePath());

                    SchedulePreference.add(getActivity(),
                            file.getName().replace(".json", ""));
                    updateSchedules();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            }
        }
    }

    private void loadSchedule() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, REQUEST_LOAD_SCHEDULE);
    }

    @Override
    public void onScheduleItemClicked(int pos) {
        if (getActivity() != null) {
            Bundle args = new Bundle();
            String path = SchedulePreference.createPath(getActivity(), mSchedules.get(pos));
            args.putString(ARG_SCHEDULE_PATH, path);
            args.putString(ARG_SCHEDULE_NAME, mSchedules.get(pos));

            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host);
            navController.navigate(R.id.toScheduleViewFragment, args);
        }
    }

    @Override
    public void onScheduleFavoriteSelected(String favorite) {
        if (getActivity() != null) {
            SchedulePreference.setFavorite(getActivity(), favorite);
        }
    }

    private void schedulesCountChanged() {
        if (mSchedules.isEmpty()) {
            mEmptySchedules.setVisibility(View.VISIBLE);
            mLayoutSchedulesCard.setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
        } else {
            mEmptySchedules.setVisibility(View.GONE);
            mLayoutSchedulesCard.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        }
    }

    private void updateSchedules() {
        mLoadingView.setVisibility(View.VISIBLE);
        mLayoutMySchedules.setVisibility(View.GONE);

//        getChildFragmentManager()
//                .beginTransaction()
//                .replace(R.id.my_schedules_container, new LoadingFragment())
//                .addToBackStack(null)
//                .commit();

        mMySchedulesLoader.forceLoad();
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

        mSchedules = data.schedules;
        mSchedulesAdapter.update(mSchedules, data.favorite);

        schedulesCountChanged();

        mLoadingView.setVisibility(View.GONE);
        mLayoutMySchedules.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<MySchedulesLoader.DataView> loader) {
    }
}
