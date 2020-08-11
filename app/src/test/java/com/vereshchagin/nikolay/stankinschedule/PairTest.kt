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

    companion object {
        private const val PATH = "src/test/resources/"
    }
}