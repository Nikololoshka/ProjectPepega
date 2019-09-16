package com.github.nikololoshka.pepegaschedule.schedule.fragments.repository;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nikololoshka.pepegaschedule.R;
import com.github.nikololoshka.pepegaschedule.settings.SchedulePreference;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import java.util.TreeMap;


public class ScheduleRepositoryFragment extends Fragment
        implements ScheduleRepositoryAdapter.OnRepositoryClickListener,
        LoaderManager.LoaderCallbacks<TreeMap<String, String>> {

    private static final int SCHEDULE_REPOSITORY_LOADER = 0;

    private Loader<TreeMap<String, String>> mRepositoryLoader;

    private View mLoadingView;
    private LinearLayout mRepositoryContainer;

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

        mLoadingView = view.findViewById(R.id.loading_fragment);
        mRepositoryContainer = view.findViewById(R.id.repository_container);

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_schedule_repository, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.update_schedule) {
            updateView();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateView() {
        mLoadingView.setVisibility(View.VISIBLE);
        mRepositoryContainer.setVisibility(View.GONE);

        mRepositoryLoader.forceLoad();
    }

    @Override
    public void onScheduleItemClicked(String name, String path) {
        if (getView() == null || getActivity() == null) {
            return;
        }

        if (SchedulePreference.schedules(getActivity()).contains(name)){
            return;
        }

        try (FileWriter fileWriter = new FileWriter(new File(SchedulePreference.createPath(getActivity(), name)))) {
            AssetManager manager = getActivity().getAssets();
            Scanner scanner = new Scanner(manager.open(path));
            while (scanner.hasNextLine()) {
                fileWriter.write(scanner.nextLine());
            }
            SchedulePreference.add(getActivity(), name);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Snackbar.make(getView(),
                "Load " + name + " ",
                Snackbar.LENGTH_SHORT).show();
    }

    @NonNull
    @Override
    public Loader<TreeMap<String, String>> onCreateLoader(int id, @Nullable Bundle args) {
        mRepositoryLoader = new ScheduleRepositoryLoader(Objects.requireNonNull(getActivity()));
        updateView();
        return mRepositoryLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<TreeMap<String, String>> loader, TreeMap<String, String> data) {
        mRepositoryAdapter.update(new ArrayList<>(data.keySet()), new ArrayList<>(data.values()));
        mLoadingView.setVisibility(View.GONE);
        mRepositoryContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<TreeMap<String, String>> loader) {
    }
}
