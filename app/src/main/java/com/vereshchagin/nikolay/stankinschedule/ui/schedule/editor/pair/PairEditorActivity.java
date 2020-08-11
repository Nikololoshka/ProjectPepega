package com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.pair;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.vereshchagin.nikolay.stankinschedule.R;
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.date.DateEditorActivity;
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.pair.list.PairDatesAdaptor;
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.pair.list.SwipeToDeleteCallback;
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.model.Schedule;
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.model.exceptions.InvalidChangePairException;
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.model.pair.ClassroomPair;
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.model.pair.DateItem;
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.model.pair.DatePair;
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.model.pair.LecturerPair;
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.model.pair.Pair;
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.model.pair.SubgroupEnum;
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.model.pair.SubgroupPair;
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.model.pair.TimePair;
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.model.pair.TitlePair;
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.model.pair.TypeEnum;
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.model.pair.TypePair;
import com.vereshchagin.nikolay.stankinschedule.utils.StatefulLayout;
import com.vereshchagin.nikolay.stankinschedule.utils.TextWatcherWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import static com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.date.DateEditorActivity.EXTRA_DATE;
import static com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.date.DateEditorActivity.EXTRA_DATE_LIST;
import static com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.date.DateEditorActivity.RESULT_DATE_ADD;
import static com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.date.DateEditorActivity.RESULT_DATE_REMOVE;

/**
 * Activity для редактирования пары.
 */
public class PairEditorActivity extends AppCompatActivity
        implements View.OnClickListener, PairDatesAdaptor.OnDateItemClickListener {

    public static final String EXTRA_SCHEDULE_PATH = "schedule_path";
    public static final String EXTRA_PAIR = "schedule_pair";

    private static final int REQUEST_ADD_DATE = 0;
    private static final int REQUEST_EDIT_DATE = 1;

    private static final String DATE_CACHE = "date_cache";
    private static final String DATE_LIST = "date_list";
    private static final String OLD_PAIR = "old_pair";
    private static final String NEW_PAIR = "new_pair";

    private StatefulLayout mStatefulLayoutMain;
    private StatefulLayout mStatefulLayoutDates;

    // поля для редактирования
    private AutoCompleteTextView mTitleEdit;
    private AutoCompleteTextView mLecturerEdit;
    private AutoCompleteTextView mClassroomEdit;
    private AppCompatSpinner mTypeSpinner;
    private AppCompatSpinner mSubgroupSpinner;
    private AppCompatSpinner mTimeStartSpinner;
    private AppCompatSpinner mTimeEndSpinner;

    /**
     * ViewModel с расписанием.
     */
    private PairEditorModel mPairEditorViewModel;
    /**
     * Редактируемая дата.
     */
    private DateItem mDateCache;
    /**
     * Список дат.
     */
    private ArrayList<DateItem> mDateItems;
    /**
     * Адаптер для дат.
     */
    private PairDatesAdaptor mPairDatesAdaptor;
    /**
     * Удаляемая пара.
     */
    private Pair mOldPair;
    /**
     * Создаваемая пара.
     */
    private Pair mNewPair;

    /**
     * Создает Intent для вызова PairEditorActivity.
     * @param context контекст.
     * @param schedulePath путь к расписанию.
     * @param editPair пара.
     * @return intent для PairEditorActivity.
     */
    @NonNull
    public static Intent newIntent(@NonNull Context context, @NonNull String schedulePath, @Nullable Pair editPair) {
        Intent intent = new Intent(context, PairEditorActivity.class);
        intent.putExtra(EXTRA_SCHEDULE_PATH, schedulePath);
        intent.putExtra(EXTRA_PAIR, editPair);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pair_editor);

        mStatefulLayoutMain = findViewById(R.id.stateful_layout);
        mStatefulLayoutMain.setAnimation(StatefulLayout.PROPERTY_ANIMATION);
        mStatefulLayoutMain.addXMLViews();

        mStatefulLayoutDates = findViewById(R.id.stateful_layout_dates);
        mStatefulLayoutDates.setAnimation(StatefulLayout.TRANSITION_ANIMATION);
        mStatefulLayoutDates.addXMLViews();

        // установка auto complete в поля
        mTitleEdit = findViewById(R.id.edit_text_title);
        mTitleEdit.setAdapter(new ArrayAdapter<>(this, R.layout.dropdown_item_multiline,
                R.id.dropdown_item, readAutoCompleteStrings(R.raw.titles)));

        mLecturerEdit = findViewById(R.id.edit_text_lecturer);
        mLecturerEdit.setAdapter(new ArrayAdapter<>(this, R.layout.dropdown_item_multiline,
                R.id.dropdown_item, readAutoCompleteStrings(R.raw.lecturers)));

        mClassroomEdit = findViewById(R.id.edit_text_classroom);
        mClassroomEdit.setAdapter(new ArrayAdapter<>(this, R.layout.dropdown_item_multiline,
                R.id.dropdown_item, readAutoCompleteStrings(R.raw.classrooms)));

        MaterialAutoCompleteTextView test = findViewById(R.id.spinner_type2);
        test.setAdapter(new ArrayAdapter<>(this, R.layout.dropdown_item_multiline,
                R.id.dropdown_item, Arrays.asList("Лекция", "Семинар", "Лабораторная")));

        mTypeSpinner = findViewById(R.id.spinner_type);
        mSubgroupSpinner = findViewById(R.id.spinner_subgroup);
        mTimeStartSpinner = findViewById(R.id.spinner_time_start);
        mTimeEndSpinner = findViewById(R.id.spinner_time_end);

        findViewById(R.id.add_date).setOnClickListener(this);

        // Watcher для проверки правильности заполнения поля.
        mTitleEdit.addTextChangedListener(new TextWatcherWrapper() {
            @Override
            public void onTextChanged(@NonNull String s) {
                checkTitleField();
            }
        });

        // Listener для ограничения списка окончания пар, при смене начала пары.
        final List<String> endTimes = Arrays.asList(getResources().getStringArray(R.array.time_end_list));
        mTimeStartSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int newPosition = mTimeEndSpinner.getSelectedItemPosition();

                ArrayAdapter adapter = new ArrayAdapter<>(getBaseContext(),
                        android.R.layout.simple_spinner_item,
                        endTimes.subList(position, endTimes.size()));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                mTimeEndSpinner.setAdapter(adapter);


                if (newPosition > 7 - position) {
                    newPosition = 0;
                }

                mTimeEndSpinner.setSelection(newPosition);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // инициализация полей
        if (savedInstanceState != null) {
            // если было сохраненное состояние
            mDateCache = savedInstanceState.getParcelable(DATE_CACHE);
            mDateItems = savedInstanceState.getParcelableArrayList(DATE_LIST);
            mOldPair = savedInstanceState.getParcelable(OLD_PAIR);
            mNewPair = savedInstanceState.getParcelable(NEW_PAIR);

        } else {
            // инициализация с начала
            Pair pair = getIntent().getParcelableExtra(EXTRA_PAIR);
            mOldPair = pair;

            if (pair != null) {
                mTitleEdit.setText(pair.title().title());
                mLecturerEdit.setText(pair.lecturer().lecturer());
                mClassroomEdit.setText(pair.classroom().classroom());

                setTypeSpinner(pair.type().type());
                setSubgroupSpinner(pair.subgroup().subgroup());
                setTimeSpinners(pair.time());

                mDateItems = pair.date().toList();
            } else {
                mDateItems = new ArrayList<>();
            }
        }

        // адаптер с датами
        String[] frequencies = getResources().getStringArray(R.array.frequency_simple_list);
        String everyWeek = frequencies[0];
        String throughWeek = frequencies[1];
        mPairDatesAdaptor = new PairDatesAdaptor(this, everyWeek, throughWeek);

        RecyclerView recycler = findViewById(R.id.recycler_dates);
        recycler.setAdapter(mPairDatesAdaptor);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        // добавляем swipe для удаления даты
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                final DateItem item = mDateItems.remove(position);
                mPairDatesAdaptor.submitList(mDateItems);

                Snackbar.make(mStatefulLayoutMain,
                        getString(R.string.pair_editor_date_removed), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.undo), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mDateItems.add(position, item);
                                mPairDatesAdaptor.submitList(mDateItems);
                                updateDatesCountView();
                            }
                        })
                        .show();

                updateDatesCountView();
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recycler);

        mPairDatesAdaptor.submitList(mDateItems);
        updateDatesCountView();

        // путь к расписанию
        String schedulePath = getIntent().getStringExtra(EXTRA_SCHEDULE_PATH);
        if (schedulePath == null) {
            return;
        }
        // получение ViewModel
        mPairEditorViewModel = new ViewModelProvider(this,
                new PairEditorModel.Factory(getApplication(), schedulePath))
                .get(PairEditorModel.class);

        mPairEditorViewModel.state().observe(this, new Observer<PairEditorModel.States>() {
            @Override
            public void onChanged(PairEditorModel.States state) {
                stateChanged(state);
            }
        });
    }

    /**
     * Создает списки с авто дополнением.
     * @param id ID ресурса со списком авто дополнения.
     * @return список авто дополнения.
     */
    @NonNull
    private ArrayList<String> readAutoCompleteStrings(@RawRes int id) {
        Scanner scanner = new Scanner(getResources().openRawResource(id));
        ArrayList<String> strings = new ArrayList<>();
        while (scanner.hasNextLine()) {
            strings.add(scanner.nextLine());
        }
        return strings;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(DATE_CACHE, mDateCache);
        outState.putParcelableArrayList(DATE_LIST, mDateItems);

        outState.putParcelable(OLD_PAIR, mOldPair);
        outState.putParcelable(NEW_PAIR, mNewPair);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_pair_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // удалить текущую пару
            case R.id.remove_pair: {
                Schedule schedule = mPairEditorViewModel.schedule().getValue();
                if (schedule != null) {
                    schedule.removePair(mOldPair);
                    mPairEditorViewModel.saveSchedule();
                }
                return true;
            }
            // завершить редактирование пары
            case R.id.apply_pair: {
                savePair();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Проверяет, пусто ли в поле названия предмета.
     * @return true если не пусто, иначе false.
     */
    private boolean checkTitleField() {
        if (mTitleEdit.getText().toString().isEmpty()) {
            mTitleEdit.setError(getString(R.string.pair_editor_empty_title));
            return false;
        }
        mTitleEdit.setError(null);
        return true;
    }

    /**
     * Проверяет, пуст ли список с датами пары.
     * @return true если не пусто, иначе false.
     */
    private boolean checkDatesList() {
        if(mDateItems == null || mDateItems.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.error)
                    .setMessage(getString(R.string.pair_editor_empty_dates_list))
                    .setNeutralButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();

            return false;
        }
        return true;
    }

    /**
     * Сохраняет пару в расписание.
     * @return true если удалось сохранить, иначе false.
     */
    private boolean savePair() {
        String errorMessage;

        try {
            // если выполнены минимальные требования
            if (!checkTitleField() || !checkDatesList()) {
                return false;
            }

            TitlePair title = new TitlePair(mTitleEdit.getText().toString());
            LecturerPair lecturer = new LecturerPair(mLecturerEdit.getText().toString());
            ClassroomPair classroom = new ClassroomPair(mClassroomEdit.getText().toString());
            TypePair type = new TypePair(typeSpinner());
            SubgroupPair subgroup = new SubgroupPair(subgroupSpinner());
            TimePair time = new TimePair(mTimeStartSpinner.getSelectedItem().toString(), mTimeEndSpinner.getSelectedItem().toString());
            DatePair date = new DatePair(mDateItems);

            mNewPair = new Pair(title, lecturer, type, subgroup, classroom, time, date);

            Schedule schedule = mPairEditorViewModel.schedule().getValue();
            if (schedule != null) {
                Schedule.possibleChangePair(schedule, mOldPair, mNewPair);

                schedule.removePair(mOldPair);
                schedule.addPair(mNewPair);
                mPairEditorViewModel.saveSchedule();
            }
            return true;

        } catch (InvalidChangePairException e) {
            errorMessage = getString(R.string.pair_editor_conflict_pair, e.conflictPair());
        }

        // если проблема с добавлением пары
        new AlertDialog.Builder(this)
                .setTitle(R.string.error)
                .setMessage(errorMessage)
                .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

        return false;
    }

    @Override
    public void onClick(View v) {
        // добавить дату
        if (v.getId() == R.id.add_date) {
            Intent intent = new Intent(this, DateEditorActivity.class);
            intent.putParcelableArrayListExtra(EXTRA_DATE_LIST, mDateItems);
            startActivityForResult(intent, REQUEST_ADD_DATE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        String title = mTitleEdit.getText().toString();
        String lecturer = mLecturerEdit.getText().toString();
        String classroom = mClassroomEdit.getText().toString();

        TypePair type = new TypePair(typeSpinner());
        SubgroupPair subgroup = new SubgroupPair(subgroupSpinner());
        TimePair time = new TimePair(mTimeStartSpinner.getSelectedItem().toString(), mTimeEndSpinner.getSelectedItem().toString());

        if (mOldPair == null) {
            // если новая пара и не было каких-либо изменений
            if (title.isEmpty() && lecturer.isEmpty() && classroom.isEmpty() && mDateItems.isEmpty()) {
                super.onBackPressed();
                return;
            }

        } else {
            // если редактируемая пара и нет изменений
            if (mOldPair.title().title().equals(title)
                    && mOldPair.lecturer().lecturer().equals(lecturer)
                    && mOldPair.classroom().classroom().equals(classroom)
                    && mOldPair.type().equals(type)
                    && mOldPair.subgroup().equals(subgroup)
                    && mOldPair.time().equals(time)) {
                // есть даты
                if (!mDateItems.isEmpty()) {
                    DatePair date = new DatePair(mDateItems);
                    if (mOldPair.date().equals(date)) {
                        super.onBackPressed();
                        return;
                    }
                }
            }
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.pair_editor_pair_changed_title)
                .setMessage(R.string.pair_editor_pair_changed_message)
                .setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PairEditorActivity.super.onBackPressed();
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNeutralButton(R.string.pair_editor_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (savePair()) {
                            dialog.dismiss();
                        }
                    }
                })
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // удаление даты пары
        if (resultCode == RESULT_DATE_REMOVE) {
            if (data == null) {
                return;
            }

            DateItem dateItem = data.getParcelableExtra(EXTRA_DATE);
            mDateItems.remove(dateItem);
            mPairDatesAdaptor.submitList(mDateItems);
            updateDatesCountView();
            return;
        }

        // добавление какой-то даты пары
        if (resultCode == RESULT_DATE_ADD) {

            // добавление новой даты пары
            if (requestCode == REQUEST_ADD_DATE) {
                if (data == null) {
                    return;
                }

                DateItem dateItem = data.getParcelableExtra(EXTRA_DATE);
                mDateItems.add(dateItem);
                Collections.sort(mDateItems);
                mPairDatesAdaptor.submitList(mDateItems);
                updateDatesCountView();

                return;
            }

            // редактирование старой даты пары
            if (requestCode == REQUEST_EDIT_DATE) {
                if (data == null) {
                    return;
                }

                DateItem dateItem = data.getParcelableExtra(EXTRA_DATE);
                mDateItems.remove(mDateCache);
                mDateItems.add(dateItem);
                Collections.sort(mDateItems);
                mPairDatesAdaptor.submitList(mDateItems);
            }
        }
    }

    /**
     * Устанавливает поле с типом пары.
     * @param type тип пары.
     */
    private void setTypeSpinner(@NonNull TypeEnum type) {
        switch (type) {
            case LECTURE:
                mTypeSpinner.setSelection(0);
                break;
            case SEMINAR:
                mTypeSpinner.setSelection(1);
                break;
            case LABORATORY:
                mTypeSpinner.setSelection(2);
                break;
        }
    }

    /**
     * Устанавливает поле с подгруппой пары.
     * @param subgroup подгруппа пары.
     */
    private void setSubgroupSpinner(@NonNull SubgroupEnum subgroup) {
        switch (subgroup) {
            case COMMON:
                mSubgroupSpinner.setSelection(0);
                break;
            case A:
                mSubgroupSpinner.setSelection(1);
                break;
            case B:
                mSubgroupSpinner.setSelection(2);
                break;
        }
    }

    /**
     * @return - тип пары, установленная в поле.
     */
    @NonNull
    private TypeEnum typeSpinner() {
        switch (mTypeSpinner.getSelectedItemPosition()) {
            case 0:
                return TypeEnum.LECTURE;
            case 1:
                return TypeEnum.SEMINAR;
            case 2:
                return TypeEnum.LABORATORY;
        }
        throw new RuntimeException("Don't select type pair. Position: " +
                mTypeSpinner.getSelectedItemPosition());
    }

    /**
     * @return - подгруппа пары, установленная в поле.
     */
    @NonNull
    private SubgroupEnum subgroupSpinner() {
        switch (mSubgroupSpinner.getSelectedItemPosition()) {
            case 0:
                return SubgroupEnum.COMMON;
            case 1:
                return SubgroupEnum.A;
            case 2:
                return SubgroupEnum.B;
        }
        throw new RuntimeException("Don't select subgroup pair. Position: " +
                mSubgroupSpinner.getSelectedItemPosition());
    }

    /**
     * Устанавливает значение времени пары в поля.
     * @param timePair - время пары.
     */
    private void setTimeSpinners(@NonNull TimePair timePair) {
        mTimeStartSpinner.setSelection(timePair.startNumber());
        mTimeEndSpinner.setSelection(timePair.duration() - 1 > 0 ? timePair.duration() - 1: 0);
    }

    @Override
    public void onDateItemClicked(int pos) {
        Intent intent = new Intent(this, DateEditorActivity.class);

        mDateCache = mDateItems.get(pos);
        intent.putExtra(EXTRA_DATE, mDateCache);
        intent.putParcelableArrayListExtra(EXTRA_DATE_LIST, mDateItems);
        startActivityForResult(intent, REQUEST_EDIT_DATE);
    }

    /**
     * Изменяет способ отображения дат в редакторе. Если дат нет, то будет показано
     * соответствующие сообщение об этом, иначе отображается список с датами.
     */
    private void updateDatesCountView() {
        if (mDateItems.isEmpty()) {
            mStatefulLayoutDates.setState(R.id.empty_dates);
        } else {
            mStatefulLayoutDates.setState(R.id.recycler_dates);
        }
    }

    /**
     * Вызывается, когда меняется состояние в ViewModel.
     * @param state состояние.
     */
    private void stateChanged(@NonNull PairEditorModel.States state) {
        switch (state) {
            case SUCCESSFULLY_SAVED: {
                Intent intent = new Intent();
                intent.putExtra(EXTRA_PAIR, mNewPair);
                setResult(RESULT_OK, intent);
                super.onBackPressed();
                break;
            }
            case SUCCESSFULLY_LOADED: {
                mStatefulLayoutMain.setState(R.id.pair_editor_content);
                break;
            }
            case LOADING: {
                mStatefulLayoutMain.setLoadState();
                break;
            }
            case ERROR: {
                Toast.makeText(getApplicationContext(), R.string.pair_editor_loading_schedule_error, Toast.LENGTH_LONG).show();
                break;
            }
        }
    }
}
