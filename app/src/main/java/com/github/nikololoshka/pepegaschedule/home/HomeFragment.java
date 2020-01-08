package com.github.nikololoshka.pepegaschedule.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.github.nikololoshka.pepegaschedule.R;
import com.github.nikololoshka.pepegaschedule.home.pager.HomePager;
import com.github.nikololoshka.pepegaschedule.settings.SchedulePreference;
import com.github.nikololoshka.pepegaschedule.utils.StatefulLayout;

import java.util.Objects;

import static com.github.nikololoshka.pepegaschedule.schedule.view.ScheduleViewFragment.ARG_SCHEDULE_NAME;
import static com.github.nikololoshka.pepegaschedule.schedule.view.ScheduleViewFragment.ARG_SCHEDULE_PATH;

/**
 * Фрагмент главной страницы.
 */
public class HomeFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<HomeLoader.DataView> , View.OnClickListener {

    private static final int HOME_LOADER = 0;

    private StatefulLayout mStatefulLayout;
    private TextView mScheduleNameView;
    private HomePager mHomePager;

    private Loader<HomeLoader.DataView> mHomeLoader;
    private HomeLoader.DataView mDataView;

    public HomeFragment() {
        super();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mStatefulLayout = view.findViewById(R.id.stateful_layout);
        mStatefulLayout.addXMLViews();
        mStatefulLayout.setLoadState();

        mHomePager = view.findViewById(R.id.home_pager);


        mScheduleNameView = view.findViewById(R.id.schedule_name);
        mScheduleNameView.setOnClickListener(this);

        mHomeLoader = LoaderManager.getInstance(this)
                .initLoader(HOME_LOADER, null, this);

        return view;
    }

    @NonNull
    @Override
    public Loader<HomeLoader.DataView> onCreateLoader(int id, @Nullable Bundle args) {
        mHomeLoader = new HomeLoader(Objects.requireNonNull(getActivity()));
        updateScheduleData();
        return mHomeLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<HomeLoader.DataView> loader,
                               HomeLoader.DataView data) {
        if (data.changeCount != SchedulePreference.changeCount()) {
            updateScheduleData();
            return;
        }

        mDataView = data;
        mHomePager.update(data.titles, data.days);

        if (mDataView.favorite == null || mDataView.favorite.isEmpty()) {
            mStatefulLayout.setState(R.id.no_favorite_schedule);
            mScheduleNameView.setText("");
        } else {
            mStatefulLayout.setState(R.id.home_pager);
            mScheduleNameView.setText(mDataView.favorite);
        }
    }

    private void updateScheduleData() {
        mStatefulLayout.setLoadState();
        mScheduleNameView.setText("");

        mHomeLoader.forceLoad();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<HomeLoader.DataView> loader) {
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.schedule_name) {
            if (getActivity() == null) {
                return;
            }

            Bundle args = new Bundle();
            String path = SchedulePreference.createPath(getActivity(), mDataView.favorite);
            args.putString(ARG_SCHEDULE_PATH, path);
            args.putString(ARG_SCHEDULE_NAME, mDataView.favorite);

            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host);
            navController.navigate(R.id.fromHomeFragmentToScheduleViewFragment, args);
        }
    }
}
