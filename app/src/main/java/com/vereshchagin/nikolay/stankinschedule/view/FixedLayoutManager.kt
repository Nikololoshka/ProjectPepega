package com.vereshchagin.nikolay.stankinschedule.view

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Аналогичен LinearLayoutManager, но можно задать
 * фиксированное количество отображаемых элементов.
 */
class FixedLayoutManager : LinearLayoutManager {

    /**
     * Максимально количество отображаемых элементов.
     */
    var fixedCount = 3
        set(value) {
            field = if (value < 0)  0 else value
        }

    constructor(
        context: Context
    ) : super(context)

    constructor(
        context: Context?, orientation: Int = RecyclerView.VERTICAL, reverseLayout: Boolean = false
    ) : super(context, orientation, reverseLayout)

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun setMeasuredDimension(widthSize: Int, heightSize: Int) {
        val maxHeight = computeHeight()
        // Log.d("MyLog", "setMeasuredDimension: $maxHeight, $heightSize")

        if (maxHeight > 0) {
            super.setMeasuredDimension(widthSize, maxHeight)
        } else {
            super.setMeasuredDimension(widthSize, heightSize)
        }
    }

    /**
     * Вычисляет высоту списка относительно количества элементов.
     * которое необходимого показать.
     */
    private fun computeHeight(): Int {
        if (childCount == 0 || fixedCount <= 0) {
            return 0
        }

        val child = getChildAt(0)
        var height: Int = child?.height ?: return 0
        val lp = child.layoutParams as RecyclerView.LayoutParams

        height += lp.topMargin + lp.bottomMargin + getBottomDecorationHeight(child)
        return height * fixedCount + paddingBottom + paddingTop
    }
}