package manager;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Status;
import com.yandex.app.model.Task;
import com.yandex.app.service.HistoryManager;
import com.yandex.app.service.TaskManager;
import com.yandex.app.utils.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
}