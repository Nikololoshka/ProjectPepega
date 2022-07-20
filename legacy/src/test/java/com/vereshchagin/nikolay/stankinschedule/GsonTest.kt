package com.vereshchagin.nikolay.stankinschedule

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.vereshchagin.nikolay.stankinschedule.model.modulejournal.MarkType
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Date
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.DateSingle
import com.vereshchagin.nikolay.stankinschedule.utils.convertors.gson.DateTimeTypeConverter
import com.vereshchagin.nikolay.stankinschedule.utils.convertors.gson.MarkTypeTypeConverter
import com.vereshchagin.nikolay.stankinschedule.view.MarksTable
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.junit.Assert
import org.junit.Test

/**
 * Тесты сериализации и десериализации данных.
 */
class GsonTest {

    @Test
    fun markType() {
        val gson = GsonBuilder()
            .registerTypeAdapter(MarkType::class.java, MarkTypeTypeConverter())
            .registerTypeAdapter(DateTime::class.java, DateTimeTypeConverter())
            .create()

        val data = MarksTable.testData()
        // правильная конвертация...
        gson.toJson(data)

        val mark = MarkType.EXAM
        Assert.assertEquals(gson.toJson(mark), "\"Э\"")
    }

    @Test
    fun dateTest() {
        val date = Date()
        date.add(DateSingle(LocalDate(2021, 2, 23)))
        val json = date.toJson().toString()
        println(json)

        val newDate = Date(Gson().fromJson(json, JsonElement::class.java))
        println(newDate)
    }
}