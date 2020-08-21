package com.vereshchagin.nikolay.stankinschedule.ui.schedule.editor.date;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.vereshchagin.nikolay.stankinschedule.R;
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.model.exceptions.InvalidDateFrequencyException;
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.model.exceptions.InvalidDateParseException;
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.model.exceptions.InvalidDayOfWeekException;
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.model.pair.DateItem;
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.model.pair.DatePair;
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.model.pair.DateRange;
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.model.pair.DateSingle;
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.model.pair.FrequencyEnum;
import com.vereshchagin.nikolay.stankinschedule.utils.CommonUtils;
import com.vereshchagin.nikolay.stankinschedule.utils.StatefulLayout;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;


/**
 * Активность для редактирование даты пары.
 */
@Deprecated
public class DateEditorActivity extends AppCompatActivity
    implements View.OnClickListener {

    public static final String EXTRA_DATE = "extra_date";
    public static final String EXTRA_DATE_LIST = "extra_date_list";

    public static final int RESULT_DATE_REMOVE = RESULT_FIRST_USER + 1;
    public static final int RESULT_DATE_ADD = RESULT_FIRST_USER + 2;

    private static final String FREQUENCY = "frequency";
    private static final String DATE_ITEMS = "date_items";

    private static final int SINGLE_MODE = 0;
    private static final int RANGE_MODE = 1;

    private static final String TAG = "DateEditorActivityLog";

    /**
     * Выбор типа даты.
     */
    private Spinner mSpinnerDate;

    // единождная дата
    private EditText mSingleDateEdit;

    // дата с дипазоном
    private EditText mRangeDateEditStart;
    private EditText mRangeDateEditEnd;
    private Spinner mSpinnerFrequency;

    /**
     * Список с датами в паре.
     */
    private ArrayList<DateItem> mDateItems;
    /**
     * Текущая периодичность в дате с диапозоном.
     */
    private int mFrequency = 7;

    private StatefulLayout mStatefulLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_editor);

        mStatefulLayout = findViewById(R.id.stateful_layout);
        mStatefulLayout.setAnimation(StatefulLayout.PROPERTY_ANIMATION);
        mStatefulLayout.addXMLViews();

        mSingleDateEdit.addTextChangedListener(new DateWatcher(mSingleDateEdit));

        mRangeDateEditStart.addTextChangedListener(new DateWatcher(mRangeDateEditStart));
        mRangeDateEditEnd.addTextChangedListener(new DateWatcher(mRangeDateEditEnd));

        mSpinnerDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case SINGLE_MODE:
                        break;
                    case RANGE_MODE:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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


        // инициализация полей
        if (savedInstanceState != null) {
            mFrequency = savedInstanceState.getInt(FREQUENCY);
            mDateItems = savedInstanceState.getParcelableArrayList(DATE_ITEMS);

        } else {
            mDateItems = getIntent().getParcelableArrayListExtra(EXTRA_DATE_LIST);
            DateItem dateItem = getIntent().getParcelableExtra(EXTRA_DATE);

            if (dateItem != null) {
                // единождная пара
                if (dateItem.frequency() == FrequencyEnum.ONCE) {
                    mSpinnerDate.setSelection(SINGLE_MODE);

                    DateSingle dateSingle = (DateSingle) dateItem;

                    mSingleDateEdit.setText(CommonUtils.dateToString(dateSingle.date(),
                            "dd.MM.yyyy", CommonUtils.locale(this)));
                } else {
                    // дата с диапазоном
                    mSpinnerDate.setSelection(RANGE_MODE);

                    DateRange dateRange = (DateRange) dateItem;

                    FrequencyEnum frequency = dateRange.frequency();
                    mSpinnerFrequency.setSelection(frequency == FrequencyEnum.EVERY ? 0 : 1);

                    Calendar startDate = dateRange.firstDate();
                    Calendar endDate = dateRange.lastDate();

                    mRangeDateEditStart.setText(CommonUtils.dateToString(startDate,
                            "dd.MM.yyyy", CommonUtils.locale(this)));

                    mRangeDateEditEnd.setText(CommonUtils.dateToString(endDate,
                            "dd.MM.yyyy", CommonUtils.locale(this)));
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
        outState.putInt(FREQUENCY, mFrequency);
        outState.putParcelableArrayList(DATE_ITEMS, mDateItems);
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
            case R.id.apply_date: {
                String errorMessage;
                try {
                    switch (mSpinnerDate.getSelectedItemPosition()) {
                        // единождная дата
                        case SINGLE_MODE: {
                            if (!checkSingleDate()) {
                                return true;
                            }

                            DateSingle dateSingle = new DateSingle(mSingleDateEdit.getText().toString(), "dd.MM.yyyy");

                            sendDateResult(dateSingle);
                            break;
                        }
                        // дата с диапазоном
                        case RANGE_MODE: {
                            if (!checkRangeDates()) {
                                return true;
                            }

                            DateRange dateRange = new DateRange(
                                    mRangeDateEditStart.getText().toString(),
                                    mRangeDateEditEnd.getText().toString(),
                                    mSpinnerFrequency.getSelectedItemPosition() == 0 ? FrequencyEnum.EVERY : FrequencyEnum.THROUGHOUT,
                                    "dd.MM.yyyy");

                            sendDateResult(dateRange);
                            break;
                        }
                    }

                    return true;

                    // не удалось распарсить даты
                } catch (InvalidDateParseException e) {
                    errorMessage = getString(R.string.date_editor_invalid_date, e.parseDate());

                    // неправильный день недели
                } catch (InvalidDayOfWeekException e) {
                    errorMessage = getString(R.string.date_editor_invalid_day_of_week);

                    // неправильная периодичность даты
                } catch (InvalidDateFrequencyException e) {
                    errorMessage = getString(R.string.date_editor_invalid_frequency);

                }

                new AlertDialog.Builder(this)
                        .setTitle(R.string.error)
                        .setMessage(errorMessage)
                        .setNeutralButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();

                return true;
            }
            // удалить дату
            case R.id.remove_date: {
                Intent intent = new Intent();
                DateItem removeDate = getIntent().getParcelableExtra(EXTRA_DATE);
                intent.putExtra(EXTRA_DATE, removeDate);
                setResult(RESULT_DATE_REMOVE, intent);

                onBackPressed();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        // picker'ы дат
        switch (v.getId()) {
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
            DateFormat format = new SimpleDateFormat("dd.MM.yyyy", CommonUtils.locale(this));
            format.setLenient(false);
            format.parse(inputDate);

            Calendar current = format.getCalendar();
            year = current.get(Calendar.YEAR);
            month = current.get(Calendar.MONTH);
            dayOfMonth = current.get(Calendar.DAY_OF_MONTH);

        } catch (ParseException e) {
            Calendar current = new GregorianCalendar();
            year = current.get(Calendar.YEAR);
            month = current.get(Calendar.MONTH);
            dayOfMonth = current.get(Calendar.DAY_OF_MONTH);
        }

        new DatePickerDialog(this, listener, year, month, dayOfMonth)
                .show();
    }

    /**
     * Проверка даты на правильность.
     * @return - true если все корректно, иначе false.
     */
    public boolean checkSingleDate() {
        try {
            String dateString = mSingleDateEdit.getText().toString();

            DateFormat format = new SimpleDateFormat("dd.MM.yyyy", CommonUtils.locale(this));
            format.setLenient(false);
            format.parse(dateString);

        } catch (ParseException ignored) {
            mSingleDateEdit.setError(getResources().getString(R.string.date_editor_enter_valid_date));
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

            DateFormat format = new SimpleDateFormat("dd.MM.yyyy", CommonUtils.locale(this));
            format.setLenient(false);

            long diff = format.parse(endString).getTime() - format.parse(startString).getTime();

            if (diff < 0) {
                mRangeDateEditStart.setError(getResources().getString(R.string.date_editor_less_than_another));
                mRangeDateEditEnd.setError(getResources().getString(R.string.date_editor_less_than_another));
                return false;
            } else if (diff == 0 || (((diff) / 86400000) % mFrequency != 0)) {
                mRangeDateEditStart.setError(getResources().getString(R.string.date_editor_frequency_mismatch));
                mRangeDateEditEnd.setError(getResources().getString(R.string.date_editor_frequency_mismatch));
                return false;
            } else {
                mRangeDateEditStart.setError(null);
                mRangeDateEditEnd.setError(null);
            }

        } catch (ParseException ignored) {
            mRangeDateEditStart.setError(getResources().getString(R.string.date_editor_enter_valid_date));
            mRangeDateEditEnd.setError(getResources().getString(R.string.date_editor_enter_valid_date));
            return false;
        }
        return true;
    }

    /**
     * Отправляет результат редактирования даты.
     * @param newDate дата.
     */
    public void sendDateResult(@NonNull DateItem newDate) {
        DateItem oldDate = getIntent().getParcelableExtra(EXTRA_DATE);
        if (!DatePair.possibleChangeDate(mDateItems, oldDate, newDate)) {
            // если не удается заменить

            ArrayList<DateItem> items = new ArrayList<>(mDateItems);
            // items.remove(newDate);

            new AlertDialog.Builder(this)
                    .setTitle(R.string.error)
                    .setMessage(String.format("%s\n\n%s %s\n%s %s",
                    getString(R.string.date_editor_impossible_added_date),
                    getString(R.string.date_editor_added_date),
                    newDate.toString(),
                    getString(R.string.date_editor_dates),
                    items.toString()))
                    .setNeutralButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();

            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, newDate);
        setResult(RESULT_DATE_ADD, intent);

        onBackPressed();
    }

    /**
     * Watcher для редактирования даты.
     */
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
                mEditText.setError(getResources().getString(R.string.date_editor_enter_valid_date));
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
