package com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.model

import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.PairModel

class ScheduleTableDay {

    private var lines: MutableList<MutableList<MutableList<Int>>> = mutableListOf()
    private var pairs: List<PairModel> = emptyList()

    fun setPairs(pairs: List<PairModel>) {
        this.pairs = pairs.sortedWith(PAIR_COMPARATOR)
        reallocate()
    }

    fun cells(
        pairsToText: (pairs: List<PairModel>) -> String = { it.joinToString("\n") }
    ): List<ScheduleTableCell> {
        val rawCells = mutableListOf<RawTableCell>()
        val rows = lines.map { line -> line.map { ids -> ids.toMutableSet() } } // deep copy

        for (rowIndex in rows.indices) {
            for (columnIndex in rows[rowIndex].indices) {
                val ids = rows[rowIndex][columnIndex]
                if (ids.isNotEmpty() && rawCells.all { it.ids != ids }) {

                    val pairsInCell = pairsFromIds(ids)
                    val duration = pairsInCell.first().time.duration
                    rawCells.add(RawTableCell(ids, rowIndex, 1, columnIndex, duration))

                    // по гориз. текущую строку
                    var prevRow = rowIndex - 1 >= 0
                    var nextRow = rowIndex + 1 < rows.size

                    for (k in 0 until duration) {
                        rows[rowIndex][columnIndex + k].addAll(ids)
                        if (nextRow && rows[rowIndex + 1][columnIndex + k].isNotEmpty()) {
                            nextRow = false
                        }
                        if (prevRow && rows[rowIndex - 1][columnIndex + k].isNotEmpty()) {
                            prevRow = false
                        }
                    }

                    // дозаполнение сверху
                    var m = rowIndex + 1
                    while (nextRow) {
                        nextRow = m + 1 < rows.size
                        for (k in 0 until duration) {
                            rows[m][columnIndex + k].addAll(ids)
                            if (nextRow && rows[m + 1][columnIndex + k].isNotEmpty()) {
                                nextRow = false
                            }
                        }
                        ++m
                        ++rawCells.last().rowSpan
                    }

                    // дозаполнение снизу
                    var n = rowIndex - 1
                    while (prevRow) {
                        prevRow = n - 1 >= 0
                        for (k in 0 until duration) {
                            rows[n][columnIndex + k].addAll(ids)
                            if (prevRow && rows[n - 1][columnIndex + k].isNotEmpty()) {
                                prevRow = false
                            }
                        }
                        --n
                        rawCells.last().apply {
                            ++rowSpan
                            --row
                        }
                    }
                }
            }
        }

        // добавление пустых ячеек
        var overIndex = pairs.size
        for (rowIndex in rows.indices) {
            for (columnIndex in rows[rowIndex].indices) {
                if (rows[rowIndex][columnIndex].isEmpty()) {
                    var prevRow = rowIndex - 1 >= 0
                    var nextRow = rowIndex + 1 < rows.size

                    rows[rowIndex][columnIndex].add(overIndex++)
                    rawCells.add(
                        RawTableCell(
                            ids = rows[rowIndex][columnIndex],
                            row = rowIndex,
                            rowSpan = 1,
                            column = columnIndex,
                            columnSpan = 1
                        ),
                    )

                    if (nextRow && rows[rowIndex + 1][columnIndex].isNotEmpty()) {
                        nextRow = false
                    }

                    if (prevRow && rows[rowIndex - 1][columnIndex].isNotEmpty()) {
                        prevRow = false
                    }

                    // дозаполнение сверху
                    var m = rowIndex + 1
                    while (nextRow) {
                        nextRow = m + 1 < rows.size
                        rows[m][columnIndex].addAll(rows[rowIndex][columnIndex])
                        ++rawCells.last().rowSpan
                        ++m
                    }

                    // дозаполнение снизу
                    var n = rowIndex - 1
                    while (prevRow) {
                        prevRow = n - 1 >= 0
                        rows[n][columnIndex].addAll(rows[rowIndex][columnIndex])
                        ++rawCells.last().rowSpan
                        --rawCells.last().row
                        --n
                    }
                }
            }
        }

        return rawCells.map { rawCell ->


            ScheduleTableCell(
                row = rawCell.row,
                column = rawCell.column,
                text = pairsToText(pairsFromIds(rawCell.ids)),
                rowSpan = rawCell.rowSpan,
                columnSpan = rawCell.columnSpan
            )
        }
    }

    private fun pairsFromIds(ids: Iterable<Int>): List<PairModel> {
        return ids.mapNotNull { index -> if (index >= pairs.size) null else pairs[index] }
    }

    private fun reallocate() {
        lines.clear()
        lines.add(MutableList(COLUMNS) { mutableListOf() })

        pairs.forEachIndexed { id, pair ->
            var isInsert = false

            for (line in lines) {
                // предположительная ячейка
                val idsInCell = line[pair.time.number()]
                val pairsInTargetCell = pairsFromIds(idsInCell)

                // Не пустая и подходит
                if (pairsInTargetCell.isNotEmpty()
                    && pairsInTargetCell.first().time.duration == pair.time.duration
                    && isMerge(pairsInTargetCell, pair)
                ) {
                    idsInCell.add(id)
                    isInsert = true
                    break
                } else {
                    // если пусто, то проверяем место для вставки
                    var isFree = true
                    for (cell in line) {
                        val pairsInCell = pairsFromIds(cell)
                        if (pairsInCell.isNotEmpty()
                            && pairsInCell.first().time.isIntersect(pair.time)
                        ) {
                            // есть пересечение с другой парой
                            isFree = false
                            break
                        }
                    }

                    // ничего не мешает
                    if (isFree) {
                        idsInCell.add(id)
                        isInsert = true
                        break
                    }
                }
            }

            // Не удалось вставить в существующую линию
            if (!isInsert) {
                lines.add(MutableList(COLUMNS) { mutableListOf() })
                lines.last()[pair.time.number()].add(id)
            }
        }
    }

    private fun isMerge(pairs: List<PairModel>, pair: PairModel): Boolean {
        return pairs.any { p -> p.subgroup == pair.subgroup }
    }

    fun lines(): Int {
        return lines.size
    }

    companion object {

        private const val COLUMNS = 8

        private val PAIR_COMPARATOR
            get() = Comparator<PairModel> { o1, o2 -> o2.time.duration - o1.time.duration }
    }

    private class RawTableCell(
        var ids: Iterable<Int>,
        var row: Int,
        var rowSpan: Int,
        var column: Int,
        var columnSpan: Int
    )
}