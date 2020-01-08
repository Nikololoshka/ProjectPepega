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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nikololoshka.pepegaschedule.R;
import com.github.nikololoshka.pepegaschedule.schedule.Schedule;
import com.github.nikololoshka.pepegaschedule.schedule.exceptions.InvalidAddPairException;
import com.github.nikololoshka.pepegaschedule.schedule.pair.ClassroomPair;
import com.github.nikololoshka.pepegaschedule.schedule.pair.DateItem;
import com.github.nikololoshka.pepegaschedule.schedule.pair.DatePair;
import com.github.nikololoshka.pepegaschedule.schedule.pair.LecturerPair;
import com.github.nikololoshka.pepegaschedule.schedule.pair.Pair;
import com.github.nikololoshka.pepegaschedule.schedule.pair.SubgroupEnum;
import com.github.nikololoshka.pepegaschedule.schedule.pair.SubgroupPair;
import com.github.nikololoshka.pepegaschedule.schedule.pair.TimePair;
import com.github.nikololoshka.pepegaschedule.schedule.pair.TitlePair;
import com.github.nikololoshka.pepegaschedule.schedule.pair.TypeEnum;
import com.github.nikololoshka.pepegaschedule.schedule.pair.TypePair;
import com.github.nikololoshka.pepegaschedule.utils.StatefulLayout;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

import static com.github.nikololoshka.pepegaschedule.schedule.editor.DateEditorActivity.EXTRA_DATE;
import static com.github.nikololoshka.pepegaschedule.schedule.editor.DateEditorActivity.EXTRA_DATE_LIST;
import static com.github.nikololoshka.pepegaschedule.schedule.editor.DateEditorActivity.RESULT_DATE_ADD;
import static com.github.nikololoshka.pepegaschedule.schedule.editor.DateEditorActivity.RESULT_DATE_REMOVE;

/**
 * Активность для редактирование пары.
 */
public class PairEditorActivity extends AppCompatActivity
        implements View.OnClickListener, PairEditorAdaptor.OnItemClickListener {

    public static final String EXTRA_SCHEDULE = "schedule_extra";
    public static final String EXTRA_PAIR = "schedule_pair";

    public static final int RESULT_PAIR_REMOVE = RESULT_FIRST_USER + 1;
    public static final int RESULT_PAIR_ADD = RESULT_FIRST_USER + 2;
    public static final int RESULT_PAIR_EDIT = RESULT_FIRST_USER + 3;

    private static final int REQUEST_ADD_DATE = 0;
    private static final int REQUEST_EDIT_DATE = 1;

    private static final String ARG_DATE_CACHE = "arg_date_cache";
    private static final String ARG_DATE_LIST = "arg_date_list";

    private AutoCompleteTextView mTitleEdit;
    private AutoCompleteTextView mLecturerEdit;
    private AutoCompleteTextView mClassroomEdit;
    private AppCompatSpinner mTypeSpinner;
    private AppCompatSpinner mSubgroupSpinner;
    private AppCompatSpinner mTimeStartSpinner;
    private AppCompatSpinner mTimeEndSpinner;

    private DateItem mDateCache;
    private ArrayList<DateItem> mDateItems;
    private ArrayList<String> mTimeEndList;

    private PairEditorAdaptor mPairEditorAdaptor;
    private StatefulLayout mStatefulLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pair_editor);

        mTitleEdit = findViewById(R.id.edit_text_title);
        mTitleEdit.setAdapter(new ArrayAdapter<>(this,
                R.layout.dropdown_item_multiline, R.id.dropdown_item,
                readAutoCompleteStrings(R.raw.titles)));

        mTitleEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkTitleField();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mLecturerEdit = findViewById(R.id.edit_text_lecturer);
        mLecturerEdit.setAdapter(new ArrayAdapter<>(this,
                R.layout.dropdown_item_multiline, R.id.dropdown_item,
                readAutoCompleteStrings(R.raw.lecturers)));

        mClassroomEdit = findViewById(R.id.edit_text_classroom);
        mClassroomEdit.setAdapter(new ArrayAdapter<>(this,
                R.layout.dropdown_item_multiline, R.id.dropdown_item,
                readAutoCompleteStrings(R.raw.classrooms)));

        mTypeSpinner = findViewById(R.id.spinner_type);
        mSubgroupSpinner = findViewById(R.id.spinner_subgroup);

        mTimeStartSpinner = findViewById(R.id.spinner_time_start);
        mTimeStartSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int newPosition = mTimeEndSpinner.getSelectedItemPosition();

                ArrayAdapter adapter = new ArrayAdapter<>(getBaseContext(),
                        android.R.layout.simple_spinner_item,
                        mTimeEndList.subList(position, mTimeEndList.size()));
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
        mTimeEndSpinner = findViewById(R.id.spinner_time_end);

        findViewById(R.id.add_date).setOnClickListener(this);
        mTimeEndList = new ArrayList<>(Arrays.asList(getResources()
                .getStringArray(R.array.time_end_list)));

        // инициализация полей
        Pair pair = getIntent().getParcelableExtra(EXTRA_PAIR);

        if (savedInstanceState != null) {
            mDateCache = savedInstanceState.getParcelable(ARG_DATE_CACHE);
            mDateItems = savedInstanceState.getParcelableArrayList(ARG_DATE_LIST);
        } else if (pair != null) {
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

        mStatefulLayout = findViewById(R.id.stateful_layout);
        mStatefulLayout.addXMLViews();

        RecyclerView recycler = findViewById(R.id.recycler_dates);
        mPairEditorAdaptor = new PairEditorAdaptor(mDateItems, this);
        recycler.setAdapter(mPairEditorAdaptor);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        // добавляем swipe для удаления даты
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                final DateItem item = mDateItems.remove(position);

                mPairEditorAdaptor.notifyItemRemoved(position);

                Snackbar.make(findViewById(R.id.pair_edit_layout),
                        getString(R.string.date_was_removed), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.undo), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mDateItems.add(position, item);
                                mPairEditorAdaptor.notifyItemInserted(position);
                                updateDatesCountView();
                            }
                        })
                        .show();

                updateDatesCountView();
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recycler);

        updateDatesCountView();
    }

    /**
     * Создает списки с автодополнением.
     * @param id - ID ресурса с списком автодополнения.
     * @return - список автодополнения.
     */
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
        outState.putParcelable(ARG_DATE_CACHE, mDateCache);
        outState.putParcelableArrayList(ARG_DATE_LIST, mDateItems);
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
            // удалить текущию пару
            case R.id.remove_pair: {
                Intent intent = new Intent();
                intent.putExtra(EXTRA_PAIR, getIntent().getParcelableExtra(EXTRA_PAIR));
                setResult(RESULT_PAIR_REMOVE, intent);
                onBackPressed();
                return true;
            }
            // завершить редактирование пары
            case R.id.apply_pair: {
                String errorMessage;

                try {
                    // если выполнены минимальные требования
                    if (!checkTitleField() || !checkDatesList()) {
                        return true;
                    }

                    Pair newPair = new Pair();
                    newPair.setTitle(TitlePair.of(mTitleEdit.getText().toString()));
                    newPair.setLecturer(LecturerPair.of(mLecturerEdit.getText().toString()));
                    newPair.setClassroom(ClassroomPair.of(mClassroomEdit.getText().toString()));
                    newPair.setType(TypePair.of(typeSpinner()));
                    newPair.setSubgroup(SubgroupPair.of(subgroupSpinner()));
                    newPair.setTime(TimePair.of(mTimeStartSpinner.getSelectedItem().toString(),
                            mTimeEndSpinner.getSelectedItem().toString()));
                    newPair.setDate(DatePair.of(mDateItems));

                    Schedule schedule = getIntent().getParcelableExtra(EXTRA_SCHEDULE);
                    Pair removedPair = getIntent().getParcelableExtra(EXTRA_PAIR);

                    // проверка, на возможность добавления пары
                    Schedule.possibleChangePair(schedule, removedPair, newPair);

                    Intent intent = new Intent();
                    intent.putExtra(EXTRA_PAIR, newPair);
                    setResult(removedPair != null ? RESULT_PAIR_EDIT : RESULT_PAIR_ADD, intent);
                    onBackPressed();

                    return true;
                } catch (InvalidAddPairException e) {
                    errorMessage = e.toString();
                } catch (Exception e) {
                    errorMessage = "Unknown error: " + e;
                }

                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle(R.string.error);
                alertDialog.setMessage(errorMessage);
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
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Проверяет, пусто ли в поле с название предмета.
     * @return - true если не пусто, иначе false.
     */
    private boolean checkTitleField() {
        if (mTitleEdit.getText().toString().isEmpty()) {
            mTitleEdit.setError(getString(R.string.empty_field));
            return false;
        }
        mTitleEdit.setError(null);
        return true;
    }

    /**
     * Проверяет, пуст ли список с датами пары.
     * @return - true если не пусто, иначе false.
     */
    private boolean checkDatesList() {
        if(mDateItems == null || mDateItems.isEmpty()) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle(R.string.error);
            alertDialog.setMessage(getString(R.string.empty_dates_list));
            alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL,
                    getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();

            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // удаление даты пары
        if (resultCode == RESULT_DATE_REMOVE) {
            if (data == null) {
                return;
            }

            DateItem dateItem = data.getParcelableExtra(EXTRA_DATE);
            mDateItems.remove(dateItem);
            mPairEditorAdaptor.notifyDataSetChanged();
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
                mPairEditorAdaptor.notifyDataSetChanged();
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
                mPairEditorAdaptor.notifyDataSetChanged();
            }
        }
    }

    /**
     * Устанавливает поле с типом пары.
     * @param type - тип пары.
     */
    private void setTypeSpinner(TypeEnum type) {
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
     * Устанавлтвает поле с подгруппой пары.
     * @param subgroup - подгруппа пары.
     */
    private void setSubgroupSpinner(SubgroupEnum subgroup) {
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
    private void setTimeSpinners(TimePair timePair) {
        mTimeEndSpinner.setSelection(timePair.duration() - 1 > 0 ? timePair.duration() - 1: 0);
        mTimeStartSpinner.setSelection(timePair.startNumber());
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
     * соотвествующие сообщение об этом, иначе отображается список с датами.
     */
    private void updateDatesCountView() {
        if (mDateItems.isEmpty()) {
            mStatefulLayout.setState(R.id.empty_dates);
        } else {
            mStatefulLayout.setState(R.id.recycler_dates);
        }
    }
}
