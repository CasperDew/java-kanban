package com.yandex.app;

import com.yandex.app.model.Epic;
import com.yandex.app.service.InMemoryTaskManager;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

        Epic test = new Epic("Проверить что-то еще", "какое-то описание");
        inMemoryTaskManager.addEpic(test);
        System.out.println(test);

        Epic testTask1 = new Epic("Обновить подзадачу", "такая задача");

        inMemoryTaskManager.updateEpic(testTask1);
        System.out.println(inMemoryTaskManager.getEpics());

        System.out.println(inMemoryTaskManager.getHistory());
    }
}
