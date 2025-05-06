package manager;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Status;
import com.yandex.app.model.Task;
import com.yandex.app.service.TaskManager;
import com.yandex.app.utils.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private static TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void getHistoryShouldReturnListOf10Tasks() {
        for (int i = 0; i < 20; i++) {
            taskManager.addTask(new Task("Some name", "Some description"));
        }

        List<Task> tasks = taskManager.getTasks();
        for (Task task : tasks) {
            taskManager.getTaskByID(task.getId());
        }

        List<Task> list = taskManager.getHistory();
        assertEquals(10, list.size(), "Неверное количество элементов в истории ");
    }

    @Test
    public void testInitialization() {
        assertNotNull(taskManager, "Задача не должна быть равна нулю");
    }


    @Test
    public void historyManagerShouldKeepDifferentVersionsOfTask(){
        Task task = new Task("Тестовая задачка", "И такое же описание", Status.IN_PROGRESS);
        taskManager.addTask(task);

        taskManager.getTaskByID(task.getId());

        assertEquals(task.getId(), taskManager.getHistory().get(0).getId());
        assertEquals(task.getName(), taskManager.getHistory().get(0).getName());
        assertEquals(task.getDescription(), taskManager.getHistory().get(0).getDescription());
        assertEquals(task.getStatus(), taskManager.getHistory().get(0).getStatus());

        Task updateTask = new Task("Обновленная задача", "Обновленное описание", Status.DONE);
        updateTask.setId(task.getId());
        taskManager.updateTask(updateTask);

        taskManager.getTaskByID(updateTask.getId());

        assertEquals(updateTask.getId(), taskManager.getHistory().get(1).getId());

        assertEquals(updateTask.getName(), taskManager.getHistory().get(1).getName());
        assertNotEquals(updateTask.getName(), taskManager.getHistory().get(0).getName());

        assertEquals(updateTask.getDescription(), taskManager.getHistory().get(1).getDescription());
        assertNotEquals(updateTask.getDescription(), taskManager.getHistory().get(0).getDescription());

        assertEquals(updateTask.getStatus(), taskManager.getHistory().get(1).getStatus());
        assertNotEquals(updateTask.getStatus(), taskManager.getHistory().get(0).getStatus());
    }

    @Test
    public void getHistoryShouldReturnOldEpicAfterUpdate() {
        Epic flatRenovation = new Epic("Сделать ремонт", "Нужно успеть за отпуск");
        taskManager.addEpic(flatRenovation);
        taskManager.getEpicByID(flatRenovation.getId());
        taskManager.updateEpic(new Epic( "Новое имя", "новое описание",
                Status.IN_PROGRESS));
        List<Task> epics = taskManager.getHistory();
        Epic oldEpic = (Epic) epics.getFirst();
        assertEquals(flatRenovation.getName(), oldEpic.getName(),
                "В истории не сохранилась старая версия эпика");
        assertEquals(flatRenovation.getDescription(), oldEpic.getDescription(),
                "В истории не сохранилась старая версия эпика");
    }
}