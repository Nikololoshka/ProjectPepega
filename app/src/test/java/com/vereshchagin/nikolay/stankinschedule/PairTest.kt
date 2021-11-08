package com.vereshchagin.nikolay.stankinschedule

import com.google.gson.GsonBuilder
import com.vereshchagin.nikolay.stankinschedule.resources.PairResources
import org.junit.Assert
import org.junit.Test

/**
 * Тесты для пары в расписании.
 */
class PairTest {

    private val gson = GsonBuilder()
        .setPrettyPrinting()
        .create()

    /**
     * Проверка на правильность загрузки/сохранения пары.
     */
    @Test
    fun loadingAndSavingPairs() {
        val pairs = PairResources.PAIRS.map { json ->
            gson.fromJson(json, Pair::class.java)
        }

        val newPairs = PairResources.PAIRS.map { json ->
            val pair = gson.fromJson(json, Pair::class.java)
            val newJson = gson.toJson(pair)
            gson.fromJson(newJson, Pair::class.java)
        }

        Assert.assertEquals(pairs, newPairs)
    }

    /**
     * Проверка на сравнение пар.
     */
    @Test
    fun pairEquals() {
        val first = PairResources.PAIRS.map { json ->
            gson.fromJson(json, Pair::class.java)
        }
        val second = PairResources.PAIRS.map { json ->
            gson.fromJson(json, Pair::class.java)
        }

        Assert.assertEquals(first, second)
    }
}