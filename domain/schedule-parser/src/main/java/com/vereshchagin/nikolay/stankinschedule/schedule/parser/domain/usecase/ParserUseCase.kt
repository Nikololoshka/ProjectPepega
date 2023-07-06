package com.vereshchagin.nikolay.stankinschedule.schedule.parser.domain.usecase

import android.graphics.Bitmap
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.Time
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.domain.model.CellBound
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.domain.model.ParseResult
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.domain.model.ParserSettings
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.domain.model.TimeCellBound
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.domain.repository.PDFRepository
import javax.inject.Inject

class ParserUseCase @Inject constructor(
    private val parser: PDFRepository
) {

    private val extractor = PairExtractor()

    suspend fun parsePDF(
        path: String,
        settings: ParserSettings
    ): List<ParseResult> {
        val details = parser.parsePDF(path, settings.parserThreshold)
        val timeCells = detectTimeCells(details.cells)

        extractor.dateYear = settings.scheduleYear
        val result = details.cells.flatMap { cell ->
            extractor.extractAllPairsFromCell(cell, timeCells)
        }

        return result
    }

    suspend fun renderPreview(path: String): Bitmap {
        return parser.renderPDF(path)
    }

    private fun detectTimeCells(cells: List<CellBound>): List<TimeCellBound> {
        var middleFirst = -1f
        var middleSecond = -1f

        for (cell in cells) {
            if (cell.text.contains("8:30")) {
                middleFirst = cell.x + cell.w / 2
            }
            if (cell.text.contains("10:20")) {
                middleSecond = cell.x + cell.w / 2
            }
        }

        if (middleFirst < 0 || middleSecond < 0) {
            throw IllegalArgumentException("Time not found")
        }

        val delta = middleSecond - middleFirst
        val startX = middleFirst - delta / 2

        return MutableList(size = Time.STARTS.size) { index ->
            TimeCellBound(
                startX = startX + delta * index,
                endX = startX + delta * (index + 1),
                startTime = Time.STARTS[index],
                endTime = Time.ENDS[index],
            )
        }
    }
}