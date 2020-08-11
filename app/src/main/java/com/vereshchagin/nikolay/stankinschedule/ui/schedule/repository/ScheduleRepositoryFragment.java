package com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository;

import android.animation.ValueAnimator;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.vereshchagin.nikolay.stankinschedule.BuildConfig;
import com.vereshchagin.nikolay.stankinschedule.R;
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.list.RepositoryItem;
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.repository.list.ScheduleRepositoryAdapter;
import com.vereshchagin.nikolay.stankinschedule.ui.settings.SchedulePreference;
import com.vereshchagin.nikolay.stankinschedule.utils.StatefulLayout;

import java.util.ArrayList;
import java.util.List;


/**
 * Фрагмент репозитория с расписаниями.
 */
public class ScheduleRepositoryFragment extends Fragment
        implements ScheduleRepositoryAdapter.OnRepositoryClickListener {

    private static final String ARG_FILTER_QUERY = "filter_query";

    private static final String TAG = "ScheduleRepositoryLog";

    private StatefulLayout mStatefulLayout;

    /**
     * ViewModel репозитория.
     */
    private ScheduleRepositoryModel mScheduleRepositoryModel;
    /**
     * Адаптер для списка расписаний.
     */
    private ScheduleRepositoryAdapter mRepositoryAdapter;

    private AppBarLayout mAppBarRepository;
    private ValueAnimator mAppBarAnimator;
    private int mTargetAppBarHeight;

    @NonNull
    private String mFilterQuery;

    public ScheduleRepositoryFragment() {
        super();
        mFilterQuery = "";

        mAppBarAnimator = new ValueAnimator();
        mAppBarAnimator.setDuration(300);
        mAppBarAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAppBarRepository.getLayoutParams().height = (int) animation.getAnimatedValue();
                mAppBarRepository.requestLayout();
            }
        });

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
        // скрыть клавиатуру при выходе
        if (getActivity() != null) {
            InputMethodManager manager = getActivity().getSystemService(InputMethodManager.class);
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

        mTargetAppBarHeight = getResources().getDimensionPixelSize(R.dimen.appbar_repository_height);

        TextView textVersion = view.findViewById(R.id.repository_version);
        textVersion.setText(getString(R.string.repository_version));

        TextView textLastUpdate = view.findViewById(R.id.repository_last_update);
        textLastUpdate.setText(getString(R.string.repository_last_update));

        mAppBarRepository = view.findViewById(R.id.app_bar_repository);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_repository);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        mRepositoryAdapter = new ScheduleRepositoryAdapter(this);
        recyclerView.setAdapter(mRepositoryAdapter);

        mScheduleRepositoryModel = new ViewModelProvider(this,
                new ScheduleRepositoryModel.Factory(getActivity().getApplication()))
                .get(ScheduleRepositoryModel.class);

        mScheduleRepositoryModel.state().observe(getViewLifecycleOwner(), new Observer<ScheduleRepositoryModel.State>() {
            @Override
            public void onChanged(ScheduleRepositoryModel.State state) {
                stateChanged(state);
            }
        });

        mScheduleRepositoryModel.schedules().observe(getViewLifecycleOwner(), new Observer<List<RepositoryItem>>() {
            @Override
            public void onChanged(List<RepositoryItem> repositoryItems) {
                mRepositoryAdapter.submitList(repositoryItems);
            }
        });

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
            SearchManager searchManager = getActivity().getSystemService(SearchManager.class);

            if (searchManager != null) {
                searchView.setSearchableInfo(searchManager
                        .getSearchableInfo(getActivity().getComponentName()));
            }
        }

        searchView.setQueryHint(getString(R.string.repository_search_hint));
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

        if (BuildConfig.DEBUG) {
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
        mScheduleRepositoryModel.loadSchedules();
    }

    /**
     * Вызывается если нужно искать расписание по запросу.
     * @param query запрос.
     */
    private void searchSchedules(@NonNull String query) {
        changeAppBarHeight(query.isEmpty() ? getResources()
                .getDimensionPixelSize(R.dimen.appbar_repository_height) : 0);

        List<RepositoryItem> repositoryItems = mScheduleRepositoryModel.schedules().getValue();

        if (repositoryItems == null) {
            return;
        }

        mFilterQuery = query;

        List<RepositoryItem> items;
        if (query.isEmpty()) {
            items = repositoryItems;
        } else {
            items = new ArrayList<>();
            // поиск
            String right = query.toLowerCase();
            for (int i = 0; i < repositoryItems.size(); i++) {
                String left = repositoryItems.get(i).name().toLowerCase();

                if (left.contains(right)) {
                    items.add(repositoryItems.get(i));
                }
            }
        }

        if (items.isEmpty()) {
            mStatefulLayout.setState(R.id.repository_empty);
            return;
        }

        mStatefulLayout.setState(R.id.repository_container);
        mRepositoryAdapter.submitList(items);
    }

    /**
     * Анимировано менят высоту appbar layout'а.
     * @param targetHeight необходимая высота.
     */
    private void changeAppBarHeight(int targetHeight) {
        if (targetHeight == mTargetAppBarHeight) {
            return;
        }

        mTargetAppBarHeight = targetHeight;

        mAppBarAnimator.cancel();
        mAppBarAnimator.setIntValues(mAppBarRepository.getMeasuredHeight(), targetHeight);
        mAppBarAnimator.start();
    }

    @Override
    public void onRepositoryItemClicked(@NonNull RepositoryItem item) {
        Context context = getContext();
        if (context == null) {
            return;
        }

        if (SchedulePreference.schedules(context).contains(item.name())){
            showMessage(getString(R.string.schedule_editor_exists));
            return;
        }

        // создаем Service для скачивания расписания
        ScheduleDownloaderService.createTask(context, item.name(), item.path());

        showMessage(String.format("%s: %s ", getString(R.string.repository_loading_schedule), item.name()));
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

    /**
     * Вызывается, когда поменялось состояние загрузки расписаний.
     * @param state сотсояние.
     */
    private void stateChanged(@NonNull ScheduleRepositoryModel.State state) {
        switch (state) {
            case OK: {
                mStatefulLayout.setState(R.id.repository_container);
                break;
            }
            case LOADING: {
                mStatefulLayout.setLoadState();
                break;
            }
            case ERROR: {
                // TODO: 28/01/20 обработка ошибки загрузки
                Toast.makeText(getContext(), "Error loading data", Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }
}
