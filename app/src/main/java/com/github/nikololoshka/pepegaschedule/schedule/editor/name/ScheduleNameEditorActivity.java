package com.github.nikololoshka.pepegaschedule.schedule.editor.name;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.nikololoshka.pepegaschedule.R;
import com.github.nikololoshka.pepegaschedule.settings.SchedulePreference;
import com.github.nikololoshka.pepegaschedule.utils.TextWatcherWrapper;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

/**
 * Activity для редактирования названия расписания.
 */
public class ScheduleNameEditorActivity extends AppCompatActivity {

    public static final String EXTRA_SCHEDULE_NAME = "schedule_name";

    private final List<String> BAN_CHARACTERS = SchedulePreference.banCharacters();

    /**
     * Поле с название расписания.
     */
    private EditText mScheduleNameEdit;
    /**
     * Слой с полей с названием расписанием.
     */
    private TextInputLayout mScheduleNameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_editor);

        @Nullable
        String scheduleName = getIntent().getStringExtra(EXTRA_SCHEDULE_NAME);

        mScheduleNameLayout = findViewById(R.id.schedule_name_layout);

        mScheduleNameEdit = findViewById(R.id.schedule_name);
        mScheduleNameEdit.setText(scheduleName);
        mScheduleNameEdit.addTextChangedListener(new TextWatcherWrapper() {
            @Override
            public void onTextChanged(@NonNull String s) {
                checkNameField();
            }
        });
    }

    /**
     * Проверяет поле на правильность. Не пусто ли и не содержит не допустимых символов.
     * @return true если правильно заполнено, иначе false.
     */
    private boolean checkNameField() {
        String name = mScheduleNameEdit.getText().toString();
        if (name.isEmpty()) {
            mScheduleNameLayout.setError(getString(R.string.schedule_editor_empty_name));
            return false;
        }

        for (String character : BAN_CHARACTERS) {
            if (name.contains(character)) {
                mScheduleNameLayout.setError(getString(R.string.schedule_editor_not_allowed_character) +
                        ": " + character);
                return false;
            }
        }

        mScheduleNameLayout.setError(null);
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
        // подтвердить изменение
        if (item.getItemId() == R.id.apply_schedule) {
            // правильно заполнено
            if (!checkNameField()) {
                return true;
            }

            // такое название уже есть
            String schedule = mScheduleNameEdit.getText().toString();
            if (SchedulePreference.contains(this, schedule)) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.error)
                        .setMessage(getString(R.string.schedule_editor_exists))
                        .setNeutralButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();

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
