package manager;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Status;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.service.HistoryManager;
import com.yandex.app.service.TaskManager;
import com.yandex.app.utils.Managers;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {
    @Test
    void shouldReturnTrueIfTaskAdded() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        TaskManager manager = Managers.getDefault(historyManager);
        manager.addTask(new Task("Задача 1", "Первая задача", Status.NEW));
        historyManager.add(manager.getTaskByID(1));
        final List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void shouldReturnTrueIfTaskDeleted() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        TaskManager manager = Managers.getDefault(historyManager);
        manager.addTask(new Task("Задача 1", "Первая задача", Status.NEW));
        historyManager.add(manager.getTaskByID(1));
        manager.deleteTaskByID(1);
        final List<Task> history = historyManager.getHistory();
        assertEquals(0, history.size(), "История пустая.");
    }

    @Test
    void shouldReturnTrueIfEpicDeleted() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        TaskManager manager = Managers.getDefault(historyManager);
        manager.addEpic(new Epic("Епик 1", "Первый эпик"));
        historyManager.add(manager.getTaskByID(1));
        manager.deleteEpicById(1);
        final List<Task> history = historyManager.getHistory();
        assertEquals(0, history.size(), "История пустая");
    }

    @Test
    void shouldReturnTrueIfSubtaskDeleted() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        TaskManager manager = Managers.getDefault(historyManager);
        manager.addEpic(new Epic("Епик 1", "Первый эпик"));
        manager.addSubtask(new Subtask("Подзадача 1", "Подзадача эпика", 1));
        historyManager.add(manager.getTaskByID(1));
        historyManager.add(manager.getSubtaskByID(2));
        manager.deleteEpicById(1);
        final List<Task> history = historyManager.getHistory();
        assertEquals(0, history.size(), "История пустая");
    }

    @Test
    void checkSizeHistoryIfTaskEquals() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task = new Task("Задача", "Первая задача", Status.NEW);
        final int sizeFromRequestHistoryShouldBe = 1;
        final int sizeForCheckRequestSize = 20;
        for (int i = 0; i <= sizeForCheckRequestSize; i++) {
            historyManager.add(task);
        }
        assertEquals(sizeFromRequestHistoryShouldBe, historyManager.getHistory().size());
    }

    @Test
    void checkSizeHistoryIfSubtaskEquals() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Epic epic = new Epic("Епик 1", "Первый эпик");
        Subtask subtask = new Subtask("Подзадача 1", "Подзадача эпика", 1);
        final int sizeFromRequestHistoryShouldBe = 1;
        final int sizeForCheckRequestSize = 20;
        for (int i = 0; i <= sizeForCheckRequestSize; i++) {
            historyManager.add(epic);
            historyManager.add(subtask);
        }
        assertEquals(sizeFromRequestHistoryShouldBe, historyManager.getHistory().size());
    }
}