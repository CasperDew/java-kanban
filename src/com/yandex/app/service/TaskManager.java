package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    int getNextID();

    Task addTask(Task task);

    Epic addEpic(Epic epic);

    Subtask addSubtask(Subtask subtask);

    Task updateTask(Task task);

    Epic updateEpic(Epic epic);

    Subtask updateSubtask(Subtask subtask);

    // методы получения по id
    Task getTaskByID(int id);

    Epic getEpicByID(int id);

    Subtask getSubtaskByID(int id);

    //получение подзадачи Эпик метода по id эпика
    List<Subtask> getEpicSubtaskByID(int epicID);

    ArrayList<Task> getTasks();

    ArrayList<Epic> getEpics();

    ArrayList<Subtask> getSubtasks();

    ArrayList<Task> getHistory();

    //очистка задач
    void deleteTasks();

    void deleteEpics();

    void deleteSubtasks();

    void deleteTaskByID(int id);

    void deleteEpicById(int id);

    void deleteSubtaskByID(int id);

}
