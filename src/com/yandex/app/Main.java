package com.yandex.app;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Status;
import com.yandex.app.model.Subtask;
import com.yandex.app.service.Task;
import com.yandex.app.service.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Epic test = new Epic("Проверить что-то еще", "какое-то описание");
        taskManager.addEpic(test);
        System.out.println(test);
        Subtask testTask1 = new Subtask("Обновить подзадачу", "такая задача", test.getId());
        taskManager.addSubtask(testTask1);
        System.out.println(test);
        testTask1.setStatus(Status.DONE);
        taskManager.updateSubtask(testTask1);
        System.out.println(testTask1);

    }
}
