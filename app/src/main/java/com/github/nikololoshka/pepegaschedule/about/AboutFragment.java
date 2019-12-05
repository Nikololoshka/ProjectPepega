package com.github.nikololoshka.pepegaschedule.about;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.github.nikololoshka.pepegaschedule.BuildConfig;
import com.github.nikololoshka.pepegaschedule.R;

import java.io.InputStream;
import java.util.Scanner;

import hakobastvatsatryan.DropdownTextView;

/**
 * Фрагмент вкладки о приложении.
 */
public class AboutFragment extends Fragment {

    public AboutFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        // установка версии приложения
        TextView version = view.findViewById(R.id.app_version);
        version.setText(String.format("Version: %s", BuildConfig.VERSION_NAME));

        // загрузка debug информации
        if (getContext() != null) {
            SharedPreferences applicationPreferences = PreferenceManager.
                    getDefaultSharedPreferences(getContext());

            SharedPreferences schedulePreferences =
                    getContext().getSharedPreferences("schedule_preference", Context.MODE_PRIVATE);

            DropdownTextView changelogView = view.findViewById(R.id.debug_view);
            changelogView.setTitleText("Debug");

            changelogView.setContentText(
                    String.valueOf(schedulePreferences.getAll()) +
                            applicationPreferences.getAll());
        }

        // загрузка changelog'а
        if (getContext() != null) {
            InputStream stream = getContext().getResources().openRawResource(R.raw.changelog);
            Scanner scanner = new Scanner(stream);

            StringBuilder builder = new StringBuilder();
            while (scanner.hasNextLine()) {
                builder.append(scanner.nextLine()).append("\n");
            }

            DropdownTextView changelogView = view.findViewById(R.id.changelog_view);
            changelogView.setTitleText(R.string.changelog);
            changelogView.setContentText(builder.toString());
        }

        return view;
    }
}
