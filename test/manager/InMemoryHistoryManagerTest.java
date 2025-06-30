package manager;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Status;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.service.TaskManager;
import com.yandex.app.utils.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {
    private TaskManager manager;

    @BeforeEach
    void setUp() {
        manager = Managers.getDefault();
    }

    @Test
    void shouldReturnTrueIfTaskAdded() {
        manager.addTask(new Task("Задача 1", "Первая задача", Status.NEW));
        manager.getTaskByID(1);
        final List<Task> history = manager.getHistory();
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void shouldReturnTrueIfTaskDeleted() {
        manager.addTask(new Task("Задача 1", "Первая задача", Status.NEW));
        manager.getTaskByID(1);
        manager.deleteTaskByID(1);
        final List<Task> history = manager.getHistory();
        assertEquals(0, history.size(), "История пустая.");
    }

    @Test
    void shouldReturnTrueIfEpicDeleted() {
        manager.addEpic(new Epic("Епик 1", "Первый эпик"));
        manager.getEpicByID(1);
        manager.deleteEpicById(1);
        final List<Task> history = manager.getHistory();
        assertEquals(0, history.size(), "История пустая");
    }

    @Test
    void shouldReturnTrueIfSubtaskDeleted() {
        manager.addEpic(new Epic("Епик 1", "Первый эпик"));
        manager.addSubtask(new Subtask("Подзадача 1", "Подзадача эпика", 1));
        manager.getTaskByID(1);
        manager.getSubtaskByID(2);
        manager.deleteEpicById(1);
        final List<Task> history = manager.getHistory();
        assertEquals(0, history.size(), "История пустая");
    }

    @Test
    void checkSizeHistoryIfTaskEquals() {
        Task task = new Task("Задача", "Первая задача", Status.NEW);
        manager.addTask(task);
        final int sizeFromRequestHistoryShouldBe = 1;
        final int sizeForCheckRequestSize = 20;
        for (int i = 0; i <= sizeForCheckRequestSize; i++) {
            manager.getTaskByID(task.getId());
        }
        assertEquals(sizeFromRequestHistoryShouldBe, manager.getHistory().size());
    }

    @Test
    void checkSizeHistoryIfSubtaskEquals() {
        Epic epic = new Epic("Епик 1", "Первый эпик");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача 1", "Подзадача эпика", 1);
        manager.addSubtask(subtask);
        final int sizeFromRequestHistoryShouldBe = 1;
        final int sizeForCheckRequestSize = 20;
        for (int i = 0; i <= sizeForCheckRequestSize; i++) {
            manager.getEpicByID(epic.getId());
            manager.getSubtaskByID(subtask.getId());
        }
        assertEquals(sizeFromRequestHistoryShouldBe, manager.getHistory().size());
    }
}