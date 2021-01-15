package com.vereshchagin.nikolay.stankinschedule.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.utils.DrawableUtils

/**
 * Стандартный TextView с заглушкой с shimmer эффектом,
 * если не установлен текст.
 */
class ShimmerTextView : AppCompatTextView {

    private val shimmerDrawable = DrawableUtils.createShimmerDrawable()
    private var placeholderCount = 0

    constructor(
        context: Context
    ) : super(context) {
        initialize(context, null, 0)
    }

    constructor(
        context: Context,
        attrs: AttributeSet
    ) : super(context, attrs) {
        initialize(context, attrs, 0)
    }

    constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttrs: Int
    ) : super(context, attrs, defStyleAttrs) {
        initialize(context, attrs, defStyleAttrs)
    }

    /**
     * Инициализация TextView.
     */
    private fun initialize(context: Context, attrs: AttributeSet?, defStyleAttrs: Int) {
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.ShimmerTextView, defStyleAttrs, 0
        )
        try {
            val countAttribute = R.styleable.ShimmerTextView_stv_count
            if (typedArray.hasValue(countAttribute)) {
                placeholderCount = typedArray.getInteger(countAttribute, 0)
            }

        } finally {
            typedArray.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val currentText = text
        if (currentText == null || currentText.isEmpty()) {
            val fontMetrics = paint.fontMetrics
            val width = paint.measureText("A") * placeholderCount
            val height = fontMetrics.bottom - fontMetrics.top + fontMetrics.leading

            setMeasuredDimension(
                width.toInt(),
                height.toInt()
            )

        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
        background = if (text != null && text.isNotEmpty()) null else shimmerDrawable
    }
}