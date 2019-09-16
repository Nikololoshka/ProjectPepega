package com.github.nikololoshka.pepegaschedule.utils;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.nikololoshka.pepegaschedule.R;


public class LoadingFragment extends Fragment {

    public LoadingFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_loading, container, false);
    }

}
