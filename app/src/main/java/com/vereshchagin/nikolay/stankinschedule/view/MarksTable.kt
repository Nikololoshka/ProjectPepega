package com.vereshchagin.nikolay.stankinschedule.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.model.modulejournal.Discipline
import com.vereshchagin.nikolay.stankinschedule.model.modulejournal.MarkType
import com.vereshchagin.nikolay.stankinschedule.model.modulejournal.SemesterMarks
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * View таблицы с оценками.
 */
class MarksTable : View {

    private val minHeaderSize = createPixelSize(25F).toFloat()
    private val cellMargin = createPixelSize(2F).toFloat()
    private val bitmapNoMark = Bitmap.createScaledBitmap(
        ContextCompat.getDrawable(context, R.drawable.drawable_no_mark)?.toBitmap()!!,
        minHeaderSize.toInt(),
        minHeaderSize.toInt(),
        false
    )

    private var totalDisciplineSize = 0F
    private var totalHeaderSize = 0F

    private val markHeaderData = listOf("М1", "М2", "К", "З", "Э", "К")
    private val marksData = testData()

    private val disciplineLayouts = arrayListOf<StaticLayout>()

    private val headerLayout = arrayListOf<Float>()
    private var headerHeight = 0F

    private val headerPainter = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val textPainter = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val linePainter = Paint(Paint.ANTI_ALIAS_FLAG)


    constructor(
        context: Context
    ) : super(context)

    constructor(
        context: Context,
        attrs: AttributeSet
    ) : super(context, attrs) {
        initialize(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttrs: Int) : super(
        context,
        attrs,
        defStyleAttrs
    ) {
        initialize(context, attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttrs: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttrs, defStyleRes) {
        initialize(context, attrs)
    }

    /**
     * Инициализация атрибутов таблицы с оценками.
     */
    private fun initialize(context: Context, attrs: AttributeSet) {
        headerPainter.textSize = createPixelSize(12F).toFloat()
        headerPainter.textAlign = Paint.Align.CENTER

        textPainter.textSize = createPixelSize(12F).toFloat()

        linePainter.strokeWidth = createPixelSize(0.5F).toFloat()
        linePainter.style = Paint.Style.STROKE
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        headerLayout.clear()
        disciplineLayouts.clear()

        val widthSize = resolveSize(0, widthMeasureSpec)
        var wrapHeight = createPixelSize(0.5F).toFloat() // ширина линии

        // заголовок с типами оценок
        val fontMetrics = headerPainter.fontMetrics
        headerHeight = fontMetrics.bottom - fontMetrics.top + fontMetrics.leading
        wrapHeight += headerHeight + cellMargin * 2

        totalHeaderSize = 0F
        for (type in markHeaderData) {
            val headerSize = max(minHeaderSize, headerPainter.measureText(type))
            headerLayout.add(headerSize)
            totalHeaderSize += headerSize
        }

        for (discipline in marksData.disciplines) {

        }

        // заголовок с дисциплинами
        totalDisciplineSize = (widthSize - totalHeaderSize)
        val maxDisciplineTextSize = (totalDisciplineSize - cellMargin * 2).toInt()

        for (discipline in marksData.disciplines) {
            val title = discipline.title
            val layout = StaticLayout.Builder
                .obtain(title, 0, title.length, textPainter, maxDisciplineTextSize)
                .build()

            disciplineLayouts.add(layout)
            wrapHeight += layout.height + cellMargin * 2
        }

        // установка конечных размеров
        val heightSize = resolveSize(wrapHeight.roundToInt(), heightMeasureSpec)
        setMeasuredDimension(widthSize, heightSize)
    }

    override fun onDraw(maybeCanvas: Canvas?) {
        val canvas = maybeCanvas ?: return super.onDraw(maybeCanvas)
        val fontExtra = (headerPainter.descent() + headerPainter.ascent()) / 2

        // рисование заголовка таблицы
        var offset = totalDisciplineSize
        for ((size, data) in headerLayout.zip(markHeaderData)) {
            canvas.drawText(
                data,
                offset + size / 2,
                cellMargin + headerHeight / 2F - fontExtra,
                headerPainter
            )
            canvas.drawLine(offset, 0F, offset, measuredHeight.toFloat(), linePainter)
            offset += size
        }

        canvas.translate(0F, headerHeight + cellMargin * 2)
        canvas.drawLine(0F, 0F, measuredWidth.toFloat(), 0F, linePainter)
        canvas.translate(cellMargin, cellMargin)

        // рисование дисциплин, их оценок и коэффициент
        for ((layout, discipline) in disciplineLayouts.zip(marksData.disciplines)) {
            layout.draw(canvas)

            // оценки дисциплины
            var markOffset = totalDisciplineSize - cellMargin
            for ((markType, size) in MarkType.values().zip(headerLayout)) {
                val mark = discipline[markType]
                when {
                    // пустое значение (т.е. крестик)
                    mark == null -> {
                        canvas.drawBitmap(
                            bitmapNoMark,
                            markOffset,
                            layout.height / 2 - minHeaderSize / 2,
                            linePainter
                        )
                    }
                    // есть оценка
                    mark != Discipline.NO_MARK -> {
                        canvas.drawText(
                            mark.toString(),
                            markOffset + size / 2,
                            layout.height / 2 - fontExtra,
                            headerPainter
                        )
                    }
                }
                markOffset += size
            }

            // коэффициент дисциплины
            canvas.drawText(
                discipline.factor.toString(),
                markOffset + headerLayout.last() / 2,
                layout.height / 2 - fontExtra,
                headerPainter
            )

            canvas.translate(0F, layout.height + cellMargin)
            canvas.drawLine(-cellMargin, 0F, measuredWidth.toFloat(), 0F, linePainter)
            canvas.translate(0F, cellMargin)
        }
    }

    /**
     * Возвращает размер в пикселях, переводя из dp.
     */
    private fun createPixelSize(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp,
            resources.displayMetrics
        ).roundToInt()
    }

    private fun testData() = SemesterMarks(
        arrayListOf(
            Discipline(
                "Геометрическое моделирование и компьютерная графика",
                linkedMapOf(
                    MarkType.FIRST_MODULE to 50,
                    MarkType.SECOND_MODULE to 54,
                    MarkType.EXAM to 52
                ),
                3.5
            ),
            Discipline(
                "Информационные системы и технологии",
                linkedMapOf(
                    MarkType.FIRST_MODULE to 54,
                    MarkType.SECOND_MODULE to 52,
                    MarkType.EXAM to 54
                ),
                3.5
            ),
            Discipline(
                "Машинное обучение и интеллектуальные системы",
                linkedMapOf(
                    MarkType.FIRST_MODULE to 52,
                    MarkType.SECOND_MODULE to 49,
                    MarkType.COURSEWORK to 49,
                    MarkType.EXAM to 48
                ),
                3.0
            ),
            Discipline(
                "Прикладная физическая культура",
                linkedMapOf(
                    MarkType.FIRST_MODULE to 50,
                    MarkType.SECOND_MODULE to 50,
                    MarkType.CREDIT to 43
                ),
                1.0
            ),
            Discipline(
                "Проектирование информационных систем",
                linkedMapOf(
                    MarkType.FIRST_MODULE to 52,
                    MarkType.SECOND_MODULE to 50,
                    MarkType.COURSEWORK to 50,
                    MarkType.EXAM to 54
                ),
                3.0
            ),
            Discipline(
                "Проектирование человеко-машинного взаимодействия",
                linkedMapOf(
                    MarkType.FIRST_MODULE to 49,
                    MarkType.SECOND_MODULE to 45,
                    MarkType.CREDIT to 47
                ),
                3.5
            ),
            Discipline(
                "Теория массового обслуживания",
                linkedMapOf(
                    MarkType.FIRST_MODULE to 42,
                    MarkType.SECOND_MODULE to 42,
                    MarkType.EXAM to 43
                ),
                4.5
            ),
            Discipline(
                "Технологии архивирования и восстановления данных",
                linkedMapOf(
                    MarkType.FIRST_MODULE to 38,
                    MarkType.SECOND_MODULE to 45,
                    MarkType.CREDIT to 41
                ),
                2.5
            )
        ),
        48,
        45
    )

    companion object {
        private const val TAG = "MarksTableLog"
    }
}