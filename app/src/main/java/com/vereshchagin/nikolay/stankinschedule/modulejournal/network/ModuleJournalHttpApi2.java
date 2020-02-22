package com.vereshchagin.nikolay.stankinschedule.modulejournal.network;

import androidx.annotation.NonNull;

import com.vereshchagin.nikolay.stankinschedule.modulejournal.network.response.MarkResponse;
import com.vereshchagin.nikolay.stankinschedule.modulejournal.network.response.SemestersResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Интерфейс для общения с HttpApi2 модульного журнала.
 *
 * Справка по запросам:
 * https://github.com/stankin/mj/blob/master/src/main/java/ru/stankin/mj/http/HttpApi2.java
 */
public interface ModuleJournalHttpApi2 {

    @POST("/webapi/api2/semesters/")
    @Headers({"Content-Type: application/x-www-form-urlencoded; charset=UTF-8"})
    @FormUrlEncoded
    Call<SemestersResponse> getSemesters(@Field("student") @NonNull String login,
                                         @Field("password") @NonNull String password);

    @POST("/webapi/api2/marks/")
    @Headers({"Content-Type: application/x-www-form-urlencoded; charset=UTF-8"})
    @FormUrlEncoded
    Call<List<MarkResponse>> getMarks(@Field("student") @NonNull String login,
                                      @Field("password") @NonNull String password,
                                      @Field("semester") @NonNull String semester);
}
