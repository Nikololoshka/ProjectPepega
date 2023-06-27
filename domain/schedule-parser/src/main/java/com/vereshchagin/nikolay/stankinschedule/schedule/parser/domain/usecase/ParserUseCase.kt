package com.vereshchagin.nikolay.stankinschedule.schedule.parser.domain.usecase

import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.Time
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.domain.model.CellBound
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.domain.model.ParseResult
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.domain.model.TimeCellBound
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.domain.repository.ParserRepository
import javax.inject.Inject

class ParserUseCase @Inject constructor(
    private val parser: ParserRepository
) {

    private val extractor = PairExtractor()

    suspend fun parsePDF(path: String): List<ParseResult> {
        val details = parser.parsePDF(path)
        val timeCells = detectTimeCells(details.cells)

        val result = details.cells.flatMap { cell ->
            extractor.extractAllPairsFromCell(cell, timeCells)
        }

        return result
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