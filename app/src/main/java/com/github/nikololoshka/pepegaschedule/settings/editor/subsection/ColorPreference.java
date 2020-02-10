package com.github.nikololoshka.pepegaschedule.settings.editor.subsection;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.github.nikololoshka.pepegaschedule.R;


/**
 * Preference выбора цвета.
 */
public class ColorPreference extends Preference
        implements ColorPickerDialog.OnColorPickerResult {

    private static final String TAG = "ColorPreferenceLog";
    private static final boolean DEBUG = true;

    private static final String COLOR_PICKER_TAG = "color_picker_tag";

    private int mColor;
    private int mStrokeWidth;


    public ColorPreference(Context context) {
        super(context);
        initialization();
    }

    public ColorPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialization();
    }

    public ColorPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialization();
    }

    public ColorPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialization();
    }

    private void initialization() {
        mStrokeWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2,
                getContext().getResources().getDisplayMetrics());
        setWidgetLayoutResource(R.layout.preference_color);
    }

    @Override
    public void onAttached() {
        super.onAttached();

        if (getContext() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getContext();
            Fragment dialogFragment = activity.getSupportFragmentManager()
                    .findFragmentByTag(COLOR_PICKER_TAG + getKey());

            if (dialogFragment != null) {
                ColorPickerDialog dialog = (ColorPickerDialog) dialogFragment;
                dialog.setCallback(this);

                if (DEBUG) {
                    Log.d(TAG, "onAttached: " + dialog);
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        ImageView imageView = (ImageView) holder.findViewById(R.id.color_view);

        GradientDrawable drawable;

        Drawable currentDrawable = imageView.getDrawable();
        if (currentDrawable != null) {
            drawable = (GradientDrawable) currentDrawable;
        }
        else {
            drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
        }


        int border = Color.rgb(Color.red(mColor) * 192 / 256,
                Color.green(mColor) * 192 / 256,
                Color.blue(mColor) * 192 / 256);

        drawable.setColor(mColor);
        drawable.setStroke(mStrokeWidth, border);

        imageView.setImageDrawable(drawable);
    }

    @Override
    protected void onClick() {
        ColorPickerDialog dialog = ColorPickerDialog.newInstance(getKey(), mColor, this);

        if (DEBUG) {
            Log.d(TAG, "onClick: color " + mColor);
        }

        if (getContext() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getContext();
            dialog.show(activity.getSupportFragmentManager(), COLOR_PICKER_TAG + getKey());
        }
    }

    @Override
    protected void onSetInitialValue(@Nullable Object defaultValue) {
        if (defaultValue instanceof Integer) {
            mColor = (Integer) defaultValue;
        } else {
            mColor = getPersistedInt(Color.WHITE);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, Color.WHITE);
    }

    @Override
    public void OnColorResult(int color) {
        if (DEBUG) {
            Log.d(TAG, "OnColorResult: " + color);
        }
        mColor = color;

        persistInt(color);
        notifyChanged();
    }
}
