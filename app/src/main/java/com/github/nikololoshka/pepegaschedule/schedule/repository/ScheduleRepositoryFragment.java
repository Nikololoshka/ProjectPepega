package com.github.nikololoshka.pepegaschedule.schedule.repository;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nikololoshka.pepegaschedule.R;
import com.github.nikololoshka.pepegaschedule.settings.SchedulePreference;
import com.github.nikololoshka.pepegaschedule.utils.StatefulLayout;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Objects;
import java.util.TreeMap;


/**
 * Фрагмент репозитория с расписаниями.
 */
public class ScheduleRepositoryFragment extends Fragment
        implements ScheduleRepositoryAdapter.OnRepositoryClickListener,
        LoaderManager.LoaderCallbacks<TreeMap<String, String>> {

    private static final int SCHEDULE_REPOSITORY_LOADER = 0;

    private Loader<TreeMap<String, String>> mRepositoryLoader;

    private StatefulLayout mStatefulLayout;
    private ScheduleRepositoryAdapter mRepositoryAdapter;

    public ScheduleRepositoryFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_repository, container, false);

        mStatefulLayout = view.findViewById(R.id.stateful_layout);
        mStatefulLayout.addXMLViews();
        mStatefulLayout.setLoadState();

        TextView textView = view.findViewById(R.id.repository_info);
        textView.setText(getString(R.string.repository_version));

        RecyclerView recyclerView = view.findViewById(R.id.recycler_repository);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        mRepositoryAdapter = new ScheduleRepositoryAdapter(this);
        recyclerView.setAdapter(mRepositoryAdapter);

        mRepositoryLoader = LoaderManager.getInstance(this)
                .initLoader(SCHEDULE_REPOSITORY_LOADER, null, this);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_schedule_repository, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.update_schedule) {
            reloadData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void reloadData() {
        mStatefulLayout.setLoadState();
        mRepositoryLoader.forceLoad();
    }

    @Override
    public void onScheduleItemClicked(String name, String path) {
        if (getContext() == null) {
            return;
        }

        if (SchedulePreference.schedules(getContext()).contains(name)){
            showMessage(getString(R.string.schedule_exists));
            return;
        }

        // создаем Service для скачивания расписания
        ScheduleDownloaderService.createTask(getContext(), name, path);

        showMessage(String.format("%s: %s ", getString(R.string.loading_schedule), name));
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
    public Loader<TreeMap<String, String>> onCreateLoader(int id, @Nullable Bundle args) {
        mRepositoryLoader = new ScheduleRepositoryLoader(Objects.requireNonNull(getActivity()));
        reloadData();
        return mRepositoryLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<TreeMap<String, String>> loader, TreeMap<String, String> data) {
        mRepositoryAdapter.update(new ArrayList<>(data.keySet()), new ArrayList<>(data.values()));
        mStatefulLayout.setState(R.id.repository_container);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<TreeMap<String, String>> loader) {
    }
}
