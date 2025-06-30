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

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    //проверяем, что экземпляры класса Task равны друг другу, если равен их id
    @Test
    public void checkTheInstancesOfTaskAreEqualToEachOther() {
        Task task = new Task("Задача", "Описание задачи", Status.NEW);
        taskManager.addTask(task);

        final Task savedTask = taskManager.getTaskByID(task.getId());

        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task, savedTask, "Задачи не совпадают");
    }

    //проверяем, что наследники класса Task равны друг другу, если равен их id
    @Test
    public void checkingTheComplianceOfEpicIfTheirIdsAreEqual() {
        Epic epic = new Epic("Тестовый эпик", "Описание эпика");
        taskManager.addEpic(epic);

        Epic savedEpic = taskManager.getEpicByID(epic.getId());

        assertEquals(epic, savedEpic, "Эпики должны быть равны!");
        assertEquals(epic.getId(), savedEpic.getId(), "Эпики должны быть равны!");
    }

    //проверяем, что объект Subtask нельзя сделать своим же эпиком;
    @Test
    public void checkingTheComplianceOfSubtaskIfTheirIdsAreEqual() {
        Epic epic = new Epic("Тестовый эпик", "Описание эпика", Status.NEW);
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", epic.getId());
        taskManager.addSubtask(subtask);
        Subtask saveSubtask = taskManager.getSubtaskByID(subtask.getId());

        assertEquals(subtask, saveSubtask, "Подзадачи должны быть равны");
        assertEquals(subtask.getId(), saveSubtask.getId(), "Подзадачи должны быть равны");
    }

    @Test
    public void addNewTask() {
        //проверяем, что InMemoryTaskManager добавляет задачи и может найти их по id;
        final Task task = new Task("Test addNewTask", "Test addNewTask description");
        taskManager.addTask(task);
        final Task savedTask = taskManager.getTaskByID(task.getId());
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    public void taskShouldRemainUnchangedAfterAddingToManager() {
        String name = "Задача";
        String description = "Описание задачи";
        Task task = new Task(name, description);
        taskManager.addTask(task);

        assertEquals(task.getName(), name, "Заголовок должен совпадать");
        assertEquals(task.getDescription(), description, "Описание должено совпадать");
    }

    @Test
    public void epicShouldRemainUnchangedAfterAddingToManager() {
        String name = "Задача";
        String description = "Описание задачи";
        Epic epic = new Epic(name, description);
        taskManager.addTask(epic);

        assertEquals(epic.getName(), name, "Заголовок должен совпадать");
        assertEquals(epic.getDescription(), description, "Описание должено совпадать");
    }

    @Test
    public void checkingWhatEpicUpdatedCorrect() {

        Epic epic = new Epic("Основной", "Основной", Status.NEW);
        taskManager.addEpic(epic);

        Epic epic1 = new Epic("Для теста обновления", "Обновление", Status.NEW);
        epic1.setId(epic.getId());

        taskManager.updateEpic(epic1);

        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", Status.DONE, epic.getId());
        taskManager.addSubtask(subtask);

        assertEquals(epic1, taskManager.getEpicByID(epic.getId()), "Эпик не обновился!");

    }

    @Test
    public void updateTaskShouldReturnTaskWithTheSameId() {
        Task expected = new Task("имя", "описание");
        taskManager.addTask(expected);

        Task updatedTask = new Task(expected.getId(), "новое имя", "новое описание", Status.DONE);
        taskManager.updateTask(updatedTask);

        Task actual = taskManager.getTaskByID(1);

        assertEquals(expected.getId(), actual.getId(), "Вернулась задачи с другим id");

        assertEquals(updatedTask.getName(), actual.getName(), "Имя задачи не обновилось");
        assertEquals(updatedTask.getDescription(), actual.getDescription(), "Описание задачи не обновилось");
        assertEquals(updatedTask.getStatus(), actual.getStatus(), "Статус задачи не обновился");
    }

    @Test
    public void updateSubtaskShouldReturnSubtaskWithTheSameId() {
        Epic epic = new Epic("имя", "описание");
        taskManager.addEpic(epic);

        Subtask expected = new Subtask("имя", "описание", epic.getId());
        taskManager.addSubtask(expected);

        Subtask updatedSubtask = new Subtask(expected.getId(), "новое имя", "новое описание",
                Status.DONE, epic.getId());

        Subtask actual = taskManager.getSubtaskByID(updatedSubtask.getId());

        assertNotNull(actual, "Обновленная подзадача не найдена после обновления");
        assertEquals(expected.getId(), actual.getId(), "Вернулась подзадача с другим id");
    }

    @Test
    public void checkingWhatAfterRemoveAllEpicRemovedAllSubtasksEverywhere() {

        Epic epic = new Epic("Основной", "Основной", Status.NEW);
        taskManager.addEpic(epic);

        Epic epic1 = new Epic("Для теста обновления", "Обновление", Status.NEW);
        taskManager.addEpic(epic1);

        Subtask subtask = new Subtask("Я подзадача", " Я описание её", Status.DONE, epic.getId());
        taskManager.addSubtask(subtask);

        assertFalse(taskManager.getEpics().isEmpty());
        assertFalse(taskManager.getSubtasks().isEmpty());
        assertFalse(epic.getSubtaskList().isEmpty());

        taskManager.deleteEpics();

        assertTrue(taskManager.getEpics().isEmpty());
        assertTrue(taskManager.getSubtasks().isEmpty());

    }

    @Test
    void checkHistoryManagerSavesTaskVersions() {
        TaskManager manager = Managers.getDefault();
        Task checkTask = new Task("Задача 1", "Первая задача", Status.NEW);
        manager.addTask(checkTask);
        manager.getTaskByID(checkTask.getId());
        Task checkTask2 = new Task(checkTask.getName(), checkTask.getDescription(), checkTask.getStatus());
        checkTask2.setId(checkTask.getId());
        manager.updateTask(checkTask2);
        checkTask.setStatus(Status.DONE);
        assertEquals(checkTask, manager.getHistory().getFirst());
    }

}