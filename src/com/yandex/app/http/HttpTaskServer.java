package com.yandex.app.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import com.yandex.app.http.adapter.DurationAdapter;
import com.yandex.app.http.adapter.LocalDateTimeAdapter;
import com.yandex.app.http.handler.*;
import com.yandex.app.service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final TaskManager taskManager;
    private final Gson gson;
    private HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = createGson();
    }

    public static void main(String[] args) throws IOException {
        System.out.println("HTTP-сервер готов к запуску на " + PORT + " порту");
    }

    public static Gson getGson() {
        return createGson();
    }

    private static Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    public void start() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);

        httpServer.createContext("/tasks", new TaskHandler(taskManager, gson));
        httpServer.createContext("/subtasks", new SubtaskHandler(taskManager, gson));
        httpServer.createContext("/epics", new EpicHandler(taskManager, gson));
        httpServer.createContext("/history", new HistoryHandler(taskManager, gson));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager, gson));

        httpServer.start();
    }

    public void stop() {
        if (httpServer != null) {
            httpServer.stop(0);
        }
    }
}
