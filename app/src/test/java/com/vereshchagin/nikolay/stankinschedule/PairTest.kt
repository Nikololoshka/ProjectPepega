package com.vereshchagin.nikolay.stankinschedule

import com.google.gson.GsonBuilder
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Pair
import org.apache.commons.io.FileUtils
import org.junit.Assert
import org.junit.Test
import java.io.IOException
import java.nio.charset.StandardCharsets

/**
 * Тесты связанные с парой.
 */
class PairTest {
    /**
     * Проверка на правильность загрузки/сохранения пары.
     */
    @Test
    fun loadingAndSaving() {
        try {
            for (i in 1..6) {
                val file = FileUtils.getFile(PATH, String.format("pair_%d.json", i))
                val json = FileUtils.readFileToString(file, StandardCharsets.UTF_8)

                val pair = GsonBuilder().registerTypeAdapter(
                    Pair::class.java,
                    Pair.Deserializer()
                ).create().fromJson(
                    json,
                    Pair::class.java
                )

                println(pair)

                val newJson = GsonBuilder()
                    .registerTypeAdapter(
                        Pair::class.java,
                        Pair.Serializer()
                    )
                    .setPrettyPrinting()
                    .create()
                    .toJson(pair)

                println(newJson)
            }

            Assert.assertTrue("Successfully!", true)

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun takePair(num: Int) : Pair {
        val file = FileUtils.getFile(PATH, String.format("pair_%d.json", num))
        val json = FileUtils.readFileToString(file, StandardCharsets.UTF_8)

        return GsonBuilder().registerTypeAdapter(
            Pair::class.java,
            Pair.Deserializer()
        ).create().fromJson(
            json,
            Pair::class.java
        )
    }

    @Test
    fun compare() {
        val pair1 = takePair(1)
        val pair2 = takePair(1)

        Assert.assertTrue(pair1 == pair2)
    }

    companion object {
        private const val PATH = "src/test/resources/"
    }
}