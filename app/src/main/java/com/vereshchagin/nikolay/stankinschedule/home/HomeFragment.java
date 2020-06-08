package com.vereshchagin.nikolay.stankinschedule.home;

import android.graphics.Paint;
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

import com.vereshchagin.nikolay.stankinschedule.R;
import com.vereshchagin.nikolay.stankinschedule.home.pager.HomePager;
import com.vereshchagin.nikolay.stankinschedule.schedule.view.ScheduleViewFragment;
import com.vereshchagin.nikolay.stankinschedule.settings.SchedulePreference;
import com.vereshchagin.nikolay.stankinschedule.utils.StatefulLayout;

import java.util.Objects;

/**
 * Фрагмент главной страницы.
 */
public class HomeFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<HomeLoader.LoadData> , View.OnClickListener {

    private static final String TAG = "HomeFragmentLog";

    private static final int HOME_LOADER = 0;

    private StatefulLayout mStatefulLayout;
    private TextView mScheduleNameView;
    private HomePager mHomePager;

    /**
     * Загрузчик данных для pager'а.
     */
    private Loader<HomeLoader.LoadData> mHomeLoader;

    @Nullable
    private String mFavoriteName;

    public HomeFragment() {
        super();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mStatefulLayout = view.findViewById(R.id.stateful_layout);
        mStatefulLayout.addXMLViews();
        mStatefulLayout.setAnimation(StatefulLayout.NO_ANIMATION);
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
    public Loader<HomeLoader.LoadData> onCreateLoader(int id, @Nullable Bundle args) {
        mHomeLoader = new HomeLoader(Objects.requireNonNull(getContext()));
        updateScheduleData();
        return mHomeLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<HomeLoader.LoadData> loader,
                               HomeLoader.LoadData data) {
        if (data.changeCount != SchedulePreference.changeCount()) {
            updateScheduleData();
            return;
        }

        mFavoriteName = data.favorite;
        mHomePager.update(data.titles, data.days);

        if (data.favorite.isEmpty()) {
            mStatefulLayout.setState(R.id.no_favorite_schedule);
            mScheduleNameView.setText("");
        } else {
            mStatefulLayout.setState(R.id.home_pager);
            mScheduleNameView.setText(data.favorite);
        }
    }

    /**
     * Обновляет данные pager'а.
     */
    private void updateScheduleData() {
        mStatefulLayout.setLoadState();
        mScheduleNameView.setText("");

        mHomeLoader.forceLoad();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<HomeLoader.LoadData> loader) {
    }

    @Override
    public void onClick(View v) {
        // нажато название расписание
        if (v.getId() == R.id.schedule_name) {
            if (getActivity() == null || mFavoriteName == null || mFavoriteName.isEmpty()) {
                return;
            }

            String path = SchedulePreference.createPath(getActivity(), mFavoriteName);

            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host);
            navController.navigate(R.id.fromHomeFragmentToScheduleViewFragment,
                    ScheduleViewFragment.createBundle(mFavoriteName, path));
        }
    }
}
