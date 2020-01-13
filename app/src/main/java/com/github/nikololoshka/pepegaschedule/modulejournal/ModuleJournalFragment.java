package com.github.nikololoshka.pepegaschedule.modulejournal;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.github.nikololoshka.pepegaschedule.BuildConfig;
import com.github.nikololoshka.pepegaschedule.R;
import com.github.nikololoshka.pepegaschedule.settings.ModuleJournalPreference;

public class ModuleJournalFragment extends Fragment {

    private static final String TAG = "ModuleJournalLog";

    public ModuleJournalFragment() {
        super();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_module_journal, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onStart:");
        }

        if (getActivity() != null) {
            if (ModuleJournalPreference.signIn(getActivity())) {
                return;
            }

            NavController controller = Navigation.findNavController(getActivity(), R.id.nav_host);
            controller.navigate(R.id.toModuleJournalLoginFragment);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (getContext() != null) {
            ModuleJournalPreference.setSignIn(getContext(), false);
        }

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onDestroy:");
        }
    }
}
