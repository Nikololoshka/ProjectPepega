package com.github.nikololoshka.pepegaschedule.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.github.nikololoshka.pepegaschedule.R;


/**
 * Анимированая кнопка "избранное".
 */
public class FavoriteButton extends AppCompatImageButton {

    private static final String TAG = "FavoriteButtonLog";
    private static final boolean DEBUG = false;

    boolean mIsFavorite;

    Drawable mNotFavorite;
    Drawable mFavorite;
    AnimatedVectorDrawableCompat mFavoriteActivated;

    public FavoriteButton(Context context) {
        super(context);
        initialization();
    }

    public FavoriteButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialization();
    }

    public FavoriteButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialization();
    }

    private void initialization() {
        mIsFavorite = false;

        mNotFavorite = getContext().getDrawable(R.drawable.ic_star_unchecked);
        mFavorite = getContext().getDrawable(R.drawable.ic_star_checked);
        mFavoriteActivated = AnimatedVectorDrawableCompat.create(getContext(), R.drawable.avd_favorite_button);

        setImageDrawable(mNotFavorite);
    }

    /**
     * Устанавливает drawable на view.
     * @param toggle - состояние включить / выключить.
     * @param animate - включить с анимацией.
     */
    public void setToggle(boolean toggle, boolean animate) {
        if (toggle == mIsFavorite) {
            return;
        }

        if (DEBUG) {
            Log.d(TAG, "setToggle: " + toggle);
        }

        mIsFavorite = toggle;

        if (animate) {
            setImageDrawable(mFavoriteActivated);
            mFavoriteActivated.start();
            return;
        }

        Drawable drawable = mIsFavorite ? mFavorite : mNotFavorite;
        setImageDrawable(drawable);
    }
}
