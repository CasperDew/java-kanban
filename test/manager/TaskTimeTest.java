package manager;


import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.service.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTimeTest {
    private InMemoryTaskManager taskManager;
    private Task task1;
    private Task task2;
    private Epic epic;
    private Subtask subtask1;
    private Subtask subtask2;

    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager();

        // Задачи с разным временем
        task1 = new Task("Задача 1", "Описание 1");
        task1.setStartTime(LocalDateTime.of(2025, 7, 1, 10, 0));
        task1.setDuration(Duration.ofMinutes(60));

        task2 = new Task("Задача 2", "Описание 2");
        task2.setStartTime(LocalDateTime.of(2025, 7, 1, 11, 0));
        task2.setDuration(Duration.ofMinutes(30));

        epic = new Epic("Епик", "Описание эпик");
        taskManager.addEpic(epic);

        subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic.getId());
        subtask1.setStartTime(LocalDateTime.of(2025, 7, 1, 5, 0));
        subtask1.setDuration(Duration.ofMinutes(30));

        subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", epic.getId());
        subtask2.setStartTime(LocalDateTime.of(2025, 7, 1, 6, 0));
        subtask2.setDuration(Duration.ofMinutes(45));
    }

    @Test
    public void shouldTaskIntersection() {
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        assertFalse(taskManager.isTaskIntersect(task1, task2), "Задачи пересекаются по времени");

        // Тест пересечения задач
        Task intersectingTask = new Task("Пересечение задачи", "Оисание");
        intersectingTask.setStartTime(LocalDateTime.of(2025, 7, 1, 10, 30));
        intersectingTask.setDuration(Duration.ofMinutes(60));

        assertTrue(taskManager.isTaskIntersect(task1, intersectingTask), "Задачи не пересекаются");
        assertTrue(taskManager.isTaskIntersectWithAny(intersectingTask), "Задачи не пересекаются");
    }

    // Проверка сохранения правильной последовательности задач
    @Test
    public void shouldPrioritizedTasks() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addEpic(epic);

        Task task1 = new Task("Задача 1", "описание задачи 1");
        task1.setStartTime(LocalDateTime.of(2025, 7, 1, 10, 0));
        task1.setDuration(Duration.ofHours(1));
        taskManager.addTask(task1);

        Task task2 = new Task("Задача 2", "Описание задачи 2");
        task2.setStartTime(LocalDateTime.of(2025, 7, 1, 11, 0));
        task2.setDuration(Duration.ofMinutes(30));
        taskManager.addTask(task2);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic.getId());
        subtask1.setStartTime(LocalDateTime.of(2025, 7, 1, 9, 0));
        subtask1.setDuration(Duration.ofMinutes(45));
        taskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", epic.getId());
        subtask2.setStartTime(LocalDateTime.of(2025, 7, 1, 10, 0));
        subtask2.setDuration(Duration.ofMinutes(30));
        taskManager.addSubtask(subtask2);

        Set<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        List<Task> taskList = new ArrayList<>(prioritizedTasks);

        assertEquals(4, taskList.size(), "Должно быть 4 задачи");

        assertEquals(subtask1, taskList.get(0), "Первой должна быть Подзадача 1");
        assertEquals(task1, taskList.get(1), "Второй должна быть Задача 1");
        assertEquals(subtask2, taskList.get(2), "Третьей должна быть Подзадача 2");
        assertEquals(task2, taskList.get(3), "Четвертой должна быть Задача 2");
    }

    //Проверка, что время окончания Эпика = времени окончания последней подзадачи
    @Test
    public void shouldEpicTimeCalculation() {
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        LocalDateTime expectedEndTime = subtask2.getStartTime().plus(subtask2.getDuration());
        assertEquals(expectedEndTime, epic.getEndTime(), "Конечное время не совпадает");
    }

    @Test
    public void shouldTaskWithoutTime() {
        Task task = new Task("Задача", "Описание");
        taskManager.addTask(task);

        assertFalse(taskManager.getPrioritizedTasks().contains(task),
                "Задача без времени не может быть в приоритетном списке.");
        assertFalse(taskManager.isTaskIntersectWithAny(task), "Задача без времени пересекается с задачей");
    }

    @Test
    public void shouldTaskUpdateWithIntersection() {
        taskManager.addTask(task1);
        task1.setStartTime(LocalDateTime.of(2025, 7, 1, 10, 25));
        taskManager.updateTask(task1);

        assertEquals(LocalDateTime.of(2025, 7, 1, 10, 25), task1.getStartTime(),
                "Задача пересекается сама с собой");
    }
}
