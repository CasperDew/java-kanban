package manager;


import com.google.gson.Gson;
import com.yandex.app.http.HttpTaskServer;
import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.service.InMemoryTaskManager;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {
    private TaskManager manager;
    private HttpTaskServer taskServer;
    private Gson gson;
    private HttpClient client;

    private static final String HOSTS = "http://localhost:8080/";

    @BeforeEach
    public void beforeEach() throws IOException {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        gson = HttpTaskServer.getGson();
        client = HttpClient.newHttpClient();
        taskServer.start();
    }

    @AfterEach
    public void afterEach() {
        taskServer.stop();
    }

    @Test
    public void shouldAddTask() throws IOException, InterruptedException {
        Task task = new Task("Задача 1", "Описание задачи 1");
        task.setStartTime(LocalDateTime.of(2025, 1, 1, 5, 0));
        task.setDuration(Duration.ofMinutes(30));

        String taskJs = gson.toJson(task);
        URI uri = URI.create(HOSTS + "tasks");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(taskJs))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Task> tasks = manager.getTasks();

        assertNotNull(tasks, "Список задач не вернулся");
        assertEquals(1, tasks.size(), "Некорректное количество задач");
        assertEquals("Задача 1", tasks.get(0).getName(), "Имя задачи не совпало");
    }

    @Test
    public void shouldAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        String epicJs = gson.toJson(epic);

        URI uri = URI.create(HOSTS + "epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(epicJs))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        assertEquals(1, manager.getEpics().size(), "Некорректное количество задач");
        assertEquals("Эпик 1", manager.getEpics().get(0).getName(), "Имя эпика не совпало");
    }

    @Test
    public void shouldAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Подзадача 1", "Описание подзадачи 1", epic.getId());
        subtask.setStartTime(LocalDateTime.of(2025, 1, 1, 5, 0));
        subtask.setDuration(Duration.ofMinutes(30));

        String subtaskJs = gson.toJson(subtask);
        URI uri = URI.create(HOSTS + "subtasks");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJs))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        assertEquals(1, manager.getSubtasks().size(), "Некорректное количество задач");
        assertEquals("Подзадача 1", manager.getSubtasks().get(0).getName(), "Имя эпика не совпало");
    }

    @Test
    public void shouldAddTaskWithTimeIntersection() throws IOException, InterruptedException {
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        task1.setStartTime(LocalDateTime.of(2025, 1, 1, 10, 0));
        task1.setDuration(Duration.ofMinutes(60));
        manager.addTask(task1);

        Task task2 = new Task("Задача 2", "Описание задачи 2");
        task2.setStartTime(LocalDateTime.of(2025, 1, 1, 10, 30));
        task2.setDuration(Duration.ofMinutes(30));

        String taskJson = gson.toJson(task2);
        URI uri = URI.create(HOSTS + "tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode(), "Задачи не пересекаются");
    }

    @Test
    public void shouldGetTasks() throws IOException, InterruptedException {
        Task task = new Task("Задача 1", "Описание задачи 1");
        manager.addTask(task);

        URI uri = URI.create(HOSTS + "tasks");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertNotNull(response.body(), "Ничего не найдено");
        assertTrue(response.body().contains("Задача 1"), "Задача 1 не найдена");
    }

    @Test
    public void shouldGetEpicSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Подзадача 1", "Описание подзадачи 1", epic.getId());
        manager.addSubtask(subtask);

        URI uri = URI.create(HOSTS + "epics/" + epic.getId() + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Подзадача 1"), "Задача не найдена");
    }

    @Test
    public void shouldGetHistory() throws IOException, InterruptedException {
        Task task = new Task("Задача 1", "Описание задачи 1");
        manager.addTask(task);
        manager.getTaskByID(task.getId());

        URI uri = URI.create(HOSTS + "history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Задача 1"), "Задача не найдена");
    }

    @Test
    public void shouldGetPrioritizedTasks() throws IOException, InterruptedException {
        Task task = new Task("Задача 1", "Описание задачи 1");
        task.setStartTime(LocalDateTime.of(2025, 1, 1, 5, 0));
        task.setDuration(Duration.ofMinutes(30));
        manager.addTask(task);

        URI uri = URI.create(HOSTS + "prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Задача 1"), "Задача не найдена");
    }

    @Test
    public void shouldGetNonExistentTask() throws IOException, InterruptedException {
        URI uri = URI.create(HOSTS + "tasks/243");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Найдена несуществующая задача");
    }

    @Test
    public void shouldDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Задача 1", "Описание задачи 1");
        manager.addTask(task);

        URI uri = URI.create(HOSTS + "tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(manager.getTasks().isEmpty(), "Задача не была удалена");
    }


}
