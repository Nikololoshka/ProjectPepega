package com.vereshchagin.nikolay.stankinschedule.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import com.vereshchagin.nikolay.stankinschedule.model.schedule.db.PairItem
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.*

/**
 *
 */
class StudentPairView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private val timePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 18F
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    private val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 16F
    }

    private val secondInfoPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 14F
    }

    private val optinalInfoPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 14F
    }

    var pair: PairItem? = null
        set(value) {
            field = value
            requestLayout()
            invalidate()
        }

    init {
        pair = PairItem(
            title = "Информатика",
            lecturer = "Чеканин В.А",
            classroom = "0404",
            type = Type.LECTURE,
            subgroup = Subgroup.COMMON,
            time = Time("12:20", "14:00"),
            date = Date().apply {
                add(DateSingle("2021-17-11"))
            }
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
//        val
    }

    override fun onDraw(canvas: Canvas) = with(canvas) {

    }

    private fun Paint.getTextBaselineByCenter(center: Float) = center - (descent() + ascent()) / 2

}