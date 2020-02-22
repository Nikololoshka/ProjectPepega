package com.vereshchagin.nikolay.stankinschedule.about;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.vereshchagin.nikolay.stankinschedule.BuildConfig;
import com.vereshchagin.nikolay.stankinschedule.R;

/**
 * Фрагмент вкладки о приложении.
 */
public class AboutFragment extends Fragment {

    public AboutFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        // установка версии приложения
        TextView version = view.findViewById(R.id.app_version);
        version.setText(String.format("Version: %s", BuildConfig.VERSION_NAME));

        return view;
    }
}
