package com.github.nikololoshka.pepegaschedule.schedule.repository;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nikololoshka.pepegaschedule.R;
import com.github.nikololoshka.pepegaschedule.settings.SchedulePreference;
import com.github.nikololoshka.pepegaschedule.utils.StatefulLayout;
import com.google.android.material.appbar.AppBarLayout;
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

    private static final String ARG_FILTER_QUERY = "filter_query";

    private static final String TAG = "ScheduleRepositoryTag";
    private static final boolean DEBUG = false;

    private Loader<TreeMap<String, String>> mRepositoryLoader;

    private StatefulLayout mStatefulLayout;
    private ScheduleRepositoryAdapter mRepositoryAdapter;
    private AppBarLayout mAppBarRepository;

    private String mFilterQuery = "";

    public ScheduleRepositoryFragment() {
        super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mFilterQuery = savedInstanceState.getString(ARG_FILTER_QUERY, "");
        }

        setHasOptionsMenu(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // скрыть клавиатуру если выходим
        if (getActivity() != null) {
            InputMethodManager manager = ContextCompat.getSystemService(getActivity(),
                    InputMethodManager.class);

            View currentFocusedView = getActivity().getCurrentFocus();
            if (currentFocusedView != null && manager != null) {
                manager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_repository, container, false);

        mStatefulLayout = view.findViewById(R.id.stateful_layout);
        mStatefulLayout.addXMLViews();
        mStatefulLayout.setLoadState();

        TextView textVersion = view.findViewById(R.id.repository_version);
        textVersion.setText(getString(R.string.repository_version));

        TextView textLastUpdate = view.findViewById(R.id.repository_last_update);
        textLastUpdate.setText(getString(R.string.repository_last_update));

        mAppBarRepository = view.findViewById(R.id.app_bar_repository);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_repository);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        mRepositoryAdapter = new ScheduleRepositoryAdapter(this);
        recyclerView.setAdapter(mRepositoryAdapter);

        mRepositoryLoader = LoaderManager.getInstance(this)
                .initLoader(SCHEDULE_REPOSITORY_LOADER, null, this);

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(ARG_FILTER_QUERY, mFilterQuery);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_schedule_repository, menu);

        final MenuItem searchItem = menu.findItem(R.id.search_schedule);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        if (getActivity() != null) {
            SearchManager searchManager = (SearchManager) getActivity()
                    .getSystemService(Context.SEARCH_SERVICE);

            if (searchManager != null) {
                searchView.setSearchableInfo(searchManager
                        .getSearchableInfo(getActivity().getComponentName()));
            }
        }

        searchView.setQueryHint(getString(R.string.schedule_editor_name));
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchSchedules(newText);
                return true;
            }
        });

        if (DEBUG) {
            Log.d(TAG, "onCreateOptionsMenu: " + mFilterQuery);
        }

        if (!mFilterQuery.isEmpty()) {
            searchView.setIconified(false);

            if (!searchItem.isActionViewExpanded()) {
                searchItem.expandActionView();
            }

            searchView.setQuery(mFilterQuery, true);
            searchView.clearFocus();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.update_schedule) {
            reloadData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Перезаружает данные с расписаниями.
     */
    private void reloadData() {
        mStatefulLayout.setLoadState();
        mRepositoryLoader.forceLoad();
    }

    /**
     * Вызывается если нужно искать расписание по запросу.
     * @param query запрос.
     */
    private void searchSchedules(String query) {
        ViewGroup.LayoutParams params = mAppBarRepository.getLayoutParams();
        params.height = query.isEmpty() ? getResources()
                .getDimensionPixelSize(R.dimen.appbar_repository_height) : 0;
        mAppBarRepository.setLayoutParams(params);

        mFilterQuery = query;
        mRepositoryAdapter.filter(query);
    }

    @Override
    public void onScheduleItemClicked(String name, String path) {
        if (getContext() == null) {
            return;
        }

        if (SchedulePreference.schedules(getContext()).contains(name)){
            showMessage(getString(R.string.schedule_editor_exists));
            return;
        }

        // создаем Service для скачивания расписания
        ScheduleDownloaderService.createTask(getContext(), name, path);

        showMessage(String.format("%s: %s ", getString(R.string.repository_loading_schedule), name));
    }

    /**
     * Показывает информационное сообщение на экран пользователю.
     * @param message сообщение.
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
