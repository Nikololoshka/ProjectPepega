package com.github.nikololoshka.pepegaschedule.settings.subsection;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.github.nikololoshka.pepegaschedule.R;
import com.github.nikololoshka.pepegaschedule.settings.ApplicationPreference;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SVBar;


/**
 * Диалог для выбора цвета.
 */
public class ColorPickerDialog extends DialogFragment
        implements DialogInterface.OnClickListener, ColorPicker.OnColorChangedListener {

    private static final String TAG = "ColorPickerDialogLog";
    private static final boolean DEBUG = false;

    interface OnColorPickerResult {
        void OnColorResult(int color);
    }

    private static final String ARG_KEY = "arg_key";
    private static final String ARG_OLD_COLOR = "arg_old_color";

    private ColorPicker mColorPicker;
    private EditText mRGBEditor;
    private int mOldColor;
    private boolean mColorEditable;

    @Nullable
    private String mColorKey;

    @Nullable
    private ColorPickerDialog.OnColorPickerResult mCallback;

    public static ColorPickerDialog newInstance(String key, int color, OnColorPickerResult callback) {
        ColorPickerDialog fragment = new ColorPickerDialog();

        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);
        args.putInt(ARG_OLD_COLOR, color);

        fragment.setArguments(args);
        fragment.setCallback(callback);

        return fragment;
    }

    public void setCallback(OnColorPickerResult callback) {
        mCallback = callback;
    }

    private void sendResult(int color) {

        if (mCallback != null) {
            mCallback.OnColorResult(color);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mColorEditable = true;
        if (getArguments() != null) {
            mOldColor = getArguments().getInt(ARG_OLD_COLOR, Color.WHITE);
            mColorKey = getArguments().getString(ARG_KEY);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getContext() == null) {
            return super.onCreateDialog(savedInstanceState);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle(R.string.choose_color);
        builder.setPositiveButton(R.string.ok, this);
        builder.setNeutralButton(R.string.default_, this);
        builder.setNegativeButton(R.string.cancel, this);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        @SuppressLint("InflateParams")
        View view = inflater.inflate(R.layout.picker_color, null);

        mColorPicker = view.findViewById(R.id.color_picker);
        SVBar svBar = view.findViewById(R.id.color_picker_sv_bar);

        mRGBEditor = view.findViewById(R.id.rgb_editor);
        mRGBEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 7 && mColorEditable) {
                    if (DEBUG) {
                        Log.d(TAG, "onTextChanged: " + s);
                    }

                    try {
                        int color = Color.parseColor(s.toString());

                        mColorPicker.setColor(color);
                        mRGBEditor.setError(null);
                    } catch (IllegalArgumentException e) {
                        mRGBEditor.setError(getString(R.string.not_color));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mColorPicker.addSVBar(svBar);
        mColorPicker.setOnColorChangedListener(this);

        mColorPicker.setOldCenterColor(mOldColor);
        mColorPicker.setColor(mOldColor);

        builder.setView(view);

        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case Dialog.BUTTON_POSITIVE:
                try {
                    int color = Color.parseColor(mRGBEditor.getText().toString());
                    sendResult(color);
                } catch (Exception ignored) {
                }
                break;
            case Dialog.BUTTON_NEUTRAL:
                if (getContext() != null && mColorKey != null) {
                    int color = ApplicationPreference.defaultColor(getContext(), mColorKey);
                    sendResult(color);
                }
                break;
        }
    }

    @Override
    public void onColorChanged(int color) {
        String hexColor = String.format("#%06X", (0xFFFFFF & color));

        mColorEditable = false;

        mRGBEditor.setText(hexColor);
        mRGBEditor.setError(null);

        mColorEditable = true;
    }
}