package com.yandex.app.http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.app.Exceptions.NotFoundException;
import com.yandex.app.model.Task;
import com.yandex.app.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public TaskHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            switch (method) {
                case "GET":
                    handleGet(exchange, path);
                    break;

                case "POST":
                    handlePost(exchange);
                    break;

                case "DELETE":
                    handleDelete(exchange, path);
                    break;

                default:
                    exchange.sendResponseHeaders(405, 0);
                    exchange.close();
            }

        } catch (NotFoundException e) {
            sendNotFound(exchange);

        } catch (IllegalArgumentException e) {
            sendHasInteractions(exchange);

        } catch (Exception e) {
            sendInternalServerError(exchange);
        }
    }

    private void handleGet(HttpExchange exchange, String path) throws IOException {
        if (path.equals("/tasks")) {
            List<Task> tasks = taskManager.getTasks();
            String response = gson.toJson(tasks);
            sendText(exchange, response);

        } else if (path.startsWith("/tasks/")) {
            String idStr = path.substring("/tasks/".length());
            try {
                int id = Integer.parseInt(idStr);
                Task task = taskManager.getTaskByID(id);

                if (task == null) {
                    throw new NotFoundException("Задача не найдена");
                }

                String response = gson.toJson(task);
                sendText(exchange, response);

            } catch (NumberFormatException e) {
                sendNotFound(exchange);
            }
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(body, Task.class);

        if (task.getId() == 0) {
            taskManager.addTask(task);
            String response = gson.toJson(task);
            sendCreated(exchange, response);

        } else {
            taskManager.updateTask(task);
            String response = gson.toJson(task);
            sendCreated(exchange, response);
        }
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        if (path.startsWith("/tasks/")) {
            String idStr = path.substring("/tasks/".length());

            try {
                int id = Integer.parseInt(idStr);
                taskManager.deleteTaskByID(id);
                exchange.sendResponseHeaders(200, 0);
                exchange.close();
            } catch (NumberFormatException e) {
                sendNotFound(exchange);
            }
        } else {
            sendNotFound(exchange);
        }
    }
}
