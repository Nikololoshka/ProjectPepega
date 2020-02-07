package com.github.nikololoshka.pepegaschedule.modulejournal.view;

import android.os.Bundle;
import android.util.Log;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.github.nikololoshka.pepegaschedule.BuildConfig;
import com.github.nikololoshka.pepegaschedule.R;
import com.github.nikololoshka.pepegaschedule.modulejournal.view.model.SemestersMarks;
import com.github.nikololoshka.pepegaschedule.modulejournal.view.model.StudentData;
import com.github.nikololoshka.pepegaschedule.modulejournal.view.paging.SemestersAdapter;
import com.github.nikololoshka.pepegaschedule.settings.ModuleJournalPreference;
import com.github.nikololoshka.pepegaschedule.utils.CommonUtils;
import com.github.nikololoshka.pepegaschedule.utils.StatefulLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.File;
import java.util.Objects;

/**
 * Фрагмент модульного журнала.
 */
public class ModuleJournalFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<ModuleJournalLoader.LoadData>,
        SemestersAdapter.OnSemestersListener {

    private static final String TAG = "ModuleJournalLog";

    private static final String RELOAD_MJ = "reload_mj";
    private static final String CURRENT_PAGE = "current_page";

    private static final int MODULE_JOURNAL_LOADER = 0;

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

    private ModuleJournalLoader mModuleJournalLoader;
    private ModuleJournalViewModel mModuleJournalViewModel;

    private boolean mReloadModuleJournal;
    private int mCurrentPage;


    public ModuleJournalFragment() {
        super();

        mReloadModuleJournal = false;
        mCurrentPage = RecyclerView.NO_POSITION;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (savedInstanceState != null) {
            mReloadModuleJournal = savedInstanceState.getBoolean(RELOAD_MJ, false);
            mCurrentPage = savedInstanceState.getInt(CURRENT_PAGE, RecyclerView.NO_POSITION);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_module_journal, container, false);
        mStatefulLayoutMain = view.findViewById(R.id.stateful_layout);
        mStatefulLayoutMain.addXMLViews();
        mStatefulLayoutMain.setLoadState();

        mStatefulLayoutPager = view.findViewById(R.id.stateful_layout_pager);
        mStatefulLayoutPager.addXMLViews();
        mStatefulLayoutPager.setAnimation(false);

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

        mModuleJournalViewModel  = new ViewModelProvider(this,
                new ViewModelProvider.NewInstanceFactory())
                .get(ModuleJournalViewModel.class);

        new TabLayoutMediator(mTabSemesters, mPagerSemesters, true,
                new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(mModuleJournalViewModel.storage().semesterTitle(position));
            }
        }).attach();

        mModuleJournalLoader = (ModuleJournalLoader) LoaderManager.getInstance(this)
                .initLoader(MODULE_JOURNAL_LOADER, null, this);

        setSemestersLoading(true);

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
                if (getContext() != null) {
                    ModuleJournalPreference.setSignIn(getContext(), false);
                    StudentData.clearCacheData(getContext().getCacheDir());
                    SemestersMarks.clearCacheData(getContext().getCacheDir());

                    navigateToLoginScreen();
                }
            }
            // обновить данные
            case R.id.mj_update_marks: {
                if (!mReloadModuleJournal) {
                    refreshAll();
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(RELOAD_MJ, mReloadModuleJournal);
        outState.putInt(CURRENT_PAGE, mCurrentPage);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getContext() != null) {
            if (!ModuleJournalPreference.signIn(getContext())) {
                navigateToLoginScreen();
            } else {
                mModuleJournalLoader.reload(true);
            }
        }
    }

    /**
     * Осуществляет переход к фргменту входа в модульный журнал.
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
        mReloadModuleJournal = true;

        mStatefulLayoutMain.setLoadState();
        setSemestersLoading(true);

        mModuleJournalViewModel.storage().setUseCache(false);
        mModuleJournalLoader.reload(false);
    }

    @NonNull
    @Override
    public Loader<ModuleJournalLoader.LoadData> onCreateLoader(int id, @Nullable Bundle args) {
        return new ModuleJournalLoader(Objects.requireNonNull(getContext()));
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ModuleJournalLoader.LoadData> loader,
                               ModuleJournalLoader.LoadData data) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onLoadFinished: " + data.error);
            Log.d(TAG, "onLoadFinished: " + data.response);
            Log.d(TAG, "onLoadFinished: " + mReloadModuleJournal);
            Log.d(TAG, "onLoadFinished: " + mCurrentPage);
        }

        // загрузились с ошибкой
        if (data.error != null) {
            mStatefulLayoutMain.setState(R.id.mj_content_error);

            String title = data.error.errorTitle();
            mErrorTitleView.setText(title != null ? title : getString(data.error.errorTitleRes()));

            String description = data.error.errorDescription();
            mErrorDescriptionView.setText(description != null ? description : getString(data.error.errorDescriptionRes()));

            mReloadModuleJournal = false;
            return;
        }

        // не ошибка, но данные почему то не получили
        if (data.response == null) {
            Toast.makeText(getContext(), "Error getting data!", Toast.LENGTH_LONG).show();
        }

        mStudentNameTextView.setText(getString(R.string.mj_student, data.response.studentName()));
        mStudentGroupTextView.setText(getString(R.string.mj_group, data.response.group()));

        mStatefulLayoutMain.setState(R.id.mj_content);

        mModuleJournalViewModel.storage().setLogin(data.login);
        mModuleJournalViewModel.storage().setPassword(data.password);
        mModuleJournalViewModel.storage().setSemesters(data.response.semesters());

        File cacheDir = getContext() == null ? null : getContext().getCacheDir();
        mModuleJournalViewModel.storage().setCacheDirectory(cacheDir);

        // если перезагрузка данных
        if (mReloadModuleJournal) {
            PagedList<SemestersMarks> marksPagedList = mModuleJournalViewModel.semesters().getValue();
            if (marksPagedList != null) {
                marksPagedList.getDataSource().invalidate();
            }
            mReloadModuleJournal = false;
        }

        boolean isObserve = mModuleJournalViewModel.semesters().hasObservers();
        if (!isObserve) {
            mModuleJournalViewModel.semesters().observe(getViewLifecycleOwner(), new Observer<PagedList<SemestersMarks>>() {
                @Override
                public void onChanged(final PagedList<SemestersMarks> semestersMarks) {
                    // TODO: 27/01/20 проблема, когда после перезагрузки выбирается не тот элемент
                    final int pos;
                    if (mCurrentPage == RecyclerView.NO_POSITION) {
                        pos = semestersMarks.size() - 1;
                    } else {
                        pos = mCurrentPage;
                    }

                    mSemestersAdapter.submitList(semestersMarks, new Runnable() {
                        @Override
                        public void run() {
                            mPagerSemesters.setCurrentItem(pos, false);
                            setSemestersLoading(false);
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ModuleJournalLoader.LoadData> loader) {

    }

    @Override
    public void onUpdateSemesters() {
        refreshAll();
    }

    /**
     * Показывает сообщение о том, что данные загружены из кэша.
     * @param position текущая отображаемая страница pager'а.
     */
    private void showCacheMessage(int position) {
        PagedList<SemestersMarks> list = mSemestersAdapter.getCurrentList();
        if (list == null) {
            return;
        }
        SemestersMarks marks = list.get(position);
        if (marks == null) {
            return;
        }
        if (!marks.isCache()) {
            return;
        }

        Snackbar.make(mStatefulLayoutMain,
                getString(R.string.mj_last_update,
                        mModuleJournalViewModel.storage().semesterTitle(position),
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
}
