package com.github.nikololoshka.pepegaschedule.modulejournal.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.github.nikololoshka.pepegaschedule.R;
import com.github.nikololoshka.pepegaschedule.modulejournal.network.ModuleJournalError;
import com.github.nikololoshka.pepegaschedule.modulejournal.view.model.SemesterMarks;
import com.github.nikololoshka.pepegaschedule.modulejournal.view.model.StudentData;
import com.github.nikololoshka.pepegaschedule.modulejournal.view.paging.SemestersAdapter;
import com.github.nikololoshka.pepegaschedule.settings.ModuleJournalPreference;
import com.github.nikololoshka.pepegaschedule.utils.CommonUtils;
import com.github.nikololoshka.pepegaschedule.utils.StatefulLayout;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.concurrent.TimeUnit;

/**
 * Фрагмент модульного журнала.
 */
public class ModuleJournalFragment extends Fragment implements SemestersAdapter.OnSemestersListener {

    private static final String TAG = "ModuleJournalLog";

    private static final String CURRENT_PAGE = "current_page";

    private StatefulLayout mStatefulLayoutMain;
    private StatefulLayout mStatefulLayoutPager;

    /**
     * Информация о студенте.
     */
    private TextView mStudentNameTextView;
    private TextView mStudentGroupTextView;

    /**
     * Поля для ошибок.
     */
    private TextView mErrorTitleView;
    private TextView mErrorDescriptionView;

    /**
     * Pager с оценками.
     */
    private ViewPager2 mPagerSemesters;
    private TabLayout mTabSemesters;
    private SemestersAdapter mSemestersAdapter;

    private ModuleJournalModel mModuleJournalModel;
    private int mCurrentPage;

    public ModuleJournalFragment() {
        super();
        mCurrentPage = RecyclerView.NO_POSITION;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (savedInstanceState != null) {
            mCurrentPage = savedInstanceState.getInt(CURRENT_PAGE, RecyclerView.NO_POSITION);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_module_journal, container, false);

        mStatefulLayoutMain = view.findViewById(R.id.stateful_layout);
        mStatefulLayoutMain.addXMLViews();
        mStatefulLayoutMain.setAnimation(StatefulLayout.TRANSITION_ANIMATION);
        mStatefulLayoutMain.setLoadState();

        mStatefulLayoutPager = view.findViewById(R.id.stateful_layout_pager);
        mStatefulLayoutPager.addXMLViews();
        mStatefulLayoutPager.setAnimation(StatefulLayout.PROPERTY_ANIMATION);
        mStatefulLayoutPager.setLoadState();

        final SwipeRefreshLayout refreshLayout = view.findViewById(R.id.mj_content);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshAll();
                refreshLayout.setRefreshing(false);
            }
        });

        AppBarLayout appBarLayout = view.findViewById(R.id.app_bar_mj);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                refreshLayout.setEnabled(verticalOffset == 0);
            }
        });

        mErrorTitleView = view.findViewById(R.id.mj_error_title);
        mErrorDescriptionView = view.findViewById(R.id.mj_error_description);

        mStudentNameTextView = view.findViewById(R.id.mj_student_name);
        mStudentGroupTextView = view.findViewById(R.id.mj_student_group);

        mTabSemesters = view.findViewById(R.id.mj_tab_semesters);
        mPagerSemesters = view.findViewById(R.id.mj_pager_semesters);

        mSemestersAdapter = new SemestersAdapter();
        mSemestersAdapter.setUpdateListener(this);

        mPagerSemesters.setAdapter(mSemestersAdapter);
        mPagerSemesters.setOffscreenPageLimit(3);
        mPagerSemesters.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                mCurrentPage = position;
                showCacheMessage(position);
            }
        });

        mModuleJournalModel = new ViewModelProvider(this,
                new ModuleJournalModel.Factory(getActivity().getApplication()))
                .get(ModuleJournalModel.class);

        new TabLayoutMediator(mTabSemesters, mPagerSemesters, true, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(mModuleJournalModel.storage().semesterTitle(position));
            }
        }).attach();

        mModuleJournalModel.studentState().observe(getViewLifecycleOwner(), new Observer<ModuleJournalModel.StudentState>() {
            @Override
            public void onChanged(ModuleJournalModel.StudentState studentState) {
                studentStateChanged(studentState);
            }
        });

        mModuleJournalModel.student().observe(getViewLifecycleOwner(), new Observer<StudentData>() {
            @Override
            public void onChanged(StudentData studentData) {
                if (studentData != null) {
                    mStudentNameTextView.setText(getString(R.string.mj_student, studentData.name()));
                    mStudentGroupTextView.setText(getString(R.string.mj_group, studentData.group()));
                }
            }
        });

        mModuleJournalModel.semesters().observe(getViewLifecycleOwner(), new Observer<PagedList<SemesterMarks>>() {
            @Override
            public void onChanged(final PagedList<SemesterMarks> semesterMarks) {
                mSemestersAdapter.submitList(semesterMarks, new Runnable() {
                    @Override
                    public void run() {
                        int pos = mCurrentPage == RecyclerView.NO_POSITION ? semesterMarks.size() - 1 : mCurrentPage;
                        mPagerSemesters.setCurrentItem(pos, false);
                        setSemestersLoading(false);
                    }
                });
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_module_journal, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            // выход из модульного журнала
            case R.id.mj_sign_out: {
                Context context = getContext();
                if (context != null) {
                    ModuleJournalPreference.setSignIn(context, false);
                    StudentData.clearCacheData(context.getCacheDir());
                    SemesterMarks.clearCacheData(context.getCacheDir());

                    WorkManager.getInstance(context)
                            .cancelAllWorkByTag(ModuleJournalWorker.WORK_TAG);

                    navigateToLoginScreen();
                }
            }
            // обновить данные
            case R.id.mj_update_marks: {
                if (mModuleJournalModel.studentState().getValue() != ModuleJournalModel.StudentState.LOADING) {
                    refreshAll();
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!mModuleJournalModel.isSingIn()) {
            navigateToLoginScreen();
        } else {
            Context context = getContext();
            if (context != null) {
                Constraints constraints = new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build();

                PeriodicWorkRequest request =
                        new PeriodicWorkRequest.Builder(ModuleJournalWorker.class, 1, TimeUnit.HOURS)
                        .addTag(ModuleJournalWorker.WORK_TAG)
                        .setInitialDelay(1,TimeUnit.HOURS)
                        .setConstraints(constraints)
                        .build();

                WorkManager.getInstance(context)
                        .enqueue(request);
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_PAGE, mCurrentPage);
    }

    /**
     * Осуществляет переход к фрагменту входа в модульный журнал.
     */
    private void navigateToLoginScreen() {
        if (getActivity() != null) {
            NavController controller = Navigation.findNavController(getActivity(), R.id.nav_host);
            controller.navigate(R.id.toModuleJournalLoginFragment);
        }
    }

    /**
     * Запускает обновление данных модульного журнала.
     */
    private void refreshAll() {
        mModuleJournalModel.reload(false);
    }

    @Override
    public void onUpdateSemesters() {
        refreshAll();
    }

    /**
     * Показывает сообщение, что данные загружены из кэша.
     * @param position текущая отображаемая страница pager'а.
     */
    private void showCacheMessage(int position) {
        PagedList<SemesterMarks> list = mSemestersAdapter.getCurrentList();
        if (list == null) {
            return;
        }

        SemesterMarks marks = list.get(position);
        if (marks == null) {
            return;
        }

        if (!marks.isCache()) {
            return;
        }

        Snackbar.make(mStatefulLayoutMain,
                getString(R.string.mj_last_update,
                        mModuleJournalModel.storage().semesterTitle(position),
                        CommonUtils.dateToString(marks.time(), "HH:mm:ss dd.MM.yyyy",
                                CommonUtils.locale(getContext()))),
                Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Устанавливает загрузку в pager и скрывает/показывает tabs.
     * @param loading true - загрузка, false - показать контент.
     */
    private void setSemestersLoading(boolean loading) {
        if (loading) {
            mTabSemesters.setVisibility(View.INVISIBLE);
            mStatefulLayoutPager.setLoadState();
        } else {
            mTabSemesters.setVisibility(View.VISIBLE);
            mStatefulLayoutPager.setState(R.id.mj_pager_semesters);
        }
    }

    /**
     * Вызывается, если состояние загрузки основной информации было изменено.
     * @param state состояие.
     */
    private void studentStateChanged(ModuleJournalModel.StudentState state) {
        switch (state) {
            // загрузились успешно
            case OK: {
                mStatefulLayoutMain.setState(R.id.mj_content);
                break;
            }
            // загружаемся
            case LOADING: {
                mStatefulLayoutMain.setLoadState();
                setSemestersLoading(true);
                break;
            }
            // загрузились с ошибкой
            case ERROR: {
                mStatefulLayoutMain.setState(R.id.mj_content_error);

                ModuleJournalError error = mModuleJournalModel.error();
                if (error != null) {
                    String title = error.errorTitle();
                    mErrorTitleView.setText(title != null ? title : getString(error.errorTitleRes()));

                    String description = error.errorDescription();
                    mErrorDescriptionView.setText(description != null ? description : getString(error.errorDescriptionRes()));
                }
                break;
            }
        }
    }
}
