package com.vereshchagin.nikolay.stankinschedule.view

import android.content.Context
import android.util.AttributeSet
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

/**
 * Реализация RecyclerView, которая будет скрывать клавиатуру при прокрутке.
 */
class KeyboardDismissingRecyclerView : RecyclerView {

    private var scrollListener: OnScrollListener? = null
    private val inputMethodManager = ContextCompat.getSystemService(
        context, InputMethodManager::class.java
    )

    constructor(
        context: Context,
    ) : super(context) {
        initialize()
    }

    constructor(
        context: Context,
        attrs: AttributeSet,
    ) : super(context, attrs) {
        initialize()
    }

    constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttrs: Int,
    ) : super(context, attrs, defStyleAttrs) {
        initialize()
    }


    private fun initialize() {
        scrollListener = object : OnScrollListener() {
            var isKeyboardDismissedByScroll = false
            override fun onScrollStateChanged(recyclerView: RecyclerView, state: Int) {
                when (state) {
                    SCROLL_STATE_DRAGGING -> if (!isKeyboardDismissedByScroll) {
                        hideKeyboard()
                        isKeyboardDismissedByScroll = !isKeyboardDismissedByScroll
                    }
                    SCROLL_STATE_IDLE -> isKeyboardDismissedByScroll = false
                }
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        scrollListener?.let { addOnScrollListener(it) }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        scrollListener?.let { removeOnScrollListener(it) }
    }

    private fun hideKeyboard() {
        inputMethodManager?.hideSoftInputFromWindow(windowToken, 0)
        clearFocus()
    }
}