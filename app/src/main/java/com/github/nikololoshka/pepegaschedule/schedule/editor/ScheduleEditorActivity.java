package com.github.nikololoshka.pepegaschedule.schedule.editor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.nikololoshka.pepegaschedule.R;
import com.github.nikololoshka.pepegaschedule.settings.SchedulePreference;

import java.util.List;

public class ScheduleEditorActivity extends AppCompatActivity {

    public static final String EXTRA_SCHEDULE_NAME = "schedule_name";

    private final List<String> BAN_CHARACTERS = SchedulePreference.banCharacters();

    private EditText mScheduleNameEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_editor);

        @Nullable
        String scheduleName = getIntent().getStringExtra(EXTRA_SCHEDULE_NAME);

        mScheduleNameEdit = findViewById(R.id.schedule_name);
        mScheduleNameEdit.setText(scheduleName);
        mScheduleNameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkNameField();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private boolean checkNameField() {
        String name = mScheduleNameEdit.getText().toString();
        if (name.isEmpty()) {
            mScheduleNameEdit.setError(getString(R.string.empty_field));
            return false;
        }

        for (String character : BAN_CHARACTERS) {
            if (name.contains(character)) {
                mScheduleNameEdit.setError(getString(R.string.not_allowed_character) +
                        ": " + character);
                return false;
            }
        }

        mScheduleNameEdit.setError(null);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_schedule_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.apply_schedule) {
            if (!checkNameField()) {
                return true;
            }

            String schedule = mScheduleNameEdit.getText().toString();

            if (SchedulePreference.contains(this, schedule)) {
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle(R.string.error);
                alertDialog.setMessage(getString(R.string.schedule_exists));
                alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL,
                        getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();

                return true;
            }

            Intent intent = new Intent();
            intent.putExtra(EXTRA_SCHEDULE_NAME, schedule);
            setResult(RESULT_OK, intent);
            onBackPressed();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
