package com.vereshchagin.nikolay.stankinschedule.parser.data.repository

import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import com.tom_roush.pdfbox.text.TextPosition
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.domain.model.CellBound
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStreamWriter
import kotlin.math.abs

class ImportRepository {

    fun import(pdf: InputStream): List<CellBound> {
        return PDDocument.load(pdf).use { document ->
            val stripper = StringBoundStripper()
            val bounds = stripper.processStringBounds(document)
            mergeStringBounds(bounds)
        }
    }

    private fun mergeStringBounds(
        bounds: List<StringBoundStripper.StringBound>
    ): List<CellBound> {
        val cells = mutableListOf<CellBound>()

        for (bound in bounds) {
            val cell = cells.find { cell ->
                abs(cell.maxFontHeight - bound.h) < 0.1f && // equal font
                        (bound.y - (cell.y + cell.h)) < cell.maxFontHeight && // close (< maxFontHeight) 'y'
                        abs(cell.x - bound.x) < 1f // equal text block start 'x'
            }
            cells += if (cell != null) {
                cells.remove(cell)
                CellBound(
                    text = cell.text + " " + bound.text,
                    x = cell.x,
                    y = cell.y,
                    h = (bound.y - cell.y) + bound.h,
                    w = cell.w,
                    maxFontHeight = cell.maxFontHeight
                )
            } else {
                CellBound(
                    text = bound.text,
                    x = bound.x,
                    y = bound.y,
                    h = bound.h,
                    w = bound.w,
                    maxFontHeight = bound.h
                )
            }
        }

        return cells
    }

    private class StringBoundStripper : PDFTextStripper() {

        private var blocks = mutableListOf<StringBound>()

        init {
            sortByPosition = true
        }

        fun processStringBounds(document: PDDocument): List<StringBound> {
            blocks = mutableListOf()

            val dummy = OutputStreamWriter(ByteArrayOutputStream())
            writeText(document, dummy)

            return blocks
        }

        override fun writeString(
            text: String,
            textPositions: MutableList<TextPosition>
        ) {
            // super.writeString(text, textPositions)

            val x = textPositions.minOf { pos -> pos.xDirAdj }
            val y = textPositions.minOf { pos -> pos.yDirAdj }
            val h = textPositions.maxOf { pos -> pos.heightDir }
            val w = textPositions.sumOf { pos -> pos.widthDirAdj.toDouble() }.toFloat()

            blocks += StringBound(text, x, y, h, w)
        }

        class StringBound(
            val text: String,
            val x: Float,
            val y: Float,
            val h: Float,
            val w: Float
        )
    }
}