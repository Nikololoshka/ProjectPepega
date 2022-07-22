package com.vereshchagin.nikolay.stankinschedule.modulejournal.ui.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.vereshchagin.nikolay.stankinschedule.core.ui.dpToPx
import com.vereshchagin.nikolay.stankinschedule.core.ui.spToPx
import com.vereshchagin.nikolay.stankinschedule.modulejournal.R
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.model.Discipline
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.model.MarkType
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.model.SemesterMarks
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * View таблицы с оценками.
 */
class MarksTable @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttrs: Int = 0, defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttrs, defStyleRes) {

    private val minCellSize = dpToPx(25F, context.resources)
    private val textCellMargin = dpToPx(4F, context.resources)
    private val markCellMargin = dpToPx(1F, context.resources)

    private val bitmapNoMark = Bitmap.createScaledBitmap(
        ContextCompat.getDrawable(context, R.drawable.no_mark)?.toBitmap()!!,
        minCellSize.toInt(),
        minCellSize.toInt(),
        false
    )

    private var totalDisciplineSize = 0F
    private var totalHeaderSize = 0F
    private var maxWrapHeight = 0f

    private val markHeaderData = listOf("М1", "М2", "К", "З", "Э", "К")
    private var marksData = emptyData()

    private val disciplineLayouts = arrayListOf<StaticLayout>()
    private val ratingLayout = arrayListOf<StaticLayout>()
    private val headerLayout = arrayListOf<Float>()
    private var headerHeight = 0F

    private var ratingText: String = "Рейтинг"
    private var accumulateRatingText: String = "Накопленный рейтинг"

    private val contentPainter = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val disciplinePainter = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val ratingPainter = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val linePainter = Paint(Paint.ANTI_ALIAS_FLAG)
    private val drawablePainter = Paint(Paint.ANTI_ALIAS_FLAG)


    /**
     * Инициализация атрибутов таблицы с оценками.
     */
    init {
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.MarksTable, defStyleAttrs, defStyleRes
        )

        // стиль линий
        val dividerColor = R.styleable.MarksTable_mt_dividerColor
        if (typedArray.hasValue(dividerColor)) {
            linePainter.color = typedArray.getColor(dividerColor, 0)
        }
        linePainter.strokeWidth = dpToPx(0.5F, context.resources)
        linePainter.style = Paint.Style.STROKE

        drawablePainter.strokeWidth = dpToPx(0.5F, context.resources)
        drawablePainter.style = Paint.Style.STROKE

        // стиль ячеек
        val textColor = R.styleable.MarksTable_mt_textColor
        if (typedArray.hasValue(textColor)) {
            val color = typedArray.getColor(textColor, 0)
            contentPainter.color = color
            disciplinePainter.color = color
            ratingPainter.color = color
        }

        contentPainter.textSize = spToPx(14F, context.resources)
        disciplinePainter.textSize = spToPx(14F, context.resources)
        ratingPainter.textSize = spToPx(14F, context.resources)

        contentPainter.textAlign = Paint.Align.CENTER
        ratingPainter.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        // ячейки с рейтингом
//        accumulateRatingText = typedArray.getStringOrThrow(R.styleable.MarksTable_mt_accumulateRating)
//        ratingText = typedArray.getStringOrThrow(R.styleable.MarksTable_mt_rating)

        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        headerLayout.clear()
        disciplineLayouts.clear()
        ratingLayout.clear()

        val widthSize = resolveSize(100, widthMeasureSpec)
        var wrapHeight = dpToPx(0.5F, context.resources) // ширина завершающей линии

        // заголовок с типами оценок
        val fontMetrics = contentPainter.fontMetrics
        headerHeight = fontMetrics.bottom - fontMetrics.top + fontMetrics.leading
        val verMargin = textCellMargin * 2
        val horTextMargin = textCellMargin * 2
        val horMarkMargin = markCellMargin * 2
        wrapHeight += headerHeight + verMargin

        for (type in markHeaderData) {
            val headerSize = max(minCellSize, contentPainter.measureText(type)) + horMarkMargin
            headerLayout.add(headerSize)
        }

        // ячейки с оценками и коэффициентом
        for (discipline in marksData.disciplines) {
            // оценки
            for ((j, type) in MarkType.values().withIndex()) {
                val mark = discipline[type]
                if (mark != null) {
                    val markSize = contentPainter.measureText(mark.toString()) + horMarkMargin
                    if (markSize > headerLayout[j]) {
                        headerLayout[j] = markSize
                    }
                }
            }
            // коэффициент
            val factorSize = contentPainter.measureText(discipline.factorHolder) + horMarkMargin
            if (factorSize > headerLayout.last()) {
                headerLayout[headerLayout.size - 1] = factorSize
            }
        }
        totalHeaderSize = headerLayout.sum()

        // заголовок с дисциплинами
        totalDisciplineSize = (widthSize - totalHeaderSize)
        val maxDisciplineTextSize = (totalDisciplineSize - horTextMargin).toInt()

        for (discipline in marksData.disciplines) {
            val title = discipline.title
            val layout = StaticLayout.Builder
                .obtain(title, 0, title.length, disciplinePainter, maxDisciplineTextSize)
                .build()

            disciplineLayouts.add(layout)
            wrapHeight += layout.height + verMargin
        }

        // рейтинг
        for (rating in listOf(ratingText, accumulateRatingText)) {
            val layout = StaticLayout.Builder
                .obtain(rating, 0, rating.length, ratingPainter, maxDisciplineTextSize)
                .build()
            ratingLayout.add(layout)
            wrapHeight += layout.height + verMargin
        }

        // установка конечных размеров
        maxWrapHeight = wrapHeight
        val heightSize = resolveSize(wrapHeight.roundToInt(), heightMeasureSpec)
        setMeasuredDimension(widthSize, heightSize)
    }

    override fun onDraw(maybeCanvas: Canvas?) {
        val canvas = maybeCanvas ?: return super.onDraw(maybeCanvas)
        val fontExtra = (contentPainter.descent() + contentPainter.ascent()) / 2

        // рисование заголовка таблицы
        var offset = totalDisciplineSize
        for ((size, data) in headerLayout.zip(markHeaderData)) {
            canvas.drawText(
                data,
                offset + size / 2,
                textCellMargin + headerHeight / 2F - fontExtra,
                contentPainter
            )
            canvas.drawLine(offset, 0F, offset, maxWrapHeight, linePainter)
            offset += size
        }

        // подводящая линия заголовка таблицы
        canvas.translate(0F, headerHeight + textCellMargin * 2)
        canvas.drawLine(0F, 0F, measuredWidth.toFloat(), 0F, linePainter)
        canvas.translate(textCellMargin, textCellMargin)

        // рисование дисциплин, их оценок и коэффициент
        for ((layout, discipline) in disciplineLayouts.zip(marksData.disciplines)) {
            layout.draw(canvas)

            // оценки дисциплины
            var markOffset = totalDisciplineSize - textCellMargin
            for ((markType, size) in MarkType.values().zip(headerLayout)) {
                val mark = discipline[markType]
                when {
                    // пустое значение (т.е. крестик)
                    mark == null -> {
                        val halfImageSize = minCellSize / 2
                        canvas.drawBitmap(
                            bitmapNoMark,
                            markOffset + size / 2 - halfImageSize,
                            layout.height / 2 - halfImageSize,
                            drawablePainter
                        )
                    }
                    // есть оценка
                    mark != Discipline.NO_MARK -> {
                        canvas.drawText(
                            mark.toString(),
                            markOffset + size / 2,
                            layout.height / 2 - fontExtra,
                            contentPainter
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
                contentPainter
            )

            drawLineAndMove(canvas, layout)
        }

        // рейтинг
        for ((value, layout) in listOf(marksData.rating, marksData.accumulatedRating).zip(
            ratingLayout
        )) {
            layout.draw(canvas)

            var ratingOffset = totalDisciplineSize - textCellMargin
            for ((i, size) in headerLayout.withIndex()) {
                if (i == 0) {
                    if (value != null && value != Discipline.NO_MARK) {
                        canvas.drawText(
                            value.toString(),
                            ratingOffset + size / 2,
                            layout.height / 2 - fontExtra,
                            contentPainter
                        )
                    }
                } else {
                    val halfImageSize = minCellSize / 2
                    canvas.drawBitmap(
                        bitmapNoMark,
                        ratingOffset + size / 2 - halfImageSize,
                        layout.height / 2 - halfImageSize,
                        drawablePainter
                    )
                }
                ratingOffset += size
            }

            drawLineAndMove(canvas, layout)
        }
    }

    /**
     * Рисует подводящую линию в таблице.
     */
    private fun drawLineAndMove(canvas: Canvas, layout: StaticLayout) {
        canvas.translate(0F, layout.height + textCellMargin)
        canvas.drawLine(-textCellMargin, 0F, measuredWidth.toFloat(), 0F, linePainter)
        canvas.translate(0F, textCellMargin)
    }

    /**
     * Устанавливает семестр с оценками для отображения
     */
    fun setSemesterMarks(data: SemesterMarks) {
        marksData = data
        requestLayout()
    }

    /**
     * Пустые данные.
     */
    private fun emptyData() = SemesterMarks(arrayListOf(), null, null)


    companion object {
        private const val TAG = "MarksTableLog"

        /**
         * Тестовые данные для отображения.
         */
        fun testData() = SemesterMarks(
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
                        MarkType.FIRST_MODULE to 0,
                        MarkType.SECOND_MODULE to 0,
                        MarkType.EXAM to 0
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
    }
}