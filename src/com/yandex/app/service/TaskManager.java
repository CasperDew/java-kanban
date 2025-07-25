package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;

import java.util.List;
import java.util.Set;

public interface TaskManager {
    int getNextID();

    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    // методы получения по id
    Task getTaskByID(int id);

    Epic getEpicByID(int id);

    Subtask getSubtaskByID(int id);

    //получение подзадачи Эпик метода по id эпика
    List<Subtask> getEpicSubtaskByID(int epicID);

    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    List<Task> getHistory();

    //Получение списка задач, отсортированных по startTime
    Set<Task> getPrioritizedTasks();

    //очистка задач
    void deleteTasks();

    void deleteEpics();

    void deleteSubtasks();

    void deleteTaskByID(int id);

    void deleteEpicById(int id);

    void deleteSubtaskByID(int id);

    //проверка на пересечение задач по времени
    boolean isTaskIntersect(Task task1, Task task2);

    //проверка на пересечение задач с другими задачами в менеджере
    boolean isTaskIntersectWithAny(Task task1);

}
