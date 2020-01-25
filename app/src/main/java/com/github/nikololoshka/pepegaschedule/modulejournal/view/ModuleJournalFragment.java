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

import com.github.nikololoshka.pepegaschedule.R;
import com.github.nikololoshka.pepegaschedule.modulejournal.view.model.SemestersMarks;
import com.github.nikololoshka.pepegaschedule.modulejournal.view.model.StudentData;
import com.github.nikololoshka.pepegaschedule.modulejournal.view.paging.SemestersAdapter;
import com.github.nikololoshka.pepegaschedule.settings.ModuleJournalPreference;
import com.github.nikololoshka.pepegaschedule.utils.StatefulLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.File;
import java.util.Objects;

/**
 * Фрагмент модульного журнала.
 */
public class ModuleJournalFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<ModuleJournalLoader.LoadData> {

    private static final String TAG = "ModuleJournalLog";

    private static final String CURRENT_PAGE = "current_page";

    private static final int MODULE_JOURNAL_LOADER = 0;

    private StatefulLayout mStatefulLayout;

    private TextView mStudentNameTextView;
    private TextView mStudentGroupTextView;

    private ViewPager2 mPagerSemesters;
    private SemestersAdapter mSemestersAdapter;

    private ModuleJournalLoader mModuleJournalLoader;
    private ModuleJournalViewModel mModuleJournalViewModel;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_module_journal, container, false);
        mStatefulLayout = view.findViewById(R.id.stateful_layout);
        mStatefulLayout.addXMLViews();
        mStatefulLayout.setState(R.id.loading_shimmer);

        mStudentNameTextView = view.findViewById(R.id.mj_student_name);
        mStudentGroupTextView = view.findViewById(R.id.mj_student_group);

        TabLayout tabLayout = view.findViewById(R.id.mj_tab_semesters);
        mPagerSemesters = view.findViewById(R.id.mj_pager_semesters);

        mSemestersAdapter = new SemestersAdapter();
        mPagerSemesters.setAdapter(mSemestersAdapter);
        mPagerSemesters.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                mCurrentPage = position;
            }
        });

        mModuleJournalViewModel  = new ViewModelProvider(this,
                new ViewModelProvider.NewInstanceFactory())
                .get(ModuleJournalViewModel.class);

        new TabLayoutMediator(tabLayout, mPagerSemesters, true,
                new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(mModuleJournalViewModel.semestersStorage().semesterTitle(position));
            }
        }).attach();

        mModuleJournalLoader = (ModuleJournalLoader) LoaderManager.getInstance(this)
                .initLoader(MODULE_JOURNAL_LOADER, null, this);

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
            case R.id.mj_sign_out: {
                if (getContext() != null) {
                    ModuleJournalPreference.setSignIn(getContext(), false);
                    StudentData.clearCacheData(getContext().getCacheDir());
                    SemestersMarks.clearCacheData(getContext().getCacheDir());

                    navigateToLoginScreen();
                }
            }
            case R.id.mj_update_marks: {
                refreshPager();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_PAGE, mCurrentPage);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getContext() != null) {
             if (!ModuleJournalPreference.signIn(getContext())) {
                 navigateToLoginScreen();
             } else {
                 mModuleJournalLoader.forceLoad();
             }
        }

    }

    private void navigateToLoginScreen() {
        if (getActivity() != null) {
            NavController controller = Navigation.findNavController(getActivity(), R.id.nav_host);
            controller.navigate(R.id.toModuleJournalLoginFragment);
        }
    }

    private void refreshPager() {
        PagedList<SemestersMarks> marksPagedList = mSemestersAdapter.getCurrentList();
        if (marksPagedList != null) {
            marksPagedList.getDataSource().invalidate();
            mCurrentPage = RecyclerView.NO_POSITION;
        }
    }

    @NonNull
    @Override
    public Loader<ModuleJournalLoader.LoadData> onCreateLoader(int id, @Nullable Bundle args) {
        return new ModuleJournalLoader(Objects.requireNonNull(getContext()));
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ModuleJournalLoader.LoadData> loader,
                               final ModuleJournalLoader.LoadData data) {

        if (data.response == null || data.login == null || data.password == null) {
            return;
        }

        mStudentNameTextView.setText(getString(R.string.mj_student, data.response.studentName()));
        mStudentGroupTextView.setText(getString(R.string.mj_group, data.response.group()));

        mStatefulLayout.setState(R.id.mj_student_data);

        mModuleJournalViewModel.semestersStorage().setLogin(data.login);
        mModuleJournalViewModel.semestersStorage().setPassword(data.password);
        mModuleJournalViewModel.semestersStorage().setSemesters(data.response.semesters());

        File cacheDir = getContext() == null ? null : getContext().getCacheDir();
        mModuleJournalViewModel.semestersStorage().setCacheDirectory(cacheDir);

        final int pos = mCurrentPage == RecyclerView.NO_POSITION ? data.response.semesters().size() - 1 : mCurrentPage;

        mModuleJournalViewModel.semestersLiveData().observe(this, new Observer<PagedList<SemestersMarks>>() {
            @Override
            public void onChanged(final PagedList<SemestersMarks> semestersMarks) {
                mSemestersAdapter.submitList(semestersMarks, new Runnable() {
                    @Override
                    public void run() {
                        mPagerSemesters.setCurrentItem(pos, false);
                        Log.d(TAG, "run: " + mCurrentPage);
                        Log.d(TAG, "run2: " + pos);
                    }
                });
            }
        });
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ModuleJournalLoader.LoadData> loader) {

    }
}
