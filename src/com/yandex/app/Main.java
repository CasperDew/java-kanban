package com.yandex.app;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Status;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.service.TaskManager;
import com.yandex.app.utils.Managers;

public class Main {

    private static final TaskManager inMemoryTaskManager = Managers.getDefault();

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


    }
}
