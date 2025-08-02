package com.yandex.app.http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.app.Exceptions.NotFoundException;
import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public EpicHandler(TaskManager taskManager, Gson gson) {
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
        if (path.equals("/epics")) {
            List<Epic> epics = taskManager.getEpics();
            String response = gson.toJson(epics);
            sendText(exchange, response);

        } else if (path.startsWith("/epics/") && path.endsWith("/subtasks")) {
            String pathWithoutSubtasks = path.substring(0, path.length() - "/subtasks".length());
            String idStr = pathWithoutSubtasks.substring("/epics/".length());

            try {
                int id = Integer.parseInt(idStr);
                Epic epic = taskManager.getEpicByID(id);

                if (epic == null) {
                    throw new NotFoundException("Эпик не найден");
                }

                List<Subtask> subtasks = taskManager.getEpicSubtaskByID(id);
                String response = gson.toJson(subtasks);
                sendText(exchange, response);

            } catch (NumberFormatException e) {
                sendNotFound(exchange);
            }

        } else if (path.startsWith("/epics/")) {
            String idStr = path.substring("/epics/".length());

            try {
                int id = Integer.parseInt(idStr);
                Epic epic = taskManager.getEpicByID(id);
                if (epic == null) {
                    throw new NotFoundException("Эпик не найден");
                }
                String response = gson.toJson(epic);
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
        Epic epic = gson.fromJson(body, Epic.class);

        taskManager.addEpic(epic);
        String response = gson.toJson(epic);
        sendText(exchange, response);
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        if (path.startsWith("/epics/")) {
            String idStr = path.substring("/epics/".length());

            try {
                int id = Integer.parseInt(idStr);
                taskManager.deleteEpicById(id);
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
