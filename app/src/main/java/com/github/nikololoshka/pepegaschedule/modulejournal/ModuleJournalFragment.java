package com.github.nikololoshka.pepegaschedule.modulejournal;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.nikololoshka.pepegaschedule.R;


public class ModuleJournalFragment extends Fragment {
    public ModuleJournalFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_module_journal, container, false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (getActivity() != null) {
            InputMethodManager manager = (InputMethodManager) getActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);

            View currentFocusedView = getActivity().getCurrentFocus();
            if (currentFocusedView != null) {
                manager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
