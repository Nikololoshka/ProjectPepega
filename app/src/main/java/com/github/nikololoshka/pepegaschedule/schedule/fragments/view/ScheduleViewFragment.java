package com.github.nikololoshka.pepegaschedule.schedule.fragments.view;


import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nikololoshka.pepegaschedule.R;
import com.github.nikololoshka.pepegaschedule.schedule.activities.PairEditorActivity;
import com.github.nikololoshka.pepegaschedule.schedule.activities.ScheduleEditorActivity;
import com.github.nikololoshka.pepegaschedule.schedule.pair.Pair;
import com.github.nikololoshka.pepegaschedule.settings.SchedulePreference;
import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.github.nikololoshka.pepegaschedule.schedule.activities.PairEditorActivity.EXTRA_PAIR;
import static com.github.nikololoshka.pepegaschedule.schedule.activities.PairEditorActivity.RESULT_PAIR_ADD;
import static com.github.nikololoshka.pepegaschedule.schedule.activities.PairEditorActivity.RESULT_PAIR_REMOVE;
import static com.github.nikololoshka.pepegaschedule.schedule.activities.ScheduleEditorActivity.EXTRA_SCHEDULE_NAME;


public class ScheduleViewFragment extends Fragment
        implements ScheduleViewAdapter.OnPairCardClickListener,
        LoaderManager.LoaderCallbacks<ScheduleViewLoader.DataView> {

    public static final String ARG_SCHEDULE_PATH = "schedule_path";
    public static final String ARG_SCHEDULE_NAME = "schedule_name";

    private static final int SCHEDULE_VIEW_LOADER = 0;

    private static final int REQUEST_SCHEDULE_NAME = 0;
    private static final int REQUEST_PAIR = 1;
    private static final int REQUEST_SAVE_SCHEDULE = 2;

    private static final int REQUEST_PERMISSION_WRITE_STORAGE = 3;

    private static final String ARG_PAIR_CACHE = "pair_cache";

    private Pair mPairCache;

    private ScheduleViewLoader.DataView mDataView;
    private String mScheduleName;
    private String mSchedulePath;
    private ScheduleViewLoader mViewLoader;

    private View mLoadingView;
    private RecyclerView mRecycler;
    private ScheduleViewAdapter mScheduleViewAdapter;

    public ScheduleViewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mSchedulePath = savedInstanceState.getString(ARG_SCHEDULE_PATH);
            mScheduleName = savedInstanceState.getString(ARG_SCHEDULE_NAME);
            mPairCache = savedInstanceState.getParcelable(ARG_PAIR_CACHE);
        } else if (getArguments() != null) {
            mSchedulePath = getArguments().getString(ARG_SCHEDULE_PATH);
            mScheduleName = getArguments().getString(ARG_SCHEDULE_NAME);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_view, container, false);

        mLoadingView = view.findViewById(R.id.loading_fragment);

        mRecycler = view.findViewById(R.id.recycler_view);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        mScheduleViewAdapter = new ScheduleViewAdapter(this);
        mRecycler.setAdapter(mScheduleViewAdapter);

        mViewLoader = (ScheduleViewLoader) LoaderManager.getInstance(this)
                .initLoader(SCHEDULE_VIEW_LOADER, null, this);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_schedule_view, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_schedule: {
                if (!isEdited()) {
                    Toast.makeText(getActivity(),"Schedule is loading", Toast.LENGTH_SHORT).show();
                    return true;
                }

                Intent intent = new Intent(getActivity(), ScheduleEditorActivity.class);
                intent.putExtra(EXTRA_SCHEDULE_NAME, mScheduleName);
                startActivityForResult(intent, REQUEST_SCHEDULE_NAME);
                return true;
            }
            case R.id.save_schedule: {
                if (!isEdited()) {
                    Toast.makeText(getActivity(),"Schedule is loading", Toast.LENGTH_SHORT).show();
                    return true;
                }

                if (getActivity() != null) {
                    int permissionStatus = ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                        saveSchedule();
                    } else {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_PERMISSION_WRITE_STORAGE);
                    }
                }
                return true;
            }
            case R.id.remove_schedule: {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle(R.string.warning);
                alertDialog.setMessage(getString(R.string.schedule_deleted));
                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                        getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,
                        getString(R.string.yes_continue),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeSchedule();
                            }
                        });
                alertDialog.show();
                return true;
            }
            case R.id.add_pair: {
                onPairCardClicked(null);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_SCHEDULE_NAME, mScheduleName);
        outState.putString(ARG_SCHEDULE_PATH, mSchedulePath);
        outState.putParcelable(ARG_PAIR_CACHE, mPairCache);
    }

    @Override
    public void onPairCardClicked(Pair pair) {
        mPairCache = pair;
        Intent intent = new Intent(getContext(), PairEditorActivity.class);
        intent.putExtra(PairEditorActivity.EXTRA_SCHEDULE, mDataView.schedule);
        intent.putExtra(PairEditorActivity.EXTRA_PAIR, pair);
        startActivityForResult(intent, REQUEST_PAIR);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_WRITE_STORAGE) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                saveSchedule();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            return;
        }

        switch (requestCode) {
            case REQUEST_PAIR: {
                if (resultCode == RESULT_PAIR_ADD) {
                    if (data == null) {
                        return;
                    }

                    Pair pair = data.getParcelableExtra(EXTRA_PAIR);
                    mDataView.schedule.removePair(mPairCache);
                    mDataView.schedule.addPair(pair);
                    SchedulePreference.addChange();

                    updateView();
                    return;
                }

                if (resultCode == RESULT_PAIR_REMOVE) {
                    if (data == null) {
                        return;
                    }

                    Pair pair = data.getParcelableExtra(EXTRA_PAIR);
                    mDataView.schedule.removePair(pair);
                    SchedulePreference.addChange();

                    updateView();
                    return;
                }

                break;
            }
            case REQUEST_SCHEDULE_NAME: {
                if (resultCode != RESULT_OK || data == null || getActivity() == null) {
                    return;
                }

                String newScheduleName = data.getStringExtra(EXTRA_SCHEDULE_NAME);
                File oldFile = new File(SchedulePreference.createPath(getActivity(), mScheduleName));
                File newFile = new File(SchedulePreference.createPath(getActivity(), newScheduleName));

                boolean isRename = false;
                try {
                    FileUtils.moveFile(oldFile, newFile);
                    isRename = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (isRename) {
                    SchedulePreference.remove(getActivity(), mScheduleName);
                    SchedulePreference.add(getActivity(), newScheduleName);

                    if (mScheduleName.equals(SchedulePreference.favorite(getActivity()))) {
                        SchedulePreference.setFavorite(getActivity(), newScheduleName);
                    }

                    mScheduleName = newScheduleName;
                    mSchedulePath = newFile.getAbsolutePath();
                    updateView();

                    if (getView() != null) {
                        Snackbar.make(getView(),
                                getString(R.string.schedule_renamed),
                                Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    if (getView() != null) {
                        Snackbar.make(getView(),
                                getString(R.string.unable_schedule_rename),
                                Snackbar.LENGTH_SHORT).show();
                    }
                }
                break;
            }
            case REQUEST_SAVE_SCHEDULE: {
                if (data == null) {
                    return;
                }

                Uri uri = data.getData();
                if (uri == null) {
                    return;
                }

                try {
                    if (getActivity() != null) {

                        DocumentFile documentFile = DocumentFile.fromTreeUri(getActivity(), uri);
                        if (documentFile == null) {
                            return;
                        }

                        documentFile = documentFile.createFile("application/json",
                                mScheduleName + SchedulePreference.fileExtension());
                        if (documentFile == null) {
                            return;
                        }

                        uri = documentFile.getUri();

                        ContentResolver resolver = getActivity().getContentResolver();
                        OutputStream stream = resolver.openOutputStream(uri);

                        mDataView.schedule.save(stream);

                        Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            }
        }
    }

    private boolean isEdited() {
        return mDataView != null && mDataView.schedule != null;
    }

    private void removeSchedule() {
        if (getActivity() == null) {
            return;
        }

        String path = SchedulePreference.createPath(getActivity(), mScheduleName);
        boolean isRemoved = FileUtils.deleteQuietly(new File(path));
        if (isRemoved) {
            SchedulePreference.remove(getActivity(), mScheduleName);
            getActivity().onBackPressed();
        }
    }

    private void saveSchedule() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_folder)),
                REQUEST_SAVE_SCHEDULE);
    }

    private void updateView() {
        if (getActivity() != null) {
            ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (bar != null)  {
                if (mScheduleName != null) {
                    bar.setTitle(mScheduleName);
                } else {
                    bar.setTitle(R.string.nav_schedule_view);
                }
            }
        }

        mLoadingView.setVisibility(View.VISIBLE);
        mRecycler.setVisibility(View.GONE);
        mViewLoader.update(mSchedulePath, mDataView != null ? mDataView.schedule : null);

        mDataView = null;
    }

    @NonNull
    @Override
    public Loader<ScheduleViewLoader.DataView> onCreateLoader(int id, @Nullable Bundle args) {
        mViewLoader = new ScheduleViewLoader(Objects.requireNonNull(getActivity()));
        updateView();
        return mViewLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ScheduleViewLoader.DataView> loader,
                               ScheduleViewLoader.DataView data) {
        if (data.schedule == null) {
            Toast.makeText(getActivity(), "Error read schedule", Toast.LENGTH_LONG).show();
            return;
        }

        mDataView = data;
        mScheduleViewAdapter.update(mDataView.daysPair, mDataView.daysFormat);

        mLoadingView.setVisibility(View.GONE);
        mRecycler.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ScheduleViewLoader.DataView> loader) {
    }
}
