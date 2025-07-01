package com.yandex.app;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Status;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.service.FileBackedTaskManager;
import com.yandex.app.service.TaskManager;
import com.yandex.app.utils.Managers;

import java.io.File;

public class Main {

    private static final TaskManager inMemoryTaskManager = Managers.getDefault();

    public static void main(String[] args) {

        File file = new File("tasks.csv");
        TaskManager manager = new FileBackedTaskManager(file);

        // Добавляем задачи
        Task task1 = new Task("Задача 1", "Описание 1", Status.NEW);
        manager.addTask(task1);

        Task task2 = new Task("Задача 2", "Описание 2", Status.NEW);
        manager.addTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", Status.NEW);
        manager.addEpic(epic1);

        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2", Status.NEW);
        manager.addEpic(epic2);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи", Status.NEW, epic1.getId());
        manager.addSubtask(subtask1);

        // Все изменения будут сохранены в файл

        // Проверим загрузку из файла
        TaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        System.out.println("Все задачи после загрузки:");
        for (Task task : loadedManager.getTasks()) {
            System.out.println(task);
        }

        System.out.println("Все эпики после загрузки:");
        for (Epic epic : loadedManager.getEpics()) {
            System.out.println(epic);
        }

        System.out.println("Все сабтаски после загрузки:");
        for (Subtask subtask : loadedManager.getSubtasks()) {
            System.out.println(subtask);
        }
    }
}
