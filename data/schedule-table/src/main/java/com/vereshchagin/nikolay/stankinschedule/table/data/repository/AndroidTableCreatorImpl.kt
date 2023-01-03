package com.vereshchagin.nikolay.stankinschedule.table.data.repository

import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import androidx.core.graphics.applyCanvas
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.model.ScheduleModel
import com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.model.ScheduleTable
import com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.model.TableConfig
import com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.model.TableMode
import com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.repository.AndroidTableCreator
import com.vereshchagin.nikolay.stankinschedule.schedule.table.domain.usecase.drawScheduleTable
import org.joda.time.LocalDate
import javax.inject.Inject
import kotlin.math.sqrt

class AndroidTableCreatorImpl @Inject constructor() : AndroidTableCreator {

    override fun createImage(schedule: ScheduleModel, config: TableConfig): Bitmap {
        val scale = config.longScreenSize / 1600f
        val width: Int = config.longScreenSize.toInt()
        val height: Int = (config.longScreenSize / sqrt(2f)).toInt()

        val table = when (config.mode) {
            TableMode.Full -> ScheduleTable(
                schedule = schedule
            )
            TableMode.Weekly -> ScheduleTable(
                schedule = schedule,
                date = LocalDate.now().plusDays(config.page * 7)
            )
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.applyCanvas {
            drawScheduleTable(
                scheduleTable = table,
                pageHeight = height,
                pageWidth = width,
                scale = scale,
                tableColor = config.color
            )
        }

        return bitmap
    }

    override fun createPdf(schedule: ScheduleModel, config: TableConfig): PdfDocument {
        val width = 842
        val height = 595

        val startDate = schedule.startDate()
        val endDate = schedule.endDate()
        if (startDate == null || endDate == null) {
            throw NoSuchElementException("Schedule is empty")
        }

        val pdf = PdfDocument()

        when (config.mode) {
            TableMode.Full -> {
                pdf.createPage(width, height, 1) { page ->
                    page.canvas.drawScheduleTable(
                        scheduleTable = ScheduleTable(schedule),
                        pageHeight = height,
                        pageWidth = width
                    )
                }
            }
            TableMode.Weekly -> {
                var from = startDate.withDayOfWeek(1)
                val to = endDate.withDayOfWeek(7)

                var pageNumber = 1
                while (from.isBefore(to)) {

                    pdf.createPage(width, height, pageNumber) { page ->
                        page.canvas.drawScheduleTable(
                            scheduleTable = ScheduleTable(
                                schedule = schedule,
                                date = from
                            ),
                            pageHeight = height,
                            pageWidth = width
                        )
                    }

                    from = from.plusDays(7)
                    ++pageNumber
                }
            }
        }

        return pdf
    }

    private fun PdfDocument.createPage(
        width: Int,
        height: Int,
        number: Int,
        creator: (page: PdfDocument.Page) -> Unit
    ) {
        val pageInfo = PdfDocument.PageInfo.Builder(width, height, number).create()
        val page = this.startPage(pageInfo)
        creator(page)
        this.finishPage(page)
    }
}