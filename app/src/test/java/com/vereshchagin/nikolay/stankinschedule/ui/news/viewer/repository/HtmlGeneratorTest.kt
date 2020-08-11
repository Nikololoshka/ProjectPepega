package com.vereshchagin.nikolay.stankinschedule.ui.news.viewer.repository

import com.google.gson.JsonParser
import com.vereshchagin.nikolay.stankinschedule.ui.news.viewer.HtmlGenerator
import org.junit.Test
import java.io.File

class HtmlGeneratorTest {

    @Test
    fun generate() {

        val path = "C:\\Users\\HOME-PC\\Desktop\\Data1\\StankinNews\\test_data.txt"
        var index = 1

        File(path).forEachLine { ops ->
            HtmlGenerator.generate(JsonParser.parseString(ops).asJsonArray)
            println((index++).toString() + " - is success")
        }
    }
}