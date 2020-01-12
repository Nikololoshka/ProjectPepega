package com.github.nikololoshka.pepegaschedule.modulejournal.connection;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Класс для соединения с модульным журналом через HttpApi2.
 *
 * Справка по запросам:
 * https://github.com/stankin/mj/blob/master/src/main/java/ru/stankin/mj/http/HttpApi2.java
 */
public class ModuleJournalConnection {
    private static final String REQUEST_SEMESTERS = "semesters";
    private static final String REQUEST_MARKS = "marks";

    private static final String URL = "https://lk.stankin.ru/webapi/api2/";
    private static final String POST_METHOD = "POST";
    private static final String CHARSET_UTF_8 = "utf-8";
    private static final String CONTENT_TYPE = "Content-type";
    private static final String CONTENT_TYPE_VALUE = "application/x-www-form-urlencoded; charset=" + CHARSET_UTF_8;

    /**
     * Создает запрос на получение семестров студента.
     * @param login логин.
     * @param password пароль.
     * @return json строка
     * @throws IOException при неудаче получить данные.
     * @throws ModuleJournalConnectionException при неудаче установить соединение.
     */
    @NonNull
    public String requestSemesters(@NonNull String login, @NonNull String password)
            throws IOException, ModuleJournalConnectionException {
        return connect(REQUEST_SEMESTERS, "student", login, "password", password);
    }

    /**
     * Создает запрос на получение оценок семестра студента.
     * @param login логин.
     * @param password пароль.
     * @param semester семестр.
     * @return json строка
     * @throws IOException при неудаче получить данные.
     * @throws ModuleJournalConnectionException при неудаче установить соединение.
     */
    @NonNull
    public String requestMarks(@NonNull String login, @NonNull String password,
                               @NonNull String semester) throws IOException, ModuleJournalConnectionException {
        return connect(REQUEST_MARKS, "student", login, "password", password,
                "semester", URLEncoder.encode(semester, CHARSET_UTF_8));
    }

    /**
     * Создает запрос к модульному журналу.
     * @param request тип запроса к модульному журналу
     * @param params параметры запроса.
     * @throws IOException при неудаче получить данные.
     * @throws ModuleJournalConnectionException при неудаче установить соединение.
     */
    @NonNull
    private String connect(@NonNull String request, @NonNull String... params)
            throws IOException, ModuleJournalConnectionException {

        URL url = new URL(URL + request + "/");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {

            connection.setRequestMethod(POST_METHOD);
            connection.setRequestProperty(CONTENT_TYPE, CONTENT_TYPE_VALUE);
            connection.setDoOutput(true);

            try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8)) {
                for (int i = 0; i < params.length; i += 2) {
                    writer.write(params[i] + "=" + params[i + 1]);

                    if (i < params.length - 2) {
                        writer.write("&");
                    }
                }
            }

            return readFromStream(connection.getInputStream());

        } catch (IOException e) {
            int code = connection.getResponseCode();
            if (code != -1 && code != HttpURLConnection.HTTP_OK) {
                InputStream stream = connection.getErrorStream();
                String message = stream == null ? connection.getResponseMessage() : readFromStream(stream);
                ModuleJournalConnectionException exception = new ModuleJournalConnectionException(code, message);
                exception.initCause(e);
                throw exception;
            }

            throw e;
        } finally {
            connection.disconnect();
        }
    }

    @NonNull
    private String readFromStream(@NonNull InputStream inputStream) {
        try (Scanner scanner = new Scanner(inputStream, CHARSET_UTF_8)) {
            StringBuilder builder = new StringBuilder();
            while (scanner.hasNextLine()) {
                builder.append(scanner.nextLine());
            }
            return builder.toString();
        }
    }
}
