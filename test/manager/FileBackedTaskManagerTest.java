package manager;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Status;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.service.FileBackedTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    private FileBackedTaskManager taskManager;
    private File file;

    @BeforeEach
    public void beforeEach() throws IOException {
        file = File.createTempFile("tasks", ".csv");
        taskManager = new FileBackedTaskManager(file);
    }

    @AfterEach
    public void tearDown() {
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void shouldSaveAndLoadTasks() {
        Task task = new Task("Задача 1", "Описание задачи 1");
        taskManager.addTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        Task loadedTask = loadedManager.getTaskByID(task.getId());

        assertNotNull(loadedTask, "Задача не найдена");
        assertEquals(task, loadedTask, "Задача не загружена в файл");
    }

    @Test
    public void shouldSaveAndLoadEpicsSubtasks() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание позадачи 1", epic.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание позадачи 2", epic.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        Epic loadedEpic = loadedManager.getEpicByID(epic.getId());
        List<Subtask> loadedSubtasks = loadedManager.getEpicSubtaskByID(epic.getId());

        assertNotNull(loadedEpic, "Эпик не найден");
        assertEquals(epic, loadedEpic, "Задача не загружена в файл");
        assertEquals(2, loadedSubtasks.size(), "Подзадачи пусты");
        assertTrue(loadedSubtasks.contains(subtask1), "subtask1 не найден");
        assertTrue(loadedSubtasks.contains(subtask2), "subtask2 не найден");
    }

    @Test
    public void shouldSaveAndLoadHistory() {
        Task task1 = new Task("Задача 1", "Описание зачдачи 1");
        Task task2 = new Task("Задача 2", "Описание зачдачи 2");
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        taskManager.getTasks();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        List<Task> loadedHistory = loadedManager.getHistory();
        assertTrue(loadedHistory.isEmpty(), "История пустая");

        assertNotNull(loadedManager.getTaskByID(task1.getId()), "Задача 1 не найдена");
        assertNotNull(loadedManager.getTaskByID(task2.getId()), "Задача 2 не найдена");
    }

    @Test
    public void shouldHandleEmptyFile() {
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        assertTrue(loadedManager.getTasks().isEmpty());
        assertTrue(loadedManager.getEpics().isEmpty());
        assertTrue(loadedManager.getSubtasks().isEmpty());
        assertTrue(loadedManager.getHistory().isEmpty());
    }

    @Test
    public void shouldHandleDeleteTask() {
        Task task = new Task("Задача 1", "Описание задачи 1");
        taskManager.addTask(task);
        taskManager.deleteTaskByID(task.getId());

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        assertTrue(loadedManager.getTasks().isEmpty(), "Список задач не пустой");
        assertNull(loadedManager.getTaskByID(task.getId()), "Задача не удалена");
    }

    @Test
    public void shouldHandleDeleteEpicWithSubtasks() {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Подзадача 1", "Описание подзадачи 1", epic.getId());
        taskManager.addSubtask(subtask);

        taskManager.deleteEpicById(epic.getId());

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        assertTrue(loadedManager.getEpics().isEmpty(), "Список эпиков не пустой");
        assertTrue(loadedManager.getSubtasks().isEmpty(), "Список подзадач не пустой");
        assertNull(loadedManager.getEpicByID(epic.getId()), "Эпик не удален");
        assertNull(loadedManager.getSubtaskByID(subtask.getId()), "Подзажача не удалена");
    }

    @Test
    public void shouldUpdateTasksInFile() {
        Task task = new Task("Задача 1", "Описание задачи 1");
        taskManager.addTask(task);

        task.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        Task loadedTask = loadedManager.getTaskByID(task.getId());
        assertEquals(Status.IN_PROGRESS, loadedTask.getStatus(), "Статус не соответствует");
    }

    @Test
    public void shouldHandleCorruptedFile() throws IOException {
        File corruptedFile = File.createTempFile("warning", "csv");
        try {
            FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(corruptedFile);
            assertTrue(loadedManager.getTasks().isEmpty());
            assertTrue(loadedManager.getEpics().isEmpty());
            assertTrue(loadedManager.getSubtasks().isEmpty());
            assertTrue(loadedManager.getHistory().isEmpty());
        } finally {
            corruptedFile.delete();
        }
    }
}
