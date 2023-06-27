package com.vereshchagin.nikolay.stankinschedule.parser.data.repository

import android.graphics.Path
import android.graphics.PointF
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.contentstream.PDFGraphicsStreamEngine
import com.tom_roush.pdfbox.cos.COSName
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImage
import com.tom_roush.pdfbox.text.PDFTextStripper
import com.tom_roush.pdfbox.text.TextPosition
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.data.test.R
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStreamWriter
import kotlin.math.abs


// @RunWith(AndroidJUnit4.class)
@SmallTest
class ImportRepositoryTest {

    lateinit var file: InputStream

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().context
        PDFBoxResourceLoader.init(context)

        file = context.resources.openRawResource(R.raw.schedule)
    }

    @Test
    fun parse() {
        val repository = ImportRepository()
        repository.import(file).forEach { cell ->
            println(cell)
        }
    }


    // @Test
    fun test() {
        PDDocument.load(file).use { document ->

            /*
            val page = document.getPage(0)
            val catcher = LineCatcher(page)
            catcher.processPage(page)

            findRects(catcher.lines)

             */


            val stripper = GetCharLocationAndSize()
            stripper.sortByPosition = true

            val dummy = OutputStreamWriter(ByteArrayOutputStream())
            stripper.writeText(document, dummy)

            println("---------------------------------")

            val blocks = mutableListOf<Rect>()
            for (rect in stripper.rects) {
                val r = blocks.find { r ->
                    abs(r.h - rect.h) < 0.1f &&
                            rect.y - r.yh < rect.h &&
                            abs(r.x - rect.x) < rect.h
                }
                blocks += if (r != null) {
                    blocks.remove(r)
                    Rect(r.text + " " + rect.text, r.x, r.y, rect.yh, r.xw, rect.h)
                } else {
                    rect
                }
            }

            for (block in blocks) {
                println(block)
            }
        }
    }


    fun findRects(lines: List<LineCatcher.Line>) {
        val vertical = lines
            .filter { line -> line.y2 - line.y1 > 0 }
            .sortedBy { line -> line.x1 }
        val horizontal = lines
            .filter { line -> line.x2 - line.x1 > 0 }

    }

    class LineCatcher(page: PDPage) : PDFGraphicsStreamEngine(page) {

        val lines = mutableListOf<Line>()
        private var temp: Pair<Float, Float>? = null

        override fun appendRectangle(p0: PointF?, p1: PointF?, p2: PointF?, p3: PointF?) {
            println("appendRectangle: $p0, $p1, $p2, $p3")
        }

        override fun drawImage(pdImage: PDImage?) {

        }

        override fun clip(windingRule: Path.FillType?) {

        }

        override fun moveTo(x: Float, y: Float) {
            temp = x to y
        }

        override fun lineTo(x: Float, y: Float) {
            temp?.let { (x1, y1) ->
                lines += Line(x1, y1, x, y)
            }
            temp = null
        }

        override fun curveTo(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float) {

        }

        override fun getCurrentPoint(): PointF {
            return PointF()
        }

        override fun closePath() {

        }

        override fun endPath() {

        }

        override fun strokePath() {

        }

        override fun fillPath(windingRule: Path.FillType?) {

        }

        override fun fillAndStrokePath(windingRule: Path.FillType?) {

        }

        override fun shadingFill(shadingName: COSName?) {

        }

        class Line(
            val x1: Float,
            val y1: Float,
            val x2: Float,
            val y2: Float
        ) {
            override fun toString(): String {
                return "Line(x1=$x1, y1=$y1, x2=$x2, y2=$y2)"
            }
        }
    }

    class Rect(
        val text: String,
        val x: Float,
        val y: Float,
        val yh: Float,
        val xw: Float,
        val h: Float
    ) {
        override fun toString(): String {
            return "Rect(text='$text', x=$x, y=$y, yh=$yh, xw=$xw)"
        }
    }

    class GetCharLocationAndSize : PDFTextStripper() {

        val rects = mutableListOf<Rect>()

        override fun writeString(text: String, textPositions: MutableList<TextPosition>) {
            val x = textPositions.minOf { pos -> pos.xDirAdj }
            val y = textPositions.minOf { pos -> pos.yDirAdj }
            val h = textPositions.maxOf { pos -> pos.heightDir }
            val yh = y + h
            val xw = x + textPositions.sumOf { pos -> pos.widthDirAdj.toDouble() }.toFloat()

            val r = Rect(text, x, y, yh, xw, h)
            println(r)
            rects += r

//            for (position: TextPosition in textPositions!!) {
//                println(
//                    position.unicode + " [(X=" + position.xDirAdj + ",Y=" +
//                            position.yDirAdj + ") height=" + position.heightDir + " width=" +
//                            position.widthDirAdj + "]"
//                )
//            }
        }
    }
}