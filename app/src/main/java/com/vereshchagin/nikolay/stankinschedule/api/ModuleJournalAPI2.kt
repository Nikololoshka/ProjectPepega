package com.vereshchagin.nikolay.stankinschedule.api

import com.vereshchagin.nikolay.stankinschedule.model.modulejournal.MarkResponse
import com.vereshchagin.nikolay.stankinschedule.model.modulejournal.SemestersResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Интерфейс для HttpApi2 модульного журнала.
 *
 * Справка по запросам:
 * https://github.com/stankin/mj/blob/master/src/main/java/ru/stankin/mj/http/HttpApi2.java
 */
interface ModuleJournalAPI2 {

    @POST("/webapi/api2/semesters/")
    @Headers("Content-Type: application/x-www-form-urlencoded; charset=UTF-8")
    @FormUrlEncoded
    fun getSemesters(
        @Field("student") login: String,
        @Field("password") password: String,
    ): Call<SemestersResponse>

    @POST("/webapi/api2/marks/")
    @Headers("Content-Type: application/x-www-form-urlencoded; charset=UTF-8")
    @FormUrlEncoded
    fun getMarks(
        @Field("student") login: String,
        @Field("password") password: String,
        @Field("semester") semester: String
    ): Call<List<MarkResponse>>
}