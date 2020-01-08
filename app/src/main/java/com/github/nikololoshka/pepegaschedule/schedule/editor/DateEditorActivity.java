package com.github.nikololoshka.pepegaschedule.schedule.editor;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.nikololoshka.pepegaschedule.R;
import com.github.nikololoshka.pepegaschedule.schedule.pair.DateItem;
import com.github.nikololoshka.pepegaschedule.schedule.pair.DatePair;
import com.github.nikololoshka.pepegaschedule.schedule.pair.DateRange;
import com.github.nikololoshka.pepegaschedule.schedule.pair.DateSingle;
import com.github.nikololoshka.pepegaschedule.schedule.pair.FrequencyEnum;
import com.github.nikololoshka.pepegaschedule.schedule.pair.exceptions.InvalidDateException;
import com.github.nikololoshka.pepegaschedule.schedule.pair.exceptions.InvalidDayOfWeekException;
import com.github.nikololoshka.pepegaschedule.schedule.pair.exceptions.InvalidFrequencyForDateException;
import com.github.nikololoshka.pepegaschedule.utils.StatefulLayout;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


/**
 * Активность для редактирование даты пары.
 */
public class DateEditorActivity extends AppCompatActivity
    implements View.OnClickListener {

    public static final String EXTRA_DATE = "extra_date";
    public static final String EXTRA_DATE_LIST = "extra_date_list";

    public static final int RESULT_DATE_REMOVE = RESULT_FIRST_USER + 1;
    public static final int RESULT_DATE_ADD = RESULT_FIRST_USER + 2;

    private static final String ARG_FREQUENCY = "arg_frequency";
    private static final String ARG_DATE_ITEMS = "arg_date_items";

    private static final int SINGLE_MODE = 0;
    private static final int RANGE_MODE = 1;

    private static final String TAG = "DateEditorActivityTag";

    private Spinner mSpinnerDate;

    private EditText mSingleDateEdit;

    private EditText mRangeDateEditStart;
    private EditText mRangeDateEditEnd;
    private Spinner mSpinnerFrequency;

    private ArrayList<DateItem> mDateItems;
    private int mFrequency = 7;

    private StatefulLayout mStatefulLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_editor);

        mStatefulLayout = findViewById(R.id.stateful_layout);
        mStatefulLayout.setAnimation(false);
        mStatefulLayout.addXMLViews();

        mSingleDateEdit = findViewById(R.id.single_date_edit);
        mSingleDateEdit.addTextChangedListener(new DateWatcher(mSingleDateEdit));

        mRangeDateEditStart = findViewById(R.id.range_date_edit_start);
        mRangeDateEditStart.addTextChangedListener(new DateWatcher(mRangeDateEditStart));
        mRangeDateEditEnd = findViewById(R.id.range_date_edit_end);
        mRangeDateEditEnd.addTextChangedListener(new DateWatcher(mRangeDateEditEnd));

        mSpinnerDate = findViewById(R.id.spinner_date);
        mSpinnerDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case SINGLE_MODE:
                        mStatefulLayout.setState(R.id.single_date_mode);
                        break;
                    case RANGE_MODE:
                        mStatefulLayout.setState(R.id.range_date_mode);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSpinnerFrequency = findViewById(R.id.spinner_frequency);
        mSpinnerFrequency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        mFrequency = 7;
                        break;
                    case 1:
                        mFrequency = 14;
                        break;
                }
                checkRangeDates();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        findViewById(R.id.single_date_picker).setOnClickListener(this);
        findViewById(R.id.range_date_start_picker).setOnClickListener(this);
        findViewById(R.id.range_date_end_picker).setOnClickListener(this);

        // инициализация полей
        if (savedInstanceState != null) {
            mFrequency = savedInstanceState.getInt(ARG_FREQUENCY);
            mDateItems = savedInstanceState.getParcelableArrayList(ARG_DATE_ITEMS);
        } else {
            mDateItems = getIntent().getParcelableArrayListExtra(EXTRA_DATE_LIST);
            DateItem dateItem = getIntent().getParcelableExtra(EXTRA_DATE);
            if (dateItem != null) {
                if (dateItem.frequency() == FrequencyEnum.ONCE) {
                    DateSingle dateSingle = (DateSingle) dateItem;
                    Calendar date = dateSingle.date();
                    if (date != null) {
                        mSingleDateEdit.setText(String.format(Locale.ROOT,
                                "%02d.%02d.%d",
                                date.get(Calendar.DAY_OF_MONTH),
                                date.get(Calendar.MONTH) + 1,
                                date.get(Calendar.YEAR)));
                    }
                } else {
                    mSpinnerDate.setSelection(1);

                    DateRange dateRange = (DateRange) dateItem;

                    FrequencyEnum frequency = dateRange.frequency();
                    mSpinnerFrequency.setSelection(frequency == FrequencyEnum.EVERY ? 0 : 1);

                    Calendar startDate = dateRange.dateStart();
                    Calendar endDate = dateRange.dateEnd();

                    if (startDate != null) {
                        mRangeDateEditStart.setText(String.format(Locale.ROOT,
                                "%02d.%02d.%d",
                                startDate.get(Calendar.DAY_OF_MONTH),
                                startDate.get(Calendar.MONTH) + 1,
                                startDate.get(Calendar.YEAR)));
                    }
                    if (endDate != null) {
                        mRangeDateEditEnd.setText(String.format(Locale.ROOT,
                                "%02d.%02d.%d",
                                endDate.get(Calendar.DAY_OF_MONTH),
                                endDate.get(Calendar.MONTH) + 1,
                                endDate.get(Calendar.YEAR)));
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_date_editor, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_FREQUENCY, mFrequency);
        outState.putParcelableArrayList(ARG_DATE_ITEMS, mDateItems);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // завершение редактирования даты
            case R.id.apply_date:
                String errorMessage;
                try {
                    switch (mSpinnerDate.getSelectedItemPosition()) {
                        case 0:
                            if (!checkSingleDate()) {
                                return true;
                            }

                            DateSingle dateSingle = DateSingle.of(mSingleDateEdit.getText().toString());
                            sendDateResult(dateSingle);
                            break;
                        case 1:
                            if (!checkRangeDates()) {
                                return true;
                            }
                            DateRange dateRange = DateRange.of(mRangeDateEditStart.getText().toString(),
                                    mRangeDateEditEnd.getText().toString(),
                                    mSpinnerFrequency.getSelectedItemPosition()
                                            == 0 ? FrequencyEnum.EVERY : FrequencyEnum.THROUGHOUT);
                            sendDateResult(dateRange);
                            break;
                        default:
                            return true;
                    }
                    return true;
                } catch (InvalidDateException e) {
                    errorMessage = getString(R.string.invalid_date, e.inputDate());
                } catch (InvalidDayOfWeekException e) {
                    Locale locale;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        locale = getResources().getConfiguration().getLocales().get(0);
                    } else {
                        locale = getResources().getConfiguration().locale;
                    }
                    String dayOfWeek = e.date().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, locale);
                    errorMessage = getString(R.string.invalid_day_of_week, dayOfWeek);
                } catch (InvalidFrequencyForDateException e) {
                    errorMessage = getString(R.string.invalid_frequency);
                } catch (Exception e) {
                    errorMessage = "Unchecked error! Please tell me about it\n\n" + e;
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

            // удалить дату
            case R.id.remove_date:
                Intent intent = new Intent();
                intent.putExtra(EXTRA_DATE, getIntent().getParcelableExtra(EXTRA_DATE));
                setResult(RESULT_DATE_REMOVE, intent);
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        // picker'ы дат
        switch (v.getId()) {
            case R.id.single_date_picker:
                createDatePickerDialog(mSingleDateEdit.getText().toString(),
                        new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mSingleDateEdit.setText(String.format(Locale.ROOT,
                                "%02d.%02d.%d", dayOfMonth, month + 1, year));
                    }
                });
                break;
            case R.id.range_date_start_picker:
                createDatePickerDialog(mRangeDateEditStart.getText().toString(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                mRangeDateEditStart.setText(String.format(Locale.ROOT,
                                        "%02d.%02d.%d", dayOfMonth, month + 1, year));
                                checkRangeDates();
                            }
                        });
                break;
            case R.id.range_date_end_picker:
                createDatePickerDialog(mRangeDateEditEnd.getText().toString(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                mRangeDateEditEnd.setText(String.format(Locale.ROOT,
                                        "%02d.%02d.%d", dayOfMonth, month + 1, year));
                                checkRangeDates();
                            }
                        });
                break;
        }
    }

    /**
     * Создает picker даты.
     * @param inputDate - начальная дата для picker'а.
     * @param listener - listener для обработки выбора.
     */
    public void createDatePickerDialog(String inputDate,
                                       DatePickerDialog.OnDateSetListener listener) {
        int year, month, dayOfMonth;

        try {
            DateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.ROOT);
            format.setLenient(false);
            format.parse(inputDate);

            Calendar current = format.getCalendar();
            year = current.get(Calendar.YEAR);
            month = current.get(Calendar.MONTH);
            dayOfMonth = current.get(Calendar.DAY_OF_MONTH);

        } catch (ParseException e) {
            Calendar current = Calendar.getInstance();
            year = current.get(Calendar.YEAR);
            month = current.get(Calendar.MONTH);
            dayOfMonth = current.get(Calendar.DAY_OF_MONTH);
        }

        new DatePickerDialog(this, listener, year, month, dayOfMonth).show();
    }

    /**
     * Проверка даты на правильность.
     * @return - true если все корректно, иначе false.
     */
    public boolean checkSingleDate() {
        try {
            String dateString = mSingleDateEdit.getText().toString();

            DateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.ROOT);
            format.setLenient(false);
            format.parse(dateString);

        } catch (ParseException ignored) {
            mSingleDateEdit.setError(getResources().getString(R.string.enter_valid_date));
            return false;
        }
        return true;
    }

    /**
     * Проверка даты на правильность.
     * @return - true если все корректно, иначе false.
     */
    public boolean checkRangeDates() {
        // TODO: случайно вызывается от mSpinnerFrequency::Listener при смене режима на "диапозон"
        try {
            String startString = mRangeDateEditStart.getText().toString();
            String endString = mRangeDateEditEnd.getText().toString();

            DateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.ROOT);
            format.setLenient(false);

            long diff = format.parse(endString).getTime() - format.parse(startString).getTime();

            if (diff < 0) {
                mRangeDateEditStart.setError(getResources().getString(R.string.date_less_than_another));
                mRangeDateEditEnd.setError(getResources().getString(R.string.date_less_than_another));
                return false;
            } else if (((diff) / 86400000) % mFrequency != 0) {
                mRangeDateEditStart.setError(getResources().getString(R.string.frequency_mismatch));
                mRangeDateEditEnd.setError(getResources().getString(R.string.frequency_mismatch));
                return false;
            } else {
                mRangeDateEditStart.setError(null);
                mRangeDateEditEnd.setError(null);
            }

        } catch (ParseException ignored) {
            mRangeDateEditStart.setError(getResources().getString(R.string.enter_valid_date));
            mRangeDateEditEnd.setError(getResources().getString(R.string.enter_valid_date));
            return false;
        }
        return true;
    }

    public void sendDateResult(DateItem dateItem) {
        if (!DatePair.possibleChangeDate(mDateItems,
                (DateItem) getIntent().getParcelableExtra(EXTRA_DATE), dateItem)) {

            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle(R.string.error);

            ArrayList<DateItem> items = new ArrayList<>(mDateItems);
            items.remove(dateItem);

            alertDialog.setMessage(String.format("%s\n\n%s %s\n%s %s",
                    getString(R.string.impossible_added_date),
                    getString(R.string.added_date),
                    dateItem.toString(),
                    getString(R.string.dates),
                    items.toString()));

            alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL,
                    getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog.show();
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, dateItem);
        setResult(RESULT_DATE_ADD, intent);
        onBackPressed();
    }

    private class DateWatcher implements TextWatcher {

        private EditText mEditText;

        DateWatcher(EditText editText) {
            mEditText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String inputDate = s.toString();
            if (inputDate.isEmpty()) {
                return;
            }
            boolean isValid = true;
            try {
                if (inputDate.length() == 2 && before == 0) {
                    if (Integer.parseInt(inputDate) < 1 || Integer.parseInt(inputDate) > 31) {
                        isValid = false;
                    } else {
                        inputDate += ".";
                        mEditText.setText(inputDate);
                        mEditText.setSelection(inputDate.length());
                    }
                } else if (inputDate.length() == 5 && before == 0) {
                    String month = inputDate.substring(3);
                    if (Integer.parseInt(month) < 1 || Integer.parseInt(month) > 12) {
                        isValid = false;
                    } else {
                        int year = Calendar.getInstance().get(Calendar.YEAR);
                        inputDate += "." + year;
                        mEditText.setText(inputDate);
                        mEditText.setSelection(inputDate.length());
                    }
                } else if (inputDate.length() != 10) {
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                isValid = false;
            }

            if (inputDate.length() == 10) {
                try {
                    DateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.ROOT);
                    format.setLenient(false);
                    format.parse(inputDate);
                } catch (ParseException e) {
                    isValid = false;
                }
            }

            if (!isValid) {
                mEditText.setError(getResources().getString(R.string.enter_valid_date));
            } else {
                mEditText.setError(null);

                // для проверки с 2-ым редактором даты
                if (mSpinnerDate.getSelectedItemPosition() == RANGE_MODE) {
                    checkRangeDates();
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
