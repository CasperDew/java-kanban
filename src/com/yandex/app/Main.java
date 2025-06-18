package com.yandex.app;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Task;
import com.yandex.app.service.TaskManager;
import com.yandex.app.utils.Managers;

public class Main {

    private static final TaskManager inMemoryTaskManager = Managers.getDefault(Managers.getDefaultHistory());

    public static void main(String[] args) {

        Epic test = new Epic("Проверить что-то еще", "какое-то описание");
        inMemoryTaskManager.addEpic(test);
        System.out.println(test);
        Epic test2 = new Epic("Задача", "какое-то описание");
        inMemoryTaskManager.addEpic(test2);

        inMemoryTaskManager.getEpicByID(1);
        inMemoryTaskManager.getEpicByID(2);

        System.out.println("История просмотров:");
        for (Task task : Main.inMemoryTaskManager.getHistory()) {
            System.out.println(task);
        }

        //проверка работы удалений
        Epic newT = new Epic("2", "какое-то описание");
        inMemoryTaskManager.addEpic(newT);
        System.out.println(" ");
        System.out.println(inMemoryTaskManager.getEpicByID(3));

        inMemoryTaskManager.deleteEpicById(3);
        System.out.println(inMemoryTaskManager.getEpicByID(3));

        System.out.println(" ");
        Task newtask = new Task("Task", "new task");
        inMemoryTaskManager.addTask(newtask);
        System.out.println(inMemoryTaskManager.getTasks());
        inMemoryTaskManager.deleteTasks();
        System.out.println(inMemoryTaskManager.getTasks());
        System.out.println(" ");


    }
}
