package com.yandex.app.http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.app.model.Task;
import com.yandex.app.service.TaskManager;

import java.io.IOException;
import java.util.Set;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if ("GET".equals(method) && "/prioritized".equals(path)) {
                Set<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
                String response = gson.toJson(prioritizedTasks);
                sendText(exchange, response);
            } else {
                exchange.sendResponseHeaders(405, 0);
                exchange.close();
            }

        } catch (Exception e) {
            sendInternalServerError(exchange);
        }
    }
}